package com.ctfloyd.tranquility.lib.interpret;

@FunctionalInterface
public interface NativeFunctionInterface {
    Value apply(AstInterpreter interpreter, ArgumentList arguments);
}
