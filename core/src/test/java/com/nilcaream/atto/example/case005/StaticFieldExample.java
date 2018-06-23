package com.nilcaream.atto.example.case005;

import javax.inject.Inject;

public class StaticFieldExample {

    @Inject
    private static StaticFieldPrototype staticPrototype;

    @Inject
    private StaticFieldPrototype prototype;

    public static StaticFieldPrototype getStaticPrototype() {
        return staticPrototype;
    }

    public static void setStaticPrototype(StaticFieldPrototype staticPrototype) {
        StaticFieldExample.staticPrototype = staticPrototype;
    }

    public StaticFieldPrototype getPrototype() {
        return prototype;
    }
}
