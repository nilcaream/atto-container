package com.nilcaream.atto;

import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import org.junit.Test;

import static com.nilcaream.atto.Logger.Level.ALL;
import static com.nilcaream.atto.Logger.standardOutputLogger;

public class InjectorDepTest {

    @Test(expected = ReflectionsNotFoundException.class)
    public void shouldBeUnavailableForMissingReflections() {
        // when
        Injector.builder().logger(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto").build();
    }

    @Test(expected = NullPointerException.class)
    public void shouldRestrictNullForDescriptor() {
        // when
        new Descriptor(getClass(), null);
    }
}
