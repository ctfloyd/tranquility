package com.ctfloyd.tranquility.lib.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class Scope {

    private final Map<String, Value> declarations;

    public Scope() {
        this.declarations = new HashMap<>();
    }

    public void put(String identifierName, Value value) {
        declarations.put(identifierName, value);
    }

    public Value get(String identifierName) {
        return declarations.get(identifierName);
    }

    public boolean has(String identifierName)  {
        return declarations.containsKey(identifierName);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Scope.class.getSimpleName() + "[", "]")
                .add("declarations=" + declarations)
                .toString();
    }
}
