package com.nilcaream.atto.example;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;

@Getter
public class ConstructorInjectionSimple {

    private final ExampleImplementationBlue blue;

    private final ExampleImplementationGreen green;

    private final RegularPrototype regularPrototype;

    @Inject
    private RegularPrototype regularPrototypeField;

    public ConstructorInjectionSimple(@Named("Blue") ExampleImplementationBlue blue, @GreenQualifier ExampleImplementationGreen green, RegularPrototype regularPrototype) {
        this.blue = blue;
        this.green = green;
        this.regularPrototype = regularPrototype;
    }
}
