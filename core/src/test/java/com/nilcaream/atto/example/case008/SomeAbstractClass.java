package com.nilcaream.atto.example.case008;

import lombok.Getter;

import javax.inject.Inject;

@Getter
public abstract class SomeAbstractClass {

    @Inject
    private SomePrototype somePrototypeSuper;
}
