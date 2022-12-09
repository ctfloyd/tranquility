package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Function;
import com.ctfloyd.tranquility.lib.interpret.JsObject;
import com.ctfloyd.tranquility.lib.interpret.Value;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class CallExpression extends AstNode {

    private final Identifier callee;

    public CallExpression(Identifier callee) {
        ASSERT(callee != null);
        this.callee = callee;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) throws Exception {
        Value value = interpreter.getIdentifier(callee.getName());
        ASSERT(value.isObject());
        JsObject object = value.asObject();
        ASSERT(object.isFunction());
        return ((Function)object).getBody().interpret(interpreter);
    }

    @Override
    public void dump(int indent) {
        System.out.println("CALL: ");
        callee.dump(indent);
    }
}
