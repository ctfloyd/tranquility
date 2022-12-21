package com.ctfloyd.tranquility.lib.interpret;

public class StringObject extends JsObject {

    private final String string;

    private StringObject(String string) {
        this.string = string;
    }

    public static StringObject create(AstInterpreter interpreter, String string)  {
        StringObject stringObject = new StringObject(string);
        stringObject.setPrototype(interpreter.getBuiltinPrototype("String"));
        return stringObject;
    }

    public String getString() {
        return string;
    }

    @Override
    public boolean isStringObject() {
        return true;
    }

}
