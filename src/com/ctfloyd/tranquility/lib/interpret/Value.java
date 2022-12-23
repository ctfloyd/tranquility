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

    public static Value _boolean(boolean value) { return new Value(ValueType.BOOLEAN, value); }
    public static Value _true() { return _boolean(true); }
    public static Value _false() { return _boolean(false); }

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

    public ValueType getType() {
        return type;
    }

    public boolean sameTypeAs(Value other) {
        if (other == null) {
            return false;
        }

        return type.equals(other.getType());
    }


    // https://tc39.es/ecma262/#sec-toobject
    public JsObject toObject(AstInterpreter interpreter) {
        if (isUndefined()) {
            // TODO: This should be a TypeError exception
            throw new RuntimeException("TypeError: value is undefined.");
        }

        if (isNull()) {
            // TODO: This should be a TypeError exception
            throw new RuntimeException("TypeError: value is null.");
        }

        if (isBoolean()) {
            // TODO: Return a boolean object
            throw new RuntimeException("NOT IMPLEMENTED");
        }

        if (isNumber()) {
            // TODO: Return a number object
            throw new RuntimeException("NOT IMPLEMENTED");
        }

        if (isString()) {
            return StringObject.create(interpreter, asString());
        }

        // TODO: Implement symbol and big int

        if (isObject()) {
            return asObject();
        }

        throw new RuntimeException("NO");
    }

    // https://tc39.es/ecma262/#sec-samevalue
    public boolean sameValue(Value y) {
        // 1. If Type(x) is different from Type(y), return false
        if (!sameTypeAs(y)) {
            return false;
        }
        // TODO: 2. If x is a Number, then a. Return Number::sameValue(x, y)
        // 3. Return SameValueNonNumber(x, y);
        return sameValueNonNumber(y);
    }

    public boolean sameValueNonNumber(Value y) {
        // 1. Assert: Type(X) is the same as Type(Y)
        assert(sameTypeAs(y));
        // TODO: 2. If x is a BigInt, then return BigInt::equal(x, y)
        // 3. If x is undefined, return true
        if (isUndefined()) {
            return true;
        }
        // 4. If x is null, return true
        if (isNull()) {
            return true;
        }
        // 5. If x is a String, then
        if (asObject().isStringObject()) {
            // a. If x and y are exactly the same sequence of code units (same length and same code units at corresponding
            // indices), return true; otherwise, return false.
            return ((StringObject) asObject()).getString().equals(((StringObject) y.asObject()).getString());
        }
        // TODO: 6. If x is a Boolean, then a. If x and y are both true or both false, return true; otherwise, return false.
        // TODO: 7
        // 8. If x and y are the same Object value, return true. Otherwise, return false;
        return asObject() == y.asObject();
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
