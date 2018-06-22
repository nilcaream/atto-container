package com.nilcaream.atto.example.case019;

import lombok.Getter;

import javax.inject.Provider;

@Getter
public class WildcardProviderConstructorHolder {

    private final Provider<?> provider;

    public WildcardProviderConstructorHolder(Provider<?> provider) {
        this.provider = provider;
    }
}
