package com.ctfloyd.tranquility.lib.parse.ast;

import com.ctfloyd.tranquility.lib.runtime.Value;

import java.util.StringJoiner;

public class BooleanLiteral extends Expression {

    private final boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    @Override
    public Value execute() {
        return Value._boolean(value);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("BooleanLiteral (Value: "  + value + ")");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BooleanLiteral.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .toString();
    }
}
