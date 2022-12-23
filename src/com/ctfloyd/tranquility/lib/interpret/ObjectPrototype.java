package com.ctfloyd.tranquility.lib.interpret;

import java.util.List;

// https://tc39.es/ecma262/#sec-properties-of-the-object-prototype-object
public class ObjectPrototype extends JsObject {

    public ObjectPrototype() {
        put("hasOwnProperty", Value.object(new NativeFunction(this::hasOwnProperty)));
    }

    private Value hasOwnProperty(AstInterpreter interpreter, List<Value> arguments) {
        // 1. Let P be ? ToPropertyKey(V)
        // FIXME: This isn't spec compliant.
        String property = ((StringObject) arguments.get(0).asObject()).getString();
        // 2. Let O be ? ToObject(this value)
        JsObject object = interpreter.getThisValue().asObject();
        // 3. Return HasOwnProperty(O, P)
        return ObjectOperation.hasOwnProperty(object, property);
    }

}
