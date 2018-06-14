package com.nilcaream.atto;

import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import org.junit.Test;

public class AttoDepTest {

    @Test(expected = ReflectionsNotFoundException.class)
    public void shouldBeUnavailableForMissingReflections() {
        // when
        Atto.builder().scanPackage("com.nilcaream.atto").build();
    }
}
