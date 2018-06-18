package com.nilcaream.atto;

import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import org.reflections.Reflections;

import java.util.Set;

class Scanner {

    private Logger logger = Logger.nullLogger();

    private Reflections reflections;

    private boolean available;

    @lombok.Builder(builderClassName = "Builder")
    private Scanner(String scanPackage, Logger logger) {
        if (logger != null) {
            this.logger = logger;
        }
        if (scanPackage != null) {
            try {
                Class.forName("org.reflections.Reflections");
                reflections = new Reflections(scanPackage);
                available = true;
                this.logger.debug("Classpath scanning enabled for package " + scanPackage);
            } catch (ClassNotFoundException ignore) {
                this.logger.warning("Classpath scanning not available for package " + scanPackage);
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
