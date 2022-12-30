package com.ctfloyd.tranquility.lib.runtime;

import com.ctfloyd.tranquility.lib.common.StringUtils;

import java.util.StringJoiner;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class Reference {

    // FIXME: 6.2.5 This could also be a Symbol or a Private name
    private final String referencedName;
    private final boolean strict;
    private final Value thisValue;

    private Value baseAsValue;
    private Environment baseAsEnvironment;

    public Reference(String referencedName, boolean strict, Value thisValue) {
        ASSERT(StringUtils.isNotBlank(referencedName));
        this.baseAsValue = null;
        this.baseAsEnvironment = null;
        this.referencedName = referencedName;
        this.strict = strict;
        this.thisValue = thisValue;
    }

    public Reference(Value base, String referencedName, boolean strict, Value thisValue) {
        this(referencedName, strict, thisValue);
        this.baseAsValue = base;
    }

    public Reference(Environment base, String referencedName, boolean strict, Value thisValue) {
        this(referencedName, strict, thisValue);
        this.baseAsEnvironment = base;
    }

    public boolean isPropertyReference() {
        if (isUnresolvableReference()) {
            return false;
        }

        if (baseAsEnvironment != null) {
            return false;
        }

        return true;
    }

    public boolean isUnresolvableReference() {
        return baseAsValue == null && baseAsEnvironment == null;
    }

    public boolean isSuperReference() {
        return thisValue != null;
    }

    public Value getValue(Realm realm){
        if (isUnresolvableReference()) {
            // FIMXE: Throw
            throw new RuntimeException("ReferenceError");
        }

        if (isPropertyReference()) {
            JsObject baseObject = baseAsValue.toObject(realm);
            // TODO: If IsPrivateReference(V) ...
            return baseObject.get(referencedName, getThisValue());
        } else {
            ASSERT(baseAsEnvironment != null);
            return baseAsEnvironment.getBindingValue(referencedName, strict);
        }
    }

    public void putValue(Realm realm, Value value) {
        if (isUnresolvableReference()) {
            if (strict) {
                // FIMXE: Throw
                throw new RuntimeException("ReferenceError");
            }
            JsObject globalObject = realm.getGlobalObject().get();
            globalObject.set(referencedName, value, false);
        } else if (isPropertyReference()) {
            JsObject baseObject = baseAsValue.toObject(realm);
            // FIXME: TODO 3.b
            boolean succeeded = baseObject.set(referencedName, value, getThisValue());
            if (!succeeded && strict) {
                // FIMXE: Throw
                throw new RuntimeException("TypeError");
            }
        } else {
            ASSERT(baseAsEnvironment != null);
            baseAsEnvironment.setMutableBinding(referencedName, value, strict);
        }
    }
    public Value getThisValue() {
        ASSERT(isPropertyReference());
        if (isSuperReference()) {
            return thisValue;
        }

        return baseAsValue;
    }

    public String getReferencedName() {
        return referencedName;
    }

    public boolean isStrict() {
        return strict;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Reference.class.getSimpleName() + "[", "]")
                .add("baseAsValue=" + baseAsValue)
                .add("baseAsEnvironment=" + baseAsEnvironment)
                .add("referencedName='" + referencedName + "'")
                .add("strict=" + strict)
                .add("thisValue=" + thisValue)
                .toString();
    }
}
