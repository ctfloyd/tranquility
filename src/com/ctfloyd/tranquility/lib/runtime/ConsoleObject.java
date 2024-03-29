package com.ctfloyd.tranquility.lib.runtime;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

/**
 * https://console.spec.whatwg.org/
 */
public class ConsoleObject extends JsObject {

    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";
    private boolean vanillaLogs = false;

    private enum LogLevel {
        ASSERT,
        DEBUG,
        ERROR,
        INFO,
        LOG,
        WARN
    }

    public ConsoleObject() {
        this(false);
    }

    public ConsoleObject(boolean vanillaLogs) {
        this.vanillaLogs = vanillaLogs;
        putNativeFunction("assert", this::_assert);
        putNativeFunction("debug", this::debug);
        putNativeFunction("error", this::error);
        putNativeFunction("info", this::info);
        putNativeFunction("log", this::log);
        putNativeFunction("warn", this::warn);
    }

    public Value _assert(ArgumentList arguments) {
        ASSERT(arguments.getFirstArgument().isBoolean());
        boolean condition = arguments.getFirstArgument().asBoolean();
        ArgumentList data = arguments.subList(1, arguments.size());

        // 1.1 If condition is true, return.
        if (condition) {
            return Value.undefined();
        }

        // 2. Let message be a string without any formatting specifiers indicating generically an assertion failure (such as "Assertion failed")
        String message = "Assertion failed";
        Value valueMessage = Value.string(message);

        // 3. If data is empty, append message to data
        if (data.isEmpty()) {
            data.addArgument(valueMessage);
        } else {
            // 4. Otherwise
            // 4.1 Let first be data[0]
            Value first = data.getFirstArgument();
            // If Type(first) is not String, then prepend message to data.
            if (!first.isString()) {
                data.prepend(valueMessage);
            } else {
                // 4.3 Otherwise
                // 4.3.1 Let concat be the concatenation of message, COLON, SPACE, and first.
                Value concat = Value.string(message + ": " + first.asString());
                // 4.3.2. Set data[0] to concat
                data.overwriteArgumentAt(0, concat);
            }
        }

        // 5 Perform Logger("assert", data)
        logImpl(LogLevel.ASSERT, data);
        return Value.undefined();
    }

    // TODO: 1.1.2 clear(): https://console.spec.whatwg.org/#clear

    public Value debug(ArgumentList arguments) {
        // 1.1.3 Perform Logger("debug", data).
        logImpl(LogLevel.DEBUG, arguments);
        return Value.undefined();
    }

    public Value error(ArgumentList arguments) {
        // 1.1.4 Perform Logger("error", data).
        logImpl(LogLevel.ERROR, arguments);
        return Value.undefined();
    }

    public Value info(ArgumentList arguments) {
        // 1.1.5 Perform Logger("info", data).
        logImpl(LogLevel.INFO, arguments);
        return Value.undefined();
    }

    public Value log(ArgumentList arguments) {
        // 1.1.6 Perform Logger("log", data).
        logImpl(LogLevel.LOG, arguments);
        return Value.undefined();
    }

    // TODO: 1.1.7  table(tabularData, properties) - https://console.spec.whatwg.org/#table
    // TODO: 1.1.8  trace(...data) - https://console.spec.whatwg.org/#trace

    public Value warn(ArgumentList arguments) {
        // 1.1.9 Perform Logger("warn", data).
        logImpl(LogLevel.WARN, arguments);
        return Value.undefined();
    }

    // TODO: 1.1.10, 1.1.11

    private void logImpl(LogLevel logLevel, ArgumentList arguments) {
        if (!vanillaLogs) {
            System.out.print(ANSI_YELLOW + "(JS Log) [" + logLevel.name() + "] ");
        }

        if (arguments == null) {
            System.out.println("null");
        } else {
            int argument = 0;
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

                if (argument != arguments.size() - 1) {
                    System.out.print(" ");
                }
                argument++;
            }
        }

        if (!vanillaLogs) {
            System.out.print(ANSI_RESET);
        }

        System.out.println();
    }

    private String getStringRepresentationForValue(Value value) {
        if (value.isString()) {
            return value.asString();
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
