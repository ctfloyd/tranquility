package com.ctfloyd.tranquility.lib.interpret;

import java.util.StringJoiner;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class Value {

    private ValueType type;
    private String stringValue;
    private Double number;
    private Boolean bool;
    private JsObject object;

    public static Value add(Value left, Value right) {
        ASSERT(left.isNumber());
        ASSERT(right.isNumber());
        Double sum = left.asDouble() + right.asDouble();
        return new Value(ValueType.NUMBER, sum);
    }

    public static Value concat(Value left, Value right) {
        ASSERT(left.isString());
        ASSERT(right.isString());
        String concat = left.asString() + right.asString();
        return Value.string(concat);
    }

    public static Value object(JsObject object) {
        return new Value(ValueType.OBJECT, object);
    }

    public static Value undefined() {
        return new Value(ValueType.UNDEFINED);
    }

    public static Value nullValue() { return new Value(ValueType.NULL); }

    public static Value number(Double value) {
        return new Value(ValueType.NUMBER, value);
    }

    public static Value number(int value) {
        return number((double) value);
    }

    public static Value string(String value) { return new Value(ValueType.STRING, value); }

    public static Value bool(boolean value) { return new Value(ValueType.BOOLEAN, value); }

    public Value(ValueType type, Object value)  {
        this.type = type;
        if (type == ValueType.STRING) {
            ASSERT(value instanceof String);
            this.stringValue = value.toString();
        } else if (type == ValueType.NUMBER)  {
            ASSERT(value instanceof Double);
            this.number = (Double) value;
        } else if (type == ValueType.BOOLEAN) {
            ASSERT(value instanceof Boolean);
            this.bool = (Boolean)  value;
        } else if (type == ValueType.OBJECT) {
            ASSERT(value instanceof JsObject);
            this.object = (JsObject) value;
        }
    }

    public Value(ValueType type) {
        ASSERT(type == ValueType.UNDEFINED || type == ValueType.NULL);
        this.type = type;
    }

    public boolean isString() {
        return type == ValueType.STRING;
    }

    public boolean isNumber() {
        return type == ValueType.NUMBER;
    }

    public boolean isBoolean() {
        return type == ValueType.BOOLEAN;
    }

    public boolean isObject() {
        return type == ValueType.OBJECT;
    }

    public boolean isNull() {
        return type == ValueType.NULL;
    }

    public boolean isUndefined() {
        return type == ValueType.UNDEFINED;
    }

    public Double asDouble() {
        ASSERT(this.type == ValueType.NUMBER);
        return number;
    }

    public int asInteger() {
        ASSERT(this.type == ValueType.NUMBER);
        return number.intValue();
    }

    public JsObject asObject() {
        ASSERT(this.type == ValueType.OBJECT);
        return object;
    }

    public String asString() {
        ASSERT(this.type == ValueType.STRING || this.type == ValueType.NUMBER);
        if (this.type == ValueType.STRING) {
            return stringValue;
        } else {
            boolean isWhole = Math.floor(number) == number.doubleValue();
            if (isWhole) {
                return "" + asInteger();
            } else {
                return "" + asBoolean();
            }
        }
    }

    public boolean asBoolean() {
        ASSERT(this.type == ValueType.BOOLEAN);
        return bool;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Value.class.getSimpleName() + "[", "]")
                .add("type=" + type)
                .add("stringValue='" + stringValue + "'")
                .add("number=" + number)
                .add("bool=" + bool)
                .add("object=" + object)
                .toString();
    }
}
