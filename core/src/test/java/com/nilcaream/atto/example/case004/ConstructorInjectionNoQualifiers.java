package com.nilcaream.atto.example.case004;

import lombok.Getter;

import javax.inject.Inject;

@Getter
public class ConstructorInjectionNoQualifiers {

    private final AnotherWhite white;

    private final AnotherOrange orange;

    private final AnotherPrototype anotherPrototypeFinal;

    @Inject
    private AnotherPrototype anotherPrototypeField;

    public ConstructorInjectionNoQualifiers(AnotherWhite white, AnotherOrange orange, AnotherPrototype anotherPrototype) {
        this.white = white;
        this.orange = orange;
        this.anotherPrototypeFinal = anotherPrototype;
    }
}
