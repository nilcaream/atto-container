package com.nilcaream.atto;

import com.nilcaream.atto.exception.AttoException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Atto {

    private int maxDepth = 32;
    private Injector injector;
    private Map<Descriptor, Object> singletons = new ConcurrentHashMap<>();


    public <T> T instance(Class<T> cls) {
        try {
            return cls.cast(instance(injector.describe(cls), new AtomicInteger(0)));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | IllegalArgumentException e) {
            throw new AttoException("Cannot create instance of " + cls.getName(), e);
        }
    }

    private Object instance(Descriptor sourceDescriptor, AtomicInteger depth) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (depth.incrementAndGet() >= maxDepth) {
            throw new AttoException("Nested injection depth exceeded: " + depth.intValue());
        }

        Object instance;
        Constructor<?> constructor = injector.getConstructor(sourceDescriptor);
        Descriptor targetDescriptor = new Descriptor(constructor.getDeclaringClass(), sourceDescriptor.getQualifier());

        if (injector.isSingleton(targetDescriptor.getCls())) {
            instance = singletons.get(targetDescriptor);
            if (instance == null) {
                instance = instance(constructor, depth);
                singletons.put(targetDescriptor, instance);
                processFields(instance, depth);
            }
        } else {
            instance = instance(constructor, depth);
            processFields(instance, depth);
        }
        return instance;
    }

    private Object instance(Constructor constructor, AtomicInteger depth) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (constructor.getParameterCount() == 0) {
            return constructor.newInstance();
        } else {
            Object[] parameters = new Object[constructor.getParameterCount()];
            Class[] parameterTypes = constructor.getParameterTypes();
            for (int i = 0, length = parameterTypes.length; i < length; i++) {
                Class cls = parameterTypes[i];
                Annotation[] annotations = constructor.getParameterAnnotations()[i];
                parameters[i] = instance(injector.describe(cls, annotations), depth);
            }
            return constructor.newInstance(parameters);
        }
    }

    private void processFields(Object instance, AtomicInteger depth) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        for (Field field : injector.getNullFields(instance)) {
            field.set(instance, instance(injector.describe(field), depth));
        }
    }

    @lombok.Builder(builderClassName = "Builder")
    private Atto(String scanPackage, int maxDepth) {
        injector = new Injector(scanPackage);
        if (maxDepth > 0) {
            this.maxDepth = maxDepth;
        }
    }
}
