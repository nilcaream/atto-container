package com.nilcaream.atto.example.case019;

import lombok.Getter;

import javax.inject.Provider;

@Getter
public class ProviderConstructorHolder {

    private final Provider<ProvidedSingleton> singletonProvider;

    public ProviderConstructorHolder(Provider<ProvidedSingleton> singletonProvider) {
        this.singletonProvider = singletonProvider;
    }
}
