package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

public class AstNode {

    public Value interpret(AstInterpreter interpreter) throws Exception {
        throw new Exception("Not implemented");
    }

    public void dump(int indent) {
        System.out.println("GENERIC AST NODE DUMP");
    }

    protected void printIndent(int indent) {
        for(int i = 0; i < indent * 3; i++) {
            System.out.print(" ");
        }
    }

    public boolean isIdentifier() {
        return false;
    }

}
