package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.JsValue;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class ReturnStatement extends AstNode {

    private AstNode argument;

    public ReturnStatement(AstNode argument) {
        ASSERT(argument != null);
        this.argument = argument;
    }

    @Override
    public JsValue interpret(AstInterpreter interpreter) throws Exception {
        return argument.interpret(interpreter);
    }
}
