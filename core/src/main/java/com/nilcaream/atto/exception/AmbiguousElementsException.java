package com.nilcaream.atto.exception;

public class AmbiguousElementsException extends AttoException {

    public AmbiguousElementsException(String message) {
        super(message);
    }

    public AmbiguousElementsException(String message, Throwable cause) {
        super(message, cause);
    }
}
