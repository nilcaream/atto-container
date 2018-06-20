package com.nilcaream.atto.example.case018;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;

@Getter
public class NamedValuesMismatchHolder {

    @Inject
    @Named("B")
    private ImplementationA implementationA;
}
