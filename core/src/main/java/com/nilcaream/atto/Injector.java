package com.nilcaream.atto;

import com.nilcaream.atto.exception.AmbiguousTargetException;
import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import com.nilcaream.atto.exception.TargetNotFoundException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Injector {

    private Scanner scanner;

    private Logger logger = Logger.nullLogger();

    @lombok.Builder(builderClassName = "Builder")
    private Injector(String scanPackage, Logger logger) {
        if (logger != null) {
            this.logger = logger;
        }
        scanner = Scanner.builder().scanPackage(scanPackage).build();
        if (scanPackage != null && !scanner.isAvailable()) {
            throw new ReflectionsNotFoundException("Reflections required on classpath for scanning package " + scanPackage);
        }
    }

    boolean isSingleton(Class<?> cls) {
        return cls.isAnnotationPresent(Singleton.class);
    }

    Descriptor describe(Class<?> cls) {
        return describe(cls, cls, getNamedAnnotations(cls), getQualifierAnnotations(cls));
    }

    Descriptor describe(Field field) {
        return describe(field, field.getType(), getNamedAnnotations(field), getQualifierAnnotations(field));
    }

    Descriptor describe(Class<?> cls, Annotation[] annotations) {
        return describe(cls, cls, getNamedAnnotations(annotations), getQualifierAnnotations(annotations));
    }

    private Descriptor describe(Object source, Class<?> cls, List<String> names, List<String> qualifiers) {
        if (names.size() > 1) { // TODO this should be possible
            logger.error("Names for %s: %s", cls.getName(), names);
            throw new AmbiguousTargetException("Too many Named annotations for " + cls.getName() + " - " + names);
        }

        if (qualifiers.size() > 1) {
            logger.error("Qualifiers for %s: %s", cls.getName(), qualifiers);
            throw new AmbiguousTargetException("Too many Qualifier annotations for " + cls.getName() + " - " + qualifiers);
        }

        Descriptor result;
        if (names.isEmpty()) {
            if (qualifiers.isEmpty()) {
                result = new Descriptor(cls, "");
            } else {
                result = new Descriptor(cls, qualifiers.get(0));
            }
        } else if (qualifiers.isEmpty()) {
            result = new Descriptor(cls, names.get(0));
        } else {
            logger.error("Names and qualifiers for %s: %s %s", cls.getName(), names, qualifiers);
            throw new AmbiguousTargetException("Both Named and Qualifier annotations are present on " + cls.getName() + " - " + names + " " + qualifiers);
        }

        logger.debug("%s for %s of type %s. %s %s", result, source, cls.getName(), names, qualifiers);
        return result;
    }

    private List<String> getQualifierAnnotations(AnnotatedElement annotatedElement) {
        return getQualifierAnnotations(annotatedElement.getAnnotations());
    }

    private List<String> getQualifierAnnotations(Annotation[] annotations) {
        return Arrays.stream(annotations)
                .map(Annotation::annotationType)
                .filter(type -> !Named.class.equals(type))
                .filter(type -> type.isAnnotationPresent(Qualifier.class))
                .map(type -> "Qualifier:" + type.getName())
                .collect(Collectors.toList());
    }

    private List<String> getNamedAnnotations(AnnotatedElement annotatedElement) {
        return getNamedAnnotations(Arrays.stream(annotatedElement.getAnnotationsByType(Named.class)));
    }

    private List<String> getNamedAnnotations(Annotation[] annotations) {
        return getNamedAnnotations(Arrays.stream(annotations)
                .filter(annotation -> Named.class.equals(annotation.annotationType())));
    }

    private List<String> getNamedAnnotations(Stream<Annotation> annotations) {
        return annotations
                .map(Named.class::cast)
                .map(named -> "Named:" + named.value())
                .collect(Collectors.toList());
    }

    List<Field> getNullFields(Object instance) throws IllegalAccessException {
        Class<?> currentCls = instance.getClass();
        List<Field> results = new ArrayList<>(20);
        while (currentCls != null) {
            for (Field field : currentCls.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    if (field.get(instance) == null) {
                        results.add(field);
                        logger.debug("Field %s is null", field);
                    }
                }
            }
            currentCls = currentCls.getSuperclass();
        }
        return results;
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

    Constructor<?> getConstructor(Descriptor descriptor) {
        if (isAbstract(descriptor.getCls())) {
            if (scanner.isAvailable()) {
                List<Descriptor> descriptors = scanner.subTypes(descriptor.getCls()).stream()
                        .filter(subType -> !isAbstract(subType))
                        .map(this::describe)
                        .filter(subDescriptor -> descriptor.getQualifier().isEmpty() || subDescriptor.getQualifier().equals(descriptor.getQualifier()))
                        .collect(Collectors.toList());
                if (descriptors.isEmpty()) {
                    throw new TargetNotFoundException("Cannot find matching sub type for " + descriptor.toString());
                } else if (descriptors.size() == 1) {
                    Descriptor targetDescriptor = descriptors.get(0);
                    Constructor<?> constructor = getConstructor(targetDescriptor.getCls());
                    logger.debug("Constructor %s for %s (source %s)", constructor, targetDescriptor, descriptor);
                    return constructor;
                } else {
                    logger.error("Matching types for %s: %s", descriptor, descriptors);
                    throw new AmbiguousTargetException("Too many matching sub types for " + descriptor.toString());
                }
            } else {
                throw new ReflectionsNotFoundException("Reflections required on classpath for creating instances by interface or for abstract classes of " + descriptor.getCls().getName() + " type");
            }
        } else {
            Constructor<?> constructor = getConstructor(descriptor.getCls());
            logger.debug("Constructor %s for %s", constructor, descriptor);
            return constructor;
        }
    }

    private boolean isAbstract(Class<?> cls) {
        return Modifier.isAbstract(cls.getModifiers()) || cls.isInterface();
    }
}
