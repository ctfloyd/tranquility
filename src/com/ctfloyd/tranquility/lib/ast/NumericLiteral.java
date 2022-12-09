package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.StringJoiner;

public class NumericLiteral extends AstNode {

    private final double value;

    public NumericLiteral(double value) {
        this.value = value;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        return Value.number(value);
    }

    @Override
    public void dump(int indent) {
        System.out.println("NUMERIC_LITERAL: "  + value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NumericLiteral.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .toString();
    }
}
