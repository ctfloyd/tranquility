package com.ctfloyd.tranquility.lib.parse.ast;

import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.Value;

import java.util.ArrayList;
import java.util.List;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class BlockStatement extends Scope {

    private final List<AstNode> children = new ArrayList<>();

    public void addChild(AstNode child) {
        ASSERT(child != null);
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
        System.out.println("BlockStatement (");
        children.forEach(child -> child.dump(indent + 1));
        printIndent(indent);
        System.out.println(")");
    }
}
