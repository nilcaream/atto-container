package com.nilcaream.atto;

import com.nilcaream.atto.example.case001.PrivateConstructor;
import com.nilcaream.atto.example.case001.TwoPrivateConstructors;
import com.nilcaream.atto.example.case002.AttoHolder;
import com.nilcaream.atto.example.case003.ExampleImplementationBlue;
import com.nilcaream.atto.example.case003.ExampleImplementationGreen;
import com.nilcaream.atto.example.case003.MultipleImplementations;
import com.nilcaream.atto.example.case004.AnotherOrange;
import com.nilcaream.atto.example.case004.AnotherWhite;
import com.nilcaream.atto.example.case004.ConstructorInjection;
import com.nilcaream.atto.example.case004.ConstructorInjectionSimple;
import com.nilcaream.atto.example.case005.StaticFieldExample;
import com.nilcaream.atto.example.case005.StaticFieldPrototype;
import com.nilcaream.atto.example.case005.StaticFinalFieldExample;
import com.nilcaream.atto.example.case006.SingletonImplementation;
import com.nilcaream.atto.example.case007.CyclicDependencies1;
import com.nilcaream.atto.example.case007.CyclicPrototype;
import com.nilcaream.atto.example.case008.SomeImplementation;
import com.nilcaream.atto.example.case009.SameFieldNameSub;
import com.nilcaream.atto.example.case010.UnambiguousAbstractHolder;
import com.nilcaream.atto.example.case010.UnambiguousHolder;
import com.nilcaream.atto.example.case011.ClassWithLogger;
import com.nilcaream.atto.example.case011.LoggerImplementation;
import com.nilcaream.atto.example.case012.StaticNestedClassImplementationHolder;
import com.nilcaream.atto.example.case012.StaticNestedClassInjectionExample;
import com.nilcaream.atto.example.case013.InnerClassImplementationHolder;
import com.nilcaream.atto.example.case013.InnerClassInjectionExample;
import com.nilcaream.atto.example.case014.MultipleNames;
import com.nilcaream.atto.example.case015.NamedClass;
import com.nilcaream.atto.example.case015.NamedClassHolder;
import com.nilcaream.atto.example.case017.PinkRequester;
import com.nilcaream.atto.example.case017.PinkSubClass;
import com.nilcaream.atto.example.case018.DifferentNamedValuesHolder;
import com.nilcaream.atto.example.case018.ImplementationA;
import com.nilcaream.atto.example.case018.ImplementationB;
import com.nilcaream.atto.example.case018.NamedValuesMismatchHolder;
import com.nilcaream.atto.example.case019.NoTypeProviderConstructorHolder;
import com.nilcaream.atto.example.case019.NoTypeProviderFieldHolder;
import com.nilcaream.atto.example.case019.ProvidedSingleton;
import com.nilcaream.atto.example.case019.ProviderConstructorHolder;
import com.nilcaream.atto.example.case019.ProviderFieldHolder;
import com.nilcaream.atto.example.case019.SpecificProvidedSingleton;
import com.nilcaream.atto.example.case019.SpecificUpperBoundWildcardProviderConstructorHolder;
import com.nilcaream.atto.example.case019.SpecificUpperBoundWildcardProviderFieldHolder;
import com.nilcaream.atto.example.case019.UpperBoundWildcardProviderConstructorHolder;
import com.nilcaream.atto.example.case019.UpperBoundWildcardProviderFieldHolder;
import com.nilcaream.atto.example.case019.WildcardProviderConstructorHolder;
import com.nilcaream.atto.example.case019.WildcardProviderFieldHolder;
import com.nilcaream.atto.exception.AmbiguousTargetException;
import com.nilcaream.atto.exception.AttoException;
import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import com.nilcaream.atto.exception.TargetNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static com.nilcaream.atto.Logger.Level.ALL;
import static com.nilcaream.atto.Logger.javaUtilLogger;
import static com.nilcaream.atto.Logger.standardOutputLogger;
import static com.nilcaream.atto.ScannerUtil.runWithReflectionsDisabled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class AttoTest {

    private Atto underTest = Atto.builder().loggerInstance(standardOutputLogger(ALL)).build();

    @Case(1)
    @Test
    void shouldErrorOutOnMultiplePublicConstructors() {
        // when
        Throwable throwable = catchThrowable(() -> {
            underTest.instance(TwoPrivateConstructors.class);
        });

        // then
        assertThat(throwable).isExactlyInstanceOf(AmbiguousTargetException.class);
    }

    @Case(1)
    @Test
    void shouldErrorOutOnPrivateConstructor() {
        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(PrivateConstructor.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(TargetNotFoundException.class);
    }

    @Case(2)
    @Test
    void shouldInjectAttoSingleton() {
        // when
        AttoHolder instance = underTest.instance(AttoHolder.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getAtto()).isNotNull();
        assertThat(underTest).isSameAs(instance.getAtto());
    }

    @Case(3)
    @Test
    void shouldErrorOutForInterfaceInjectionWithoutScanner() {
        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(MultipleImplementations.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(ReflectionsNotFoundException.class);
    }

    @Case(3)
    @Test
    void shouldInjectMultipleImplementations() {
        // given
        underTest = Atto.builder().loggerInstance(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto.example").build();

        // when
        MultipleImplementations instance1 = underTest.instance(MultipleImplementations.class);
        MultipleImplementations instance2 = underTest.instance(MultipleImplementations.class);

        // then
        Stream.of(instance1, instance2).forEach(instance -> {
            assertThat(instance).isNotNull();

            assertThat(instance.getBlue1())
                    .isNotNull()
                    .isExactlyInstanceOf(ExampleImplementationBlue.class);
            assertThat(instance.getBlue2())
                    .isNotNull()
                    .isExactlyInstanceOf(ExampleImplementationBlue.class)
                    .isSameAs(instance.getBlue1());

            assertThat(instance.getGreen1())
                    .isNotNull()
                    .isExactlyInstanceOf(ExampleImplementationGreen.class);
            assertThat(instance.getGreen2())
                    .isNotNull()
                    .isExactlyInstanceOf(ExampleImplementationGreen.class)
                    .isNotSameAs(instance.getGreen1());
        });

        assertThat(instance1).isNotSameAs(instance2);
        assertThat(instance1.getGreen1()).isNotSameAs(instance2.getGreen1());
        assertThat(instance1.getGreen2()).isNotSameAs(instance2.getGreen2());

        assertThat(instance1.getBlue1())
                .isSameAs(instance2.getBlue1())
                .isSameAs(instance2.getBlue2())
                .isSameAs(instance1.getBlue2());
    }

    @Case(4)
    @Test
    void shouldInjectByConstructorWithScanning() {
        // given
        underTest = Atto.builder().loggerInstance(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto.example").build();

        // when
        ConstructorInjection instance = underTest.instance(ConstructorInjection.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getWhite())
                .isNotNull()
                .isExactlyInstanceOf(AnotherWhite.class);
        assertThat(instance.getOrange())
                .isNotNull()
                .isExactlyInstanceOf(AnotherOrange.class);
        assertThat(instance.getAnotherPrototypeField()).isNotNull();
        assertThat(instance.getAnotherPrototypeFinal())
                .isNotNull()
                .isNotSameAs(instance.getAnotherPrototypeField());
    }

    @Case(4)
    @Test
    void shouldInjectByConstructorWithoutScanning() {
        // when
        ConstructorInjectionSimple instance = underTest.instance(ConstructorInjectionSimple.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getWhite())
                .isNotNull()
                .isExactlyInstanceOf(AnotherWhite.class);
        assertThat(instance.getOrange())
                .isNotNull()
                .isExactlyInstanceOf(AnotherOrange.class);
        assertThat(instance.getAnotherPrototypeField()).isNotNull();
        assertThat(instance.getAnotherPrototypeFinal())
                .isNotNull()
                .isNotSameAs(instance.getAnotherPrototypeField());
    }

    @Case(5)
    @Test
    void shouldErrorOutStaticFinalFieldInjection() {
        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(StaticFinalFieldExample.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(AttoException.class);
    }

    @Case(5)
    @Test
    void shouldInjectStaticAndInstanceFields() {
        // when
        StaticFieldExample.setStaticPrototype(null);
        StaticFieldExample instance = underTest.instance(StaticFieldExample.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getPrototype()).isNotNull();
        assertThat(StaticFieldExample.getStaticPrototype())
                .isNotNull()
                .isExactlyInstanceOf(StaticFieldPrototype.class)
                .isNotSameAs(instance.getPrototype());
    }

    @Case(5)
    @Test
    void shouldInjectStaticFieldForClass() {
        // when
        StaticFieldExample.setStaticPrototype(null);
        underTest.inject(StaticFieldExample.class);

        // then
        assertThat(StaticFieldExample.getStaticPrototype())
                .isNotNull()
                .isExactlyInstanceOf(StaticFieldPrototype.class);
    }

    @Case(6)
    @Test
    void shouldErrorOutOnTooShallowInjection() {
        // given
        underTest = Atto.builder().loggerInstance(standardOutputLogger(ALL)).maxDepth(1).build();

        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(SingletonImplementation.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(AttoException.class);
    }

    @Case(7)
    @Test
    void shouldErrorOutCyclicPrototype() {
        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(CyclicPrototype.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(AttoException.class);
    }

    @Case(7)
    @Test
    void shouldSupportCyclicDependenciesFieldInjection1() {
        // when
        CyclicDependencies1 instance = underTest.instance(CyclicDependencies1.class);

        // then
        assertThat(instance).isNotNull();

        // instance
        assertThat(instance.getCyclicDependencies1()).isNotNull();
        assertThat(instance.getCyclicDependencies2()).isNotNull();
        assertThat(instance.getCyclicDependencies3()).isNotNull();

        // instance - 1
        assertThat(instance.getCyclicDependencies1().getCyclicDependencies1()).isNotNull();
        assertThat(instance.getCyclicDependencies1().getCyclicDependencies2()).isNotNull();
        assertThat(instance.getCyclicDependencies1().getCyclicDependencies3()).isNotNull();

        assertThat(instance).isSameAs(instance.getCyclicDependencies1());
        assertThat(instance.getCyclicDependencies1()).isSameAs(instance.getCyclicDependencies1().getCyclicDependencies1());

        // instance - 2
        assertThat(instance.getCyclicDependencies2().getCyclicDependencies1()).isNotNull();
        assertThat(instance.getCyclicDependencies2().getCyclicDependencies3()).isNotNull();
        assertThat(instance).isSameAs(instance.getCyclicDependencies2().getCyclicDependencies1());

        // instance - 3
        assertThat(instance.getCyclicDependencies3().getCyclicDependencies1()).isNotNull();
        assertThat(instance.getCyclicDependencies3().getCyclicDependencies2()).isNotNull();
        assertThat(instance.getCyclicDependencies3().getCyclicDependencies3()).isNotNull();
        assertThat(instance.getCyclicDependencies1().getCyclicDependencies3())
                .isSameAs(instance.getCyclicDependencies3())
                .isSameAs(instance.getCyclicDependencies2().getCyclicDependencies3())
                .isSameAs(instance.getCyclicDependencies3().getCyclicDependencies3());
    }

    @Case(8)
    @Test
    void shouldInjectFieldsOfSuperClass() {
        // when
        SomeImplementation instance = underTest.instance(SomeImplementation.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getSomePrototypeSub()).isNotNull();
        assertThat(instance.getSomeSingletonSub()).isNotNull();
        assertThat(instance.getSomePrototypeSuper()).isNotNull();

        assertThat(instance.getSomePrototypeSub()).isNotSameAs(instance.getSomePrototypeSuper());
    }

    @Case(9)
    @Test
    void shouldInjectFieldsWithSameName() {
        // when
        SameFieldNameSub instance = underTest.instance(SameFieldNameSub.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getTheName()).isNotNull();
        assertThat(instance.getTheNameSub()).isNotNull();
        assertThat(instance.getTheNameSub()).isNotSameAs(instance.getTheName());
    }

    @Case(10)
    @Test
    void shouldErrorOutOnInjectingQualifiedBeanToDefaultField() {
        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(UnambiguousHolder.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(TargetNotFoundException.class);
    }

    @Case(10)
    @Test
    void shouldErrorOutOnInjectingQualifiedBeanToDefaultFieldByInterface() {
        // given
        underTest = Atto.builder().loggerInstance(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto.example").build();

        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(UnambiguousAbstractHolder.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(TargetNotFoundException.class);
    }

    @Case(11)
    @Test
    void shouldCreateInstanceWithCustomLogger() {
        // given
        int count = LoggerImplementation.getCount();
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerClass(LoggerImplementation.class).build();

        // when
        ClassWithLogger instance = underTest.instance(ClassWithLogger.class);

        // then
        assertThat(instance.getLogger()).isExactlyInstanceOf(LoggerImplementation.class);
        assertThat(LoggerImplementation.getCount()).isEqualTo(count + 1);
        assertThat(LoggerImplementation.getLogs()).contains(LoggerImplementation.class.getName());
    }

    @Case(11)
    @Test
    void shouldCreateInstanceWithJulLogger() {
        // given
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerInstance(javaUtilLogger()).build();

        // when
        ClassWithLogger instance = underTest.instance(ClassWithLogger.class);

        // then
        assertThat(instance.getLogger()).isExactlyInstanceOf(LoggerImplementation.class);
    }

    @Case(12)
    @Test
    void shouldInjectStaticNestedClassInstance() {
        // given
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerInstance(standardOutputLogger(ALL)).build();

        // when
        StaticNestedClassInjectionExample instance = underTest.instance(StaticNestedClassInjectionExample.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getImplementation())
                .isNotNull()
                .isExactlyInstanceOf(StaticNestedClassImplementationHolder.StaticNestedClassImplementation.class);
    }

    @Case(13)
    @Test
    void shouldInjectInnerClassInstanceAndOuterClassField() {
        // given
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerInstance(standardOutputLogger(ALL)).build();

        // when
        InnerClassInjectionExample instance = underTest.instance(InnerClassInjectionExample.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getImplementation())
                .isNotNull()
                .isExactlyInstanceOf(InnerClassImplementationHolder.InnerClassImplementation.class);
        assertThat(InnerClassImplementationHolder.InnerClassImplementation.class.cast(instance.getImplementation()).getFieldFromOuterClass()).isNotNull();

    }

    @Case(14)
    @Test
    void shouldErrorOutOnNotMatchingQualifierWithoutScanning() {
        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(MultipleNames.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(TargetNotFoundException.class);
    }

    @Case(15)
    @Test
    void shouldErrorOutOnNonAbstractSuperClassWithNotMatchingQualifierInjection() {
        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(NamedClassHolder.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(TargetNotFoundException.class);
    }

    @Case(15)
    @Test
    void shouldInjectSubClassOfNonAbstractClassWithMatchingQualifier() {
        // given
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerInstance(standardOutputLogger(ALL)).build();

        // when
        NamedClassHolder instance = underTest.instance(NamedClassHolder.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getNamedSuperClass())
                .isNotNull()
                .isExactlyInstanceOf(NamedClass.class);
    }

    @Case(17)
    @Test
    void shouldInjectNotMatchingNonAbstractClass() {
        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(PinkRequester.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(TargetNotFoundException.class);

    }

    @Case(17)
    @Test
    void shouldInjectMatchingNonAbstractClassByScanning() {
        // given
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerInstance(standardOutputLogger(ALL)).build();

        // when
        PinkRequester instance = underTest.instance(PinkRequester.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getPinkWannabe())
                .isNotNull()
                .isExactlyInstanceOf(PinkSubClass.class);
    }

    @Case(18)
    @Test
    void shouldInjectFollowingNamedQualifierValue() {
        // given
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerInstance(standardOutputLogger(ALL)).build();

        // when
        DifferentNamedValuesHolder instance = underTest.instance(DifferentNamedValuesHolder.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getImplementationA())
                .isNotNull()
                .isExactlyInstanceOf(ImplementationA.class);
        assertThat(instance.getImplementationB())
                .isNotNull()
                .isExactlyInstanceOf(ImplementationB.class);
    }

    @Case(18)
    @Test
    void shouldErrorOutOnNamedValuesMismatch() {
        // given
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerInstance(standardOutputLogger(ALL)).build();

        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(NamedValuesMismatchHolder.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(TargetNotFoundException.class);
    }

    @Case(18)
    @Test
    void shouldErrorOutOnNamedValuesMismatchWithoutScanner() {
        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(NamedValuesMismatchHolder.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(TargetNotFoundException.class);
    }

    @Case(19)
    @Test
    void shouldInjectProviderField() {
        // when
        ProviderFieldHolder instance = underTest.instance(ProviderFieldHolder.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getProvider()).isNotNull();
        assertThat(instance.getProvider().get())
                .isNotNull()
                .isExactlyInstanceOf(ProvidedSingleton.class)
                .isSameAs(instance.getProvider().get());
    }

    @Case(19)
    @Test
    void shouldInjectProviderByConstructor() {
        // when
        ProviderConstructorHolder instance = underTest.instance(ProviderConstructorHolder.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getProvider()).isNotNull();
        assertThat(instance.getProvider().get())
                .isNotNull()
                .isExactlyInstanceOf(ProvidedSingleton.class)
                .isSameAs(instance.getProvider().get());
    }

    @Case(19)
    @Test
    void shouldErrorOutOnNoTypeProviderFieldInjection() {
        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(NoTypeProviderFieldHolder.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(AmbiguousTargetException.class);
    }

    @Case(19)
    @Test
    void shouldErrorOutOnNoTypeProviderInjectionByConstructor() {
        // when
        Throwable throwable = catchThrowable(() -> underTest.instance(NoTypeProviderConstructorHolder.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(AmbiguousTargetException.class);
    }

    @Case(19)
    @Test
    void shouldInjectWildcardProviderByField() {
        // when
        WildcardProviderFieldHolder instance = underTest.instance(WildcardProviderFieldHolder.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getProvider()).isNotNull();
        assertThat(instance.getProvider().get())
                .isNotNull()
                .isExactlyInstanceOf(Object.class)
                .isNotSameAs(instance.getProvider().get());
    }

    @Case(19)
    @Test
    void shouldInjectWildcardProviderByConstructor() {
        // when
        WildcardProviderConstructorHolder instance = underTest.instance(WildcardProviderConstructorHolder.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getProvider()).isNotNull();
        assertThat(instance.getProvider().get())
                .isNotNull()
                .isExactlyInstanceOf(Object.class)
                .isNotSameAs(instance.getProvider().get());
    }

    @Case(19)
    @Test
    void shouldInjectUpperBoundWildcardProviderByField() {
        // when
        UpperBoundWildcardProviderFieldHolder instance = underTest.instance(UpperBoundWildcardProviderFieldHolder.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getProvider()).isNotNull();
        assertThat(instance.getProvider().get())
                .isNotNull()
                .isExactlyInstanceOf(ProvidedSingleton.class)
                .isSameAs(instance.getProvider().get());
    }

    @Case(19)
    @Test
    void shouldInjectUpperBoundWildcardProviderByConstructor() {
        // when
        UpperBoundWildcardProviderConstructorHolder instance = underTest.instance(UpperBoundWildcardProviderConstructorHolder.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getProvider()).isNotNull();
        assertThat(instance.getProvider().get())
                .isNotNull()
                .isExactlyInstanceOf(ProvidedSingleton.class)
                .isSameAs(instance.getProvider().get());
    }

    @Case(19)
    @Test
    void shouldInjectSpecificUpperBoundWildcardProviderByField() {
        // given
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerInstance(standardOutputLogger(ALL)).build();

        // when
        SpecificUpperBoundWildcardProviderFieldHolder instance = underTest.instance(SpecificUpperBoundWildcardProviderFieldHolder.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getProvider()).isNotNull();
        assertThat(instance.getProvider().get())
                .isNotNull()
                .isExactlyInstanceOf(SpecificProvidedSingleton.class)
                .isSameAs(instance.getProvider().get());
    }

    @Case(19)
    @Test
    void shouldInjectSpecificUpperBoundWildcardProviderByConstructor() {
        // given
        underTest = Atto.builder().scanPackage("com.nilcaream.atto.example").loggerInstance(standardOutputLogger(ALL)).build();

        // when
        SpecificUpperBoundWildcardProviderConstructorHolder instance = underTest.instance(SpecificUpperBoundWildcardProviderConstructorHolder.class);

        // then
        assertThat(instance).isNotNull();
        assertThat(instance.getProvider()).isNotNull();
        assertThat(instance.getProvider().get())
                .isNotNull()
                .isExactlyInstanceOf(SpecificProvidedSingleton.class)
                .isSameAs(instance.getProvider().get());
    }

    @Test
    void shouldBeUnavailableForMissingReflections() {
        runWithReflectionsDisabled(() -> {
            // when
            Throwable throwable = catchThrowable(() -> Atto.builder().loggerInstance(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto").build());

            // then
            assertThat(throwable).isExactlyInstanceOf(ReflectionsNotFoundException.class);
        });
    }
}
