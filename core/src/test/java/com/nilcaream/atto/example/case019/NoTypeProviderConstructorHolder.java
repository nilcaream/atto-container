package com.nilcaream.atto.example.case019;

import lombok.Getter;

import javax.inject.Provider;

@Getter
public class NoTypeProviderConstructorHolder {

    private final Provider provider;

    public NoTypeProviderConstructorHolder(Provider singletonProvider) {
        this.provider = singletonProvider;
    }
}
