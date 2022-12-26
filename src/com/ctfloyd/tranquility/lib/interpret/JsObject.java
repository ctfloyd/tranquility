package com.ctfloyd.tranquility.lib.interpret;

import java.util.*;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class JsObject {

    private static final String PROTOTYPE_PROPERTY = "__proto__";

    private final Map<String, PropertyDescriptor> properties = new HashMap<>();
    private JsObject prototype;

    protected JsObject() {

    }

    public static JsObject create(AstInterpreter interpreter, Map<String, PropertyDescriptor> properties) {
        JsObject object = new JsObject();
        properties.forEach((k, v) -> object.put(interpreter, k, v));
        object.setPrototypeOf(interpreter.getBuiltinPrototype(BuiltinPrototype.OBJECT));
        return object;
    }

    public static JsObject ordinaryObjectCreate(AstInterpreter interpreter, JsObject prototype) {
        JsObject object = new JsObject();
        object.setPrototypeOf(prototype);
        return object;
    }

    // https://tc39.es/ecma262/#sec-hasownproperty
    public Value hasOwnProperty(AstInterpreter interpreter, String property) {
        // 1. Let desc be ?O.[[GetOwnProperty]](P).
        Optional<PropertyDescriptor> desc = getOwnProperty(interpreter, property);
        // 2. If desc is undefined, return false.
        if (desc.isEmpty()) {
            return Value._false();
        }
        // 3. Return true
        return Value._true();
    }

    public Value get(AstInterpreter interpreter, String propertyName) {
        PropertyDescriptor descriptor = properties.get(propertyName);
        if (descriptor != null) {
            return descriptor.get(interpreter);
        }

        Value value = null;
        JsObject currentProtoType = prototype;
        while (value == null && currentProtoType != null) {
            value = currentProtoType.get(interpreter, propertyName);

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

    // https://tc39.es/ecma262/#sec-ordinary-object-internal-methods-and-internal-slots-defineownproperty-p-desc
    public boolean defineOwnProperty(AstInterpreter interpreter, String property, PropertyDescriptor descriptor) {
        // TODO: Handle differently based on whether or not the object is exotic
        // 1. Return ? OrdinaryDefineOwnProperty(O, P, Desc).
        return ordinaryDefineOwnProperty(interpreter, property, descriptor);
    }

    // https://tc39.es/ecma262/#sec-ordinarydefineownproperty
    private boolean ordinaryDefineOwnProperty(AstInterpreter interpreter, String propertyName, PropertyDescriptor propertyDescriptor) {
        // 1. Let current be ? O.[[GetOwnProperty]](P).
        Optional<PropertyDescriptor> current = getOwnProperty(interpreter, propertyName);
        // FIXME: isExtensible is not implemented
        // 2. Let extensible be ? isExtensible(O).
        // FIXME: The following function is not implemented.
        // 3. Return ValidateAndApplyPropertyDescriptor(O, P, extensible, Desc, current).
        if (current.isPresent()) {
            properties.put(propertyName, propertyDescriptor);
        }
        return true;
    }

    // https://tc39.es/ecma262/#sec-ordinary-object-internal-methods-and-internal-slots-hasproperty-p
    public boolean hasProperty(AstInterpreter interpreter, String propertyName) {
        // TODO: Handle differently based on whether or not the object is exotic
        // 1. Return ? OrdinaryHasProperty(O, P).
        return ordinaryHasProperty(interpreter, propertyName);
    }

    // https://tc39.es/ecma262/#sec-ordinaryhasproperty
    private boolean ordinaryHasProperty(AstInterpreter interpreter, String propertyName) {
        // 1. Let hasOwn be ? O.[[GetOwnProperty]](P).
        Optional<PropertyDescriptor> hasOwn = getOwnProperty(interpreter, propertyName);
        // 2. If hasOwn is not undefined, return true
        if (hasOwn.isPresent()) {
            return true;
        }
        // 3. Let parent be ? O.[[GetProtoTypeOf]]().
        JsObject parent = getPrototypeOf().toObject(interpreter);
        // 4. If parent is not null, then
        if (parent != null) {
            // a. Return ? parent.[[HasProperty]](P).
            return parent.hasProperty(interpreter, propertyName);
        }
        return false;
    }

    // https://tc39.es/ecma262/#sec-ordinary-object-internal-methods-and-internal-slots-getownproperty-p
    public Optional<PropertyDescriptor> getOwnProperty(AstInterpreter interpreter, String propertyName) {
        // TODO: Handle differently based on whether or not the object is exotic
        // 1. Return OrdinaryGetOwnProperty(P)
        return ordinaryGetOwnProperty(interpreter, propertyName);
    }

    // https://tc39.es/ecma262/#sec-ordinarygetownproperty
    private Optional<PropertyDescriptor> ordinaryGetOwnProperty(AstInterpreter interpreter, String propertyName) {
        // 1. If O does not have an own property with key P, return undefined
        if (!properties.containsKey(propertyName)) {
            return Optional.empty();
        }
        // 2. Let d be a newly created Property Descriptor with no fields.
        PropertyDescriptor d = new PropertyDescriptor(Value._null(), true, true, true);
        // 3. Let X be O's own property whose key is P
        PropertyDescriptor x = properties.get(propertyName);
        // 4. If x is a data property, then
        // FIXME: Implement this
        if (true) {
            // a. Set D.[[Value]] to the value of X's [[Value]] attribute.
            d.set(interpreter, x.get(interpreter));
            // b. Set d.[[Writable]] to the value of X's [[Writable]] attribute
            d.setWritable(x.isWritable());
        } else { // 5. Else,
            // FIXME: Do this
            // a. Assert: X is an accessor property
            //  ASSERT(x.isAccessorProperty());
            // b. Set D.[[Get]] to the value of X's [[Get]] attribute.
            // c. Set D.[[Set]] to the value of X's [[Set]] attribute.
        }

        // 6. Set D.[[Enumerable]] to the value of X's [[Enumerable]] attribute
        d.setEnumerable(x.isEnumerable());
        // 7. Set D.[[Configurable]] to the value of X's [[Configurable]] attribute
        d.setConfigurable(x.isConfigurable());
        // 8. Return D
        return Optional.of(d);

    }

    // https://tc39.es/ecma262/#sec-objectdefineproperties
    public Value defineProperties(AstInterpreter interpreter, Value properties) {
        // 1. Let props be ? ToObject(properties).
        JsObject props = properties.toObject(interpreter);
        // 2. Let keys be ? props.[[OwnPropertyKeys]]().
        List<String> keys = props.ownPropertyKeys();
        // 3. Let descriptors be a new empty List.
        List<List<Object>> descriptors = new ArrayList<>();
        // 4. For each element nextKey of keys, do
        for (String nextKey : keys) {
            // a. Let propDesc be ? props.[[GetOwnProperty]](nextKey).
            Optional<PropertyDescriptor> propDesc = props.getOwnProperty(interpreter, nextKey);
            // b. If propDesc is not undefined and propDesc.[[Enumerable]] is true, then
            if (propDesc.isPresent() && propDesc.get().isEnumerable()) {
                // FIXME: We are never getting description objects here, instead we are getting the value.
                // i. Let descObj be Get(props, nextKey);
                Value descObj = props.get(interpreter, nextKey);
                // ii. Let desc be ? ToPropertyDescriptor(descObj).
                PropertyDescriptor descriptor = descObj.toPropertyDescriptor(interpreter);
                // iii. Append the pair (a two element List) consisting of nextKey and desc to the end of descriptors
                List<Object> pair = new ArrayList<>();
                pair.add(nextKey);
                pair.add(descriptor);
                descriptors.add(pair);
            }
        }
        // 5. For each element pair of descriptors, do
        for (List<Object> pair : descriptors) {
            ASSERT(pair.size() == 2);
            // a. Let P be the first element of pair.
            String p = (String) pair.get(0);
            // b. Let desc be the second element of pair.
            PropertyDescriptor desc = (PropertyDescriptor) pair.get(1);
            // c. Perform ? DefinePropertyOrThrow(O, P, desc).
            definePropertyOrThrow(interpreter, p, desc);
        }
        // 6. Return o
        return Value.object(this);
    }

    public void put(AstInterpreter interpreter, String propertyName, PropertyDescriptor propertyDescriptor) {
        if (propertyName.equals(PROTOTYPE_PROPERTY)) {
            ASSERT(propertyDescriptor.get(interpreter).isObject());
            setPrototypeOf(propertyDescriptor.get(interpreter).asObject());
            return;
        }

        properties.put(propertyName, propertyDescriptor);
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

        // FIXME: This definitely isn't correct!
        properties.put(propertyName, new PropertyDescriptor(value, true, true, false));
    }

    public Value getPrototypeOf() {
        if (prototype == null) {
            return Value._null();
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

    // https://tc39.es/ecma262/#sec-definepropertyorthrow
    private void definePropertyOrThrow(AstInterpreter interpreter, String property, PropertyDescriptor propertyDescriptor) {
        // 1. Let success be ? O.[[DefineOwnProperty]](P, desc).
        boolean success = defineOwnProperty(interpreter, property, propertyDescriptor);
        // 2. If success is false, throw a TypeError exception.
        if (!success) {
            // FIXME: Throw a type error exception.
            throw new RuntimeException("TypeError!");
        }
        // 3. Return unused.
    }

}
