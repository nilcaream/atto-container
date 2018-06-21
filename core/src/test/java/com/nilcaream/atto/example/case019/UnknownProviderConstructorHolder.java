package com.nilcaream.atto.example.case019;

import lombok.Getter;

import javax.inject.Provider;

@Getter
public class UnknownProviderConstructorHolder {

    private final Provider provider;

    public UnknownProviderConstructorHolder(Provider singletonProvider) {
        this.provider = singletonProvider;
    }
}
