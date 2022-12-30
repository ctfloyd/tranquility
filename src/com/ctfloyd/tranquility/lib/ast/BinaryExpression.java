package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Callable;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class BinaryExpression extends Expression {

    private final Map<BinaryExpressionOperator, Callable<Value>> HANDLERS = new HashMap<>();

    private final AstNode left;
    private final AstNode right;
    private final BinaryExpressionOperator operator;

    public BinaryExpression(AstNode left, AstNode right, BinaryExpressionOperator operator) {
        HANDLERS.put(BinaryExpressionOperator.PLUS, this::plus);
        HANDLERS.put(BinaryExpressionOperator.LESS_THAN, this::lessThan);
        HANDLERS.put(BinaryExpressionOperator.LESS_THAN_EQUALS, this::lessThanEquals);
        HANDLERS.put(BinaryExpressionOperator.GREATER_THAN, this::greaterThan);
        HANDLERS.put(BinaryExpressionOperator.GREATER_THAN_EQUALS, this::greaterThanEquals);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public Value execute() {
        Callable<Value> handler = HANDLERS.get(operator);
        if (handler == null) {
            throw new UnsupportedOperationException("NOT IMPLEMENTED");
        }
        try {
            return handler.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setRuntime(Runtime runtime) {
        super.setRuntime(runtime);
        left.setRuntime(runtime);
        right.setRuntime(runtime);
    }

    private Value plus() {
        Value leftValue;
        if (left.isIdentifier()) {
            leftValue = ((Identifier) left).getReference().getValue(getRealm());
        } else {
            leftValue = left.execute();
        }

        Value rightValue;
        if (right.isIdentifier()) {
            rightValue = ((Identifier) right).getReference().getValue(getRealm());
        } else {
            rightValue = right.execute();
        }
        if (leftValue.isNumber() && rightValue.isNumber()) {
            return Value.add(leftValue, rightValue);
        } else if (leftValue.isString() && rightValue.isString()) {
            return Value.concat(leftValue, rightValue);
        } else if (leftValue.isNumber() && rightValue.isString()) {
            Value rightValueAsNumber = Value.number(Double.parseDouble(rightValue.asString()));
            return Value.add(leftValue, rightValueAsNumber);
        } else {
            throw new UnsupportedOperationException("Cannot handle binary 'PLUS' operator for values (" + leftValue + ", " + rightValue + ")");
        }
    }

    private Value lessThan() {
        Value leftValue = left.execute();
        Value rightValue = right.execute();
        // FIXME: Types should be coerced
        ASSERT(leftValue.isNumber());
        ASSERT(rightValue.isNumber());
        return Value._boolean(leftValue.asDouble() < rightValue.asDouble());
    }

    private Value lessThanEquals() {
        Value leftValue = left.execute();
        Value rightValue = right.execute();
        // FIXME: Types should be coerced
        ASSERT(leftValue.isNumber());
        ASSERT(rightValue.isNumber());
        return Value._boolean(leftValue.asDouble() <= rightValue.asDouble());
    }

    private Value greaterThan() {
        Value leftValue = left.execute();
        Value rightValue = right.execute();
        // FIXME: Types should be coerced
        ASSERT(leftValue.isNumber());
        ASSERT(rightValue.isNumber());
        return Value._boolean(leftValue.asDouble() > rightValue.asDouble());
    }

    private Value greaterThanEquals() {
        Value leftValue = left.execute();
        Value rightValue = right.execute();
        // FIXME: Types should be coerced
        ASSERT(leftValue.isNumber());
        ASSERT(rightValue.isNumber());
        return Value._boolean(leftValue.asDouble() >= rightValue.asDouble());
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
