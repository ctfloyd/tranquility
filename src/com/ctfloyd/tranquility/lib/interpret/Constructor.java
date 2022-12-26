package com.ctfloyd.tranquility.lib.interpret;

import java.util.List;

public abstract class Constructor extends Function {

    public Constructor(String name, List<String> argumentNames) {
        super(name, argumentNames, null);
    }

    public abstract JsObject construct(AstInterpreter interpreter, ArgumentList arguments, JsObject object);

    @Override
    public boolean isConstructor() {
        return true;
    }
}
