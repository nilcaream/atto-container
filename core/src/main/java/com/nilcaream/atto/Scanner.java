package com.nilcaream.atto;

import org.reflections.Reflections;

import java.util.Set;

class Scanner {

    private Reflections reflections;

    private boolean available = true;

    {
        try {
            Class.forName("org.reflections.Reflections");
            reflections = new Reflections();
        } catch (ClassNotFoundException e) {
            available = false;
        }
    }

    <T> Set<Class<? extends T>> subTypes(Class<T> cls) {
        return reflections.getSubTypesOf(cls);
    }

    public boolean isAvailable() {
        return available;
    }
}
