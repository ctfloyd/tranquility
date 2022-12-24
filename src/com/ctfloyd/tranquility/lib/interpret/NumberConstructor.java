package com.ctfloyd.tranquility.lib.interpret;

import java.util.List;

public class NumberConstructor extends Constructor {

    public static Double EPSILON = Math.pow(2, -52);

    public NumberConstructor(List<String> argumentNames) {
        super("Number", argumentNames);
        put("EPSILON", Value.number(EPSILON));
    }

    @Override
    public NumberObject construct(AstInterpreter interpreter, ArgumentList arguments, JsObject object) {
        return NumberObject.create(interpreter, arguments.getFirstArgument().asDouble());
    }
}
