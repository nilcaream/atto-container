package com.nilcaream.atto;

import com.nilcaream.atto.example.ExampleImplementationBlue;
import com.nilcaream.atto.example.ExampleImplementationGreen;
import com.nilcaream.atto.example.ExampleInterface;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScannerTest {

    private Scanner underTest = new Scanner("com.nilcaream.atto");

    @Test
    public void shouldFindTestSubTypes() {
        // when
        Set<Class<? extends ExampleInterface>> implementations = underTest.subTypes(ExampleInterface.class);

        // then
        assertNotNull(implementations);
        assertEquals(2, implementations.size());
        assertTrue(implementations.contains(ExampleImplementationBlue.class));
        assertTrue(implementations.contains(ExampleImplementationGreen.class));
    }

}