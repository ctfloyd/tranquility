package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.Value;

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
    public Value execute() {
        Value lastValue = Value.undefined();
        for (AstNode child : children) {
            lastValue = child.execute();
        }
        return lastValue;
    }

    @Override
    public void setRuntime(Runtime runtime) {
        super.setRuntime(runtime);
        for (AstNode child : children) {
            child.setRuntime(runtime);
        }
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
