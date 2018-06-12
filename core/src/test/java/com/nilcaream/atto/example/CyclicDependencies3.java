package com.nilcaream.atto.example;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Getter
@Singleton
public class CyclicDependencies3 {

    @Inject
    private CyclicDependencies1 cyclicDependencies1;

    @Inject
    private CyclicDependencies2 cyclicDependencies2;

    @Inject
    private CyclicDependencies3 cyclicDependencies3;

}
