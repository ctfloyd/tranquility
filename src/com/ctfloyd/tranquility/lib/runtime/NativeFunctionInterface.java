package com.ctfloyd.tranquility.lib.runtime;

@FunctionalInterface
public interface NativeFunctionInterface {
    Value apply(ArgumentList arguments);
}
