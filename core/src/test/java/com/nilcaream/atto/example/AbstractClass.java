package com.nilcaream.atto.example;

import com.nilcaream.atto.example.case008.SomePrototype;
import lombok.Getter;

import javax.inject.Inject;

@Getter
public abstract class AbstractClass {

    @Inject
    private SomePrototype regularPrototype;
}
