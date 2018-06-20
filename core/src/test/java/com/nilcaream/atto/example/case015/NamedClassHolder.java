package com.nilcaream.atto.example.case015;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;

@Getter
public class NamedClassHolder {

    @Inject
    @Named("JustNamedClass")
    private NamedSuperClass namedSuperClass;
}
