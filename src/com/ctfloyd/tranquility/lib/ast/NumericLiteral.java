package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.runtime.Value;

import java.util.StringJoiner;

public class NumericLiteral extends Expression {

    private final double value;

    public NumericLiteral(double value) {
        this.value = value;
    }

    @Override
    public Value execute() {
        return Value.number(value);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("NumericLiteral (Value: " + value + ")");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NumericLiteral.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .toString();
    }
}
