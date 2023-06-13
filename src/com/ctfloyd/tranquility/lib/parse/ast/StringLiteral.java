package com.ctfloyd.tranquility.lib.parse.ast;

import com.ctfloyd.tranquility.lib.runtime.Value;

import java.util.StringJoiner;

public class StringLiteral extends Expression {

    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public Value execute() {
        return Value.string(value);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("StringLiteral (Value: "  + value + ")");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StringLiteral.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .toString();
    }
}
