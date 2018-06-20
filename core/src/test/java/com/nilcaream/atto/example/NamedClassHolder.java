package com.nilcaream.atto.example;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;

@Getter
public class NamedClassHolder {

    @Inject
    @Named("NamedExample")
    private NamedSuperClass namedSuperClass;
}
