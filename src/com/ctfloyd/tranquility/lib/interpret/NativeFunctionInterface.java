package com.ctfloyd.tranquility.lib.interpret;

import java.util.List;

@FunctionalInterface
public interface NativeFunctionInterface {
    Value apply(AstInterpreter interpreter, List<Value> arguments);
}
