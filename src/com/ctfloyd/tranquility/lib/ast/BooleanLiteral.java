package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.StringJoiner;

public class BooleanLiteral extends AstNode {

    private final boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        return Value.bool(value);
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
