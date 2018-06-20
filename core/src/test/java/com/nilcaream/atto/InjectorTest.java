package com.nilcaream.atto;

import com.nilcaream.atto.example.MultipleNames;
import com.nilcaream.atto.example.NamedExample;
import com.nilcaream.atto.example.TooManyAnnotations;
import com.nilcaream.atto.example.case003.ExampleImplementationGreen;
import com.nilcaream.atto.example.case003.ExampleInterface;
import com.nilcaream.atto.example.case003.GreenQualifier;
import com.nilcaream.atto.example.case003.MultipleImplementations;
import com.nilcaream.atto.exception.AmbiguousTargetException;
import com.nilcaream.atto.exception.TargetNotFoundException;
import org.junit.Test;

import java.lang.reflect.Field;

import static com.nilcaream.atto.Logger.Level.ALL;
import static com.nilcaream.atto.Logger.standardOutputLogger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InjectorTest {

    private Injector underTest = Injector.builder().logger(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto").build();

    @Test(expected = AmbiguousTargetException.class)
    public void shouldErrorOutOnTooManyAnnotations() {
        // when
        underTest.describe(TooManyAnnotations.class);
    }

    @Test(expected = AmbiguousTargetException.class)
    public void shouldErrorOutTooManyMatchingConstructors() {
        // given
        Descriptor descriptor = new Descriptor(ExampleInterface.class, "Named:Clone");

        // when
        underTest.getConstructor(descriptor);
    }

    @Test(expected = TargetNotFoundException.class)
    public void shouldErrorOutNotFoundDescriptor() {
        // given
        Descriptor descriptor = new Descriptor(ExampleInterface.class, "Named:NotFoundAnywhere");

        // when
        underTest.getConstructor(descriptor);
    }

    @Test
    public void shouldDescribeNamedField() throws NoSuchFieldException {
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
    public void shouldDescribeQualifierField() throws NoSuchFieldException {
        // given
        Field field = MultipleImplementations.class.getDeclaredField("green1");

        // when
        Descriptor descriptor = underTest.describe(field);

        // then
        assertNotNull(descriptor);
        assertEquals(ExampleInterface.class, descriptor.getCls());
        assertEquals("Qualifier:" + GreenQualifier.class.getName(), descriptor.getQualifier());
    }

    @Test
    public void shouldDescribeNotAnnotatedClass() {
        // when
        Descriptor descriptor = underTest.describe(MultipleImplementations.class);

        // then
        assertNotNull(descriptor);
        assertEquals(MultipleImplementations.class, descriptor.getCls());
        assertEquals("", descriptor.getQualifier());
    }

    @Test
    public void shouldDescribeNamedClass() {
        // when
        Descriptor descriptor = underTest.describe(NamedExample.class);

        // then
        assertNotNull(descriptor);
        assertEquals(NamedExample.class, descriptor.getCls());
        assertEquals("Named:TestName", descriptor.getQualifier());
    }

    @Test
    public void shouldDescribeQualifiedClass() {
        // when
        Descriptor descriptor = underTest.describe(ExampleImplementationGreen.class);

        // then
        assertNotNull(descriptor);
        assertEquals(ExampleImplementationGreen.class, descriptor.getCls());
        assertEquals("Qualifier:com.nilcaream.atto.example.case003.GreenQualifier", descriptor.getQualifier());
    }

    public void should() throws NoSuchFieldException {
        // given
        Field field = MultipleNames.class.getDeclaredField("alreadyNamed");

        // when
        Descriptor descriptor = underTest.describe(field);

        // then
        assertNotNull(descriptor);
    }
}
