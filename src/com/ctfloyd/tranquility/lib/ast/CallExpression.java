package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Function;
import com.ctfloyd.tranquility.lib.interpret.JsObject;
import com.ctfloyd.tranquility.lib.interpret.JsValue;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class CallExpression extends AstNode {

    private final Identifier callee;

    public CallExpression(Identifier callee) {
        ASSERT(callee != null);
        this.callee = callee;
    }

    @Override
    public JsValue interpret(AstInterpreter interpreter) throws Exception {
        JsValue value = interpreter.getIdentifier(callee.getName());
        ASSERT(value.isObject());
        JsObject object = value.asObject();
        ASSERT(object.isFunction());
        return ((Function)object).getBody().interpret(interpreter);
    }
}
