package com.ctfloyd.tranquility.lib.interpret;

public enum Intrinsic {
    NUMBER("Number", new NumberConstructor()),
    OBJECT("Object", new ObjectConstructor());

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
