package com.nilcaream.atto;

import com.nilcaream.atto.example.case003.ExampleImplementationClone1;
import com.nilcaream.atto.example.case003.ExampleImplementationGreen;
import com.nilcaream.atto.example.case003.ExampleInterface;
import com.nilcaream.atto.example.case003.GreenQualifier;
import com.nilcaream.atto.example.case003.MultipleImplementations;
import com.nilcaream.atto.example.case004.AnotherOrange;
import com.nilcaream.atto.example.case004.OrangeQualifier;
import com.nilcaream.atto.example.case016.NamedExample;
import com.nilcaream.atto.example.case016.TooManyAnnotations;
import com.nilcaream.atto.exception.AmbiguousTargetException;
import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import com.nilcaream.atto.exception.TargetNotFoundException;
import org.junit.Test;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.nilcaream.atto.Descriptor.DEFAULT_QUALIFIER;
import static com.nilcaream.atto.Logger.Level.ALL;
import static com.nilcaream.atto.Logger.standardOutputLogger;
import static com.nilcaream.atto.ScannerUtil.runWithReflectionsDisabled;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

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
        Annotation qualifier = ExampleImplementationClone1.class.getAnnotation(Named.class);
        Descriptor descriptor = new Descriptor(ExampleInterface.class, qualifier);

        // when
        underTest.getConstructor(descriptor);
    }

    @Test(expected = TargetNotFoundException.class)
    public void shouldErrorOutNotFoundDescriptor() {
        // given
        Annotation qualifier = AnotherOrange.class.getAnnotation(OrangeQualifier.class);
        Descriptor descriptor = new Descriptor(ExampleInterface.class, qualifier);

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
        assertEquals(field.getAnnotation(Named.class), descriptor.getQualifier());
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
        assertEquals(field.getAnnotation(GreenQualifier.class), descriptor.getQualifier());
    }

    @Test
    public void shouldDescribeNotAnnotatedClass() {
        // when
        Descriptor descriptor = underTest.describe(MultipleImplementations.class);

        // then
        assertNotNull(descriptor);
        assertEquals(MultipleImplementations.class, descriptor.getCls());
        assertSame(DEFAULT_QUALIFIER, descriptor.getQualifier());
    }

    @Test
    public void shouldDescribeNamedClass() {
        // when
        Named qualifier = NamedExample.class.getAnnotation(Named.class);
        Descriptor descriptor = underTest.describe(NamedExample.class);

        // then
        assertNotNull(descriptor);
        assertEquals(NamedExample.class, descriptor.getCls());
        assertEquals(qualifier, descriptor.getQualifier());
        assertEquals("TestName", Named.class.cast(descriptor.getQualifier()).value());
    }

    @Test
    public void shouldDescribeQualifiedClass() {
        // when
        GreenQualifier qualifier = ExampleImplementationGreen.class.getAnnotation(GreenQualifier.class);
        Descriptor descriptor = underTest.describe(ExampleImplementationGreen.class);

        // then
        assertNotNull(descriptor);
        assertEquals(ExampleImplementationGreen.class, descriptor.getCls());
        assertSame(qualifier, descriptor.getQualifier());
    }

    @Test(expected = NullPointerException.class)
    public void shouldRestrictNullForDescriptor() {
        // when
        new Descriptor(getClass(), null);
    }

    @Test
    public void shouldReturnClassForDefaultQualifierAnnotationType() {
        // then
        assertSame(DEFAULT_QUALIFIER.getClass(), DEFAULT_QUALIFIER.annotationType());
    }

    @Test(expected = ReflectionsNotFoundException.class)
    public void shouldBeUnavailableForMissingReflections() {
        runWithReflectionsDisabled(() -> {
            // when
            Injector.builder().logger(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto").build();
        });
    }
}

