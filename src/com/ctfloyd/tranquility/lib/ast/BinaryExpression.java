package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.StringJoiner;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class BinaryExpression extends AstNode {

    private final AstNode left;
    private final AstNode right;
    private final BinaryExpressionOperator operator;

    public BinaryExpression(AstNode left, AstNode right, BinaryExpressionOperator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) throws RuntimeException {
        if (operator == BinaryExpressionOperator.PLUS) {
            Value leftValue = left.interpret(interpreter);
            Value rightValue = right.interpret(interpreter);
            if (leftValue.isNumber() && rightValue.isNumber()) {
                return Value.add(leftValue, rightValue);
            } else if (leftValue.isString() && rightValue.isString()) {
                return Value.concat(leftValue, rightValue);
            } else {
                throw new UnsupportedOperationException("Cannot handle binary 'PLUS' operator for values (" + leftValue + ", " + rightValue + ")");
            }
        } else if (operator == BinaryExpressionOperator.LESS_THAN) {
            Value leftValue = left.interpret(interpreter);
            Value rightValue = right.interpret(interpreter);
            // FIXME: Types should be coerced
            ASSERT(leftValue.isNumber());
            ASSERT(rightValue.isNumber());
            return Value.bool(leftValue.asDouble() < rightValue.asDouble());
        } else if (operator == BinaryExpressionOperator.LESS_THAN_EQUALS) {
            Value leftValue = left.interpret(interpreter);
            Value rightValue = right.interpret(interpreter);
            // FIXME: Types should be coerced
            ASSERT(leftValue.isNumber());
            ASSERT(rightValue.isNumber());
            return Value.bool(leftValue.asDouble() <= rightValue.asDouble());
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
