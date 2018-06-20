package com.nilcaream.atto.example.case006;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Getter
@Singleton
public class SingletonImplementation {

    @Inject
    private RegularPrototype regularPrototypeSub;

    @Inject
    private RegularSingleton regularSingletonSub;

}
