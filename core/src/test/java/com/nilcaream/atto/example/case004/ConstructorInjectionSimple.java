package com.nilcaream.atto.example.case004;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;

@Getter
public class ConstructorInjectionSimple {

    private final AnotherWhite white;

    private final AnotherOrange orange;

    private final AnotherPrototype anotherPrototypeFinal;

    @Inject
    private AnotherPrototype anotherPrototypeField;

    public ConstructorInjectionSimple(@Named("White") AnotherWhite white, @OrangeQualifier AnotherOrange orange, AnotherPrototype anotherPrototype) {
        this.white = white;
        this.orange = orange;
        this.anotherPrototypeFinal = anotherPrototype;
    }
}
