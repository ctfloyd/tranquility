package com.ctfloyd.tranquility.lib.interpret;

import java.util.List;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

// https://tc39.es/ecma262/#sec-properties-of-the-object-prototype-object
public class ObjectPrototype extends JsObject {

    public ObjectPrototype() {
        putNativeFunction("hasOwnProperty", this::hasOwnProperty);
        putNativeFunction("isPrototypeOf", this::isPrototypeOf);
    }

    // https://tc39.es/ecma262/#sec-object.prototype.hasownproperty
    private Value hasOwnProperty(AstInterpreter interpreter, List<Value> arguments) {
        // 1. Let P be ? ToPropertyKey(V)
        // FIXME: This isn't spec compliant.
        Value value  = arguments.get(0);
        ASSERT(value.isString());
        String property = value.asString();
        // 2. Let O be ? ToObject(this value)
        JsObject object = interpreter.getThisValue().toObject(interpreter);
        // 3. Return HasOwnProperty(O, P)
        return object.hasOwnProperty(property);
    }

    // https://tc39.es/ecma262/#sec-object.prototype.isprototypeof
    private Value isPrototypeOf(AstInterpreter interpreter, List<Value> arguments) {
        // 1. If V is not an Object, return false
        Value v = arguments.get(0);
        if (!v.isObject()) {
            return Value._false();
        }

        // 2. Let O be ? ToObject(this value).
        Value o = Value.object(interpreter.getThisValue().toObject(interpreter));
        // 3. Repeat
        while (true) {
            // a. Set V to ? V.[[GetPrototypeOf]]();
            v = v.asObject().getPrototypeOf();
            // b. If V is null, return false
            if (v.isNull()) {
                return Value._false();
            }
            // c. If SameValue(O, V) is true, return true
            if (o.sameValue(v)) {
                return Value._true();
            }
        }
    }

}
