package com.ctfloyd.tranquility.lib.runtime;

/**
 * https://tc39.es/ecma262/#sec-object-environment-records
 */
public class ObjectEnvironment extends Environment {

    // The binding object of this Environment Record.
    private JsObject bindingObject;
    // Indicates whether this Environment Record is created for a with statement.
    private boolean withEnvironment;

    // https://tc39.es/ecma262/#sec-newobjectenvironment
    public ObjectEnvironment(JsObject bindingObject, boolean withEnvironment, Environment outer) {
        this.bindingObject = bindingObject;
        this.withEnvironment = withEnvironment;
        this.outerEnvironment = outer;
    }

    public JsObject getBindingObject() {
        return bindingObject;
    }

    @Override
    public boolean hasBinding(String bindingName) {
        // 1. Let bindingObject be envRec.[[bindingObject]]
        // 2. Let foundBinding be ? HasProperty(bindingObject, N).
        boolean foundBinding = bindingObject.hasProperty(bindingName);
        // 3. If foundBinding is false, return false
        if (!foundBinding) {
            return false;
        }
        // 4. If envRec.[[IsWithEnvironment]] is false, return true.
        if (!isWithEnvironment()) {
            return true;
        }
        // FIXME: unscopables are not yet implemented
        // 5. Let unscopables be ? Get(bindingObject, @@unscopables).
        // 6. If unscopables is an Object, then
        //  a. Let blocked be ToBoolean(? Get(unscopables, N)).
        //  b. If blocked is true, return false.
        return true;
    }

    @Override
    public void createMutableBinding(String bindingName, boolean canDelete) {
        // 1. Let bindingObject be envRec.[[BindingObject]]
        // 2. Perform ? DefinePropertyOrThrow(bindingObject, N, PropertyDescriptor { [[Value]]: undefined, [[Writable]]:
        //  true, [[Enumerable]]: true, [[Configurable]]: D}}
        bindingObject.definePropertyOrThrow(bindingName, new PropertyDescriptor(Value.undefined(), true, true, canDelete));
        // 3. Return unused
    }

    @Override
    public void createImmutableBinding(String bindingName, boolean strictBinding) {
        // UNUSED
        throw new RuntimeException("CreateImmutableBinding implementation of ObjectEnvironment should never be used.");
    }

    @Override
    public void initializeBinding(String bindingName, Value value) {
        // 1. Perform ? envRec.setMutableBinding(N, V, false).
        setMutableBinding(bindingName, value, false);
        // 2. Return unused
    }

    @Override
    public void setMutableBinding(String bindingName, Value value, boolean shouldThrowExceptions) {
        // 1. Let bindingObject be envRec.[[BindingObject]].
        // 2. Let stillExists be ? HasProperty(bindingObject, N).
        boolean stillExists = bindingObject.hasProperty(bindingName);
        // 3. If stillExists is false and S is true, throw a ReferenceError exception.
        if (!stillExists && shouldThrowExceptions) {
            // FIXME: Throw a ReferenceError
            throw new RuntimeException("ReferenceError");
        }
        // 4. Perform ? Set(bindingObject, N, V, S).
        bindingObject.set(bindingName, value, shouldThrowExceptions);
        // 5. Return unused.
    }

    @Override
    public Value getBindingValue(String bindingName, boolean shouldThrowReferenceErrorIfBindingDoesNotExist) {
        // 1. Let bindingObject be envRec.[[BindingObject]]
        // 2. Let value be ? HasProperty(bindingObject, N).
        boolean value = bindingObject.hasProperty(bindingName);
        // 3. If value is false, then
        if (!value) {
            // a. If S is false, return undefined; otherwise throw a ReferenceErrorException.
            if (!shouldThrowReferenceErrorIfBindingDoesNotExist) {
                return Value._false();
            } else {
                // FIXME: Throw a ReferenceError
                throw new RuntimeException("ReferenceError");
            }
        }

        return bindingObject.get(bindingName);
    }

    @Override
    public boolean deleteBinding(String bindingName) {
        // 1. Let bindingObject be envRec.[[BindingObject]]
        // 2. Return ? bindingObject.[[Delete]](N).
        return bindingObject.delete(bindingName);
    }

    @Override
    public boolean hasThisBinding() {
        // 1. Return false.
        return false;
    }

    @Override
    public boolean hasSuperBinding() {
        // 1. Return false.
        return false;
    }

    @Override
    public Value withBaseObject() {
        // 1. If envRec.[[IsWithEnvironment]]] is true, return envRec.[[BindingObject]].
        // 2. Otherwise return undefined.
        return isWithEnvironment() ? Value.object(bindingObject) : Value.undefined();
    }

    private boolean isWithEnvironment() {
        return withEnvironment;
    }
}
