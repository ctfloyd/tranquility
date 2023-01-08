package com.ctfloyd.tranquility.lib.runtime;

import java.util.Stack;

public class Runtime {

    private final Stack<ExecutionContext> executionContextStack;
    private final Realm realm;

    public Runtime() {
        executionContextStack = new Stack<>();
        realm = Realm.initializeHostDefinedRealm(executionContextStack);
    }

    public ExecutionContext getCurrentExecutionContext() {
        return executionContextStack.peek();
    }

    public Value getThisValue() {
        return getCurrentExecutionContext().getThisValue();
    }

    public void setThisValue(Value value) {
        getCurrentExecutionContext().setThisValue(value);
    }

    public Realm getRealm() {
        return realm;
    }

    public Reference resolveBinding(String name)  {
        return resolveBinding(name, null);
    }

    public Reference resolveBinding(String name, Environment environment)  {
        if (environment == null) {
            environment = getCurrentExecutionContext().getLexicalEnvironment();
        }
        // TODO: strict mode determination
        return environment.getIdentifierReference(name, false);
    }

    public void pushExecutionContext(ExecutionContext executionContext) {
        executionContextStack.push(executionContext);
    }

    public ExecutionContext popExecutionContext() {
        return executionContextStack.pop();
    }
}
