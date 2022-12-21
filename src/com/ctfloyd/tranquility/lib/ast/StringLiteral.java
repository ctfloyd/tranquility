package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.StringObject;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.StringJoiner;

public class StringLiteral extends AstNode {

    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        return Value.object(StringObject.create(interpreter, value));
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
