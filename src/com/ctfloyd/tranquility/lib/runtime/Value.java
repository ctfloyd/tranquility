package com.ctfloyd.tranquility.lib.runtime;

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

    public static Value _null() { return new Value(ValueType.NULL); }

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
    public JsObject toObject(Realm realm) {
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
            return NumberObject.create(realm, asDouble());
        }

        if (isString()) {
            return StringObject.create(realm, asString());
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

    // https://tc39.es/ecma262/#sec-topropertykey
    public String toPropertyKey() {
        // 1. Let key be ? ToPrimitive(argument, string);
        Value key = toPrimitive("string");
        // TODO: 2. If key is a symbol, then
            // a. Return key
        // 3. Return !ToString(key)
        return key._toString().asString();
    }

    public Value toPrimitive() {
        return toPrimitive(null);
    }

    // TODO: Don't reference the preferred type by string
    public Value toPrimitive(String preferredType) {
        // 1. If input is an Object, then
        if (isObject()) {
            // TODO: Implement according to specification
            // a. Let exoticToPrim be ? GetMethod(input, @@toPrimitive).
            if (asObject().isStringObject()) {
                return Value.string(((StringObject) asObject()).getString());
            }
        }

        return this;
    }

    // https://tc39.es/ecma262/#sec-tostring
    public Value _toString() {
        // 1. If argument is a String, return argument
        if (isString()) {
            return this;
        }

        // TODO: 2. If argument is a symbol, throw a typeError Exception
        // 3. If argument is undefined, return "undefined"
        if (isUndefined()) {
            return Value.string("undefined");
        }
        // 4. If argument is null, return "null"
        if (isNull()) {
            return Value.string("null");
        }

        // 5. If argument is true, return "true"
        // 6. If argument is false, return "false"
        if (isBoolean()) {
            return asBoolean() ? Value.string("true") : Value.string("false");
        }

        // TODO: 7. If argument is a Number, return Number::toString(10);
        // TODO: 8. If argument is a BigInt, return BigInt::toString(10);

        // 9. Assert: argument is an Object
        ASSERT(isObject());

        // 10: Let primValue be ? ToPrimitive(string);
        Value primitiveValue = toPrimitive("string");

        // 11. Assert primValue is not an Object
        ASSERT(!primitiveValue.isObject());

        // 12. Return ? ToString(primValue)
        return primitiveValue._toString();
    }

    // https://tc39.es/ecma262/#sec-parsefloat-string
    public Value trimString(TrimStringSpecifier where) {
        // 1. Let str be ? RequireObjectCoercible(string).
        // TODO: This isn't quite right.
        ASSERT(isString());
        //2. Let S be ? ToString(string);
        Value S = _toString();
        // 3. If where is start, let T be the String value that is a copy of S with leading white space removed.
        Value T = null;
        if (where == TrimStringSpecifier.START) {
            T = Value.string(S.asString().stripLeading());
        // 4. Else If where is end, let T be the String value that is a copy of S with trailing white space removed.
        } else if (where == TrimStringSpecifier.END) {
            T = Value.string(S.asString().stripTrailing());
        // 5. Else,
        } else {
            // a. Assert: where is start+end
            ASSERT(where == TrimStringSpecifier.START_AND_END);
            // b. Let T be the String value that is a copy of S with both leading and trailing white space removed.
            T = Value.string(S.asString().trim());
        }
        // 6. Return T.
        return T;
    }

    //  https://tc39.es/ecma262/#sec-tonumber
    // TODO: I'm not sure this supposed to return the number object. I think it should return a number primitive.
    public NumberObject _toNumber(Realm realm) {
        // 1. If argument is a Number, return argument.
        if (isNumber()) {
            return NumberObject.create(realm, this.asDouble());
        }

        if (isObject() && asObject().isNumberObject()) {
            return (NumberObject) this.asObject();
        }
        // TODO: 2. If argument is either a Symbol or a BigInt, throw a TypeError exception.
        // 3. If argument is undefined, return NaN.
        if (isUndefined()) {
            return NumberObject.createNaN(realm);
        }
        // 4. If argument is either null or false, return +0𝔽.
        if (isNull() || (isBoolean() && asBoolean() == false)) {
            return NumberObject.createPositiveInfinity(realm);
        }
        // 5. If argument is true, return 1𝔽.
        if (isBoolean() && asBoolean()) {
            return NumberObject.create(realm, 1.0);
        }
        // TODO: 6. If argument is a String, return StringToNumber(argument).
        // 7. Assert: argument is an Object.
        ASSERT(this.isObject());
        // 8. Let primValue be ? ToPrimitive(argument, number).
        Value primValue = toPrimitive("number");
        // 9. Assert: primValue is not an Object.
        ASSERT(!primValue.isObject());
        // 10. Return ? ToNumber(primValue).
        return primValue._toNumber(realm);
    }

    // https://tc39.es/ecma262/#sec-isintegralnumber
    public boolean isIntegralNumber(Realm realm) {
        // 1. If argument is not a Number, return false
        if (isUndefined() || isNull()) {
            return false;
        }

        JsObject object = toObject(realm);
        if (!object.isNumberObject()) {
            return false;
        }

        // 2. If argument is not finite, return false
        NumberObject number = (NumberObject) object;
        if (number.isInfinite()) {
            return false;
        }

        // 3. If floor(abs(R(argument))) !== abs(R(argument)), return false; 4. return true
        return Math.floor(Math.abs(number.getValue())) == Math.abs(number.getValue());
    }

    // https://tc39.es/ecma262/#sec-topropertydescriptor
    public PropertyDescriptor toPropertyDescriptor() {
        // 1. If obj is not an Object, throw a TypeError exception.
        if (!isObject()) {
            // FIXME: Throw a TypeError
            throw new RuntimeException("TypeError: Obj is not an object.");
        }

        // 2. Let desc bew a new Property Descriptor that initially has no fields.
        PropertyDescriptor desc = PropertyDescriptor.create(Value._null(), PropertyDescriptorWritable.NO, PropertyDescriptorEnumerable.NO, PropertyDescriptorConfigurable.NO);

        JsObject obj = asObject();
        // 3. Let hasEnumerable be ? HasProperty(Obj, "enumerable").
        boolean hasEnumerable = obj.hasProperty("enumerable");
        // 4. If hasEnumerable is true, then
        if (hasEnumerable) {
            // a. Let enumerable be ToBoolean(? Get(Obj, "enumerable")
            boolean enumerable = obj.get("enumerable").asBoolean();
            // b. Set desc.[[Enumerable]] to be enumerable
            desc.setEnumerable(enumerable);
        }
        // 5. Let hasConfigurable be ? HasProperty(obj, "configurable")
        boolean hasConfigurable = obj.hasProperty("configurable");
        // 6. If hasConfigurable is true, then
        if (hasConfigurable) {
            // a. Let configurable be ToBoolean(? Get(Obj, "configurable")
            boolean configurable = obj.get("configurable").asBoolean();
            // b. Set desc.[[Configurable]] to be configurable
            desc.setConfigurable(configurable);
        }
        // 7-14, Are the same as steps 1-6 but for properties: value, writable, get, and set respectively
        boolean hasValue = obj.hasProperty("value");
        if (hasValue) {
            Value value = obj.get("value");
            desc.setValue(value);
        }

        boolean hasWritable = obj.hasProperty("writable");
        if (hasWritable) {
            boolean writable = obj.get("writable").asBoolean();
            desc.setWritable(writable);
        }

        boolean hasGet = obj.hasProperty("get");
        if (hasGet) {
            Value getter = obj.get("get");
            // FIXME: Do it
            // b. If isCallable(getter) is false then getter is not undefined, throw a TypeError exception
            // c. Set desc.setGetter
        }
        // FIXME: Implement 14-15
        return desc;
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
