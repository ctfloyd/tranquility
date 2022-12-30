package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.runtime.Function;
import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.Value;

import java.util.List;

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
        printIndent(indent);
        System.out.println("FunctionDeclaration (");
        printIndent(indent + 1);
        System.out.println("Name: " + name);
        blockStatement.dump(indent + 1);
        printIndent(indent);
        System.out.println(")");
    }

    @Override
    public Value execute() {
        Function f = new Function(name, arguments, blockStatement);
        f.setRuntime(getRuntime());
        Value value = Value.object(f);
        getRuntime().getCurrentExecutionContext().getLexicalEnvironment().initializeBinding(name, value);
        return value;
    }

    @Override
    public void setRuntime(Runtime runtime) {
        super.setRuntime(runtime);
        blockStatement.setRuntime(runtime);
    }
}
