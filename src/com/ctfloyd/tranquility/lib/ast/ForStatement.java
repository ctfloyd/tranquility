package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.runtime.DeclarativeEnvironment;
import com.ctfloyd.tranquility.lib.runtime.Environment;
import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.Value;

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
    public Value execute() {
        Environment env = new DeclarativeEnvironment(getRuntime().getCurrentExecutionContext().getLexicalEnvironment());
        getRuntime().getCurrentExecutionContext().setLexicalEnvironment(env);
        initializer.execute();
        while (test.execute().asBoolean()) {
            body.execute();
            update.execute();
        }
        getRuntime().getCurrentExecutionContext().setLexicalEnvironment(env.getOuterEnvironment());
        return Value.undefined();
    }

    @Override
    public void setRuntime(Runtime runtime) {
        super.setRuntime(runtime);
        initializer.setRuntime(runtime);
        test.setRuntime(runtime);
        update.setRuntime(runtime);
        body.setRuntime(runtime);
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
