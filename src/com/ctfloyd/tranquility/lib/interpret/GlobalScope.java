package com.ctfloyd.tranquility.lib.interpret;

public class GlobalScope extends Scope {

    public GlobalScope() {
        put("console", Value.object(new ConsoleObject()));
    }

}
