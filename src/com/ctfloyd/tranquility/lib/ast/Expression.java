package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Reference;

public abstract class Expression extends AstNode {

    public Reference getReference(AstInterpreter interpreter) {
        throw new RuntimeException("GetReference is not implemented for this node.");
    }

    @Override
    public boolean isExpression() {
        return true;
    }

}
