package com.nilcaream.atto;

import org.junit.jupiter.api.Test;

import static com.nilcaream.atto.Logger.Level.ALL;
import static com.nilcaream.atto.Logger.standardOutputLogger;
import static org.assertj.core.api.Assertions.assertThat;

class ScannerDepTest {

    @Test
    void shouldBeUnavailableForMissingReflections() {
        // when
        Scanner underTest = Scanner.builder().logger(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto").build();

        // then
        assertThat(underTest.isAvailable()).isFalse();
    }
}
