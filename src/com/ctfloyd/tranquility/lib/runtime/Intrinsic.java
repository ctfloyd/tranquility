package com.ctfloyd.tranquility.lib.runtime;

import java.util.function.Supplier;

public enum Intrinsic {
    OBJECT("Object", ObjectConstructor::new),
    NUMBER("Number", NumberConstructor::new),
    STRING("String", StringPrototype::new),
    ARRAY("Array", ArrayPrototype::new);

    private final String globalName;
    private final Supplier<JsObject> globalValueSupplier;

    Intrinsic(String globalName, Supplier<JsObject> globalValueSupplier) {
        this.globalName = globalName;
        this.globalValueSupplier = globalValueSupplier;
    }

    public String getGlobalName() {
        return globalName;
    }

    public JsObject getGlobalValue() {
        return globalValueSupplier.get();
    }
}
