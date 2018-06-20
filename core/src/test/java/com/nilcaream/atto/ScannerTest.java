package com.nilcaream.atto;

import com.nilcaream.atto.example.case003.ExampleImplementationBlue;
import com.nilcaream.atto.example.case003.ExampleImplementationClone1;
import com.nilcaream.atto.example.case003.ExampleImplementationClone2;
import com.nilcaream.atto.example.case003.ExampleImplementationGreen;
import com.nilcaream.atto.example.case003.ExampleInterface;
import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import org.junit.Test;

import java.util.Set;

import static com.nilcaream.atto.Logger.Level.ALL;
import static com.nilcaream.atto.Logger.standardOutputLogger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScannerTest {

    private Scanner underTest = Scanner.builder().logger(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto").build();

    @Test(expected = ReflectionsNotFoundException.class)
    public void shouldErrorOutOnMissingPackageOnInitialization() {
        // given
        underTest = Scanner.builder().logger(standardOutputLogger(ALL)).build();

        // when
        assertFalse(underTest.isAvailable());
        underTest.subTypes(ExampleInterface.class);
    }

    @Test
    public void shouldFindTestSubTypes() {
        // when
        Set<Class<? extends ExampleInterface>> implementations = underTest.subTypes(ExampleInterface.class);

        // then
        assertNotNull(implementations);
        assertEquals(4, implementations.size());
        assertTrue(implementations.contains(ExampleImplementationBlue.class));
        assertTrue(implementations.contains(ExampleImplementationGreen.class));
        assertTrue(implementations.contains(ExampleImplementationClone1.class));
        assertTrue(implementations.contains(ExampleImplementationClone2.class));
    }

}