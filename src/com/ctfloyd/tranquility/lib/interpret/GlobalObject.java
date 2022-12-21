package com.ctfloyd.tranquility.lib.interpret;

public class GlobalObject extends JsObject {

    public GlobalObject() {
        put("console", Value.object(new ConsoleObject()));
    }

}
