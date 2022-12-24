package com.ctfloyd.tranquility.lib.interpret;

import java.util.Collections;

public class NumberConstructor extends Constructor {

    public static Double EPSILON = Math.pow(2, -52);

    public NumberConstructor() {
        super("Number", Collections.singletonList("value"));
        put("EPSILON", Value.number(EPSILON));
        putNativeFunction("isFinite", this::isFinite);
    }

    @Override
    public NumberObject construct(AstInterpreter interpreter, ArgumentList arguments, JsObject object) {
        return NumberObject.create(interpreter, arguments.getFirstArgument().asDouble());
    }

    // https://tc39.es/ecma262/#sec-number.isfinite
    public Value isFinite(AstInterpreter interpreter, ArgumentList arguments) {
        // 1. If number is not a Number, return false
        JsObject object = arguments.getFirstArgument().toObject(interpreter);
        if (!object.isNumber()) {
            return Value._false();
        }
        // 2. If number is not finite return false
        NumberObject number = (NumberObject) object;
        if (number.isInfinite()) {
            return Value._false();
        }
        // 3. otherwise, return true.
        return Value._true();
    }
}
