package com.ctfloyd.tranquility.lib.ast;

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
}
