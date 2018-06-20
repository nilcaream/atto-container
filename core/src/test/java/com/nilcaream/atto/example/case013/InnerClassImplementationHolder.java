package com.nilcaream.atto.example.case013;

import com.nilcaream.atto.example.case008.Prototype8;

import javax.inject.Inject;

public class InnerClassImplementationHolder {

    @Inject
    private Prototype8 regularPrototype;

    public final class InnerClassImplementation implements InnerClassInterface {

        public Prototype8 getFieldFromOuterClass() {
            return regularPrototype;
        }
    }
}
