package com.ctfloyd.tranquility.lib.interpret;

import java.util.Map;

// https://tc39.es/ecma262/#sec-the-environment-record-type-hierarchy
public abstract class Environment {

    protected Map<String, Binding> bindings;
    protected Environment outerEnvironment;

    // https://tc39.es/ecma262/#table-abstract-methods-of-environment-records
    public abstract boolean hasBinding(String bindingName);
    public abstract void createMutableBinding(String bindingName, boolean canDelete);
    public abstract void createImmutableBinding(String bindingName, boolean strictBinding);
    public abstract void initializeBinding(String bindingName, Value value);
    public abstract void setMutableBinding(String bindingName, Value value, boolean shouldThrowExceptions);
    public abstract Value getBindingValue(String bindingName, boolean shouldThrowReferenceErrorIfBindingDoesNotExist);
    public abstract boolean deleteBinding(String bindingName);
    public abstract boolean hasThisBinding();
    public abstract boolean hasSuperBinding();
    public abstract Value withBaseObject();

    public void setOuterEnvironment(Environment outerEnvironment) {
        this.outerEnvironment = outerEnvironment;
    }

    public Environment getOuterEnvironment() {
        return outerEnvironment;
    }
}
