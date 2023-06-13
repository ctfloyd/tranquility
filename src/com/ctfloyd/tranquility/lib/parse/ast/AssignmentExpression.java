package com.ctfloyd.tranquility.lib.parse.ast;

import com.ctfloyd.tranquility.lib.runtime.JsObject;
import com.ctfloyd.tranquility.lib.runtime.Reference;
import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.Value;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class AssignmentExpression extends Expression {

    private final Expression leftHandSide;
    private final Expression rightHandSide;
    private final AssignmentExpressionOperator operator;

    public AssignmentExpression(Expression leftHandSide, Expression rightHandSide, AssignmentExpressionOperator operator) {
        ASSERT(leftHandSide != null);
        ASSERT(rightHandSide != null);
        ASSERT(operator != null);
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
        this.operator = operator;
    }

    @Override
    public Value execute() {
        // AssignmentExpression : LeftHandSideExpression = AssignmentExpression
        Reference reference = leftHandSide.getReference();
        Value value = rightHandSide.execute();
        if (operator == AssignmentExpressionOperator.EQUALS) {
            Value base = reference.getValue(getRealm());
            if (base.isObject()) {
                JsObject object = base.asObject();
                object.set(reference.getReferencedName(), value, true);
            } else {
                reference.putValue(getRealm(), value);
            }
            return value;
        } else {
            throw new UnsupportedOperationException("Not implemented.");
        }
    }

    @Override
    public void setRuntime(Runtime runtime) {
        super.setRuntime(runtime);
        leftHandSide.setRuntime(runtime);
        rightHandSide.setRuntime(runtime);
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
