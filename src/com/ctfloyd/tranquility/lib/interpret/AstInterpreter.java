package com.ctfloyd.tranquility.lib.interpret;

import com.ctfloyd.tranquility.lib.ast.BlockStatement;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Optional;

public class AstInterpreter {

    private final Deque<Scope> scopes;

    public AstInterpreter() {
        scopes = new ArrayDeque<>();
        scopes.add(new GlobalScope());
    }

    public void enterScope() {
        scopes.push(new Scope());
    }

    public void leaveScope() {
        scopes.pop();
    }

    public Value getIdentifier(String identifier)  {
        Iterator<Scope> backwards = scopes.descendingIterator();
        while (backwards.hasNext()) {
            Scope scope = backwards.next();
            if (scope.has(identifier)) {
                return scope.get(identifier);
            }
        }
        return Value.undefined();
    }

    public void setIdentifier(String identifier) {
        setIdentifier(identifier, Optional.empty());
    }

    public void setIdentifier(String identifier, Optional<Value> value) {
        Scope scope = getCurrentScope();
        if (value.isPresent()) {
            scope.put(identifier, value.get());
        } else {
            scope.put(identifier, Value.undefined());
        }
    }

    public Scope getCurrentScope() {
        return scopes.peek();
    }
}
