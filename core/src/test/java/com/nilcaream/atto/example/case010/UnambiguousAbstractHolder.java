package com.nilcaream.atto.example.case010;

import lombok.Getter;

import javax.inject.Inject;

@Getter
public class UnambiguousAbstractHolder {

    @Inject
    private UnambiguousRedInterface unambiguousRed;

    @Inject
    private UnambiguousPurpleInterface unambiguousPurple;
}
