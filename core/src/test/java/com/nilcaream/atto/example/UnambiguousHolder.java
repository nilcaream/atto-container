package com.nilcaream.atto.example;

import lombok.Getter;

import javax.inject.Inject;

@Getter
public class UnambiguousHolder {

    @Inject
    private UnambiguousRed unambiguousRed;

    @Inject
    private UnambiguousPurple unambiguousPurple;
}
