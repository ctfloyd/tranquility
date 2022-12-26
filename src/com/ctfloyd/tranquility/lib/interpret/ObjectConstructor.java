package com.ctfloyd.tranquility.lib.interpret;

import java.util.Collections;
import java.util.List;

public class ObjectConstructor extends Constructor {

    public ObjectConstructor() {
        super("Object", Collections.emptyList());
        putNativeFunction("assign", this::assign);
    }

    @Override
    public JsObject construct(AstInterpreter interpreter, ArgumentList arguments, JsObject object) {
        Value argument = arguments.getFirstArgument();

        // FIXME: Make this follow spec better
        if (object == null) {
            object = new JsObject();
            object.setPrototypeOf(interpreter.getBuiltinPrototype(BuiltinPrototype.OBJECT));
        }

        if (argument.isUndefined() || argument.isNull()) {
            object = new JsObject();
            object.setPrototypeOf(interpreter.getBuiltinPrototype(BuiltinPrototype.OBJECT));
        } else {
            object = argument.toObject(interpreter);
        }
        return object;
    }

    // https://tc39.es/ecma262/#sec-object.assign
    private Value assign(AstInterpreter interpreter, ArgumentList argumentList) {
        // 1. Let to be te toObject(target);
        JsObject to = argumentList.getFirstArgument().toObject(interpreter);
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
                JsObject from = nextSource.toObject(interpreter);
                // ii. Let keys be from.[[OwnPropertyKeys]]().
                List<String> keys = from.ownPropertyKeys();
                // iii. For each element nextKeys of keys, do
                for (String nextKey : keys) {
                    // 1. Let desc be ? from.[[GetOwnProperty]](nextKey);.
                    // FIXME: Use GetOwnProperty instead of inherited get
                    Value desc = from.get(nextKey);
                    // FIXME: Do the enumerable check:
                    // 2. If desc is not undefined and desc.[[Enumerable]] is true, then
                    if (!desc.isUndefined()) {
                        // a. Let propValue be ? Get(from, nextKey).
                        Value propValue = from.get(nextKey);
                        // b. Perform ? Set(to, nextKey, propValue, true).
                        to.put(nextKey, propValue);
                    }
                }
            }
        }

        return Value.object(to);
    }

}
