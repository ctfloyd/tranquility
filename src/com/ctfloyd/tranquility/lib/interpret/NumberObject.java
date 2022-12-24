package com.ctfloyd.tranquility.lib.interpret;

public class NumberObject extends JsObject {

    private final double value;

    private NumberObject(double value) {
        this.value = value;
    }

    public static NumberObject create(AstInterpreter interpreter, double _double) {
        NumberObject number = new NumberObject(_double);
        number.setPrototypeOf(interpreter.getBuiltinPrototype(BuiltinPrototype.NUMBER));
        return number;
    }
}
