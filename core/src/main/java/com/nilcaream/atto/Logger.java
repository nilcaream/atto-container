package com.nilcaream.atto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;
import java.util.logging.LogManager;

public interface Logger extends BiConsumer<Logger.Level, String> {

    enum Level {
        ALL, DEBUG, INFO, WARNING, ERROR, OFF
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS");

    default void debug(String message, Object... args) {
        accept(Level.DEBUG, message, args);
    }

    default void info(String message, Object... args) {
        accept(Level.INFO, message, args);
    }

    default void warning(String message, Object... args) {
        accept(Level.WARNING, message, args);
    }

    default void error(String message, Object... args) {
        accept(Level.ERROR, message, args);
    }

    default void accept(Level level, String message, Object... args) {
        if (args == null || args.length == 0) {
            accept(level, message);
        } else {
            accept(level, String.format(message, args));
        }
    }

    static Logger nullLogger() {
        return (level, message) -> {
        };
    }

    static Logger standardOutputLogger() {
        return standardOutputLogger(Level.INFO);
    }

    static Logger standardOutputLogger(Level min) {
        return (level, message) -> {
            if (level.compareTo(min) >= 0 && level != Level.OFF) {
                System.out.printf("%-23s %8s %s\n", LocalDateTime.now().format(formatter), level, message);
            }
        };
    }

    static Logger javaUtilLogger() {
        return new Logger() {
            java.util.logging.Logger logger = LogManager.getLogManager().getLogger("com.nilcaream.atto");

            @Override
            public void accept(Level level, String message) {
                switch (level) {
                    case ALL:
                    case DEBUG:
                        logger.finer(message);
                        break;
                    case INFO:
                        logger.info(message);
                        break;
                    case WARNING:
                        logger.warning(message);
                        break;
                    case ERROR:
                        logger.severe(message);
                        break;
                }
            }
        };
    }
}
