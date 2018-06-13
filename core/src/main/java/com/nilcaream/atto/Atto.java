package com.nilcaream.atto;

import com.nilcaream.atto.exception.AttoException;

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
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
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
                instance = constructor.newInstance();
                singletons.put(targetDescriptor, instance);
                processFields(instance, depth);
            }
        } else {
            instance = constructor.newInstance();
            processFields(instance, depth);
        }
        return instance;
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
