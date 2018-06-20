package com.nilcaream.atto.example.case011;

import com.nilcaream.atto.Logger;

import javax.inject.Inject;

public class ClassWithLogger {

    @Inject
    private Logger logger;

    public Logger getLogger() {
        return logger;
    }
}
