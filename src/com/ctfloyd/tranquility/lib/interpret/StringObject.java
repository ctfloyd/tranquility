package com.ctfloyd.tranquility.lib.interpret;

public class StringObject extends JsObject {

    private final String string;

    private StringObject(String string) {
        this.string = string;

        // https://tc39.es/ecma262/#sec-properties-of-string-instances-length
        put("length", Value.number(this.string.length()));
    }

    public static StringObject create(AstInterpreter interpreter, String string)  {
        StringObject stringObject = new StringObject(string);
        stringObject.setPrototypeOf(interpreter.getBuiltinPrototype(BuiltinPrototype.STRING));
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
