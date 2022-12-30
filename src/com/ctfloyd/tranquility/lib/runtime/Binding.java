package com.ctfloyd.tranquility.lib.runtime;

public class Binding {

    private Value value;
    private boolean mutable;
    private boolean deletable;
    private boolean strict;

    public Binding(Value value, boolean mutable, boolean deletable, boolean strict) {
        this.value = value;
        this.mutable = mutable;
        this.deletable = deletable;
        this.strict = strict;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public boolean isMutable() {
        return mutable;
    }

    public boolean isImmutable() {
        return !isMutable();
    }

    public void setMutable(boolean mutable) {
        this.mutable = mutable;
    }

    public boolean isUninitialized() {
        return value == null;
    }

    public boolean isStrict() {
        return strict;
    }
}
