package com.ctfloyd.tranquility.lib.interpret;

import java.util.Collections;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class PropertyDescriptor {

    private Value value = Value.undefined();
    private boolean writable = false;
    private boolean enumerable = false;
    private boolean configurable = false;

    public PropertyDescriptor(Value value, boolean writable, boolean enumerable, boolean configurable) {
        ASSERT(value != null);
        this.value = value;
        this.writable = writable;
        this.enumerable = enumerable;
        this.configurable = configurable;
    }

    public Value get(AstInterpreter interpreter) {
        if (value.isObject() && value.asObject().isFunction()) {
            return ((Function) value.asObject()).call(interpreter, new ArgumentList(Collections.emptyList()));
        } else {
            return value;
        }
    }

    public void set(AstInterpreter interpreter, Value value) {
        if (!writable) {
            // TODO: Throw a JS error
            throw new RuntimeException("Property is not writable.");
        }

        // FIXME: This doesn't seem like quite the correct behavior
        if (value.isObject()) {
            ASSERT(value.asObject().isFunction());
            ((Function) value.asObject()).call(interpreter, new ArgumentList(Collections.singletonList(value)));
        } else {
            this.value = value;
        }
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
