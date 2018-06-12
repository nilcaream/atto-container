package com.nilcaream.atto.example;

import lombok.Getter;

import javax.inject.Inject;

@Getter
public abstract class AbstractClass {

    @Inject
    private RegularPrototype regularPrototype;
}
