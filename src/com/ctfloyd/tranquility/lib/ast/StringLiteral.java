package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.StringJoiner;

public class StringLiteral extends AstNode {

    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        return Value.string(value);
    }

    @Override
    public void dump(int indent) {
        System.out.println("STRING_LITERAL: "  + value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StringLiteral.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .toString();
    }
}
