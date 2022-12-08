package com.ctfloyd.tranquility.lib.interpret;

import java.util.HashMap;
import java.util.Map;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class JsObject {

    private final Map<String, Value> properties = new HashMap<>();

    public Value get(String propertyName) {
        return properties.getOrDefault(propertyName, Value.undefined());
    }

    public void put(String propertyName, Value value) {
        ASSERT(value != null);
        ASSERT(propertyName != null);
        ASSERT(!propertyName.isBlank());
        properties.put(propertyName, value);
    }

    public boolean isFunction() {
        return false;
    }

}
