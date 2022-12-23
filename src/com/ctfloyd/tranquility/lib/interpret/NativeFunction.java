package com.ctfloyd.tranquility.lib.interpret;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class NativeFunction extends JsObject {

    private final NativeFunctionInterface function;

    public NativeFunction(NativeFunctionInterface function) {
        ASSERT(function != null);
        this.function = function;
    }

    public Value call(AstInterpreter interpreter, ArgumentList arguments) {
        return function.apply(interpreter, arguments);
    }

    public boolean isNativeFunction() {
        return true;
    }
}
