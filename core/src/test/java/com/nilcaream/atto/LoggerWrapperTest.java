package com.nilcaream.atto;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class LoggerWrapperTest {

    private LoggerWrapper underTest = new LoggerWrapper();

    @Test
    void shouldDelegateToImplementation() {
        // given
        Logger implementation = mock(Logger.class);
        underTest.setImplementation(implementation);

        // when
        underTest.debug("1 debug");
        underTest.info("2 info");
        underTest.warning("3 warning");
        underTest.error("4 error");
        underTest.accept(Logger.Level.INFO, "accept info");
        underTest.accept(Logger.Level.WARNING, "accept warning %s", true);

        // then
        verify(implementation).debug(eq("1 debug"));
        verify(implementation).info(eq("2 info"));
        verify(implementation).warning(eq("3 warning"));
        verify(implementation).error(eq("4 error"));
        verify(implementation).accept(eq(Logger.Level.INFO), eq("accept info"));
        verify(implementation).accept(eq(Logger.Level.WARNING), eq("accept warning %s"), eq(true));
        verifyNoMoreInteractions(implementation);
    }
}
