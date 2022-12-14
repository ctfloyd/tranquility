package com.ctfloyd.tranquility.lib.interpret;

import java.util.List;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

/**
 * https://console.spec.whatwg.org/
 */
public class ConsoleObject extends JsObject {

    private enum LogLevel {
        DEBUG,
        ASSERT,
        LOG,
        ERROR,
        WARN
    }

    public ConsoleObject() {
        put("log", Value.object(new NativeFunction(this::log)));
        put("assert", Value.object(new NativeFunction(this::assertImpl)));
    }

    public Value assertImpl(List<Value> arguments) {
        ASSERT(arguments.get(0).isBoolean());
        boolean condition = arguments.get(0).asBoolean();
        List<Value> data = arguments.subList(1, arguments.size());

        // 1.1 If condition is true, return.
        if (condition) {
            return Value.undefined();
        }

        // 2. Let message be a string without any formatting specifiers indicating generically an assertion failure (such as "Assertion failed")
        String message = "Assertion failed";
        Value valueMessage = Value.string(message);

        // 3. If data is empty, append message to data
        if (data.isEmpty()) {
            data.add(valueMessage);
        } else {
            // 4. Otherwise
            // 4.1 Let first be data[0]
            Value first = data.get(0);
            // If Type(first) is not String, then prepend message to data.
            if (!first.isString()) {
                data.add(0, valueMessage);
            } else {
                // 4.3 Otherwise
                // 4.3.1 Let concat be the concatenation of message, COLON, SPACE, and first.
                Value concat = Value.string(message + ": " + first.asString());
                // 4.3.2. Set data[0] to concat
                data.set(0, concat);
            }
        }

        // 5 Perform Logger("assert", data)
        logImpl(LogLevel.ASSERT, data);
        return Value.undefined();
    }

    public Value log(List<Value> arguments) {
        logImpl(LogLevel.LOG, arguments);
        return Value.undefined();
    }

    private void logImpl(LogLevel logLevel, List<Value> arguments) {
        System.out.print("(JS Log) [" + logLevel.name() + "] ");
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

    }


}
