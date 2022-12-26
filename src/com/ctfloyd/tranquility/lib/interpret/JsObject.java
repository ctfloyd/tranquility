package com.ctfloyd.tranquility.lib.interpret;

import java.util.*;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class JsObject {

    private static final String PROTOTYPE_PROPERTY = "__proto__";

    private final Map<String, Value> properties = new HashMap<>();
    private JsObject prototype;

    protected JsObject() {

    }

    public static JsObject create(AstInterpreter interpreter, Map<String, Value> properties) {
        JsObject object = new JsObject();
        properties.forEach(object::put);
        object.setPrototypeOf(interpreter.getBuiltinPrototype(BuiltinPrototype.OBJECT));
        return object;
    }

    // https://tc39.es/ecma262/#sec-hasownproperty
    public Value hasOwnProperty(String property) {
        // 1. Let desc be ?O.[[GetOwnProperty]](P).
        Value desc = get(property);
        // 2. If desc is undefined, return false.
        if (desc.isUndefined()) {
            return Value._false();
        }
        // 3. Return true
        return Value._true();
    }

    public Value get(String propertyName) {
        Value value = properties.get(propertyName);
        if (value != null) {
            return value;
        }

        JsObject currentProtoType = prototype;
        while (value == null && currentProtoType != null) {
            value = currentProtoType.get(propertyName);

            Value prototypeValue = prototype.getPrototypeOf();
            if (prototypeValue.isNull()) {
                currentProtoType = null;
            } else {
                currentProtoType = prototypeValue.asObject();
            }
        }

        return Objects.requireNonNullElseGet(value, Value::undefined);
    }

    // https://tc39.es/ecma262/#sec-ordinary-object-internal-methods-and-internal-slots-ownpropertykeys
    public List<String> ownPropertyKeys() {
        // 1. Let keys be a new empty List
        List<String> keys = new ArrayList<>();
        // FIXME: Override in ArrayObject
        // 2. For each own property key P of O such that P is an array index, in ascending numeric index order, do
            // a. Append P to keys
        // FIXME: Going to need to store properties as a linked hash map in the future to retain insertion order
        // 3. For each own property key P of O such that P is a String and P is not an array index, in ascending chronological
        // order of property creation, do
        // a. Append P to keys
        keys.addAll(properties.keySet());
        // FIXME: No symbols yet
        // 4. For each own property key P of O such that P is a Symbol, in ascending chronological order of property creation, do
        // a. Append P to keys
        return keys;
    }

    public void put(String propertyName, Value value) {
        ASSERT(value != null);
        ASSERT(propertyName != null);
        ASSERT(!propertyName.isBlank());

        if (propertyName.equals(PROTOTYPE_PROPERTY)) {
            ASSERT(value.isObject());
            setPrototypeOf(value.asObject());
            return;
        }

        properties.put(propertyName, value);
    }

    public Value getPrototypeOf() {
        if (prototype == null) {
            return Value.nullValue();
        }

        return Value.object(prototype);
    }

    public void setPrototypeOf(JsObject prototype) {
        this.prototype = prototype;
    }

    protected void putNativeFunction(String name, NativeFunctionInterface function) {
        put(name, Value.object(new NativeFunction(function)));
    }

    public boolean isFunction() { return false; }
    public boolean isConstructor() { return false; }
    public boolean isArray() { return false; }
    public boolean isNativeFunction() { return false; }
    public boolean isStringObject() { return false; }
    public boolean isNumberObject() { return false; }

}
