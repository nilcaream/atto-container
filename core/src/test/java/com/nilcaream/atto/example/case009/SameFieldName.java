package com.nilcaream.atto.example.case009;

import javax.inject.Inject;

public class SameFieldName {

    @Inject
    private SameFieldYellow theName;

    public SameFieldYellow getTheName() {
        return theName;
    }
}
