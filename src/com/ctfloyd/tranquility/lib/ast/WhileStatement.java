package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.runtime.DeclarativeEnvironment;
import com.ctfloyd.tranquility.lib.runtime.Environment;
import com.ctfloyd.tranquility.lib.runtime.Value;

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
    public Value execute() {
        while(test.execute().asBoolean()) {
            Environment env = new DeclarativeEnvironment(getRuntime().getCurrentExecutionContext().getLexicalEnvironment());
            getRuntime().getCurrentExecutionContext().setLexicalEnvironment(env);
            body.execute();
            getRuntime().getCurrentExecutionContext().setLexicalEnvironment(env.getOuterEnvironment());
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
