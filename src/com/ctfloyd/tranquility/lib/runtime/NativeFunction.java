package com.ctfloyd.tranquility.lib.runtime;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class NativeFunction extends JsObject {

    private final NativeFunctionInterface function;

    public NativeFunction(NativeFunctionInterface function) {
        ASSERT(function != null);
        this.function = function;
    }

    public Value call(ArgumentList arguments) {
        return function.apply(arguments);
    }

    public boolean isNativeFunction() {
        return true;
    }
}
