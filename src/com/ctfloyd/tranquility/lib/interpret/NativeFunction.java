package com.ctfloyd.tranquility.lib.interpret;

import java.util.List;
import java.util.function.Function;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class NativeFunction extends JsObject {

    private final Function<List<Value>, Value> function;

    public NativeFunction(Function<List<Value>, Value> function) {
        ASSERT(function != null);
        this.function = function;
    }

    public Value call(List<Value> arguments) {
        return function.apply(arguments);
    }

    public boolean isNativeFunction() {
        return true;
    }
}
