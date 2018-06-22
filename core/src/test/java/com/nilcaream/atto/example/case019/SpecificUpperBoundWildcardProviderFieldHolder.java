package com.nilcaream.atto.example.case019;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

@Getter
public class SpecificUpperBoundWildcardProviderFieldHolder {

    @Inject
    @Named("Specific")
    private Provider<? extends ProvidedSingleton> provider;
}
