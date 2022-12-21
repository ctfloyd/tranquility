package com.ctfloyd.tranquility.lib.interpret;

import java.util.ArrayList;
import java.util.List;

public class ArrayObject extends JsObject {

    private final List<Value> array;

    private ArrayObject(List<Value> values) {
        array = new ArrayList<>(values);
    }

    public static ArrayObject create(AstInterpreter interpreter, List<Value> values) {
        ArrayObject arrayObject = new ArrayObject(values);
        arrayObject.setPrototype(interpreter.getBuiltinPrototype("Array"));
        return arrayObject;
    }

    public void add(Value value) {
        array.add(value);
    }

    public int length() {
        return array.size();
    }

    public boolean isArray() {
        return true;
    }
}
