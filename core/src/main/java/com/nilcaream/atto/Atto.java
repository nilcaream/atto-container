package com.nilcaream.atto;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Atto {

    private Injector injector = new Injector();
    private Map<Descriptor, Object> singletons = new ConcurrentHashMap<>();


    public <T> T instance(Class<T> cls) {
        try {
            return internalInstance(injector.describe(cls));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new AttoException("Cannot create instance of " + cls.getName(), e);
        }
    }

    private <T> T internalInstance(Descriptor descriptor) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object instance;
        if (injector.isSingleton(descriptor.getCls())) {
            instance = singletons.get(descriptor);
            if (instance == null) {
                instance = injector.getConstructor(descriptor).newInstance();
                singletons.put(descriptor, instance);
                processFields(instance);
            }
        } else {
            instance = injector.getConstructor(descriptor).newInstance();
            processFields(instance);
        }
        return (T) descriptor.getCls().cast(instance);
    }

    private void processFields(Object instance) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<Class, List<Field>> classToFields = injector.getNullFields(instance);
        for (Map.Entry<Class, List<Field>> entry : classToFields.entrySet()) {
            Class cls = entry.getKey(); // TODO rethink this approach
            List<Field> fields = entry.getValue();
            for (Field field : fields) {
                field.set(instance, internalInstance(injector.describe(field)));
            }
        }
    }
}
