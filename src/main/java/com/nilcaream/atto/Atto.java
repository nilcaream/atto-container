package com.nilcaream.atto;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Atto {

    private Scanner scanner;
    private final Map<Descriptor, Object> singletons = new ConcurrentHashMap<>();

    private Atto() {
    }

    public <T> T instance(Class<T> cls) {
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    @Accessors(fluent = true, chain = true)
    public static final class Builder {

        private boolean classPathScanning;

        public Atto build() {
            Atto atto = new Atto();
            if (classPathScanning) {
                atto.scanner = new Scanner();
            }
            return atto;
        }
    }
}
