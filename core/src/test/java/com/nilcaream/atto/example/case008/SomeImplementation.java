package com.nilcaream.atto.example.case008;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Getter
@Singleton
public class SomeImplementation extends SomeAbstractClass {

    @Inject
    private SomePrototype somePrototypeSub;

    @Inject
    private SomeSingleton someSingletonSub;

}
