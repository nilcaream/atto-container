package com.nilcaream.atto.example.case003;

import com.nilcaream.atto.example.GreenQualifier;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;

@Getter
public class ConstructorInjectionSimple {

    private final ExampleImplementationBlue blue;

    private final ExampleImplementationGreen green;

    private final Prototype3 regularPrototype;

    @Inject
    private Prototype3 regularPrototypeField;

    public ConstructorInjectionSimple(@Named("Blue") ExampleImplementationBlue blue, @GreenQualifier ExampleImplementationGreen green, Prototype3 regularPrototype) {
        this.blue = blue;
        this.green = green;
        this.regularPrototype = regularPrototype;
    }
}
