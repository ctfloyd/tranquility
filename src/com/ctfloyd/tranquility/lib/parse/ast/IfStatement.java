package com.ctfloyd.tranquility.lib.parse.ast;

import com.ctfloyd.tranquility.lib.runtime.DeclarativeEnvironment;
import com.ctfloyd.tranquility.lib.runtime.Environment;
import com.ctfloyd.tranquility.lib.runtime.Value;

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
    public Value execute() {
        Value result = test.execute();
        ASSERT(result.isBoolean());
        Value value = Value.undefined();
        if (result.asBoolean()) {
            Environment env = new DeclarativeEnvironment(getRuntime().getCurrentExecutionContext().getLexicalEnvironment());
            getRuntime().getCurrentExecutionContext().setLexicalEnvironment(env);
            value = body.execute();
            getRuntime().getCurrentExecutionContext().setLexicalEnvironment(env.getOuterEnvironment());
        } else if (alternate != null){
            Environment env = new DeclarativeEnvironment(getRuntime().getCurrentExecutionContext().getLexicalEnvironment());
            getRuntime().getCurrentExecutionContext().setLexicalEnvironment(env);
            value = alternate.execute();
            getRuntime().getCurrentExecutionContext().setLexicalEnvironment(env.getOuterEnvironment());
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
