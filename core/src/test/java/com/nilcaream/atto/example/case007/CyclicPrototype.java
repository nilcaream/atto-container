package com.nilcaream.atto.example.case007;

import lombok.Getter;

import javax.inject.Inject;

@Getter
public class CyclicPrototype {

    @Inject
    private CyclicPrototype cyclicPrototype;

}
