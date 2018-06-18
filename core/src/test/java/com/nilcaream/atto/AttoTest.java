package com.nilcaream.atto;

import com.nilcaream.atto.example.AmbiguousConstructors;
import com.nilcaream.atto.example.ClassWithLogger;
import com.nilcaream.atto.example.ConstructorInjection;
import com.nilcaream.atto.example.ConstructorInjectionSimple;
import com.nilcaream.atto.example.CyclicDependencies1;
import com.nilcaream.atto.example.CyclicPrototype;
import com.nilcaream.atto.example.ExampleImplementationBlue;
import com.nilcaream.atto.example.ExampleImplementationGreen;
import com.nilcaream.atto.example.InnerClassImplementationHolder;
import com.nilcaream.atto.example.InnerClassInjectionExample;
import com.nilcaream.atto.example.LoggerImplementation;
import com.nilcaream.atto.example.MultipleImplementations;
import com.nilcaream.atto.example.PrivateConstructor;
import com.nilcaream.atto.example.SameFieldNameChild;
import com.nilcaream.atto.example.SingletonImplementation;
import com.nilcaream.atto.example.StaticFinalFieldExample;
import com.nilcaream.atto.example.StaticNestedClassImplementationHolder;
import com.nilcaream.atto.example.StaticNestedClassInjectionExample;
import com.nilcaream.atto.example.UnambiguousAbstractHolder;
import com.nilcaream.atto.example.UnambiguousHolder;
import com.nilcaream.atto.example.UnambiguousPurple;
import com.nilcaream.atto.example.UnambiguousRed;
import com.nilcaream.atto.exception.AmbiguousTargetException;
import com.nilcaream.atto.exception.AttoException;
import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import com.nilcaream.atto.exception.TargetNotFoundException;
import org.junit.Test;

import java.util.stream.Stream;

import static com.nilcaream.atto.Logger.Level.ALL;
import static com.nilcaream.atto.Logger.standardOutputLogger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class AttoTest {

    private Atto underTest = Atto.builder().loggerInstance(standardOutputLogger(ALL)).build();

    @Test(expected = AmbiguousTargetException.class)
    public void shouldErrorOutOnAmbiguousConstructors() {
        // when
        underTest.instance(AmbiguousConstructors.class);
    }

    @Test(expected = TargetNotFoundException.class)
    public void shouldErrorOutOnPrivateConstructor() {
        // when
        underTest.instance(PrivateConstructor.class);
    }

    @Test(expected = ReflectionsNotFoundException.class)
    public void shouldErrorOutForInterfacesWithoutScanner() {
        // when
        underTest.instance(MultipleImplementations.class);
    }

    @Test(expected = AttoException.class)
    public void shouldErrorOutCyclicPrototype() {
        // when
        underTest.instance(CyclicPrototype.class);
    }

    @Test(expected = AttoException.class)
    public void shouldErrorOutStaticFinalFieldInjection() {
        // when
        underTest.instance(StaticFinalFieldExample.class);
    }

    @Test(expected = AttoException.class)
    public void shouldErrorOutOnTooShallowInjection() {
        // given
        underTest = Atto.builder().loggerInstance(standardOutputLogger(ALL)).maxDepth(1).build();

        // when
        underTest.instance(SingletonImplementation.class);
    }

    @Test
    public void shouldInjectMultipleImplementations() {
        // given
        underTest = Atto.builder().loggerInstance(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto.example").build();

        // when
        MultipleImplementations instance1 = underTest.instance(MultipleImplementations.class);
        MultipleImplementations instance2 = underTest.instance(MultipleImplementations.class);

        // then
        Stream.of(instance1, instance2).forEach(instance -> {
            assertNotNull(instance);

            assertNotNull(instance.getBlue1());
            assertEquals(ExampleImplementationBlue.class, instance.getBlue1().getClass());
            assertNotNull(instance.getBlue2());
            assertEquals(ExampleImplementationBlue.class, instance.getBlue2().getClass());

            assertSame(instance.getBlue1(), instance.getBlue2());

            assertNotNull(instance.getGreen1());
            assertEquals(ExampleImplementationGreen.class, instance.getGreen1().getClass());
            assertNotNull(instance.getGreen2());
            assertEquals(ExampleImplementationGreen.class, instance.getGreen2().getClass());

            assertNotSame(instance.getGreen1(), instance.getGreen2());
        });

        assertNotSame(instance1, instance2);
        assertNotSame(instance1.getGreen1(), instance2.getGreen1());
        assertNotSame(instance1.getGreen2(), instance2.getGreen2());
        assertSame(instance1.getBlue1(), instance2.getBlue1());
        assertSame(instance1.getBlue1(), instance2.getBlue2());
        assertSame(instance1.getBlue2(), instance2.getBlue1());
    }

    @Test
    public void shouldSupportCyclicDependenciesFieldInjection1() {
        // when
        CyclicDependencies1 instance = underTest.instance(CyclicDependencies1.class);

        // then
        assertNotNull(instance);

        // instance
        assertNotNull(instance.getCyclicDependencies1());
        assertNotNull(instance.getCyclicDependencies2());
        assertNotNull(instance.getCyclicDependencies3());

        // instance - 1
        assertNotNull(instance.getCyclicDependencies1().getCyclicDependencies1());
        assertNotNull(instance.getCyclicDependencies1().getCyclicDependencies2());
        assertNotNull(instance.getCyclicDependencies1().getCyclicDependencies3());

        assertSame(instance, instance.getCyclicDependencies1());
        assertSame(instance.getCyclicDependencies1(), instance.getCyclicDependencies1().getCyclicDependencies1());

        // instance - 2
        assertNotNull(instance.getCyclicDependencies2().getCyclicDependencies1());
        assertNotNull(instance.getCyclicDependencies2().getCyclicDependencies3());
        assertSame(instance, instance.getCyclicDependencies2().getCyclicDependencies1());

        // instance - 3
        assertNotNull(instance.getCyclicDependencies3().getCyclicDependencies1());
        assertNotNull(instance.getCyclicDependencies3().getCyclicDependencies2());
        assertNotNull(instance.getCyclicDependencies3().getCyclicDependencies3());
        assertSame(instance.getCyclicDependencies1().getCyclicDependencies3(), instance.getCyclicDependencies3());
        assertSame(instance.getCyclicDependencies2().getCyclicDependencies3(), instance.getCyclicDependencies3());
        assertSame(instance.getCyclicDependencies3(), instance.getCyclicDependencies3().getCyclicDependencies3());
    }

    @Test
    public void shouldInjectFieldsOfSuperClass() {
        // when
        SingletonImplementation instance = underTest.instance(SingletonImplementation.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getRegularPrototype());
        assertNotNull(instance.getRegularPrototypeSub());
        assertNotNull(instance.getRegularSingletonSub());

        assertNotSame(instance.getRegularPrototype(), instance.getRegularPrototypeSub());
    }

    @Test
    public void shouldInjectFieldsWithSameName() {
        // when
        SameFieldNameChild instance = underTest.instance(SameFieldNameChild.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getTheNameFromChild());
        assertNotNull(instance.getTheNameFromParent());
    }

    @Test
    public void shouldInjectByConstructorWithScanning() {
        // given
        underTest = Atto.builder().loggerInstance(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto.example").build();

        // when
        ConstructorInjection instance = underTest.instance(ConstructorInjection.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getBlue());
        assertNotNull(instance.getGreen());
        assertNotNull(instance.getRegularPrototype());
        assertNotNull(instance.getRegularPrototypeField());

        assertEquals(ExampleImplementationBlue.class, instance.getBlue().getClass());
        assertEquals(ExampleImplementationGreen.class, instance.getGreen().getClass());
        assertNotSame(instance.getRegularPrototypeField(), instance.getRegularPrototype());
    }

    @Test
    public void shouldInjectByConstructorWithoutScanning() {
        // when
        ConstructorInjectionSimple instance = underTest.instance(ConstructorInjectionSimple.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getBlue());
        assertNotNull(instance.getGreen());
        assertNotNull(instance.getRegularPrototype());
        assertNotNull(instance.getRegularPrototypeField());

        assertEquals(ExampleImplementationBlue.class, instance.getBlue().getClass());
        assertEquals(ExampleImplementationGreen.class, instance.getGreen().getClass());
        assertNotSame(instance.getRegularPrototypeField(), instance.getRegularPrototype());
    }

    @Test
    public void shouldInjectUnambiguousWithoutQualifiers() {
        // when
        UnambiguousHolder instance = underTest.instance(UnambiguousHolder.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getUnambiguousRed());
        assertNotNull(instance.getUnambiguousPurple());

        assertEquals(UnambiguousRed.class, instance.getUnambiguousRed().getClass());
        assertEquals(UnambiguousPurple.class, instance.getUnambiguousPurple().getClass());
    }

    @Test
    public void shouldInjectUnambiguousWithoutQualifiersByInterface() {
        // given
        underTest = Atto.builder().loggerInstance(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto.example").build();

        // when
        UnambiguousAbstractHolder instance = underTest.instance(UnambiguousAbstractHolder.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getUnambiguousRed());
        assertNotNull(instance.getUnambiguousPurple());

        assertEquals(UnambiguousRed.class, instance.getUnambiguousRed().getClass());
        assertEquals(UnambiguousPurple.class, instance.getUnambiguousPurple().getClass());
    }

    @Test
    public void shouldCreateInstanceWithCustomLogger() {
        // given
        int count = LoggerImplementation.getCount();
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerClass(LoggerImplementation.class).build();

        // when
        ClassWithLogger instance = underTest.instance(ClassWithLogger.class);

        // then
        assertEquals(LoggerImplementation.class, instance.getLogger().getClass());
        assertEquals(count + 1, LoggerImplementation.getCount());
        assertTrue(LoggerImplementation.getLogs().contains(LoggerImplementation.class.getName()));
    }

    @Test
    public void shouldInjectStaticNestedClassInstance() {
        // given
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerInstance(standardOutputLogger(ALL)).build();

        // when
        StaticNestedClassInjectionExample instance = underTest.instance(StaticNestedClassInjectionExample.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getImplementation());
        assertEquals(StaticNestedClassImplementationHolder.StaticNestedClassImplementation.class, instance.getImplementation().getClass());
    }

    @Test
    public void shouldInjectInnerClassInstanceAndOuterClassField() {
        // given
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerInstance(standardOutputLogger(ALL)).build();

        // when
        InnerClassInjectionExample instance = underTest.instance(InnerClassInjectionExample.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getImplementation());
        assertEquals(InnerClassImplementationHolder.InnerClassImplementation.class, instance.getImplementation().getClass());
        assertNotNull(InnerClassImplementationHolder.InnerClassImplementation.class.cast(instance.getImplementation()).getFieldFromOuterClass());
    }
}