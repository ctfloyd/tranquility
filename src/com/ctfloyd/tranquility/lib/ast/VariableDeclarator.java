package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.Optional;
import java.util.StringJoiner;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class VariableDeclarator extends AstNode {

    private final String name;
    private final AstNode value;

    public VariableDeclarator(String name) {
        ASSERT(name != null);
        ASSERT(!name.isEmpty());
        this.name = name;
        this.value = null;
    }

    public VariableDeclarator(String name, AstNode value) {
        ASSERT(name != null);
        ASSERT(!name.isEmpty());
        ASSERT(value != null);
        this.name = name;
        this.value = value;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) throws Exception {
        Value variableValue = Value.undefined();
        if (value != null) {
            variableValue = value.interpret(interpreter);
        }

        interpreter.setIdentifier(name, Optional.of(variableValue));
        return Value.undefined();
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("VariableDeclarator (");
        printIndent(indent + 1);
        System.out.println("[Name] (" + name + ")");
        printIndent(indent + 1);
        System.out.println("[Value] {");
        value.dump(indent + 1);
        printIndent(indent + 1);
        System.out.println("}");
        printIndent(indent);
        System.out.println(")");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VariableDeclarator.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("value=" + value)
                .toString();
    }
}
