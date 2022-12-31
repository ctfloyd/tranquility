package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.runtime.Reference;
import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.Value;

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

    // https://tc39.es/ecma262/#sec-variable-statement-runtime-semantics-evaluation
    @Override
    public Value execute() {
        Reference leftHandSide = getRuntime().resolveBinding(name);
        Value rightHandSide = value.execute();
        leftHandSide.putValue(getRealm(), rightHandSide);
        return Value.undefined();
    }

    @Override
    public void setRuntime(Runtime runtime) {
        super.setRuntime(runtime);
        value.setRuntime(runtime);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("VariableDeclarator (");
        printIndent(indent + 1);
        System.out.println("[Name] (" + name + ")");
        printIndent(indent + 1);
        System.out.println("[Value] {");
        if (value != null) {
            value.dump(indent + 1);
        } else {
            printIndent(indent + 1);
            System.out.println("undefined");
        }
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
