package com.nilcaream.atto;

import com.nilcaream.atto.exception.ReflectionsNotFoundException;
import org.junit.jupiter.api.Test;

import static com.nilcaream.atto.Logger.Level.ALL;
import static com.nilcaream.atto.Logger.standardOutputLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class InjectorDepTest {

    @Test
    void shouldBeUnavailableForMissingReflections() {
        // when
        Throwable throwable = catchThrowable(() -> Injector.builder().logger(standardOutputLogger(ALL)).scanPackage("com.nilcaream.atto").build());

        // then
        assertThat(throwable).isExactlyInstanceOf(ReflectionsNotFoundException.class);
    }
}
