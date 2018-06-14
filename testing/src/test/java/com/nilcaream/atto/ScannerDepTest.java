package com.nilcaream.atto;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class ScannerDepTest {

    @Test
    public void shouldBeUnavailableForMissingReflections() {
        // when
        Scanner underTest = new Scanner("com.nilcaream.atto");

        // then
        assertFalse(underTest.isAvailable());
    }
}
