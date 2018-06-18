package com.nilcaream.atto.example;

import javax.inject.Inject;

public class InnerClassImplementationHolder {

    @Inject
    private RegularPrototype regularPrototype;

    public final class InnerClassImplementation implements InnerClassInterface {

        public RegularPrototype getFieldFromOuterClass() {
            return regularPrototype;
        }
    }
}
