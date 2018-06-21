package com.nilcaream.atto.example.case019;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Provider;

@Getter
public class WildcardProviderFieldHolder {

    @Inject
    private Provider<?> provider;
}
