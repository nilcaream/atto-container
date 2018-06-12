package com.nilcaream.atto;

import com.nilcaream.atto.example.ExampleImplementationBlue;
import com.nilcaream.atto.example.ExampleImplementationGreen;
import com.nilcaream.atto.example.ExampleInterface;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScannerTest {

    private Scanner underTest = new Scanner();

    @Test
    void shouldFindTestSubTypes() {
        // when
        Set<Class<? extends ExampleInterface>> implementations = underTest.subTypes(ExampleInterface.class);

        // then
        assertNotNull(implementations);
        assertEquals(2, implementations.size());
        assertTrue(implementations.contains(ExampleImplementationBlue.class));
        assertTrue(implementations.contains(ExampleImplementationGreen.class));
    }

}