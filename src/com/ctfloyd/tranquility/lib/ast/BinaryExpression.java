package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.StringJoiner;

public class BinaryExpression extends AstNode {

    private final AstNode left;
    private final AstNode right;
    private final BinaryExpressionOperator operator;

    public BinaryExpression(AstNode left, AstNode right, BinaryExpressionOperator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public AstNode getLeft() {
        return left;
    }

    public AstNode getRight() {
        return right;
    }

    public BinaryExpressionOperator getOperator() {
        return operator;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) throws Exception {
        if (operator == BinaryExpressionOperator.PLUS) {
            Value leftValue = left.interpret(interpreter);
            Value rightValue = right.interpret(interpreter);
            return Value.add(leftValue, rightValue);
        } else {
            throw new UnsupportedOperationException("NOT IMPLEMENTED");
        }
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("BinaryExpression (");
        left.dump(indent + 1);
        printIndent(indent);
        System.out.println(operator.name());
        right.dump(indent + 1);
        printIndent(indent);
        System.out.println(")");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BinaryExpression.class.getSimpleName() + "[", "]")
                .add("left=" + left)
                .add("right=" + right)
                .add("operator=" + operator)
                .toString();
    }
}
