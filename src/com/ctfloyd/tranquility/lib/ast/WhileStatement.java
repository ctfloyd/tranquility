package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class WhileStatement extends AstNode {

    private final AstNode test;
    private final BlockStatement body;

    public WhileStatement(AstNode test, BlockStatement body) {
        ASSERT(test != null);
        ASSERT(body != null);
        this.test = test;
        this.body = body;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        while(test.interpret(interpreter).asBoolean()) {
            interpreter.enterScope();
            body.interpret(interpreter);
            interpreter.leaveScope();
        }
        return Value.undefined();
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("WhileStatement (");
        test.dump(indent + 1);
        body.dump(indent + 1);
        printIndent(indent);
        System.out.println(")");
    }
}
