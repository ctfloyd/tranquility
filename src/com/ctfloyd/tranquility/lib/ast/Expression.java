package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.runtime.Reference;

public abstract class Expression extends AstNode {

    public Reference getReference() {
        throw new RuntimeException("GetReference is not implemented for this node.");
    }

    @Override
    public boolean isExpression() {
        return true;
    }

}
