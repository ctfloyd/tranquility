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
                System.out.print(value.asString());
            }

            if (value.isNumber()) {
                System.out.print(value.asDouble());
            }

            if (value.isNull()) {
                System.out.print("null");
            }

            if (value.isUndefined()) {
                System.out.print("undefined");
            }

            if (value.isObject()) {
                System.out.print("[object Object]");
            }

            System.out.print(" ");
        }
        System.out.println();

        return Value.undefined();
    }

}
