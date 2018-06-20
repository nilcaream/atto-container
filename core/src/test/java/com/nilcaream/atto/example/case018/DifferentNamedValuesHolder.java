package com.nilcaream.atto.example.case018;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;

@Getter
public class DifferentNamedValuesHolder {

    @Inject
    @Named("A")
    private BaseInterface implementationA;

    @Inject
    @Named("B")
    private BaseInterface implementationB;
}
