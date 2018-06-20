package com.nilcaream.atto.example.case012;

import javax.inject.Inject;

public class StaticNestedClassInjectionExample {

    @Inject
    private StaticNestedClassInterface implementation;

    public StaticNestedClassInterface getImplementation() {
        return implementation;
    }
}
