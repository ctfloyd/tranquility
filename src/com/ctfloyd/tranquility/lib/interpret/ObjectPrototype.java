package com.ctfloyd.tranquility.lib.interpret;

import java.util.List;

// https://tc39.es/ecma262/#sec-properties-of-the-object-prototype-object
public class ObjectPrototype extends JsObject {

    public ObjectPrototype() {
        put("hasOwnProperty", Value.object(new NativeFunction(this::hasOwnProperty)));
        put("isPrototypeOf", Value.object(new NativeFunction(this::isPrototypeOf)));
    }

    // https://tc39.es/ecma262/#sec-object.prototype.hasownproperty
    private Value hasOwnProperty(AstInterpreter interpreter, List<Value> arguments) {
        // 1. Let P be ? ToPropertyKey(V)
        // FIXME: This isn't spec compliant.
        String property = ((StringObject) arguments.get(0).asObject()).getString();
        // 2. Let O be ? ToObject(this value)
        JsObject object = interpreter.getThisValue().asObject();
        // 3. Return HasOwnProperty(O, P)
        return ObjectOperation.hasOwnProperty(object, property);
    }

    // https://tc39.es/ecma262/#sec-object.prototype.isprototypeof
    private Value isPrototypeOf(AstInterpreter interpreter, List<Value> arguments) {
        // 1. If V is not an Object, return false
        Value v = arguments.get(0);
        if (!v.isObject()) {
            return Value._false();
        }

        // 2. Let O be ? ToObject(this value).
        Value o = ObjectOperation.toObject(interpreter, interpreter.getThisValue());
        // 3. Repeat
        while (true) {
            // a. Set V to ? V.[[GetPrototypeOf]]();
            v = v.asObject().getPrototype();
            // b. If V is null, return false
            if (v.isNull()) {
                return Value._false();
            }
            // c. If SameValue(O, V) is true, return true
            if (ComparisonOperation.sameValue(o, v).asBoolean()) {
                return Value._true();
            }
        }
    }

}
