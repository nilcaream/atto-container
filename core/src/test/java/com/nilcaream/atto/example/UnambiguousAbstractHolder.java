package com.nilcaream.atto.example;

import lombok.Getter;

import javax.inject.Inject;

@Getter
public class UnambiguousAbstractHolder {

    @Inject
    private UnambiguousRedInterface unambiguousRed;

    @Inject
    private UnambiguousPurpleInterface unambiguousPurple;
}
