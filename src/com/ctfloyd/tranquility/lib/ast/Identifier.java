package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.StringJoiner;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class Identifier extends AstNode {

    private final String name;

    public Identifier(String name) {
        ASSERT(name != null);
        ASSERT(!name.isEmpty());
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void dump(int indent) {
        System.out.println("Identifier: " + name);
    }

    @Override
    public Value interpret(AstInterpreter interpreter) throws Exception {
        return interpreter.getIdentifier(name);
    }

    @Override
    public boolean isIdentifier() {
        return true;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Identifier.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .toString();
    }
}
