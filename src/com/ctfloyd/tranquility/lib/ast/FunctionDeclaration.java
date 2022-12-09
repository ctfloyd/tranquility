package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Function;
import com.ctfloyd.tranquility.lib.interpret.GlobalObject;
import com.ctfloyd.tranquility.lib.interpret.Value;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class FunctionDeclaration extends AstNode {

    private final String name;
    private final BlockStatement blockStatement;

    public FunctionDeclaration(String name, BlockStatement blockStatement) {
        ASSERT(name != null);
        ASSERT(!name.isEmpty());
        ASSERT(blockStatement != null);
        this.name = name;
        this.blockStatement = blockStatement;
    }

    @Override
    public void dump(int indent) {
        System.out.println("FUNCTION DECLARATION {");
        System.out.println("NAME: " + name);
        blockStatement.dump(0);
        System.out.println("}");
    }

    @Override
    public Value interpret(AstInterpreter interpreter) throws Exception {
        GlobalObject globalObject = interpreter.getGlobalObject();
        Value value = Value.object(new Function(name, blockStatement));
        globalObject.put(name, value);
        return value;
    }
}
