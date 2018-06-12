package com.nilcaream.atto;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Injector {

    private Scanner scanner = new Scanner();

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
            throw new AttoException("Too many Named annotations for " + cls.getName());
        }

        List<String> qualifiers = Arrays.stream(annotatedElement.getAnnotations())
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(Qualifier.class))
                .map(qualifier -> "Qualifier:" + qualifier.annotationType().getName())
                .collect(Collectors.toList());

        if (qualifiers.size() > 1) {
            throw new AttoException("Too many Qualifier annotations for " + cls.getName());
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
            throw new AttoException("Both Named and Qualifier annotations are present on " + cls.getName());
        }
        return result;
    }

    List<Field> getFields(Class<?> cls) {
        Class<?> currentCls = cls;
        List<Field> results = new ArrayList<>(20);
        while (currentCls != null) {
            for (Field field : cls.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    results.add(field);
                }
            }
            currentCls = cls.getSuperclass();
        }
        return results;
    }

    Map<Class, List<Field>> getNullFields(Object instance) throws IllegalAccessException {
        Class<?> currentCls = instance.getClass();
        Map<Class, List<Field>> results = new LinkedHashMap<>(20);
        while (currentCls != null) {
            List<Field> fields = new ArrayList<>();
            for (Field field : currentCls.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    if (field.get(instance) == null) {
                        fields.add(field);
                    }
                }
            }
            if (!fields.isEmpty()) {
                results.put(currentCls, fields);
            }
            currentCls = currentCls.getSuperclass();
        }
        return results;
    }

    Constructor<?> getConstructor(Class<?> cls) {
        List<Constructor<?>> constructors = Arrays.stream(cls.getDeclaredConstructors())
                .filter(constructor -> Modifier.isPublic(constructor.getModifiers()))
                .collect(Collectors.toList());

        if (constructors.size() == 0) {
            throw new AttoException("Cannot find public constructors for " + cls.getName());
        } else if (constructors.size() == 1) {
            return constructors.get(0);
        } else {
            throw new AttoException("Ambiguous public constructors for " + cls.getName());
        }
    }

    Constructor<?> getConstructor(Descriptor descriptor) {
        if (descriptor.getCls().isInterface()) {
            if (scanner.isAvailable()) {
                List<Descriptor> descriptors = scanner.subTypes(descriptor.getCls()).stream()
                        .map(subType -> describe(subType))
                        .filter(subTypeDescriptor -> subTypeDescriptor.getQualifier().equals(descriptor.getQualifier()))
                        .collect(Collectors.toList());
                if (descriptors.isEmpty()) {
                    throw new AttoException("Cannot find matching sub type for " + descriptor.toString());
                } else if (descriptors.size() == 1) {
                    return getConstructor(descriptors.get(0).getCls());
                } else {
                    throw new AttoException("Too many matching sub types for " + descriptor.toString());
                }
            } else {
                throw new AttoException("Cannot instantiate by interface with Reflections missing on classpath");
            }
        } else {
            return getConstructor(descriptor.getCls());
        }
    }
}
