package com.nilcaream.atto;

import org.junit.Test;

import static com.nilcaream.atto.Logger.Level.ALL;
import static com.nilcaream.atto.Logger.standardOutputLogger;
import static org.junit.Assert.assertFalse;

public class ScannerDepTest {

    @Test
    public void shouldBeUnavailableForMissingReflections() {
        // when
        Scanner underTest = Scanner.builder().logger(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto").build();

        // then
        assertFalse(underTest.isAvailable());
    }
}
