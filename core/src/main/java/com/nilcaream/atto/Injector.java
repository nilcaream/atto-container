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

    Injector(String scanPackage) {
        scanner = new Scanner(scanPackage);
        if (scanPackage != null && !scanner.isAvailable()) {
            throw new ReflectionsNotFoundException("Reflections required on classpath for scanning package " + scanPackage);
        }
    }

    boolean isSingleton(Class<?> cls) {
        return cls.isAnnotationPresent(Singleton.class);
    }

    Descriptor describe(Class<?> cls) {
        return describe(cls, getNamedAnnotations(cls), getQualifierAnnotations(cls));
    }

    Descriptor describe(Field field) {
        return describe(field.getType(), getNamedAnnotations(field), getQualifierAnnotations(field));
    }

    Descriptor describe(Class<?> cls, Annotation[] annotations) {
        return describe(cls, getNamedAnnotations(annotations), getQualifierAnnotations(annotations));
    }

    private Descriptor describe(Class<?> cls, List<String> names, List<String> qualifiers) {
        if (names.size() > 1) { // TODO this should be possible
            throw new AmbiguousTargetException("Too many Named annotations for " + cls.getName() + " - " + names);
        }

        if (qualifiers.size() > 1) {
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
            throw new AmbiguousTargetException("Both Named and Qualifier annotations are present on " + cls.getName() + " - " + names + " " + qualifiers);
        }
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
                    return getConstructor(descriptors.get(0).getCls());
                } else {
                    throw new AmbiguousTargetException("Too many matching sub types for " + descriptor.toString());
                }
            } else {
                throw new ReflectionsNotFoundException("Reflections required on classpath for creating instances by interface or for abstract classes for " + descriptor.getCls().getName());
            }
        } else {
            return getConstructor(descriptor.getCls());
        }
    }

    private boolean isAbstract(Class<?> cls) {
        return Modifier.isAbstract(cls.getModifiers()) || cls.isInterface();
    }
}
