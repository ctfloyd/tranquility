package com.ctfloyd.tranquility.lib.interpret;

public class ComparisonOperation {

    // https://tc39.es/ecma262/#sec-samevalue
    public static Value sameValue(Value x,  Value y) {
        // 1. If Type(x) is different from Type(y), return false
        if (!x.sameTypeAs(y)) {
            return Value._false();
        }
        // TODO: 2. If x is a Number, then a. Return Number::sameValue(x, y)
        // 3. Return SameValueNonNumber(x, y);
        return sameValueNonNumber(x, y);
    }

    public static Value sameValueNonNumber(Value x, Value y) {
        // 1. Assert: Type(X) is the same as Type(Y)
        assert(x.sameTypeAs(y));
        // TODO: 2. If x is a BigInt, then return BigInt::equal(x, y)
        // 3. If x is undefined, return true
        if (x.isUndefined()) {
            return Value._true();
        }
        // 4. If x is null, return true
        if (x.isNull()) {
            return Value._true();
        }
        // 5. If x is a String, then
        if (x.asObject().isStringObject()) {
            // a. If x and y are exactly the same sequence of code units (same length and same code units at corresponding
            // indices), return true; otherwise, return false.
            return Value._boolean(((StringObject) x.asObject()).getString().equals(((StringObject) y.asObject()).getString()));
        }
        // TODO: 6. If x is a Boolean, then a. If x and y are both true or both false, return true; otherwise, return false.
        // TODO: 7
        // 8. If x and y are the same Object value, return true. Otherwise, return false;
        return Value._boolean(x.asObject() == y.asObject());
    }

}
