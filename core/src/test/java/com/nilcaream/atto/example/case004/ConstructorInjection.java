package com.nilcaream.atto.example.case004;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;

@Getter
public class ConstructorInjection {

    private final AnotherInterface white;

    private final AnotherInterface orange;

    private final AnotherPrototype anotherPrototypeFinal;

    @Inject
    private AnotherPrototype anotherPrototypeField;

    public ConstructorInjection(@Named("White") AnotherInterface white, @OrangeQualifier AnotherInterface orange, AnotherPrototype anotherPrototype) {
        this.white = white;
        this.orange = orange;
        this.anotherPrototypeFinal = anotherPrototype;
    }
}
