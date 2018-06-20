package com.nilcaream.atto.example.case004;

import lombok.Getter;

import javax.inject.Inject;

@Getter
public class CyclicPrototype {

    @Inject
    private CyclicPrototype cyclicPrototype;

}
