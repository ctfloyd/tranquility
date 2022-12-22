package com.ctfloyd.tranquility.lib.interpret;

import java.util.List;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

/**
 * https://console.spec.whatwg.org/
 */
public class ConsoleObject extends JsObject {

    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";

    private enum LogLevel {
        ASSERT,
        DEBUG,
        ERROR,
        INFO,
        LOG,
        WARN
    }

    public ConsoleObject() {
        put("assert", Value.object(new NativeFunction(this::assertImpl)));
        put("debug", Value.object(new NativeFunction(this::debug)));
        put("error", Value.object(new NativeFunction(this::error)));
        put("info", Value.object(new NativeFunction(this::info)));
        put("log", Value.object(new NativeFunction(this::log)));
        put("warn", Value.object(new NativeFunction(this::warn)));
    }

    public Value assertImpl(AstInterpreter interpreter, List<Value> arguments) {
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

    // TODO: 1.1.2 clear(): https://console.spec.whatwg.org/#clear

    public Value debug(AstInterpreter interpreter, List<Value> arguments) {
        // 1.1.3 Perform Logger("debug", data).
        logImpl(LogLevel.DEBUG, arguments);
        return Value.undefined();
    }

    public Value error(AstInterpreter interpreter, List<Value> arguments) {
        // 1.1.4 Perform Logger("error", data).
        logImpl(LogLevel.ERROR, arguments);
        return Value.undefined();
    }

    public Value info(AstInterpreter interpreter, List<Value> arguments) {
        // 1.1.5 Perform Logger("info", data).
        logImpl(LogLevel.INFO, arguments);
        return Value.undefined();
    }

    public Value log(AstInterpreter interpreter, List<Value> arguments) {
        // 1.1.6 Perform Logger("log", data).
        logImpl(LogLevel.LOG, arguments);
        return Value.undefined();
    }

    // TODO: 1.1.7  table(tabularData, properties) - https://console.spec.whatwg.org/#table
    // TODO: 1.1.8  trace(...data) - https://console.spec.whatwg.org/#trace

    public Value warn(AstInterpreter interpreter, List<Value> arguments) {
        // 1.1.9 Perform Logger("warn", data).
        logImpl(LogLevel.WARN, arguments);
        return Value.undefined();
    }

    // TODO: 1.1.10, 1.1.11

    private void logImpl(LogLevel logLevel, List<Value> arguments) {
        System.out.print(ANSI_YELLOW + "(JS Log) [" + logLevel.name() + "] ");
        if (arguments == null) {
            System.out.println("null");
        } else {
            for (Value value : arguments) {
                if (value.isObject()) {
                    if (value.asObject().isStringObject()) {
                       System.out.print(((StringObject)value.asObject()).getString());
                    } else if (value.asObject().isArray()) {
                        ArrayObject array = (ArrayObject) value.asObject();
                        System.out.print("Array [");
                        int size = array.length();
                        for (int i = 0; i < size; i++) {
                            String stringValue = getStringRepresentationForValue(array.getValueAtIndex(i));
                            if (i == size - 1) {
                                System.out.print(stringValue);
                            } else {
                                System.out.print(stringValue + ", ");
                            }
                        }
                        System.out.print("]");
                    } else {
                        System.out.print("[object Object]");
                    }
                } else {
                    System.out.print(getStringRepresentationForValue(value));
                }
                System.out.print(" ");
            }
        }
        System.out.println(ANSI_RESET);
    }

    private String getStringRepresentationForValue(Value value) {
        if (value.isString()) {
            return "\"" + value.asString() + "\"";
        }

        if (value.isNumber()) {
            return value.asDouble().toString();
        }

        if (value.isBoolean()) {
            return value.asBoolean() ? "true" : "false";
        }

        if (value.isNull()) {
            return "null";
        }

        if (value.isUndefined()) {
            return "undefined";
        }

        return "???";
    }
}
