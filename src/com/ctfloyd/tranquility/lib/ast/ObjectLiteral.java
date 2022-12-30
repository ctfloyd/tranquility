package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.runtime.JsObject;
import com.ctfloyd.tranquility.lib.runtime.Value;

import java.util.Collections;
import java.util.Map;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class ObjectLiteral extends Expression {

    private final Map<Identifier, AstNode> properties;

    public ObjectLiteral(Map<Identifier, AstNode> properties) {
        ASSERT(properties != null);
        this.properties = properties;
    }

    @Override
    public Value execute() {
        JsObject object = JsObject.create(getRealm(), Collections.emptyMap());
        for (Map.Entry<Identifier, AstNode> entry : properties.entrySet()) {
            object.set(entry.getKey().getName(), entry.getValue().execute(), true);
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

    public boolean isObjectLiteral() {
        return true;
    }
}
