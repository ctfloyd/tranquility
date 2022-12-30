package com.ctfloyd.tranquility.lib.runtime;

public enum Intrinsic {
    OBJECT("Object", new ObjectConstructor()),
    NUMBER("Number", new NumberConstructor()),
    STRING("String", new StringPrototype()),
    ARRAY("Array", new ArrayPrototype());

    private final String globalName;
    private final JsObject globalValue;

    Intrinsic(String globalName, JsObject globalValue) {
        this.globalName = globalName;
        this.globalValue = globalValue;
    }

    public String getGlobalName() {
        return globalName;
    }

    public JsObject getGlobalValue() {
        return globalValue;
    }
}
