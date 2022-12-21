package com.ctfloyd.tranquility.lib.interpret;

import java.util.*;

public class AstInterpreter {

    private final Map<String, JsObject> prototypes;
    private final Deque<Scope> scopes;
    private final Stack<Value> thisStack;
    private final GlobalObject globalObject;

    public AstInterpreter() {
        globalObject = new GlobalObject();
        prototypes = new HashMap<>();
        prototypes.put("String", new StringPrototype());
        prototypes.put("Array", new ArrayPrototype());
        scopes = new ArrayDeque<>();
        thisStack = new Stack<>();
    }

    public JsObject getBuiltinPrototype(String builtin) {
        return prototypes.getOrDefault(builtin, null);
    }

    public void pushThisValue(Value value) {
        thisStack.add(value);
    }

    public void popThisValue() {
        thisStack.pop();
    }

    public Value getThisValue() {
        if (thisStack.isEmpty()) {
            return Value.object(globalObject);
        }

        return thisStack.peek();
    }

    public void enterScope() {
        scopes.push(new Scope());
    }

    public void leaveScope() {
        scopes.pop();
    }

    public Value getIdentifier(String identifier)  {
        Optional<Scope> identifierScope = getIdentifierScope(identifier);
        if (identifierScope.isPresent()) {
            return identifierScope.get().get(identifier);
        } else if (!globalObject.get(identifier).isUndefined()){
            return globalObject.get(identifier);
        }
        return Value.undefined();
    }

    public Optional<Scope> getIdentifierScope(String identifier) {
        Iterator<Scope> backwards = scopes.descendingIterator();
        while (backwards.hasNext()) {
            Scope scope = backwards.next();
            if (scope.has(identifier)) {
                return Optional.of(scope);
            }
        }

        return Optional.empty();
    }

    public boolean hasIdentifier(String identifier) {
        Iterator<Scope> backwards = scopes.descendingIterator();
        while (backwards.hasNext()) {
            Scope scope = backwards.next();
            if (scope.has(identifier)) {
                return true;
            }
        }
        return false;
    }

    public void setIdentifier(String identifier, Optional<Value> valueOptional) {
        Optional<Scope> scope = getIdentifierScope(identifier);
        Value value = valueOptional.orElseGet(Value::undefined);
        if (scope.isPresent()) {
            scope.get().put(identifier, value);
        } else {
            globalObject.put(identifier, value);
        }
    }

    public Scope getCurrentScope() {
        return scopes.peekLast();
    }
}
