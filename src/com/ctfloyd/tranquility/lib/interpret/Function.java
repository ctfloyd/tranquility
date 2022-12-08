package com.ctfloyd.tranquility.lib.interpret;

import com.ctfloyd.tranquility.lib.ast.BlockStatement;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class Function extends JsObject {

    private final String name;
    private final BlockStatement body;

    public Function(String name, BlockStatement body) {
        super();
        ASSERT(name != null);
        ASSERT(!name.isEmpty());
        ASSERT(body != null);
        this.name = name;
        this.body = body;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    public BlockStatement getBody() {
       return body;
    }
}
