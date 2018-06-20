package com.nilcaream.atto.example.case003;

import com.nilcaream.atto.example.GreenQualifier;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;

@Getter
public class ConstructorInjection {

    private final ExampleInterface blue;

    private final ExampleInterface green;

    private final Prototype3 regularPrototype;

    @Inject
    private Prototype3 regularPrototypeField;

    public ConstructorInjection(@Named("Blue") ExampleInterface blue, @GreenQualifier ExampleInterface green, Prototype3 regularPrototype) {
        this.blue = blue;
        this.green = green;
        this.regularPrototype = regularPrototype;
    }
}
