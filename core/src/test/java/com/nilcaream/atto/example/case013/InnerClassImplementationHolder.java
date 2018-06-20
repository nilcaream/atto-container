package com.nilcaream.atto.example.case013;

import com.nilcaream.atto.example.case008.SomePrototype;

import javax.inject.Inject;

public class InnerClassImplementationHolder {

    @Inject
    private SomePrototype regularPrototype;

    public final class InnerClassImplementation implements InnerClassInterface {

        public SomePrototype getFieldFromOuterClass() {
            return regularPrototype;
        }
    }
}
