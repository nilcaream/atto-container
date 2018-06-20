package com.nilcaream.atto.example.case009;

import javax.inject.Inject;

public class SameFieldNameChild extends SameFieldNameParent {

    @Inject
    private SameFieldBlack theName;

    public SameFieldBlack getTheNameFromChild() {
        return theName;
    }
}
