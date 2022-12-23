package com.ctfloyd.tranquility.lib.interpret;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

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

    // https://tc39.es/ecma262/#sec-toobject
    public static Value toObject(AstInterpreter interpreter,  Value value) {
        if (value.isUndefined()) {
            // TODO: This should be a TypeError exception
            throw new RuntimeException("TypeError: value is undefined.");
        }

        if (value.isNull()) {
            // TODO: This should be a TypeError exception
            throw new RuntimeException("TypeError: value is null.");
        }

        if (value.isBoolean()) {
            // TODO: Return a boolean object
            throw new RuntimeException("NOT IMPLEMENTED");
        }

        if (value.isNumber()) {
            // TODO: Return a number object
            throw new RuntimeException("NOT IMPLEMENTED");
        }

        if (value.isString()) {
            return Value.object(StringObject.create(interpreter, value.asString()));
        }

        // TODO: Implement symbol and big int

        if (value.isObject()) {
            return value;
        }

        ASSERT(false);
        return null;
    }

}
