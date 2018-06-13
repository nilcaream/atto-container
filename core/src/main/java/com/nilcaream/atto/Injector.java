package com.nilcaream.atto;

import com.nilcaream.atto.exception.AmbiguousElementsException;
import com.nilcaream.atto.exception.AttoException;
import com.nilcaream.atto.exception.ReflectionsNotFoundException;

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
        return describe(cls, cls);
    }

    Descriptor describe(Field field) {
        return describe(field.getType(), field);
    }

    private Descriptor describe(Class<?> cls, AnnotatedElement annotatedElement) {
        List<String> names = Arrays.stream(annotatedElement.getAnnotationsByType(Named.class))
                .map(Named.class::cast)
                .map(named -> "Named:" + named.value())
                .collect(Collectors.toList());

        if (names.size() > 1) {
            throw new AmbiguousElementsException("Too many Named annotations for " + cls.getName());
        }

        List<String> qualifiers = Arrays.stream(annotatedElement.getAnnotations())
                .map(Annotation::annotationType)
                .filter(type -> !Named.class.equals(type))
                .filter(type -> type.isAnnotationPresent(Qualifier.class))
                .map(type -> "Qualifier:" + type.getName())
                .collect(Collectors.toList());

        if (qualifiers.size() > 1) {
            throw new AmbiguousElementsException("Too many Qualifier annotations for " + cls.getName());
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
            throw new AmbiguousElementsException("Both Named and Qualifier annotations are present on " + cls.getName());
        }
        return result;
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
            throw new AttoException("Cannot find public constructors for " + cls.getName());
        } else if (constructors.size() == 1) {
            return constructors.get(0);
        } else {
            throw new AmbiguousElementsException("Too many public constructors for " + cls.getName());
        }
    }

    Constructor<?> getConstructor(Descriptor descriptor) {
        if (isAbstract(descriptor.getCls())) {
            if (scanner.isAvailable()) {
                List<Descriptor> descriptors = scanner.subTypes(descriptor.getCls()).stream()
                        .filter(subType -> !isAbstract(subType))
                        .map(this::describe)
                        .filter(subDescriptor -> subDescriptor.getQualifier().equals(descriptor.getQualifier()))
                        .collect(Collectors.toList());
                if (descriptors.isEmpty()) {
                    throw new AttoException("Cannot find matching sub type for " + descriptor.toString());
                } else if (descriptors.size() == 1) {
                    return getConstructor(descriptors.get(0).getCls());
                } else {
                    throw new AmbiguousElementsException("Too many matching sub types for " + descriptor.toString());
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
