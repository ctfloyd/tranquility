package com.ctfloyd.tranquility.lib.runtime;

import java.util.Collections;

public class NumberConstructor extends Constructor {

    public static Double EPSILON = Math.pow(2, -52);

    public NumberConstructor() {
        super("Number", Collections.singletonList("value"));
        set("EPSILON", Value.number(EPSILON), true);
        putNativeFunction("isFinite", this::isFinite);
        putNativeFunction("isInteger", this::isInteger);
    }

    @Override
    public NumberObject construct(Realm realm, ArgumentList arguments, JsObject object) {
        return NumberObject.create(realm, arguments.getFirstArgument().asDouble());
    }

    // https://tc39.es/ecma262/#sec-number.isfinite
    public Value isFinite(ArgumentList arguments) {
        // 1. If number is not a Number, return false
        Value unknown = arguments.getFirstArgument();
        if (unknown.isUndefined() || unknown.isNull()) {
            return Value._false();
        }

        JsObject object = unknown.toObject(getRealm());
        if (!object.isNumberObject()) {
            return Value._false();
        }
        // 2. If number is not finite return false
        NumberObject number = (NumberObject) object;
        if (number.isInfinite()) {
            return Value._false();
        }
        // 3. otherwise, return true.
        return Value._true();
    }

    // https://tc39.es/ecma262/#sec-number.isinteger
    public Value isInteger(ArgumentList argumentList) {
        // 1. Return isIntegralNumber(number);
        return Value._boolean(argumentList.getFirstArgument().isIntegralNumber(getRealm()));
    }
}
