package com.ctfloyd.tranquility.lib.runtime;

import com.ctfloyd.tranquility.lib.ast.AstNode;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class Argument extends AstNode {

    private String name;
    private Value value;

    public Argument(String name, Value value) {
        ASSERT(name != null);
        ASSERT(!name.isEmpty());
        ASSERT(value != null);
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
