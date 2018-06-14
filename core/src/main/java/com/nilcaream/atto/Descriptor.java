package com.nilcaream.atto;

import lombok.NonNull;
import lombok.Value;

@Value
class Descriptor {

    @NonNull
    private Class<?> cls;

    @NonNull
    private String qualifier;
}
