package com.ctfloyd.tranquility.lib.interpret;

import java.util.*;

public class AstInterpreter {

    private final Map<BuiltinPrototype, JsObject> prototypes;
    private final Deque<Scope> scopes;
    private final Stack<Value> thisStack;
    private final GlobalObject globalObject;

    public AstInterpreter() {
        globalObject = new GlobalObject();
        prototypes = new HashMap<>();
        prototypes.put(BuiltinPrototype.STRING, new StringPrototype());
        prototypes.put(BuiltinPrototype.ARRAY, new ArrayPrototype());
        prototypes.put(BuiltinPrototype.OBJECT, new ObjectPrototype());
        prototypes.put(BuiltinPrototype.NUMBER, new NumberPrototype());
        scopes = new ArrayDeque<>();
        thisStack = new Stack<>();
    }

    public JsObject getBuiltinPrototype(BuiltinPrototype prototype) {
        return prototypes.getOrDefault(prototype, null);
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

    public Value getIdentifier(AstInterpreter interpreter, String identifier)  {
        Optional<Scope> identifierScope = getIdentifierScope(identifier);
        if (identifierScope.isPresent()) {
            return identifierScope.get().get(identifier);
        } else if (!globalObject.get(interpreter, identifier).isUndefined()){
            return globalObject.get(interpreter, identifier);
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

    public void setIdentifier(AstInterpreter interpreter, String identifier, Optional<Value> valueOptional) {
        Optional<Scope> identifierScope = getIdentifierScope(identifier);
        Value value = valueOptional.orElseGet(Value::undefined);
        if (identifierScope.isPresent()) {
            identifierScope.get().put(identifier, value);
        } else {
            if (!globalObject.get(interpreter, identifier).isUndefined()) {
                globalObject.set(identifier, value, true);
            } else {
                Optional<Scope> currentScope = getCurrentScope();
                if (currentScope.isPresent()) {
                    currentScope.get().put(identifier, value);
                } else {
                    globalObject.set(identifier, value, true);
                }
            }
        }
    }

    public Optional<Scope> getCurrentScope() {
        return Optional.ofNullable(scopes.peekLast());
    }
}
