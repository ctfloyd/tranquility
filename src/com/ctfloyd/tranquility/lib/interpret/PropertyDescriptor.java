package com.ctfloyd.tranquility.lib.interpret;

import java.util.Optional;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class PropertyDescriptor {

    private Value value = null;
    private boolean writable = false;
    private boolean enumerable = false;
    private boolean configurable = false;
    private Function get = null;
    private Function set = null;

    public PropertyDescriptor(Value value, boolean writable, boolean enumerable, boolean configurable) {
        ASSERT(value != null);
        this.value = value;
        this.writable = writable;
        this.enumerable = enumerable;
        this.configurable = configurable;
    }

    public PropertyDescriptor(Value value) {
        this.value = value;
    }

    // https://tc39.es/ecma262/#sec-isaccessordescriptor
    public boolean isAccessorDescriptor() {
        // 1. If Desc is undefined, return false
        if (value == null) {
            return false;
        }
        // 2. If Desc has a [[Get]] field, return true.
        if (getGet().isPresent()) {
            return true;
        }
        // 3. If Desc has a [[Set]] field, return true.
        if (getSet().isPresent()) {
            return true;
        }
        // 4. Return false;
        return false;
    }

    // https://tc39.es/ecma262/#sec-isdatadescriptor
    public boolean isDataDescriptor() {
        // 1. If Desc is undefined, return false
        if (value == null) {
            return false;
        }
        // 2. If Desc has a [[Value]] field, return true
        if (value != null) {
            return true;
        }
        // 3. If desc has a [[Writable]] field, return true.
        if (writable) {
            return true;
        }
        // 4. Return false
        return false;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Optional<Function> getGet() {
        return Optional.ofNullable(get);
    }

    public void setGet(Function get) {
        this.get = get;
    }

    public Optional<Function> getSet() {
        return Optional.ofNullable(set);
    }

    public void setSet(Function set) {
        this.set = set;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public boolean isEnumerable() {
        return enumerable;
    }

    public void setEnumerable(boolean enumerable) {
        this.enumerable = enumerable;
    }

    public boolean isConfigurable() {
        return configurable;
    }

    public void setConfigurable(boolean configurable) {
        this.configurable = configurable;
    }
}
