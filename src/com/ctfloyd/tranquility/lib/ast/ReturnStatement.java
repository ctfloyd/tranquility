package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.StringJoiner;

public class ReturnStatement extends AstNode {

    private AstNode argument;

    public ReturnStatement(AstNode argument) {
        // argument can be null if it's a void return
        this.argument = argument;
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("Return (");
        argument.dump(indent + 1);
        printIndent(indent);
        System.out.println(")");
    }

    @Override
    public Value interpret(AstInterpreter interpreter) throws Exception {
        return argument.interpret(interpreter);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ReturnStatement.class.getSimpleName() + "[", "]")
                .add("argument=" + argument)
                .toString();
    }
}
