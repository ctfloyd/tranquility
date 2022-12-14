package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class ForStatement extends AstNode {

    private final AstNode initializer;
    private final AstNode test;
    private final AstNode update;
    private final BlockStatement body;

    public ForStatement(AstNode initializer, AstNode test, AstNode update, BlockStatement body) {
        ASSERT(initializer != null);
        ASSERT(test != null);
        ASSERT(update != null);
        ASSERT(body != null);
        this.initializer = initializer;
        this.test = test;
        this.update = update;
        this.body = body;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) throws Exception {
        interpreter.enterScope();
        initializer.interpret(interpreter);
        while (test.interpret(interpreter).asBoolean()) {
            interpreter.enterScope();
            body.interpret(interpreter);
            interpreter.leaveScope();
            update.interpret(interpreter);
        }
        interpreter.leaveScope();
        return Value.undefined();
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("ForStatement (");
        initializer.dump(indent + 1);
        test.dump(indent + 1);
        update.dump(indent + 1);
        body.dump(indent + 1);
        printIndent(indent);
        System.out.println(")");
    }
}
