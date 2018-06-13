package com.nilcaream.atto.example;

import javax.inject.Inject;

public class SameFieldNameParent {

    @Inject
    private SameFieldYellow theName;

    public SameFieldYellow getTheNameFromParent() {
        return theName;
    }
}
