package com.nilcaream.atto;

import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import org.junit.Test;

public class InjectorDepTest {

    @Test(expected = ReflectionsNotFoundException.class)
    public void shouldBeUnavailableForMissingReflections() {
        // when
        new Injector("com.nilcaream.atto");
    }

    @Test(expected = NullPointerException.class)
    public void shouldRestrictNullForDescriptor() {
        // when
        new Descriptor(getClass(), null);
    }
}
