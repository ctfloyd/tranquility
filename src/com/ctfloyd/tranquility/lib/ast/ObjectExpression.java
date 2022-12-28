package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.JsObject;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.Collections;
import java.util.Map;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class ObjectExpression extends AstNode {

    private final Map<Identifier, AstNode> properties;

    public ObjectExpression(Map<Identifier, AstNode> properties) {
        ASSERT(properties != null);
        this.properties = properties;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        JsObject object = JsObject.create(interpreter, Collections.emptyMap());
        for (Map.Entry<Identifier, AstNode> entry : properties.entrySet()) {
            object.set(entry.getKey().getName(), entry.getValue().interpret(interpreter), true);
        }
        return Value.object(object);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("ObjectExpression (");
        for (Map.Entry<Identifier, AstNode> entry : properties.entrySet()) {
            entry.getKey().dump(indent + 1);
            entry.getValue().dump(indent + 1);
        }
        printIndent(indent);
        System.out.println(")");
    }
}
