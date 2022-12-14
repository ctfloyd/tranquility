package com.ctfloyd.tranquility.lib.runtime;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ObjectConstructor extends Constructor {

    public ObjectConstructor() {
        super("Object", Collections.emptyList());
        setPrototypeOf(new ObjectPrototype());
        putNativeFunction("assign", this::assign);
        putNativeFunction("create", this::create);
    }

    @Override
    public JsObject construct(Realm realm, ArgumentList arguments, JsObject object) {
        Value argument = arguments.getFirstArgument();

        // FIXME: Make this follow spec better
        if (object == null) {
            JsObject.create(getRealm(), Collections.emptyMap());
        }

        if (argument.isUndefined() || argument.isNull()) {
            JsObject.create(getRealm(), Collections.emptyMap());
        } else {
            object = argument.toObject(realm);
        }
        return object;
    }

    // https://tc39.es/ecma262/#sec-object.assign
    private Value assign(ArgumentList argumentList) {
        // 1. Let to be te toObject(target);
        JsObject to = argumentList.getFirstArgument().toObject(getRealm());
        // 2. If only one argument was passed, return to
        if (argumentList.size() == 1) {
            return Value.object(to);
        }
        // 3. For each element nextSource of sources, do
        ArgumentList sources = argumentList.subList(1, argumentList.size());
        for (Value nextSource : sources) {
            // a. If nextSource is neither undefined nor null, then
            if (!nextSource.isUndefined() && !nextSource.isNull()) {
                // i. Let from be !ToObject(nextSource).
                JsObject from = nextSource.toObject(getRealm());
                // ii. Let keys be from.[[OwnPropertyKeys]]().
                List<String> keys = from.ownPropertyKeys();
                // iii. For each element nextKeys of keys, do
                for (String nextKey : keys) {
                    // 1. Let desc be ? from.[[GetOwnProperty]](nextKey);.
                    Optional<PropertyDescriptor> desc = from.getOwnProperty(nextKey);
                    // 2. If desc is not undefined and desc.[[Enumerable]] is true, then
                    if (desc.isPresent() && desc.get().isEnumerable()) {
                        // a. Let propValue be ? Get(from, nextKey).
                        Value propValue = from.get(nextKey);
                        // b. Perform ? Set(to, nextKey, propValue, true).
                        to.set(nextKey, propValue, true);
                    }
                }
            }
        }

        return Value.object(to);
    }

    // https://tc39.es/ecma262/#sec-object.create
    public Value create(ArgumentList arguments) {
        Value o = arguments.getFirstArgument();
        // 1. If O is not an Object and O is not null, throw a TypeError exception
        // FIXME: Throw TypeError exception
        if (!o.isObject() && !o.isNull()) {
            throw new RuntimeException("TypeError: O is not an object and O is not null.");
        }
        // 2. Let obj be OrdinaryObjectCreate(O).
        JsObject obj = JsObject.ordinaryObjectCreate(o.asObject());
        // 3. If Properties is not undefined, then
        Value properties = arguments.getSecondArgument();
        if (!properties.isUndefined()) {
            // a. Return ? ObjectDefineProperties(obj, Properties).
            obj.defineProperties(properties);
        }
        // 4. Return obj.
        return Value.object(obj);
    }

}
