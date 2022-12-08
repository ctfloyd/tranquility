package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.JsValue;

import java.util.StringJoiner;

public class NumericLiteral extends AstNode {

    private final double value;

    public NumericLiteral(double value) {
        this.value = value;
    }

    @Override
    public JsValue interpret(AstInterpreter interpreter) {
        return JsValue.number(value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NumericLiteral.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .toString();
    }
}
