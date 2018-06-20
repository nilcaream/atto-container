package com.nilcaream.atto;

import com.nilcaream.atto.exception.AttoException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Atto {

    private LoggerWrapper logger = new LoggerWrapper();
    private int maxDepth = 32;
    private Map<Descriptor, Object> singletons = new HashMap<>();

    private Injector injector;

    public synchronized <T> T instance(Class<T> cls) {
        try {
            return cls.cast(instance(injector.describe(cls), new AtomicInteger(0)));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | IllegalArgumentException e) {
            logger.error(e.toString());
            throw new AttoException("Cannot create instance of " + cls.getName(), e);
        } catch (AttoException e) {
            logger.error(e.toString());
            throw e;
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
                logger.info("Created singleton " + targetDescriptor);
                singletons.put(targetDescriptor, instance);
                processFields(instance, depth);
            } else {
                logger.info("Returned singleton " + targetDescriptor);
            }
        } else {
            instance = instance(constructor, depth);
            logger.info("Created prototype " + targetDescriptor);
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
    }

    private static final class LoggerWrapper implements Logger {

        private Logger implementation = Logger.nullLogger();

        void setImplementation(Logger implementation) {
            this.implementation = implementation;
        }

        @Override
        public void debug(String message, Object... args) {
            implementation.debug(message, args);
        }

        @Override
        public void info(String message, Object... args) {
            implementation.info(message, args);
        }

        @Override
        public void warning(String message, Object... args) {
            implementation.warning(message, args);
        }

        @Override
        public void error(String message, Object... args) {
            implementation.error(message, args);
        }

        @Override
        public void accept(Level level, String message, Object... args) {
            implementation.accept(level, message, args);
        }

        @Override
        public void accept(Level level, String s) {
            implementation.accept(level, s);
        }
    }
}
