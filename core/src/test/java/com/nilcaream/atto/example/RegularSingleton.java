package com.nilcaream.atto.example;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
@Getter
public class RegularSingleton {

    private String uuid = UUID.randomUUID().toString();

    @Inject
    private RegularPrototype regularPrototype;

}
