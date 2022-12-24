package com.ctfloyd.tranquility.lib.interpret;

import com.ctfloyd.tranquility.lib.ast.BlockStatement;

import java.util.List;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class Function extends JsObject {

    private final String name;
    private List<String> argumentNames;
    private final BlockStatement body;

    public Function(String name, List<String> argumentNames, BlockStatement body) {
        super();
        ASSERT(name != null);
        ASSERT(!name.isEmpty());
        ASSERT(argumentNames != null);
        this.name = name;
        this.body = body;
        this.argumentNames = argumentNames;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    public BlockStatement getBody() {
       return body;
    }

    public int getNumberOfArguments() {
        return argumentNames.size();
    }

    public String getArgumentNameAt(int i) {
        return argumentNames.get(i);
    }

    public Value call(AstInterpreter interpreter) {
        return body.interpret(interpreter);
    }
}
