package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.ArrayList;
import java.util.List;

public class Program extends AstNode {

    private final List<AstNode> children;

    public Program() {
        children = new ArrayList<>();
    }

    public List<AstNode> getChildren() {
        return children;
    }

    public void addChild(AstNode child) {
        children.add(child);
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        Value lastValue = Value.undefined();
        for (AstNode child : children) {
            lastValue = child.interpret(interpreter);
        }
        return lastValue;
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("Program (");
        children.forEach(child -> child.dump(indent + 1));
        printIndent(indent);
        System.out.println(")");
    }
}
