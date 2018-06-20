package com.nilcaream.atto.example.case008;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Getter
@Singleton
public class SomeSingleton {

    @Inject
    private SomePrototype somePrototype;

}
