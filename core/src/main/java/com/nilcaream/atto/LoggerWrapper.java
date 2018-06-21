package com.nilcaream.atto;

class LoggerWrapper implements Logger {

    private Logger implementation = Logger.nullLogger();

    void setImplementation(Logger implementation) {
        this.implementation = implementation;
    }

    @Override
    public void debug(String message, Object... args) {
        implementation.debug(message, args);
    }

    @Override
    public void info(String message, Object... args) {
        implementation.info(message, args);
    }

    @Override
    public void warning(String message, Object... args) {
        implementation.warning(message, args);
    }

    @Override
    public void error(String message, Object... args) {
        implementation.error(message, args);
    }

    @Override
    public void accept(Level level, String message, Object... args) {
        implementation.accept(level, message, args);
    }

    @Override
    public void accept(Level level, String s) {
        implementation.accept(level, s);
    }
}
