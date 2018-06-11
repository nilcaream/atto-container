package com.nilcaream.atto;

import org.reflections.Reflections;

import java.util.Set;

class Scanner {

    private Reflections reflections;

    {
        try {
            Class.forName("org.reflections.Reflections");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Reflections not found on the class path");
        }
        reflections = new Reflections();
    }

    <T> Set<Class<? extends T>> subTypes(Class<T> cls) {
        return reflections.getSubTypesOf(cls);
    }
}
