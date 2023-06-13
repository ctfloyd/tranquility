package com.ctfloyd.tranquility.lib.parse.ast;

import java.util.Collections;
import java.util.List;

public class BindingIdentifier extends Expression implements BoundName {

    private final String stringValue;

    public BindingIdentifier(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public List<String> getBoundNames() {
        return Collections.singletonList(stringValue);
    }
}
