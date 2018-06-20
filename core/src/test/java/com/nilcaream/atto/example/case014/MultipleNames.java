package com.nilcaream.atto.example.case014;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;

@Getter
public class MultipleNames {

    @Inject
    @Named("FieldName")
    private AlreadyNamed alreadyNamed;

    public MultipleNames(@Named("ConstructorName") AlreadyNamed alreadyNamed) {
        this.alreadyNamed = alreadyNamed;
    }
}
