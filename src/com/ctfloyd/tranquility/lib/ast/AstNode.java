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

    public boolean isIdentifier() {
        return false;
    }

}
