package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.*;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class MemberExpression extends AstNode {

    private final AstNode object;
    private final Identifier property;
    private final boolean computed;

    public MemberExpression(AstNode object, Identifier property, boolean computed) {
        ASSERT(object != null);
        ASSERT(property != null);
        this.object = object;
        this.property = property;
        this.computed = computed;
    }

    public AstNode getObject() {
        return object;
    }

    private boolean isComputed() {
        return computed;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        Value unknownValue = object.interpret(interpreter).toObject(interpreter);
        ASSERT(unknownValue.isObject(), "Left hand side of a member expression should be an object.");
        JsObject object = unknownValue.asObject();

        Value propertyName;
        if (isComputed()) {
            propertyName = property.interpret(interpreter);
        } else {
            propertyName = Value.string(property.getName());
        }
        return object.get(propertyName.asString());
    }


    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("MemberExpression (");
        object.dump(indent + 1);
        property.dump(indent + 1);
        printIndent(indent);
        System.out.println(")");
    }

    @Override
    public boolean isMemberExpression() {
        return true;
    }
}
