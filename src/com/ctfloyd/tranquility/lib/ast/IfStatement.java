package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.StringJoiner;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class IfStatement extends AstNode {

    private final AstNode test;
    private final AstNode body;
    private final AstNode alternate;

    public IfStatement(AstNode test, AstNode body, AstNode alternate) {
        ASSERT(test != null);
        ASSERT(body != null);
        this.test = test;
        this.body = body;
        this.alternate = alternate;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        Value result = test.interpret(interpreter);
        ASSERT(result.isBoolean());
        Value value = Value.undefined();
        if (result.asBoolean()) {
            interpreter.enterScope();
            value = body.interpret(interpreter);
            interpreter.leaveScope();
        } else if (alternate != null){
            interpreter.enterScope();
            value = alternate.interpret(interpreter);
            interpreter.leaveScope();
        }
        return value;
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("IfStatement {");
        test.dump(indent + 1);
        body.dump(indent + 1);
        printIndent(indent);
        System.out.println("");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IfStatement.class.getSimpleName() + "[", "]")
                .add("test=" + test)
                .add("body=" + body)
                .toString();
    }
}
