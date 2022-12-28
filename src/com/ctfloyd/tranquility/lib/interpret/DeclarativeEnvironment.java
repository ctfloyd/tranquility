package com.ctfloyd.tranquility.lib.interpret;

import java.util.HashMap;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

/**
 * https://tc39.es/ecma262/#sec-declarative-environment-records
 * Each Declarative Environment Record is associated with an ECMAScript program scope containing variable, constant, let,
 * class, module, import, and/or function declarations. A Declarative Environment Record binds the set of identifiers
 * defined by the declarations contained within its scope.
 */
public class DeclarativeEnvironment extends Environment {

    // https://tc39.es/ecma262/#sec-newdeclarativeenvironment
    public DeclarativeEnvironment(Environment outerEnvironment) {
        this.bindings = new HashMap<>();
        this.outerEnvironment = outerEnvironment;
    }

    @Override
    public boolean hasBinding(String bindingName) {
        // 1. If envRec has a binding for the name that is the value of N, return true.
        // 2. Return false.
        return bindings.containsKey(bindingName);
    }

    @Override
    public void createMutableBinding(String bindingName, boolean canDelete) {
        // 1. Assert: envRec does not already have a binding for N.
        ASSERT(!hasBinding(bindingName));
        // 2. Create a mutable binding in envRec for N and record that it is uninitialized. If D is true, record
        // that the newly created binding may be deleted by a subsequent DeleteBinding call.
        bindings.put(bindingName, new Binding(null, true, canDelete, false));
        // 3. Return unused
    }

    @Override
    public void createImmutableBinding(String bindingName, boolean strictBinding) {
        // 1. Assert: envRec does not already have a binding for N.
        ASSERT(!hasBinding(bindingName));
        // 2. Create an immutable binding in envRec for N and record that it is uninitialized. If S is true, record that the newly
        // created binding is a strictBinding
        bindings.put(bindingName, new Binding(null, false, false, strictBinding));
        // 3. Return unused
    }

    @Override
    public void initializeBinding(String bindingName, Value value) {
        // 1. Assert: envRec must have an uninitialized binding for N.
        ASSERT(hasBinding(bindingName) && bindings.get(bindingName).isUninitialized());
        ASSERT(value != null);
        // 2. Set the bound value for N in envRec to V.
        bindings.get(bindingName).setValue(value);
        // 3. Record that the binding for N in envRec has been initialized.
            // NOTE: This is implied by setting the value
        // 4. Return unused
    }

    @Override
    public void setMutableBinding(String bindingName, Value value, boolean shouldThrowExceptions) {
        // 1. If envRec does not have a binding for N, then
        if (!hasBinding(bindingName)) {
            // a. If S is true, throw a ReferenceError exception
            if (shouldThrowExceptions) {
                // TODO: Make it throw a ReferenceError
                throw new RuntimeException("TODO: Should throw ReferenceError exception.");
            }
            // b. Perform !envRec.CreateMutableBinding(N, true).
            createMutableBinding(bindingName, true);
            // c. Perform !envRec.InitializeBinding(N, V).
            initializeBinding(bindingName, value);
            // d. Return unused.
            return;
        }

        Binding binding = bindings.get(bindingName);
        // 2. If the binding for N in envRecord is a strict binding, set S to true.
        if (binding.isStrict()) {
            shouldThrowExceptions = true;
        }
        // 3. If the binding for N in envRec has not yet been initialized, throw a ReferenceError exception.
        if (binding.isUninitialized()) {
            // TODO: Make it throw a ReferenceError
            throw new RuntimeException("TODO: Should throw ReferenceError exception.");
        } else if (binding.isMutable()) {
            // 4. Else if the binding for N in envRec is a mutable binding, change its bound value to V.
            binding.setValue(value);
        } else {
            // 5. Else,
            // a. Assert: This is an attempt to change the value of an immutable binding
            ASSERT(binding.isImmutable());
            // b. If S is true, throw a TypeError exception
            if (shouldThrowExceptions) {
                // TODO: Make it throw a TypeError
                throw new RuntimeException("TODO: Should throw TypeError exception.");
            }
        }
        // 6. Return unused
    }

    @Override
    public Value getBindingValue(String bindingName, boolean shouldThrowReferenceErrorIfBindingDoesNotExist) {
        // 1. Assert: envRec has a binding for N
        ASSERT(hasBinding(bindingName));

        Binding binding = bindings.get(bindingName);
        // 2. If the binding for N in envRec is an uninitialized binding, throw a ReferenceError exception.
        if (!binding.isUninitialized()) {
            // TODO: Make it throw a ReferenceError
            throw new RuntimeException("TODO: Should throw ReferenceError exception.");
        }
        // 3. return the value currently bound to N in envRec.
        return binding.getValue();
    }

    @Override
    public boolean deleteBinding(String bindingName) {
        // 1. Assert: envRec has a binding for the name that is the value of N.
        ASSERT(hasBinding(bindingName));

        Binding binding = bindings.get(bindingName);
        // 2. If the binding for N in envRec cannot be deleted, return false.
        if (!binding.isDeletable()) {
            return false;
        }
        // 3. Remove the binding for N for envRec.
        bindings.remove(bindingName);
        // 4. Return true.
        return true;
    }

    @Override
    public boolean hasThisBinding() {
        // 1. Return false
        return false;
    }

    @Override
    public boolean hasSuperBinding() {
        // 1. Return false
        return false;
    }

    @Override
    public Value withBaseObject() {
        // 1. Return undefined
        return Value.undefined();
    }
}
