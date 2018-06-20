package com.nilcaream.atto.example.case008;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Getter
@Singleton
public class Implementation8 extends AbstractClass8 {

    @Inject
    private Prototype8 prototypeSub;

    @Inject
    private Singleton8 singletonSub;

}
