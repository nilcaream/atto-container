package com.nilcaream.atto;

import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import org.junit.Test;

import static com.nilcaream.atto.Logger.Level.ALL;
import static com.nilcaream.atto.Logger.standardOutputLogger;

public class AttoDepTest {

    @Test(expected = ReflectionsNotFoundException.class)
    public void shouldBeUnavailableForMissingReflections() {
        // when
        Atto.builder().loggerInstance(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto").build();
    }
}
