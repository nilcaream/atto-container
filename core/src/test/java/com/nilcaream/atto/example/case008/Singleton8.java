package com.nilcaream.atto.example.case008;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Getter
@Singleton
public class Singleton8 {

    @Inject
    private Prototype8 prototype8;

}
