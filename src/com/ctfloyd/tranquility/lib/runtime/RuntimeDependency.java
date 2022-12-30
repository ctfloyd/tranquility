package com.ctfloyd.tranquility.lib.runtime;

public class RuntimeDependency {

    private Runtime runtime;

    public Runtime getRuntime() {
        return runtime;
    }

    public void setRuntime(Runtime runtime) {
        this.runtime = runtime;
    }

    public Realm getRealm() {
        return runtime.getRealm();
    }
}
