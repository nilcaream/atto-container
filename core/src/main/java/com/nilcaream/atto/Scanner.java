package com.nilcaream.atto;

import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import org.reflections.Reflections;

import java.util.Set;

class Scanner {

    private Reflections reflections;

    private boolean available;

    Scanner(String scanPackage) {
        if (scanPackage != null) {
            try {
                Class.forName("org.reflections.Reflections");
                reflections = new Reflections(scanPackage);
                available = true;
            } catch (ClassNotFoundException ignore) {
                // ignore
            }
        }
    }

    <T> Set<Class<? extends T>> subTypes(Class<T> cls) {
        if (available) {
            return reflections.getSubTypesOf(cls);
        } else {
            throw new ReflectionsNotFoundException("Reflections required on classpath for getting sub types of " + cls.getName());
        }
    }

    boolean isAvailable() {
        return available;
    }
}
