package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.runtime.RuntimeDependency;
import com.ctfloyd.tranquility.lib.runtime.Value;

public class AstNode extends RuntimeDependency {

    public Value execute() throws RuntimeException {
        throw new RuntimeException("Not implemented");
    }

    public void dump(int indent) {
        System.out.println("GENERIC AST NODE DUMP");
    }

    protected void printIndent(int indent) {
        for(int i = 0; i < indent * 3; i++) {
            System.out.print(" ");
        }
    }

    public boolean isMemberExpression() { return false; }
    public boolean isIdentifier() {
        return false;
    }
    public boolean isExpression() { return false; }
    public boolean isObjectLiteral() { return false; }
    public boolean isScopeNode() { return false; }
    public boolean isFunctionDeclaration() { return false; }
}
