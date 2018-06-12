package com.nilcaream.atto.example;

import lombok.Getter;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class RegularPrototype {

    private static final AtomicInteger counter = new AtomicInteger(0);

    private Date date = new Date(0);

    private int id;

    {
        id = counter.incrementAndGet();
    }

}
