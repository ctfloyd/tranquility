package com.ctfloyd.tranquility.lib.interpret;

public class ObjectOperation {

    public static Value hasOwnProperty(JsObject object, String property) {
        // 1. Let desc be ?O.[[GetOwnProperty]](P).
        System.out.println("Object properties: " + object.getProperties());
        Value desc = object.get(property);
        // 2. If desc is undefined, return false.
        System.out.println("Desc is: " + desc);
        if (desc.isUndefined()) {
            return Value._false();
        }
        // 3. Return true
        return Value._true();
    }

}
