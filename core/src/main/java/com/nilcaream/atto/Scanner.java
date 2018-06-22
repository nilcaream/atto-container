package com.nilcaream.atto;

import com.google.common.annotations.VisibleForTesting;
import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import org.reflections.Reflections;

import java.util.Set;

import static com.nilcaream.atto.Logger.nullLogger;

class Scanner {

    @VisibleForTesting
    static String reflectionsCheckClass = "org.reflections.Reflections";

    private Reflections reflections;

    private boolean available;

    @lombok.Builder(builderClassName = "Builder")
    private Scanner(String scanPackage, Logger logger) {
        Logger log = logger == null ? nullLogger() : logger;

        if (scanPackage != null) {
            try {
                Class.forName(reflectionsCheckClass);
                reflections = new Reflections(scanPackage);
                available = true;
                log.debug("Classpath scanning enabled for package " + scanPackage);
            } catch (ClassNotFoundException ignore) {
                log.warning("Classpath scanning not available for package " + scanPackage);
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
