package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.JsObject;
import com.ctfloyd.tranquility.lib.interpret.Value;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class MemberExpression extends AstNode {

    private final AstNode object;
    private final AstNode property;

    public MemberExpression(AstNode object, AstNode property) {
        ASSERT(object != null);
        ASSERT(property != null);
        this.object = object;
        this.property = property;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) throws Exception {
        Value unknownValue = object.interpret(interpreter);
        ASSERT(unknownValue.isObject());
        JsObject object = unknownValue.asObject();
        Value propertyName = property.interpret(interpreter);
        ASSERT(propertyName.isString());
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
}
