package com.ctfloyd.tranquility.lib.runtime;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

// https://tc39.es/ecma262/multipage/indexed-collections.html#sec-array-objects
public class ArrayPrototype extends JsObject {

    public ArrayPrototype() {
        putNativeFunction("push", this::push);
    }

    // https://tc39.es/ecma262/multipage/indexed-collections.html#sec-array.prototype.push
    private Value push(ArgumentList items) {
        // 1. Let O be ? ToObject(this value).
        JsObject object = getRuntime().getThisValue().toObject(getRealm());
        ASSERT(object.isArray());
        ArrayObject o = (ArrayObject) object;

        // 2. Let len be ? LengthOfArrayLike(O).
        int len = o.length();
        // 3. Let argCount be the number of elements in items.
        int argCount = items.size();
        // 4. If len + argCount > 2^53 - 1, throw a TypeError exception.
        if (len + argCount > (2^53 - 1)) {
            throw new RuntimeException("TODO: This should be a TypeError.");
        }

        // 5. For each element E of items, do
        for (Value item : items) {
            //  a. Perform ? Set(O, ! ToString(𝔽(len)), E, true).
            // FIXME: Make this follow what the spec actually wants
            o.add(item);
            // b. Set len to len + 1.
            len += 1;
        }

        //  6. Perform ? Set(O, "length", 𝔽(len), true).
        // FIXME: Do that
        // 7. Return 𝔽(len).
        return Value.number(len);
    }

}
