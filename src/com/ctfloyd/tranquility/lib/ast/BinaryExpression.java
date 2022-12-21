package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class BinaryExpression extends AstNode {

    private final Map<BinaryExpressionOperator, Function<AstInterpreter, Value>> HANDLERS = new HashMap<>();

    private final AstNode left;
    private final AstNode right;
    private final BinaryExpressionOperator operator;

    public BinaryExpression(AstNode left, AstNode right, BinaryExpressionOperator operator) {
        HANDLERS.put(BinaryExpressionOperator.PLUS, this::plus);
        HANDLERS.put(BinaryExpressionOperator.LESS_THAN, this::lessThan);
        HANDLERS.put(BinaryExpressionOperator.LESS_THAN_EQUALS, this::lessThanEquals);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        Function<AstInterpreter, Value> handler = HANDLERS.get(operator);
        if (handler == null) {
            throw new UnsupportedOperationException("NOT IMPLEMENTED");
        }
        return handler.apply(interpreter);
    }

    private Value plus(AstInterpreter interpreter) {
        Value leftValue = left.interpret(interpreter);
        Value rightValue = right.interpret(interpreter);
        if (leftValue.isNumber() && rightValue.isNumber()) {
            return Value.add(leftValue, rightValue);
        } else if (leftValue.isString() && rightValue.isString()) {
            return Value.concat(leftValue, rightValue);
        } else {
            throw new UnsupportedOperationException("Cannot handle binary 'PLUS' operator for values (" + leftValue + ", " + rightValue + ")");
        }
    }

    private Value lessThan(AstInterpreter interpreter) {
        Value leftValue = left.interpret(interpreter);
        Value rightValue = right.interpret(interpreter);
        // FIXME: Types should be coerced
        ASSERT(leftValue.isNumber());
        ASSERT(rightValue.isNumber());
        return Value.bool(leftValue.asDouble() < rightValue.asDouble());
    }

    private Value lessThanEquals(AstInterpreter interpreter) {
        Value leftValue = left.interpret(interpreter);
        Value rightValue = right.interpret(interpreter);
        // FIXME: Types should be coerced
        ASSERT(leftValue.isNumber());
        ASSERT(rightValue.isNumber());
        return Value.bool(leftValue.asDouble() <= rightValue.asDouble());
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
