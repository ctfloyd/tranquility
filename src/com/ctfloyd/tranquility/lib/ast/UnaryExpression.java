package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class UnaryExpression extends AstNode {

    private final UnaryExpressionOperator operator;
    private final boolean prefix;
    private final AstNode argument;

    public UnaryExpression(AstNode argument, boolean prefix, UnaryExpressionOperator operator) {
        ASSERT(argument != null);
        ASSERT(operator != null);
        this.argument = argument;
        this.prefix = prefix;
        this.operator = operator;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) throws RuntimeException {
        if (operator == UnaryExpressionOperator.MINUS) {
            if (prefix) {
                Value value = argument.interpret(interpreter);
                if (value.isNumber()) {
                    return Value.number(-1 * value.asDouble());
                }
            }
        }
        ASSERT(false);
        return Value.undefined();
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("UnaryExpression (");
        printIndent(indent);
        System.out.println("[Operator]: " + operator);
        printIndent(indent);
        System.out.println("[Prefix]: " + prefix);
        printIndent(indent);
        System.out.println("[Argument] { ");
        argument.dump(indent + 1);
        printIndent(indent);
        System.out.println("[Argument] }");
        printIndent(indent);
        System.out.println(")");
    }
}
