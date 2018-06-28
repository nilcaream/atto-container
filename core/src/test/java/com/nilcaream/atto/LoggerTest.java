package com.nilcaream.atto;

import org.junit.jupiter.api.Test;

class LoggerTest {

    @Test
    void shouldCreateJulLogger() {
        // given
        Logger logger = Logger.javaUtilLogger();

        // when
        callAllMethods(logger);
    }

    @Test
    void shouldCreateSoutLogger() {
        // given
        Logger logger = Logger.standardOutputLogger();

        // when
        callAllMethods(logger);
    }

    private void callAllMethods(Logger logger) {
        logger.debug("debug");
        logger.info("info");
        logger.warning("warning");
        logger.error("error");
        logger.accept(Logger.Level.ALL, "all");
    }
}
