package com.ctfloyd.tranquility.lib.runtime;

// A host-defined prototype for the global objects. Largely used to set value properties on the global object.
// https://tc39.es/ecma262/#sec-global-object
public class GlobalPrototype extends JsObject {

    public GlobalPrototype(Realm realm) {
        // This property has the attributes { [[Writable]]: true, [[Enumerable]]: false, [[Configurable]]: true }.
        defineOwnProperty("globalThis", PropertyDescriptor.create(globalThis(realm), PropertyDescriptorWritable.YES, PropertyDescriptorEnumerable.NO, PropertyDescriptorConfigurable.YES));
        // This property has the attributes { [[Writable]]: false, [[Enumerable]]: false, [[Configurable]]: false }.
        defineOwnProperty("Infinity", PropertyDescriptor.create(infinity(realm), PropertyDescriptorWritable.YES, PropertyDescriptorEnumerable.NO, PropertyDescriptorConfigurable.YES));
        // This property has the attributes { [[Writable]]: false, [[Enumerable]]: false, [[Configurable]]: false }.
        defineOwnProperty("NaN", PropertyDescriptor.create(nan(realm), PropertyDescriptorWritable.NO, PropertyDescriptorEnumerable.NO, PropertyDescriptorConfigurable.NO));
        // This property has the attributes { [[Writable]]: false, [[Enumerable]]: false, [[Configurable]]: false }.
        defineOwnProperty("undefined", PropertyDescriptor.create(Value.undefined(), PropertyDescriptorWritable.NO, PropertyDescriptorEnumerable.NO, PropertyDescriptorConfigurable.NO));

        // TODO: 19.2.1 - eval
        defineOwnProperty("isFinite", PropertyDescriptor.create(Value.object(Intrinsic.IS_FINITE.getGlobalValue(realm))));
        defineOwnProperty("isNaN", PropertyDescriptor.create(Value.object(Intrinsic.IS_NAN.getGlobalValue(realm))));
        defineOwnProperty("parseFloat", PropertyDescriptor.create(Value.object(Intrinsic.PARSE_FLOAT.getGlobalValue(realm))));

    }

    // https://tc39.es/ecma262/#sec-globalthis
    private Value globalThis(Realm realm) {
        // The inital value of the "globalThis" property of the global object in a Realm Record realm is realm.[[GlobalEnv]].[[GlobalThisValue]].
        return realm.getGlobalEnvironment().map(GlobalEnvironment::getGlobalThisValue).orElse(Value.undefined());
    }

    // https://tc39.es/ecma262/#sec-value-properties-of-the-global-object-infinity
    private Value infinity(Realm realm) {
        return Value.object(NumberObject.createPositiveInfinity(realm));
    }

    // https://tc39.es/ecma262/#sec-value-properties-of-the-global-object-nan
    private Value nan(Realm realm) {
        return Value.object(NumberObject.createNaN(realm));
    }


}
