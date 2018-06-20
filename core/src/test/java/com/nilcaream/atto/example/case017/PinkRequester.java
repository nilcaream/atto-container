package com.nilcaream.atto.example.case017;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;

@Getter
public class PinkRequester {

    @Inject
    @Named("Pink")
    private EmptySuperClass pinkWannabe;
}
