package com.nilcaream.atto;

import com.nilcaream.atto.example.ExampleInterface;
import com.nilcaream.atto.example.GreenQualifier;
import com.nilcaream.atto.example.MultipleImplementations;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InjectorTest {

    private Injector underTest = new Injector();

    @Test
    void shouldDescribeNamedField() throws NoSuchFieldException {
        // given
        Field field = MultipleImplementations.class.getDeclaredField("blue1");

        // when
        Descriptor descriptor = underTest.describe(field);

        // then
        assertNotNull(descriptor);
        assertEquals(ExampleInterface.class, descriptor.getCls());
        assertEquals("Named:Blue", descriptor.getQualifier());
    }

    @Test
    void shouldDescribeQualifierField() throws NoSuchFieldException {
        // given
        Field field = MultipleImplementations.class.getDeclaredField("green1");

        // when
        Descriptor descriptor = underTest.describe(field);

        // then
        assertNotNull(descriptor);
        assertEquals(ExampleInterface.class, descriptor.getCls());
        assertEquals("Qualifier:" + GreenQualifier.class.getName(), descriptor.getQualifier());
    }
}