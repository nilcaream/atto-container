package com.nilcaream.atto.example.case009;

import com.nilcaream.atto.example.SameFieldYellow;

import javax.inject.Inject;

public class SameFieldNameParent {

    @Inject
    private SameFieldYellow theName;

    public SameFieldYellow getTheNameFromParent() {
        return theName;
    }
}
