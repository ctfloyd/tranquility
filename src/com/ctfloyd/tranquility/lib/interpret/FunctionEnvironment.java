package com.ctfloyd.tranquility.lib.interpret;

import java.util.Optional;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

/**
 * https://tc39.es/ecma262/#sec-function-environment-records
 */
public class FunctionEnvironment extends DeclarativeEnvironment {

    private Value thisValue = null;
    private BindingStatus thisBindingStatus = BindingStatus.UNINITIALIZED;
    private Function functionObject = null;
    private JsObject newTarget = null;

    public FunctionEnvironment(Environment outerEnvironment) {
        super(outerEnvironment);
    }

    public Value bindThisValue(Value thisValue) {
        // 1. Assert: envRec.[[ThisBindingStatus]] is not lexical
        ASSERT(thisBindingStatus != BindingStatus.LEXICAL);
        // 2. If envRec.[[ThisBindingStatus]] is initialized, throw a ReferenceError exception.
        if (thisBindingStatus == BindingStatus.INITIALIZED) {
            // FIXME: Throw a ReferenceError
            throw new RuntimeException("ReferenceError");
        }
        // 3. Set envRec.[[ThisValue]] to V.
        this.thisValue = thisValue;
        // 4. Set encREc.[[ThisBindingStatus]] to initialized.
        this.thisBindingStatus = BindingStatus.INITIALIZED;
        // 5. Return V.
        return thisValue;
    }

    public boolean hasThisBinding() {
        // 1. If envRec.[[ThisBindingStatus]] is lexical, return false; otherwise, return true.
        return thisBindingStatus != BindingStatus.LEXICAL;
    }

    public boolean hasSuperBinding() {
        // 1. If envRec.[[ThisBindingStatus]] is lexical, return false.
        if (thisBindingStatus == BindingStatus.LEXICAL) {
            return false;
        }
        // 2. If envRec.[[FunctionObject]].[[HomeObject]] is undefined, return false; otherwise, return true.
        ASSERT(functionObject != null);
        return functionObject.getHomeObject().isPresent();
    }

    public Value getThisBinding() {
        // 1. Assert: envRec.[[ThisBindingStatus]] is not Lexical
        ASSERT(thisBindingStatus != BindingStatus.LEXICAL);
        // 2. If envRec.[[ThisBindingStatus]] is uninitialized, throw a ReferenceError exception.
        if (thisBindingStatus == BindingStatus.UNINITIALIZED) {
            // FIXME: Throw a ReferenceError
            throw new RuntimeException("ReferenceError.");
        }
        // 3. Return envRec.[[ThisValue]]
        return thisValue;
    }

    public Value getSuperBase() {
        ASSERT(functionObject != null);
        // 1. Let home be envRec.[[FunctionObject]].[[HomeObject]]
        Optional<JsObject> home = functionObject.getHomeObject();
        // 2. If home is undefined, return undefined.
        if (home.isEmpty()) {
            return Value.undefined();
        }
        // 3. Assert: home is an Object [Implicit]
        // 4. Return ? home.[[GetPrototypeOf]]().
        return home.get().getPrototypeOf();
    }
}
