package com.nilcaream.atto.example.case019;

import lombok.Getter;

import javax.inject.Provider;

@Getter
public class UpperBoundWildcardProviderConstructorHolder {

    private final Provider<? extends ProvidedSingleton> provider;

    public UpperBoundWildcardProviderConstructorHolder(Provider<? extends ProvidedSingleton> provider) {
        this.provider = provider;
    }
}
