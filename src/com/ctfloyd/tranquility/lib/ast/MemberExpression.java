package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.JsObject;
import com.ctfloyd.tranquility.lib.interpret.Value;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class MemberExpression extends AstNode {

    private final AstNode object;
    private final Identifier property;

    public MemberExpression(AstNode object, Identifier property) {
        ASSERT(object != null);
        ASSERT(property != null);
        this.object = object;
        this.property = property;
    }

    public AstNode getObject() {
        return object;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        Value unknownValue = object.interpret(interpreter);
        ASSERT(unknownValue.isObject());
        JsObject object = unknownValue.asObject();
        Value propertyName = Value.string(property.getName());
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
