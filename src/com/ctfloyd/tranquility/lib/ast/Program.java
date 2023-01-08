package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.Value;

import java.util.ArrayList;
import java.util.List;

public class Program extends Scope {

    private final List<AstNode> statements;

    public Program() {
        statements = new ArrayList<>();
    }

    public void addStatement(AstNode statement) {
        statements.add(statement);
    }

    @Override
    public Value execute() {
        Value lastValue = Value.undefined();
        for (AstNode child : statements) {
            if (child.isFunctionDeclaration()) {
                continue;
            }

            lastValue = child.execute();
        }
        return lastValue;
    }

    @Override
    public void setRuntime(Runtime runtime) {
        super.setRuntime(runtime);
        for (AstNode child : statements) {
            child.setRuntime(runtime);
        }
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("Program (");
        statements.forEach(child -> child.dump(indent + 1));
        printIndent(indent);
        System.out.println(")");
    }


}
