package com.ctfloyd.tranquility.lib.runtime;

public class GlobalObject extends JsObject {

    // 19.1 Value Properties of the Global Object
    public GlobalObject() {
        // TODO: 19.1.1, 19.1.2, 19.1.3
        set("console", Value.object(new ConsoleObject()), true);
        // 19.1.4
        set("undefined", Value.undefined(), true);
    }

}
