package com.nilcaream.atto.example.case019;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Provider;

@Getter
public class UpperBoundWildcardProviderFieldHolder {

    @Inject
    private Provider<? extends ProvidedSingleton> provider;
}
