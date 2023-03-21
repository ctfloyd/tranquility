package com.ctfloyd.tranquility.lib.runtime;

public enum Intrinsic {
    OBJECT("Object", unused -> new ObjectConstructor()),
    NUMBER("Number", unused -> new NumberConstructor()),
    STRING("String", unused -> new StringPrototype()),
    ARRAY("Array", unused -> new ArrayPrototype()),
    IS_FINITE("isFinite", realm -> new NativeFunction(args -> IntrinsicImplementation.isFinite(realm, args))),
    IS_NAN("isNaN", realm -> new NativeFunction(args -> IntrinsicImplementation.isNaN(realm, args))),
    PARSE_FLOAT("parseFloat", realm -> new NativeFunction(args -> IntrinsicImplementation.parseFloat(realm, args)));


    private final String globalName;
    private final IntrinsicFunctionInterface intrinsicFunctionInterface;

    Intrinsic(String globalName, IntrinsicFunctionInterface intrinsicFunctionInterface) {
        this.globalName = globalName;
        this.intrinsicFunctionInterface = intrinsicFunctionInterface;
    }

    public String getGlobalName() {
        return globalName;
    }

    public JsObject getGlobalValue(Realm realm) {
        return intrinsicFunctionInterface.apply(realm);
    }

    @FunctionalInterface
    public interface IntrinsicFunctionInterface {
        JsObject apply(Realm realm);
    }
}
