package com.nilcaream.atto.example.case019;

import lombok.Getter;

import javax.inject.Provider;

@Getter
public class ProviderConstructorHolder {

    private final Provider<ProvidedSingleton> provider;

    public ProviderConstructorHolder(Provider<ProvidedSingleton> provider) {
        this.provider = provider;
    }
}
