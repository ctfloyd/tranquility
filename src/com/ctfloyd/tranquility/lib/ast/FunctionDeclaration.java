package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Function;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.List;
import java.util.Optional;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class FunctionDeclaration extends AstNode {

    private final String name;
    private final List<String> arguments;
    private final BlockStatement blockStatement;

    public FunctionDeclaration(String name, List<String> arguments, BlockStatement blockStatement) {
        ASSERT(name != null);
        ASSERT(!name.isEmpty());
        ASSERT(blockStatement != null);
        ASSERT(arguments != null);
        this.name = name;
        this.blockStatement = blockStatement;
        this.arguments = arguments;
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
        Value value = Value.object(new Function(name, arguments, blockStatement));
        interpreter.setIdentifier(name, Optional.of(value));
        return value;
    }
}
