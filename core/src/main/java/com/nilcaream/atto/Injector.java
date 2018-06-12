package com.nilcaream.atto;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class Injector {

    boolean isSingleton(Class<?> cls) {
        return cls.isAnnotationPresent(Singleton.class);
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
}
