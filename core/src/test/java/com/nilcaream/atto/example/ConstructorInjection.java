package com.nilcaream.atto.example;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;

@Getter
public class ConstructorInjection {

    private final ExampleInterface blue;

    private final ExampleInterface green;

    private final RegularPrototype regularPrototype;

    @Inject
    private RegularPrototype regularPrototypeField;

    public ConstructorInjection(@Named("Blue") ExampleInterface blue, @GreenQualifier ExampleInterface green, RegularPrototype regularPrototype) {
        this.blue = blue;
        this.green = green;
        this.regularPrototype = regularPrototype;
    }
}
