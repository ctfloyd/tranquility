package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.StringJoiner;

public class ExpressionStatement extends AstNode {

    private final AstNode expression;

    public ExpressionStatement(AstNode expression) {
        this.expression = expression;
    }

    public AstNode getExpression() {
        return expression;
    }

    public Value interpret(AstInterpreter interpreter) {
        return expression.interpret(interpreter);
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
