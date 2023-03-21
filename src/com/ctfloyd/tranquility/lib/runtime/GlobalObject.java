package com.ctfloyd.tranquility.lib.runtime;

// https://tc39.es/ecma262/#sec-global-object
public class GlobalObject extends JsObject {

    // 19.1 Value Properties of the Global Object
    private GlobalObject() {

    }

    public static GlobalObject create(Realm realm) {
        GlobalObject global = new GlobalObject();
        global.setPrototypeOf(new GlobalPrototype(realm));
        return global;
    }

}
