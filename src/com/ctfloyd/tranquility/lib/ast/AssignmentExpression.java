package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.Optional;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class AssignmentExpression extends AstNode {

    private final Identifier identifier;
    private final AstNode rightHandSide;
    private final AssignmentExpressionOperator operator;

    public AssignmentExpression(Identifier identifier, AstNode rightHandSide, AssignmentExpressionOperator operator) {
        ASSERT(identifier != null);
        ASSERT(rightHandSide != null);
        ASSERT(operator != null);
        this.identifier = identifier;
        this.rightHandSide = rightHandSide;
        this.operator = operator;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        if (operator == AssignmentExpressionOperator.EQUALS) {
            String variable = identifier.getName();
            Value newValue = rightHandSide.interpret(interpreter);
            ASSERT(interpreter.hasIdentifier(variable));
            interpreter.setIdentifier(variable, Optional.of(newValue));
            return newValue;
        } else {
            throw new UnsupportedOperationException("Not implemented.");
        }
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("AssignmentExpression (");
        identifier.dump(indent + 1);
        printIndent(indent + 1);
        System.out.println("Operator (" + operator + ")");
        rightHandSide.dump(indent + 1);
    }
}
