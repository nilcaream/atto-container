package com.nilcaream.atto;

class ScannerUtil {

    static synchronized void runWithReflectionsDisabled(Runnable runnable) {
        String original = Scanner.reflectionsCheckClass;
        try {
            Scanner.reflectionsCheckClass = "not.available.class";
            runnable.run();
        } finally {
            Scanner.reflectionsCheckClass = original;
        }
    }
}
