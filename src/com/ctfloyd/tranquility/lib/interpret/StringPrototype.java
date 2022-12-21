package com.ctfloyd.tranquility.lib.interpret;

import java.util.List;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class StringPrototype extends JsObject {

    public StringPrototype() {
        put("length", Value.object(new NativeFunction(this::length)));
    }

    public Value length(AstInterpreter interpreter, List<Value> arguments) {
        ASSERT(arguments.isEmpty());
        Value value = interpreter.getThisValue();
        ASSERT(value != null);
        ASSERT(value.isObject());
        ASSERT(value.asObject().isStringObject());
        StringObject stringObject = (StringObject) value.asObject();
        int length = stringObject.getString().length();
        return Value.number(length);
    }

}
