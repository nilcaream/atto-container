package com.nilcaream.atto;

import lombok.NonNull;
import lombok.Value;

import java.lang.annotation.Annotation;

@Value
class Descriptor {

    public static final Annotation DEFAULT_QUALIFIER = new Descriptor.Default();

    @NonNull
    private Class<?> cls;

    @NonNull
    private Annotation qualifier;

    private static final class Default implements Annotation {

        @Override
        public Class<? extends Annotation> annotationType() {
            return Default.class;
        }

        @Override
        public String toString() {
            return "@" + Descriptor.class.getName() + ".Default()";
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && getClass().equals(obj.getClass());
        }
    }
}
