package com.ctfloyd.tranquility.lib.runtime;

import java.util.List;

public abstract class Constructor extends Function {

    public Constructor(String name, List<String> argumentNames) {
        super(name, argumentNames, null);
    }

    public abstract JsObject construct(Realm realm, ArgumentList arguments, JsObject object);

    @Override
    public Value call(ArgumentList arguments) {
        // FIXME: I highly doubt this is supposed to return a reference to itself when it's called, but otherwise
        // property lookups for objects are broken. This is likely because property lookups are supposed to take their
        // own [[Get]] and [[Set]] internal methods, but we always set one. If the getter is a method, then it's supposed
        // to be called. Let's dance around that logic here for now, unless it breaks down in another spot.
        return Value.object(this);
    }

    @Override
    public boolean isConstructor() {
        return true;
    }
}
