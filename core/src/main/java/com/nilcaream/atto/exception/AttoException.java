package com.nilcaream.atto.exception;

public class AttoException extends RuntimeException {

    public AttoException(String message) {
        super(message);
    }

    public AttoException(String message, Throwable cause) {
        super(message, cause);
    }
}
