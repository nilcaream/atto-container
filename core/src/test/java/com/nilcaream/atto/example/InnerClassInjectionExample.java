package com.nilcaream.atto.example;

import javax.inject.Inject;

public class InnerClassInjectionExample {

    @Inject
    private InnerClassInterface implementation;

    public InnerClassInterface getImplementation() {
        return implementation;
    }
}
