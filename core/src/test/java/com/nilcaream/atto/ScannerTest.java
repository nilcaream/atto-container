package com.nilcaream.atto;

import com.nilcaream.atto.example.case003.ExampleImplementationBlue;
import com.nilcaream.atto.example.case003.ExampleImplementationClone1;
import com.nilcaream.atto.example.case003.ExampleImplementationClone2;
import com.nilcaream.atto.example.case003.ExampleImplementationGreen;
import com.nilcaream.atto.example.case003.ExampleInterface;
import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.nilcaream.atto.Logger.Level.ALL;
import static com.nilcaream.atto.Logger.standardOutputLogger;
import static com.nilcaream.atto.ScannerUtil.runWithReflectionsDisabled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ScannerTest {

    private Scanner underTest = Scanner.builder().logger(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto").build();

    @Test
    void shouldErrorOutOnMissingPackageOnInitialization() {
        // given
        underTest = Scanner.builder().logger(standardOutputLogger(ALL)).build();

        // when
        assertThat(underTest.isAvailable()).isFalse();
        Throwable throwable = catchThrowable(() -> underTest.subTypes(ExampleInterface.class));

        // then
        assertThat(throwable).isExactlyInstanceOf(ReflectionsNotFoundException.class);

    }

    @Test
    void shouldFindTestSubTypes() {
        // when
        Set<Class<? extends ExampleInterface>> implementations = underTest.subTypes(ExampleInterface.class);

        // then
        assertThat(implementations)
                .isNotNull()
                .hasSize(4)
                .contains(ExampleImplementationBlue.class, ExampleImplementationGreen.class, ExampleImplementationClone1.class, ExampleImplementationClone2.class);
    }

    @Test
    void shouldBeUnavailableForMissingReflections() {
        runWithReflectionsDisabled(() -> {
            // when
            Scanner underTest = Scanner.builder().logger(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto").build();

            // then
            assertThat(underTest.isAvailable()).isFalse();
        });
    }
}