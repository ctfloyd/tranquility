package com.ctfloyd.tranquility.lib.interpret;

import java.util.HashMap;
import java.util.Map;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class JsObject {

    private final Map<String, JsValue> properties = new HashMap<>();

    public JsValue get(String propertyName) {
        return properties.getOrDefault(propertyName, JsValue.undefined());
    }

    public void put(String propertyName, JsValue value) {
        ASSERT(value != null);
        ASSERT(propertyName != null);
        ASSERT(!propertyName.isBlank());
        properties.put(propertyName, value);
    }

    public boolean isFunction() {
        return false;
    }

}
