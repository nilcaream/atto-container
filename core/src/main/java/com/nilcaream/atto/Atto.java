package com.nilcaream.atto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Atto {

    private Scanner scanner;
    private final Map<Descriptor, Object> singletons = new ConcurrentHashMap<>();

    public <T> T instance(Class<T> cls) {
        return null;
    }

    @lombok.Builder(builderClassName = "Builder")
    private Atto(boolean classPathScanning) {
        if (classPathScanning) {
            scanner = new Scanner();
        }
    }
}
