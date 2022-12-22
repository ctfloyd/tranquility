package com.ctfloyd.tranquility.lib.interpret;

public class GlobalObject extends JsObject {

    // 19.1 Value Properties of the Global Object
    public GlobalObject() {
        // TODO: 19.1.1, 19.1.2, 19.1.3
        put("console", Value.object(new ConsoleObject()));
        // 19.1.4
        put("undefined", Value.undefined());
    }

}
