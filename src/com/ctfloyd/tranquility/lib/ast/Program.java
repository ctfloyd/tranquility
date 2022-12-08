package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.JsValue;

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
    public JsValue interpret(AstInterpreter interpreter) throws Exception {
        JsValue lastValue = JsValue.undefined();
        for (AstNode child : children) {
            lastValue = child.interpret(interpreter);
        }
        return lastValue;
    }
}
