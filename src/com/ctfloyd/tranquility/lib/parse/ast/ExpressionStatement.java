package com.ctfloyd.tranquility.lib.parse.ast;

import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.Value;

import java.util.StringJoiner;

public class ExpressionStatement extends AstNode {

    private final Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    public AstNode getExpression() {
        return expression;
    }

    @Override
    public Value execute() {
        return expression.execute();
    }

    @Override
    public void setRuntime(Runtime runtime) {
        super.setRuntime(runtime);
        expression.setRuntime(runtime);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("ExpressionStatement (");
        expression.dump(indent + 1);
        printIndent(indent);
        System.out.println(")");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ExpressionStatement.class.getSimpleName() + "[", "]")
                .add("expression=" + expression)
                .toString();
    }
}
