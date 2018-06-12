package com.nilcaream.atto.example;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Getter
public class SingletonImplementation extends AbstractClass {

    @Inject
    private RegularPrototype regularPrototypeSub;

    @Inject
    private RegularSingleton regularSingletonSub;

}
