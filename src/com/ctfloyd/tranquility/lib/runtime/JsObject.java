package com.ctfloyd.tranquility.lib.runtime;

import java.util.*;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class JsObject extends RuntimeDependency {

    private final Map<String, PropertyDescriptor> properties = new HashMap<>();

    private JsObject prototype;

    protected JsObject() {}

    public static JsObject create(Realm realm, Map<String, Value> properties) {
        JsObject object = new JsObject();
        properties.forEach((k, v) -> object.set(k, v, true));
        object.setPrototypeOf(realm.getIntrinsics().get(Intrinsic.OBJECT).getPrototypeOf().asObject());
        return object;
    }

    public static JsObject ordinaryObjectCreate(JsObject prototype) {
        JsObject object = new JsObject();
        object.setPrototypeOf(prototype);
        return object;
    }

    public static JsObject ordinaryObjectCreate(JsObject prototype, List<String> additionalInternalSlotsList) {
        return ordinaryObjectCreate(prototype);
    }

    // https://tc39.es/ecma262/#sec-hasownproperty
    public Value hasOwnProperty(String property) {
        // 1. Let desc be ?O.[[GetOwnProperty]](P).
        Optional<PropertyDescriptor> desc = getOwnProperty(property);
        // 2. If desc is undefined, return false.
        if (desc.isEmpty()) {
            return Value._false();
        }
        // 3. Return true
        return Value._true();
    }

    // https://tc39.es/ecma262/#sec-ordinary-object-internal-methods-and-internal-slots-get-p-receiver
    public Value get(String propertyName, Value receiver) {
        // FIXME: Do different things based whether this is an ordinary or exotic object.
        // 1. Return ? OrdinaryGet(O, P, Receiver).
        return ordinaryGet(propertyName, receiver);
    }

    public Value get(String propertyName) {
        return ordinaryGet(propertyName, Value.object(this));
    }

    // https://tc39.es/ecma262/#sec-object-environment-records
    public void set(String propertyName, Value value, boolean shouldThrow)  {
        // FIXME: Do different things based whether this is an ordinary or exotic object.
        // 1. Let success be ? O.[[Set]](P, V, O).
        boolean success = ordinarySet(propertyName, value, Value.object(this));
        // 2. If success is false and Throw is true, throw a TypeError exception.
        if (!success && shouldThrow) {
            // FIXME: Throw a TypeError exception.
            throw new RuntimeException("TypeError");
        }
        // 3. Return unused
    }

    // https://tc39.es/ecma262/#sec-ordinary-object-internal-methods-and-internal-slots-set-p-v-receiver
    public boolean set(String propertyName, Value value, Value receiver) {
        // 1. Return ? ordinarySet(O, P, V, receiver).
        return ordinarySet(propertyName, value, receiver);
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
    public boolean defineOwnProperty(String property, PropertyDescriptor descriptor) {
        // TODO: Handle differently based on whether or not the object is exotic
        // 1. Return ? OrdinaryDefineOwnProperty(O, P, Desc).
        return ordinaryDefineOwnProperty(property, descriptor);
    }

    // https://tc39.es/ecma262/#sec-ordinary-object-internal-methods-and-internal-slots-hasproperty-p
    public boolean hasProperty(String propertyName) {
        // TODO: Handle differently based on whether or not the object is exotic
        // 1. Return ? OrdinaryHasProperty(O, P).
        return ordinaryHasProperty(propertyName);
    }

    // https://tc39.es/ecma262/#sec-ordinary-object-internal-methods-and-internal-slots-delete-p
    public boolean delete(String propertyName) {
        return ordinaryDelete(propertyName);
    }

    // https://tc39.es/ecma262/#sec-ordinary-object-internal-methods-and-internal-slots-getownproperty-p
    public Optional<PropertyDescriptor> getOwnProperty(String propertyName) {
        // TODO: Handle differently based on whether or not the object is exotic
        // 1. Return OrdinaryGetOwnProperty(P)
        return ordinaryGetOwnProperty(propertyName);
    }

    // https://tc39.es/ecma262/#sec-objectdefineproperties
    public Value defineProperties(Value properties) {
        // 1. Let props be ? ToObject(properties).
        JsObject props = properties.toObject(getRealm());
        // 2. Let keys be ? props.[[OwnPropertyKeys]]().
        List<String> keys = props.ownPropertyKeys();
        // 3. Let descriptors be a new empty List.
        List<List<Object>> descriptors = new ArrayList<>();
        // 4. For each element nextKey of keys, do
        for (String nextKey : keys) {
            // a. Let propDesc be ? props.[[GetOwnProperty]](nextKey).
            Optional<PropertyDescriptor> propDesc = props.getOwnProperty(nextKey);
            // b. If propDesc is not undefined and propDesc.[[Enumerable]] is true, then
            if (propDesc.isPresent() && propDesc.get().isEnumerable()) {
                // i. Let descObj be Get(props, nextKey);
                Value descObj = props.get(nextKey);
                // ii. Let desc be ? ToPropertyDescriptor(descObj).
                PropertyDescriptor descriptor = descObj.toPropertyDescriptor();
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
            definePropertyOrThrow(p, desc);
        }
        // 6. Return o
        return Value.object(this);
    }

    // https://tc39.es/ecma262/#sec-definepropertyorthrow
    public void definePropertyOrThrow(String property, PropertyDescriptor propertyDescriptor) {
        // 1. Let success be ? O.[[DefineOwnProperty]](P, desc).
        boolean success = defineOwnProperty(property, propertyDescriptor);
        // 2. If success is false, throw a TypeError exception.
        if (!success) {
            // FIXME: Throw a type error exception.
            throw new RuntimeException("TypeError!");
        }
        // 3. Return unused.
    }

    // https://tc39.es/ecma262/#sec-ordinary-object-internal-methods-and-internal-slots-isextensible
    public boolean isExtensible() {
        // 1. Return OrdinaryIsExtensible(O).
        return ordinaryIsExtensible();
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

    public boolean isFunction() { return false; }
    public boolean isConstructor() { return false; }
    public boolean isArray() { return false; }
    public boolean isNativeFunction() { return false; }
    public boolean isStringObject() { return false; }
    public boolean isNumberObject() { return false; }

    protected void putNativeFunction(String name, NativeFunctionInterface function) {
        properties.put(name, new PropertyDescriptor(Value.object(new NativeFunction(function))));
    }

    // https://tc39.es/ecma262/#sec-ordinaryget
    private Value ordinaryGet(String propertyName, Value receiver) {
        // 1. Let desc be ? O.[[GetOwnProperty]](P).
        Optional<PropertyDescriptor> desc = getOwnProperty(propertyName);
        // 2. if desc is undefined, then
        if (desc.isEmpty()) {
            // a. Let parent b ? O.[[GetPrototypeOf]]().
            Value parent = getPrototypeOf();
            // b. If parent is null, return undefined.
            if (parent.isNull()) {
                return Value.undefined();
            }
            // c. Return ? parent.[[Get]](P, receiver).
            return parent.asObject().get(propertyName, receiver);
        }
        PropertyDescriptor descriptor = desc.get();
        // 3. If isDataDescriptor(desc) is true, return desc.[[Value]]
        if (descriptor.isDataDescriptor()) {
            return descriptor.getValue();
        }
        // 4. Assert: IsAccessorDescriptor(desc) is true.
        ASSERT(descriptor.isAccessorDescriptor());
        // 5. Let getter be desc.[[Get]].
        Optional<Function> getter = descriptor.getGet();
        // 6. If getter is undefined, return undefined.
        if (getter.isEmpty()) {
            return Value.undefined();
        }
        // 7. Return ? Call(getter, Receiver).
        return getter.get().call(new ArgumentList(receiver));
    }

    // https://tc39.es/ecma262/#sec-ordinarygetownproperty
    private Optional<PropertyDescriptor> ordinaryGetOwnProperty(String propertyName) {
        // 1. If O does not have an own property with key P, return undefined
        if (!properties.containsKey(propertyName)) {
            return Optional.empty();
        }
        // 2. Let d be a newly created Property Descriptor with no fields.
        PropertyDescriptor d = new PropertyDescriptor(Value._null(), true, true, true);
        // 3. Let X be O's own property whose key is P
        PropertyDescriptor x = properties.get(propertyName);
        // 4. If x is a data property, then
        if (x.isDataDescriptor()) {
            // a. Set D.[[Value]] to the value of X's [[Value]] attribute.
            d.setValue(x.getValue());
            // b. Set d.[[Writable]] to the value of X's [[Writable]] attribute
            d.setWritable(x.isWritable());
        } else {
            // 5. Else,
            // a. Assert: X is an accessor property
            ASSERT(x.isAccessorDescriptor());
            // b. Set D.[[Get]] to the value of X's [[Get]] attribute.
            d.setGet(x.getGet().get());
            // c. Set D.[[Set]] to the value of X's [[Set]] attribute.
            d.setSet(x.getSet().get());
        }

        // 6. Set D.[[Enumerable]] to the value of X's [[Enumerable]] attribute
        d.setEnumerable(x.isEnumerable());
        // 7. Set D.[[Configurable]] to the value of X's [[Configurable]] attribute
        d.setConfigurable(x.isConfigurable());
        // 8. Return D
        return Optional.of(d);

    }

    // https://tc39.es/ecma262/#sec-ordinarydelete
    private boolean ordinaryDelete(String propertyName) {
        // 1. Let desc be ? O.[[GetOwnProperty]](P).
        Optional<PropertyDescriptor> desc = getOwnProperty(propertyName);
        // 2. If desc is undefined, return true
        if (desc.isEmpty()) {
            return true;
        }
        // 3. If desc.[[Configurable]] is true, then
        if (desc.get().isConfigurable()) {
            // a. Remove the own property with name P from O.
            properties.remove(propertyName);
            // b. Return true.
            return true;
        }
        // 4. Return false.
        return false;
    }

    // https://tc39.es/ecma262/#sec-ordinaryhasproperty
    private boolean ordinaryHasProperty(String propertyName) {
        // 1. Let hasOwn be ? O.[[GetOwnProperty]](P).
        Optional<PropertyDescriptor> hasOwn = getOwnProperty(propertyName);
        // 2. If hasOwn is not undefined, return true
        if (hasOwn.isPresent()) {
            return true;
        }
        // 3. Let parent be ? O.[[GetProtoTypeOf]]().
        Value parent = getPrototypeOf();
        // 4. If parent is not null, then
        if (parent != null && !parent.isNull()) {
            // a. Return ? parent.[[HasProperty]](P).
            return parent.asObject().hasProperty(propertyName);
        }
        return false;
    }

    // https://tc39.es/ecma262/#sec-ordinarydefineownproperty
    private boolean ordinaryDefineOwnProperty(String propertyName, PropertyDescriptor propertyDescriptor) {
        // 1. Let current be ? O.[[GetOwnProperty]](P).
        Optional<PropertyDescriptor> current = getOwnProperty(propertyName);
        // 2. Let extensible be ? isExtensible(O).
        boolean extensible = isExtensible();
        // 3. Return ValidateAndApplyPropertyDescriptor(O, P, extensible, Desc, current).
        return validateAndApplyPropertyDescriptor(propertyName, extensible, propertyDescriptor, current);
    }

    private boolean validateAndApplyPropertyDescriptor(String propertyName, boolean extensible, PropertyDescriptor propertyDescriptor, Optional<PropertyDescriptor> currentDescriptorOptional) {
        if (currentDescriptorOptional.isEmpty()) {
            if (!extensible) {
                return false;
            }
            if (propertyDescriptor.isAccessorDescriptor()) {
                throw new RuntimeException("need to implement");
            } else {
                PropertyDescriptor newDesc = new PropertyDescriptor(propertyDescriptor.getValue(),
                        propertyDescriptor.isWritable(), propertyDescriptor.isEnumerable(), propertyDescriptor.isConfigurable());
                properties.put(propertyName, newDesc);
            }
            return true;
        }

        PropertyDescriptor currentDescriptor = currentDescriptorOptional.get();
        if (currentDescriptor.isDataDescriptor() && propertyDescriptor.isAccessorDescriptor()) {
            throw new RuntimeException("need to implement");
        } else if (currentDescriptor.isAccessorDescriptor() && propertyDescriptor.isDataDescriptor()) {
            throw new RuntimeException("need to implement");
        } else {
            // FIXME: This logic definitely is not correct
            currentDescriptor.setValue(propertyDescriptor.getValue());
            properties.put(propertyName, currentDescriptor);
        }

        return true;
    }

    // https://tc39.es/ecma262/#sec-ordinaryset
    private boolean ordinarySet(String propertyName, Value value, Value receiver) {
        // 1. Let ownDesc be ? O.[[GetOwnProperty]](P).
        Optional<PropertyDescriptor> ownDesc = getOwnProperty(propertyName);
        // 2. Return ? OrdinarySetWithOwnDescriptor(O, P, V, Receiver, ownDesc);
        return ordinarySetWithOwnDescriptor(propertyName, value, receiver, ownDesc);
    }

    // https://tc39.es/ecma262/#sec-ordinarysetwithowndescriptor
    private boolean ordinarySetWithOwnDescriptor(String propertyName, Value value, Value receiver, Optional<PropertyDescriptor> descriptor) {
        // 1. If ownDesc is undefined, then
        PropertyDescriptor ownDesc = descriptor.orElse(null);
        if (ownDesc == null) {
            // a. Let parent be ? O.[[GetPrototypeOf]]().
            Value parent = getPrototypeOf();
            // b. If parent is not null, then
            if (parent != null && !parent.isNull()) {
                // i. Return ? parent.[[Set]](P, V, Receiver).
                return parent.asObject().set(propertyName, value, receiver);
            } else {
                // c. Else,
                //   i. Set ownDesc to the PropertyDescriptor(undefined, true, true, true).
                ownDesc = new PropertyDescriptor(Value.undefined(), true, true, true);
            }
        }
        ASSERT(ownDesc != null);
        // 2. If isDataDescriptor(ownDesc) is true, then
        if (ownDesc.isDataDescriptor()) {
            // a. If ownDesc.[[Writable]] is false, return false
            if (!ownDesc.isWritable()) {
                return false;
            }
            // b. If Receiver is not an Object, return false
            if (!receiver.isObject()) {
                return false;
            }
            // c. Let existingDescriptor be ? Receiver.[[GetOwnProperty]](P).
            Optional<PropertyDescriptor> existingDescriptor = receiver.asObject().getOwnProperty(propertyName);
            // d. If existingDescriptor is not undefined, then
            if (existingDescriptor.isPresent()) {
                // i. if isAccessorDescriptor(existingDescriptor) is true, return false.
                if (existingDescriptor.get().isAccessorDescriptor()) {
                    return false;
                }
                // ii. if existingDescriptor.[[Writable]] is false, return false.
                if (!existingDescriptor.get().isWritable()) {
                    return false;
                }
                // iii. Let valueDesc be the PropertyDescriptor(V).
                PropertyDescriptor valueDesc = new PropertyDescriptor(value);
                // iv. Return ? Receiver.[[DefineOwnProperty]](P, valueDesc).
                return receiver.asObject().defineOwnProperty(propertyName, valueDesc);
            } else {
                // e. Else,
                // i. Assert: Receiver does not currently have a property P
                ASSERT(!receiver.asObject().hasOwnProperty(propertyName).asBoolean());
                // ii. Return ? CreateDataProperty(Receiver, P, V).
                return createDataProperty(propertyName, value);
            }
        }
        // 3. Assert isAccessorDescriptor(ownDesc) is true.
        // FIXME: Enable this
        // ASSERT(ownDesc.isAccessorDescriptor());
        // 4. Let setter be ownDesc.[[Set]].
        Optional<?> setter = ownDesc.getSet();
        // 5. If setter is undefined, return false
        if (!setter.isPresent()) {
            return false;
        }
        // 6. Perform ? Call(setter, Receiver, << V >>).
        // 7. Return true.
        return true;
    }

    private boolean createDataProperty(String propertyName, Value value) {
        PropertyDescriptor newDesc = new PropertyDescriptor(value, true, true, true);
        return defineOwnProperty(propertyName, newDesc);
    }

    private boolean ordinaryIsExtensible() {
       // FIXME: Do the correct thing, it changes based on whether or not an object is exotic.
        return true;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JsObject.class.getSimpleName() + "[", "]")
                .add("properties=" + properties)
                .add("prototype=" + prototype)
                .toString();
    }
}
