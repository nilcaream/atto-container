package com.nilcaream.atto.example;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Getter
@Singleton
public class SingletonImplementation extends AbstractClass {

    @Inject
    private RegularPrototype regularPrototypeSub;

    @Inject
    private RegularSingleton regularSingletonSub;

}
