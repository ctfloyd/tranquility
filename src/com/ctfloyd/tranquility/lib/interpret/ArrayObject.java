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
        arrayObject.setPrototypeOf(interpreter.getBuiltinPrototype(BuiltinPrototype.ARRAY));
        arrayObject.enumerateProperties();
        return arrayObject;
    }

    public Value getValueAtIndex(int index) {
        return array.get(index);
    }

    public void add(Value value) {
        array.add(value);
        enumerateProperties();
    }

    public int length() {
        return array.size();
    }

    public boolean isArray() {
        return true;
    }

    private void enumerateProperties() {
        for (int i = 0; i < length(); i++) {
            set("" + i, getValueAtIndex(i), true);
        }
        set("length", Value.number(length()), true);
    }
}
