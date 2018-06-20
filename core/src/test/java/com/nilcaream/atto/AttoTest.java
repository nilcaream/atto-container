package com.nilcaream.atto;

import com.nilcaream.atto.example.case001.PrivateConstructor;
import com.nilcaream.atto.example.case001.TwoPrivateConstructors;
import com.nilcaream.atto.example.case003.ExampleImplementationBlue;
import com.nilcaream.atto.example.case003.ExampleImplementationGreen;
import com.nilcaream.atto.example.case003.MultipleImplementations;
import com.nilcaream.atto.example.case004.AnotherOrange;
import com.nilcaream.atto.example.case004.AnotherWhite;
import com.nilcaream.atto.example.case004.ConstructorInjection;
import com.nilcaream.atto.example.case004.ConstructorInjectionSimple;
import com.nilcaream.atto.example.case005.StaticFinalFieldExample;
import com.nilcaream.atto.example.case006.SingletonImplementation;
import com.nilcaream.atto.example.case007.CyclicDependencies1;
import com.nilcaream.atto.example.case007.CyclicPrototype;
import com.nilcaream.atto.example.case008.SomeImplementation;
import com.nilcaream.atto.example.case009.SameFieldNameSub;
import com.nilcaream.atto.example.case010.UnambiguousAbstractHolder;
import com.nilcaream.atto.example.case010.UnambiguousHolder;
import com.nilcaream.atto.example.case010.UnambiguousPurple;
import com.nilcaream.atto.example.case010.UnambiguousRed;
import com.nilcaream.atto.example.case011.ClassWithLogger;
import com.nilcaream.atto.example.case011.LoggerImplementation;
import com.nilcaream.atto.example.case012.StaticNestedClassImplementationHolder;
import com.nilcaream.atto.example.case012.StaticNestedClassInjectionExample;
import com.nilcaream.atto.example.case013.InnerClassImplementationHolder;
import com.nilcaream.atto.example.case013.InnerClassInjectionExample;
import com.nilcaream.atto.example.case014.MultipleNames;
import com.nilcaream.atto.example.case015.NamedClass;
import com.nilcaream.atto.example.case015.NamedClassHolder;
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

    @Case(1)
    @Test(expected = AmbiguousTargetException.class)
    public void shouldErrorOutOnMultiplePublicConstructors() {
        // when
        underTest.instance(TwoPrivateConstructors.class);
    }

    @Case(1)
    @Test(expected = TargetNotFoundException.class)
    public void shouldErrorOutOnPrivateConstructor() {
        // when
        underTest.instance(PrivateConstructor.class);
    }

    @Case(3)
    @Test(expected = ReflectionsNotFoundException.class)
    public void shouldErrorOutForInterfaceInjectionWithoutScanner() {
        // when
        underTest.instance(MultipleImplementations.class);
    }

    @Case(3)
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

    @Case(4)
    @Test
    public void shouldInjectByConstructorWithScanning() {
        // given
        underTest = Atto.builder().loggerInstance(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto.example").build();

        // when
        ConstructorInjection instance = underTest.instance(ConstructorInjection.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getWhite());
        assertNotNull(instance.getOrange());
        assertNotNull(instance.getAnotherPrototypeField());
        assertNotNull(instance.getAnotherPrototypeFinal());

        assertEquals(AnotherOrange.class, instance.getOrange().getClass());
        assertEquals(AnotherWhite.class, instance.getWhite().getClass());
        assertNotSame(instance.getAnotherPrototypeFinal(), instance.getAnotherPrototypeField());
    }

    @Case(4)
    @Test
    public void shouldInjectByConstructorWithoutScanning() {
        // when
        ConstructorInjectionSimple instance = underTest.instance(ConstructorInjectionSimple.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getWhite());
        assertNotNull(instance.getOrange());
        assertNotNull(instance.getAnotherPrototypeField());
        assertNotNull(instance.getAnotherPrototypeFinal());

        assertEquals(AnotherOrange.class, instance.getOrange().getClass());
        assertEquals(AnotherWhite.class, instance.getWhite().getClass());
        assertNotSame(instance.getAnotherPrototypeFinal(), instance.getAnotherPrototypeField());
    }

    @Case(5)
    @Test(expected = AttoException.class)
    public void shouldErrorOutStaticFinalFieldInjection() {
        // when
        underTest.instance(StaticFinalFieldExample.class);
    }

    @Case(6)
    @Test(expected = AttoException.class)
    public void shouldErrorOutOnTooShallowInjection() {
        // given
        underTest = Atto.builder().loggerInstance(standardOutputLogger(ALL)).maxDepth(1).build();

        // when
        underTest.instance(SingletonImplementation.class);
    }

    @Case(7)
    @Test(expected = AttoException.class)
    public void shouldErrorOutCyclicPrototype() {
        // when
        underTest.instance(CyclicPrototype.class);
    }

    @Case(7)
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

    @Case(8)
    @Test
    public void shouldInjectFieldsOfSuperClass() {
        // when
        SomeImplementation instance = underTest.instance(SomeImplementation.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getSomePrototypeSub());
        assertNotNull(instance.getSomeSingletonSub());
        assertNotNull(instance.getSomePrototypeSuper());

        assertNotSame(instance.getSomePrototypeSub(), instance.getSomePrototypeSuper());
    }

    @Case(9)
    @Test
    public void shouldInjectFieldsWithSameName() {
        // when
        SameFieldNameSub instance = underTest.instance(SameFieldNameSub.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getTheName());
        assertNotNull(instance.getTheNameSub());
        assertNotSame(instance.getTheNameSub(), instance.getTheName());
    }

    @Case(10)
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

    @Case(10)
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

    @Case(11)
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

    @Case(12)
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

    @Case(13)
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

    @Case(14)
    @Test(expected = TargetNotFoundException.class)
    public void shouldErrorOutOnNotMatchingQualifierWithoutScanning() {
        // when
        underTest.instance(MultipleNames.class);
    }

    @Case(15)
    @Test(expected = TargetNotFoundException.class)
    public void shouldErrorOutOnNonAbstractSuperClassWithNotMatchingQualifierInjection() {
        // when
        underTest.instance(NamedClassHolder.class);
    }

    @Case(15)
    @Test
    public void shouldInjectSubClassOfNonAbstractClassWithMatchingQualifier() {
        // given
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerInstance(standardOutputLogger(ALL)).build();

        // when
        NamedClassHolder instance = underTest.instance(NamedClassHolder.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getNamedSuperClass());
        assertEquals(NamedClass.class, instance.getNamedSuperClass().getClass());
    }
}
