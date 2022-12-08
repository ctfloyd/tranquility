package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.ArrayList;
import java.util.List;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class BlockStatement extends AstNode {

    private final List<AstNode> children = new ArrayList<>();

    public void addChild(AstNode child) {
        ASSERT(child != null);
        children.add(child);
    }

    @Override
    public Value interpret(AstInterpreter interpreter) throws Exception {
        Value lastValue = Value.undefined();
        for (AstNode child : children) {
            lastValue = child.interpret(interpreter);
        }
        return lastValue;
    }
}
