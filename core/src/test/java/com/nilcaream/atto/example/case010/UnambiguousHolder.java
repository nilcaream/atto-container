package com.nilcaream.atto.example.case010;

import lombok.Getter;

import javax.inject.Inject;

@Getter
public class UnambiguousHolder {

    @Inject
    private UnambiguousRed unambiguousRed;

    @Inject
    private UnambiguousPurple unambiguousPurple;
}
