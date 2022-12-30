package com.ctfloyd.tranquility.lib.runtime;

public class StringObject extends JsObject {

    private final String string;

    private StringObject(String string) {
        this.string = string;

        // https://tc39.es/ecma262/#sec-properties-of-string-instances-length
        set("length", Value.number(this.string.length()), true);
    }

    public static StringObject create(Realm realm, String string)  {
        StringObject stringObject = new StringObject(string);
        stringObject.setPrototypeOf(realm.getIntrinsics().get(Intrinsic.STRING));
        return stringObject;
    }

    public String getString() {
        return string;
    }

    @Override
    public boolean isStringObject() {
        return true;
    }

}
