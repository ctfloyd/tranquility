package com.ctfloyd.tranquility.lib.interpret;

import java.util.List;

/**
 * https://console.spec.whatwg.org/
 */
public class ConsoleObject extends JsObject {

    public ConsoleObject() {
        put("log", Value.object(new NativeFunction(this::log)));
    }

    public Value log(List<Value> arguments) {
        if (arguments == null) {
            System.out.println("null");
        }

        for (Value value : arguments) {
            if (value.isString()) {
                System.out.println(value.asString());
            }

            if (value.isNumber()) {
                System.out.println(value.asDouble());
            }

            if (value.isNull()) {
                System.out.println("null");
            }

            if (value.isUndefined()) {
                System.out.println("undefined");
            }

            if (value.isObject()) {
                System.out.println("[object Object]");
            }
        }

        return Value.undefined();
    }

}
