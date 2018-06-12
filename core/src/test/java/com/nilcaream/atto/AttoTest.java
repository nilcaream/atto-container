package com.nilcaream.atto;

import com.nilcaream.atto.example.AmbiguousConstructors;
import com.nilcaream.atto.example.CyclicDependencies1;
import com.nilcaream.atto.example.ExampleImplementationBlue;
import com.nilcaream.atto.example.ExampleImplementationGreen;
import com.nilcaream.atto.example.MultipleImplementations;
import com.nilcaream.atto.example.PrivateConstructor;
import com.nilcaream.atto.example.SingletonImplementation;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AttoTest {

    private Atto underTest = Atto.builder().build();

    @Test
    void shouldErrorOutOnAmbiguousConstructors() {
        assertThrows(AttoException.class, () -> underTest.instance(AmbiguousConstructors.class));
    }

    @Test
    void shouldErrorOutOnPrivateConstructor() {
        assertThrows(AttoException.class, () -> underTest.instance(PrivateConstructor.class));
    }

    @Test
    void shouldErrorOutForInterfacesWithoutScanner() {
        assertThrows(AttoException.class, () -> underTest.instance(MultipleImplementations.class));
    }

    @Test
    void shouldInjectMultipleImplementations() {
        // given
        underTest = Atto.builder().classPathScanning(true).build();

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
    }

    @Test
    void shouldSupportCyclicDependenciesFieldInjection1() {
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

        assertNotSame(instance, instance.getCyclicDependencies1());
        assertNotSame(instance.getCyclicDependencies1(), instance.getCyclicDependencies1().getCyclicDependencies1());

        // instance - 2
        assertNotNull(instance.getCyclicDependencies2().getCyclicDependencies1());
        assertNotNull(instance.getCyclicDependencies2().getCyclicDependencies3());
        assertNotSame(instance, instance.getCyclicDependencies2().getCyclicDependencies1());

        // instance - 3
        assertNotNull(instance.getCyclicDependencies3().getCyclicDependencies1());
        assertNotNull(instance.getCyclicDependencies3().getCyclicDependencies2());
        assertNotNull(instance.getCyclicDependencies3().getCyclicDependencies3());
        assertSame(instance.getCyclicDependencies1().getCyclicDependencies3(), instance.getCyclicDependencies3());
        assertSame(instance.getCyclicDependencies2().getCyclicDependencies3(), instance.getCyclicDependencies3());
        assertSame(instance.getCyclicDependencies3(), instance.getCyclicDependencies3().getCyclicDependencies3());
    }

    @Test
    void shouldInjectFieldsOfAbstractClass() {
        // when
        SingletonImplementation instance = underTest.instance(SingletonImplementation.class);

        // then
        assertNotNull(instance);
        assertNotNull(instance.getRegularPrototype());
        assertNotNull(instance.getRegularPrototypeSub());
        assertNotNull(instance.getRegularSingletonSub());

        assertNotSame(instance.getRegularPrototype(), instance.getRegularPrototypeSub());
    }
}