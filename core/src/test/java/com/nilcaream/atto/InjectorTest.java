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
import org.junit.jupiter.api.Test;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.nilcaream.atto.Descriptor.DEFAULT_QUALIFIER;
import static com.nilcaream.atto.Logger.Level.ALL;
import static com.nilcaream.atto.Logger.standardOutputLogger;
import static com.nilcaream.atto.ScannerUtil.runWithReflectionsDisabled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class InjectorTest {

    private Injector underTest = Injector.builder().logger(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto").build();

    @Test
    void shouldErrorOutOnTooManyAnnotations() {
        // when
        Throwable throwable = catchThrowable(() -> underTest.describe(TooManyAnnotations.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(AmbiguousTargetException.class);
    }

    @Test
    void shouldErrorOutTooManyMatchingConstructors() {
        // given
        Annotation qualifier = ExampleImplementationClone1.class.getAnnotation(Named.class);
        Descriptor descriptor = new Descriptor(ExampleInterface.class, qualifier);

        // when
        Throwable throwable = catchThrowable(() -> underTest.getConstructor(descriptor));

        // then
        assertThat(throwable).isExactlyInstanceOf(AmbiguousTargetException.class);
    }

    @Test
    void shouldErrorOutNotFoundDescriptor() {
        // given
        Annotation qualifier = AnotherOrange.class.getAnnotation(OrangeQualifier.class);
        Descriptor descriptor = new Descriptor(ExampleInterface.class, qualifier);

        // when
        Throwable throwable = catchThrowable(() -> underTest.getConstructor(descriptor));

        // then
        assertThat(throwable).isExactlyInstanceOf(TargetNotFoundException.class);
    }

    @Test
    void shouldDescribeNamedField() throws NoSuchFieldException {
        // given
        Field field = MultipleImplementations.class.getDeclaredField("blue1");
        assertThat(field.getAnnotation(Named.class)).isNotNull();

        // when
        Descriptor descriptor = underTest.describe(field);

        // then
        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getCls()).isSameAs(ExampleInterface.class);
        assertThat(descriptor.getQualifier()).isSameAs(field.getAnnotation(Named.class));
    }

    @Test
    void shouldDescribeQualifierField() throws NoSuchFieldException {
        // given
        Field field = MultipleImplementations.class.getDeclaredField("green1");

        // when
        Descriptor descriptor = underTest.describe(field);

        // then
        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getCls()).isSameAs(ExampleInterface.class);
        assertThat(descriptor.getQualifier()).isSameAs(field.getAnnotation(GreenQualifier.class));
    }

    @Test
    void shouldDescribeNotAnnotatedClass() {
        // when
        Descriptor descriptor = underTest.describe(MultipleImplementations.class);

        // then
        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getCls()).isSameAs(MultipleImplementations.class);
        assertThat(descriptor.getQualifier()).isSameAs(DEFAULT_QUALIFIER);
    }

    @Test
    void shouldDescribeNamedClass() {
        // when
        Named qualifier = NamedExample.class.getAnnotation(Named.class);
        Descriptor descriptor = underTest.describe(NamedExample.class);

        // then
        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getCls()).isSameAs(NamedExample.class);
        assertThat(descriptor.getQualifier()).isSameAs(qualifier);
        assertThat(Named.class.cast(descriptor.getQualifier()).value()).isEqualTo("TestName");
    }

    @Test
    void shouldDescribeQualifiedClass() {
        // when
        GreenQualifier qualifier = ExampleImplementationGreen.class.getAnnotation(GreenQualifier.class);
        Descriptor descriptor = underTest.describe(ExampleImplementationGreen.class);

        // then
        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getCls()).isSameAs(ExampleImplementationGreen.class);
        assertThat(descriptor.getQualifier()).isSameAs(qualifier);
    }

    @Test
    void shouldRestrictNullForDescriptorClass() throws NoSuchFieldException {
        // given
        Named annotation = MultipleImplementations.class.getDeclaredField("blue1").getAnnotation(Named.class);
        assertThat(annotation).isNotNull();

        // when
        Throwable throwable = catchThrowable(() -> new Descriptor(null, annotation));

        // then
        assertThat(throwable).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRestrictNullForDescriptorQualifier() {
        // when
        Throwable throwable = catchThrowable(() -> new Descriptor(getClass(), null));

        // then
        assertThat(throwable).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldReturnClassForDefaultQualifierAnnotationType() {
        // then
        assertThat(DEFAULT_QUALIFIER.annotationType()).isSameAs(DEFAULT_QUALIFIER.getClass());
    }

    @Test
    void shouldBeUnavailableForMissingReflections() {
        runWithReflectionsDisabled(() -> {
            // when
            Throwable throwable = catchThrowable(() -> Injector.builder().logger(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto").build());

            // then
            assertThat(throwable).isExactlyInstanceOf(ReflectionsNotFoundException.class);
        });
    }
}

