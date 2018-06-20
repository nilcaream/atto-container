package com.nilcaream.atto.example.case006;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Getter
@Singleton
public class RegularSingleton {

    private String uuid = UUID.randomUUID().toString();

    @Inject
    private RegularPrototype regularPrototype;

}
