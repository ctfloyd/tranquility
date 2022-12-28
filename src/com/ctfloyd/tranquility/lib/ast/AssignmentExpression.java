package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.JsObject;
import com.ctfloyd.tranquility.lib.interpret.Reference;
import com.ctfloyd.tranquility.lib.interpret.Value;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class AssignmentExpression extends AstNode {

    private final Expression leftHandSide;
    private final AstNode rightHandSide;
    private final AssignmentExpressionOperator operator;

    public AssignmentExpression(Expression leftHandSide, AstNode rightHandSide, AssignmentExpressionOperator operator) {
        ASSERT(leftHandSide != null);
        ASSERT(rightHandSide != null);
        ASSERT(operator != null);
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
        this.operator = operator;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        Reference reference = leftHandSide.getReference(interpreter);
        Value value = rightHandSide.interpret(interpreter);
        if (operator == AssignmentExpressionOperator.EQUALS) {
            Value base = reference.getBase();
            ASSERT(base.isObject());
            JsObject object = base.asObject();
            object.set(reference.getReferencedName(), value, true);
            return value;
        } else {
            throw new UnsupportedOperationException("Not implemented.");
        }
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("AssignmentExpression (");
        leftHandSide.dump(indent + 1);
        printIndent(indent + 1);
        System.out.println("Operator (" + operator + ")");
        rightHandSide.dump(indent + 1);
    }
}
