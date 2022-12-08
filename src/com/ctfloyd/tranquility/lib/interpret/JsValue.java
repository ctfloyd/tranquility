package com.ctfloyd.tranquility.lib.interpret;

import java.util.StringJoiner;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class JsValue {

    private JsValueType type;
    private String stringValue;
    private Double number;
    private Boolean bool;
    private JsObject object;

    public static JsValue add(JsValue left, JsValue right) {
        ASSERT(left.isNumber());
        ASSERT(right.isNumber());
        Double sum = left.asDouble() + right.asDouble();
        return new JsValue(JsValueType.NUMBER, sum);
    }

    public static JsValue object(JsObject object) {
        return new JsValue(JsValueType.OBJECT, object);
    }

    public static JsValue undefined() {
        return new JsValue(JsValueType.UNDEFINED);
    }

    public static JsValue number(Double value) {
        return new JsValue(JsValueType.NUMBER, value);
    }

    public JsValue(JsValueType type, Object value)  {
        this.type = type;
        if (type == JsValueType.STRING) {
            ASSERT(value instanceof String);
            this.stringValue = value.toString();
        } else if (type == JsValueType.NUMBER)  {
            ASSERT(value instanceof Double);
            this.number = (Double) value;
        } else if (type == JsValueType.BOOLEAN) {
            ASSERT(value instanceof Boolean);
            this.bool = (Boolean)  value;
        } else if (type == JsValueType.OBJECT) {
            ASSERT(value instanceof JsObject);
            this.object = (JsObject) value;
        }
    }

    public JsValue(JsValueType type) {
        ASSERT(type == JsValueType.UNDEFINED || type == JsValueType.NULL);
        this.type = type;
    }

    public boolean isString() {
        return type == JsValueType.STRING;
    }

    public boolean isNumber() {
        return type == JsValueType.NUMBER;
    }

    public boolean isBoolean() {
        return type == JsValueType.BOOLEAN;
    }

    public boolean isObject() {
        return type == JsValueType.OBJECT;
    }

    public boolean isNull() {
        return type == JsValueType.NULL;
    }

    public boolean isUndefined() {
        return type == JsValueType.UNDEFINED;
    }

    public Double asDouble() {
        ASSERT(this.type == JsValueType.NUMBER);
        return number;
    }

    public JsObject asObject() {
        ASSERT(this.type == JsValueType.OBJECT);
        return object;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JsValue.class.getSimpleName() + "[", "]")
                .add("type=" + type)
                .add("stringValue='" + stringValue + "'")
                .add("number=" + number)
                .add("bool=" + bool)
                .add("object=" + object)
                .toString();
    }
}
