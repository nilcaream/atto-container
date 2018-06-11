package com.nilcaream.atto;

import lombok.Value;

@Value
class Descriptor {

    private Class<?> cls;
    private String qualifier;
}
