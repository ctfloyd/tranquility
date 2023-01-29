package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.runtime.Reference;
import com.ctfloyd.tranquility.lib.runtime.Value;

import java.util.StringJoiner;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class Identifier extends BindingIdentifier {

    public Identifier(String name) {
        super(name);
        ASSERT(name != null);
        ASSERT(!name.isEmpty());
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("Identifier (Name:  " + getStringValue() + ")");
    }

    @Override
    public Value execute() {
        throw new RuntimeException("NO");
    }

    public Reference getReference() {
        return getRuntime().resolveBinding(getStringValue());
    }

    @Override
    public boolean isIdentifier() {
        return true;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Identifier.class.getSimpleName() + "[", "]")
                .add("name='" + getStringValue() + "'")
                .toString();
    }
}
