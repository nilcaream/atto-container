package com.nilcaream.atto.example.case019;

import lombok.Getter;

import javax.inject.Named;
import javax.inject.Provider;

@Getter
public class SpecificUpperBoundWildcardProviderConstructorHolder {

    private final Provider<? extends ProvidedSingleton> provider;

    public SpecificUpperBoundWildcardProviderConstructorHolder(@Named("Specific") Provider<? extends ProvidedSingleton> provider) {
        this.provider = provider;
    }
}
