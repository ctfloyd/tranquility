package com.ctfloyd.tranquility.lib.interpret;

import java.util.StringJoiner;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class NumberObject extends JsObject {

    private final double value;
    private final boolean positiveInfinity;
    private final boolean negativeInfinity;

    private NumberObject(double value) {
        this.value = value;
        this.positiveInfinity = false;
        this.negativeInfinity = false;
    }

    private NumberObject(boolean positiveInfinity, boolean negativeInfinity) {
        ASSERT(positiveInfinity != negativeInfinity);
        this.value = 0;
        this.positiveInfinity = positiveInfinity;
        this.negativeInfinity = negativeInfinity;
    }

    public static NumberObject create(AstInterpreter interpreter, double _double) {
        NumberObject number = new NumberObject(_double);
        number.setPrototypeOf(interpreter.getBuiltinPrototype(BuiltinPrototype.NUMBER));
        return number;
    }

    public static NumberObject createPositiveInfinity(AstInterpreter interpreter) {
        NumberObject number = new NumberObject(true, false);
        number.setPrototypeOf(interpreter.getBuiltinPrototype(BuiltinPrototype.NUMBER));
        return number;
    }

    public static NumberObject createNegativeInfinity(AstInterpreter interpreter) {
        NumberObject number = new NumberObject(false, true);
        number.setPrototypeOf(interpreter.getBuiltinPrototype(BuiltinPrototype.NUMBER));
        return number;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean isNumberObject() {
        return true;
    }

    public boolean isInfinite() {
        return positiveInfinity || negativeInfinity;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NumberObject.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .add("positiveInfinity=" + positiveInfinity)
                .add("negativeInfinity=" + negativeInfinity)
                .toString();
    }
}
