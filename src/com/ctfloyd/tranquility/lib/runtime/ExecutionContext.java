package com.ctfloyd.tranquility.lib.runtime;


// https://tc39.es/ecma262/#sec-execution-contexts
public class ExecutionContext {

    private Environment lexicalEnvironment;
    private Environment variableEnvironment;
    private Realm realm;
    private Function function;

    private Value thisValue = Value.undefined();

    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public Environment getLexicalEnvironment() {
        return lexicalEnvironment;
    }

    public void setLexicalEnvironment(Environment environment) {
        this.lexicalEnvironment = environment;
    }

    public Environment getVariableEnvironment() {
        return variableEnvironment;
    }

    public void setVariableEnvironment(Environment variableEnvironment) {
        this.variableEnvironment = variableEnvironment;
    }

    public Value getThisValue() {
        return thisValue;
    }

    public void setThisValue(Value thisValue) {
        if (thisValue == null) {
            throw new RuntimeException("Tried to set this value to Java null.");
        }
        this.thisValue = thisValue;
    }

}
