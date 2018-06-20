package com.nilcaream.atto.example.case009;

import javax.inject.Inject;

public class SameFieldNameSub extends SameFieldName {

    @Inject
    private SameFieldBlack theName;

    public SameFieldBlack getTheNameSub() {
        return theName;
    }
}
