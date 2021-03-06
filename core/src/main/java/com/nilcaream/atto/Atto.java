package com.nilcaream.atto;

import com.nilcaream.atto.exception.AttoException;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class Atto {

    private int maxDepth = 32;
    private final LoggerWrapper logger = new LoggerWrapper();
    private final Map<Descriptor, Object> singletons = new HashMap<>();

    private final Injector injector;
    private final Descriptor attoDescriptor;

    public synchronized void inject(Class cls) {
        handleExceptions(cls, () -> {
            processFields(cls, null, new AtomicInteger(0));
            return null;
        });
    }

    public synchronized <T> T instance(Class<T> cls) {
        return instance(cls, injector.describe(cls));
    }

    private synchronized <T> T instance(Class<T> cls, Descriptor descriptor) {
        return handleExceptions(cls, () -> cls.cast(instance(descriptor, new AtomicInteger(0))));
    }

    private <T> T handleExceptions(Class source, Callable<T> callable) {
        try {
            return callable.call();
        } catch (AttoException e) {
            logger.error(e.toString());
            throw e;
        } catch (Exception e) {
            logger.error(e.toString());
            throw new AttoException("Cannot process " + source.getName(), e);
        }
    }

    private Object instance(Descriptor sourceDescriptor, AtomicInteger depth) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (depth.incrementAndGet() >= maxDepth) {
            throw new AttoException("Nested injection depth exceeded: " + depth.intValue());
        } else if (sourceDescriptor.equals(attoDescriptor)) {
            return this;
        }

        Object instance;
        Constructor<?> constructor = injector.getConstructor(sourceDescriptor);
        Descriptor targetDescriptor = new Descriptor(constructor.getDeclaringClass(), sourceDescriptor.getQualifier());

        if (injector.isSingleton(targetDescriptor.getCls())) {
            instance = singletons.get(targetDescriptor);
            if (instance == null) {
                instance = instance(constructor, depth);
                logger.info("Created singleton " + targetDescriptor);
                singletons.put(targetDescriptor, instance);
                processFields(targetDescriptor.getCls(), instance, depth);
            } else {
                logger.info("Returned singleton " + targetDescriptor);
            }
        } else {
            instance = instance(constructor, depth);
            logger.info("Created prototype " + targetDescriptor);
            processFields(targetDescriptor.getCls(), instance, depth);
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
                if (Provider.class.isAssignableFrom(cls)) {
                    Descriptor descriptor = injector.describe(constructor, constructor.getGenericParameterTypes()[i], annotations);
                    parameters[i] = (Provider) () -> instance(descriptor.getCls(), descriptor);
                } else {
                    parameters[i] = instance(injector.describe(cls, annotations), depth);
                }
            }
            return constructor.newInstance(parameters);
        }
    }

    private void processFields(Class cls, Object instance, AtomicInteger depth) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        for (Field field : injector.getNullFields(cls, instance)) {
            Descriptor descriptor = injector.describe(field);
            if (Provider.class.isAssignableFrom(field.getType())) {
                field.set(instance, (Provider) () -> instance(descriptor.getCls(), descriptor));
            } else {
                field.set(instance, instance(descriptor, depth));
            }
        }
    }

    @lombok.Builder(builderClassName = "Builder")
    private Atto(String scanPackage, int maxDepth, Logger loggerInstance, Class<? extends Logger> loggerClass) {
        if (loggerInstance != null) {
            logger.setImplementation(loggerInstance);
        }

        injector = Injector.builder().scanPackage(scanPackage).logger(logger).build();

        if (maxDepth > 0) {
            this.maxDepth = maxDepth;
        }

        if (loggerClass != null) {
            logger.setImplementation(instance(loggerClass));
        }

        attoDescriptor = injector.describe(Atto.class);
        singletons.put(attoDescriptor, this);
    }
}
