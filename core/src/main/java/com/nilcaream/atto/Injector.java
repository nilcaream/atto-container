package com.nilcaream.atto;

import com.nilcaream.atto.exception.AmbiguousTargetException;
import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import com.nilcaream.atto.exception.TargetNotFoundException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nilcaream.atto.Descriptor.DEFAULT_QUALIFIER;
import static java.lang.reflect.Modifier.isStatic;

class Injector {

    private final Scanner scanner;

    private Logger logger = Logger.nullLogger();


    @lombok.Builder(builderClassName = "Builder")
    private Injector(String scanPackage, Logger logger) {
        if (logger != null) {
            this.logger = logger;
        }
        scanner = Scanner.builder().logger(this.logger).scanPackage(scanPackage).build();
        if (scanPackage != null && !scanner.isAvailable()) {
            throw new ReflectionsNotFoundException("Reflections required on classpath for scanning package " + scanPackage);
        }
    }

    boolean isSingleton(Class<?> cls) {
        return cls.isAnnotationPresent(Singleton.class);
    }

    Descriptor describe(Class<?> cls) {
        return describe(cls, cls, cls.getAnnotations());
    }

    Descriptor describe(Field field) {
        Class<?> type = field.getType();
        if (Provider.class.isAssignableFrom(type)) {
            return describe(field, parametrizedType(field, field.getGenericType()), field.getAnnotations());
        } else {
            return describe(field, type, field.getAnnotations());
        }
    }

    Descriptor describe(Class<?> cls, Annotation[] annotations) {
        return describe(cls, cls, annotations);
    }


    Descriptor describe(Object source, Type type, Annotation[] annotations) {
        Class cls = parametrizedType(source, type);
        return describe(cls, cls, annotations);
    }

    private Descriptor describe(Object source, Class<?> cls, Annotation[] annotations) {
        Annotation qualifier = getQualifier(cls, annotations);
        Descriptor result = new Descriptor(cls, qualifier);
        logger.debug("%s for %s of type %s", result, source, cls.getName());
        return result;
    }

    private Class parametrizedType(Object source, Type type) {
        Class result = null;

        if (type != null && ParameterizedType.class.isAssignableFrom(type.getClass())) {
            Type typeArgument = getFirst(source, type, ParameterizedType.class.cast(type).getActualTypeArguments());
            if (Class.class.isAssignableFrom(typeArgument.getClass())) {
                result = Class.class.cast(typeArgument);
            } else if (WildcardType.class.isAssignableFrom(typeArgument.getClass())) {
                // TODO what about lower bound or multiple bounds?
                Type upperBound = getFirst(source, type, WildcardType.class.cast(typeArgument).getUpperBounds());
                if (Class.class.isAssignableFrom(upperBound.getClass())) {
                    result = Class.class.cast(upperBound);
                }
            }
        }

        return Optional.ofNullable(result).orElseThrow(() -> new AmbiguousTargetException("Cannot determine generic parameters of " + type + " for " + source));
    }

    private <T> T getFirst(Object source, Type type, T[] elements) {
        if (elements != null && elements.length == 1) {
            return elements[0];
        } else {
            throw new AmbiguousTargetException("Cannot determine generic parameters of " + type + " for " + source);
        }
    }

    private Annotation getQualifier(Class<?> cls, Annotation[] annotations) {
        List<Annotation> qualifiers = Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(Qualifier.class))
                .collect(Collectors.toList());

        if (qualifiers.isEmpty()) {
            return DEFAULT_QUALIFIER;
        } else if (qualifiers.size() == 1) {
            return qualifiers.get(0);
        } else {
            logger.error("Qualifiers for %s: %s", cls.getName(), qualifiers);
            throw new AmbiguousTargetException("Too many qualifiers for " + cls.getName() + " - " + qualifiers);
        }
    }

    List<Field> getNullFields(Class cls, Object instance) throws IllegalAccessException {
        Class<?> currentCls = cls;
        List<Field> results = new ArrayList<>(16);
        while (currentCls != null) {
            for (Field field : currentCls.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    if (isStatic(field.getModifiers()) || (!isStatic(field.getModifiers()) && instance != null)) {
                        field.setAccessible(true);
                        if (field.get(instance) == null) {
                            results.add(field);
                            logger.debug("Field %s is null", field.toGenericString());
                        }
                    }
                }
            }
            currentCls = currentCls.getSuperclass();
        }
        return results;
    }

    Constructor<?> getConstructor(Descriptor descriptor) {
        if (isAbstract(descriptor.getCls())) {
            if (scanner.isAvailable()) {
                return getConstructorByScanning(descriptor);
            } else {
                throw new ReflectionsNotFoundException("Reflections required on classpath for creating instances by interface or for abstract classes of " + descriptor.getCls().getName() + " type");
            }
        } else {
            Constructor<?> constructor = getConstructor(descriptor.getCls());
            if (describe(constructor.getDeclaringClass()).getQualifier().equals(descriptor.getQualifier())) {
                return constructor;
            } else if (scanner.isAvailable()) {
                return getConstructorByScanning(descriptor);
            } else {
                throw new TargetNotFoundException("Cannot find matching type and scanning is not available for " + descriptor);
            }
        }
    }

    private Constructor<?> getConstructorByScanning(Descriptor descriptor) {
        List<Descriptor> descriptors = scanner.subTypes(descriptor.getCls()).stream()
                .filter(subType -> !isAbstract(subType))
                .map(this::describe)
                .filter(subDescriptor -> subDescriptor.getQualifier().equals(descriptor.getQualifier()))
                .collect(Collectors.toList());
        if (descriptors.isEmpty()) {
            throw new TargetNotFoundException("Cannot find matching sub type for " + descriptor);
        } else if (descriptors.size() == 1) {
            Descriptor targetDescriptor = descriptors.get(0);
            Constructor<?> constructor = getConstructor(targetDescriptor.getCls());
            logger.debug("Constructor %s for %s (source %s)", constructor, targetDescriptor, descriptor);
            return constructor;
        } else {
            logger.error("Matching sub types for %s: %s", descriptor, descriptors);
            throw new AmbiguousTargetException("Too many matching sub types for " + descriptor);
        }
    }

    private Constructor<?> getConstructor(Class<?> cls) {
        List<Constructor<?>> constructors = Arrays.stream(cls.getDeclaredConstructors())
                .filter(constructor -> Modifier.isPublic(constructor.getModifiers()))
                .collect(Collectors.toList());

        if (constructors.size() == 0) {
            throw new TargetNotFoundException("Cannot find public constructors for " + cls.getName());
        } else if (constructors.size() == 1) {
            return constructors.get(0);
        } else {
            logger.error("Constructors %s for %s", constructors, cls.getName());
            throw new AmbiguousTargetException("Too many public constructors for " + cls.getName());
        }
    }

    private boolean isAbstract(Class<?> cls) {
        return Modifier.isAbstract(cls.getModifiers()) || cls.isInterface();
    }
}
