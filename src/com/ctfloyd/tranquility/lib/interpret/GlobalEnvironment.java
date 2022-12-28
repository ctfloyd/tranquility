package com.ctfloyd.tranquility.lib.interpret;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// https://tc39.es/ecma262/#sec-global-environment-records
public class GlobalEnvironment extends Environment {

    private final ObjectEnvironment objectRecord;
    private final DeclarativeEnvironment declarativeRecord;
    private final List<String> variableNames;
    private JsObject globalThisValue;

    // https://tc39.es/ecma262/#sec-newglobalenvironment
    public GlobalEnvironment(AstInterpreter interpreter, JsObject globalObject, JsObject globalThisValue) {
        this.objectRecord = new ObjectEnvironment(interpreter, globalObject, false, null);
        this.declarativeRecord = new DeclarativeEnvironment(null);
        this.globalThisValue = globalThisValue;
        this.variableNames = new ArrayList<>();
        this.outerEnvironment = null;
    }

    @Override
    public boolean hasBinding(String bindingName) {
        // 1. Let DclRec be envRec.[[DeclarativeRecord]]
        // 2. If ! DclRec.HasBinding(N) is true, return true
        if (declarativeRecord.hasBinding(bindingName)) {
            return true;
        }
        // 3. Let ObjRec be envRec.[[ObjectRecord]]
        // 4. Return ? ObjRec.hasBinding(N).
        return objectRecord.hasBinding(bindingName);
    }

    @Override
    public void createMutableBinding(String bindingName, boolean canDelete) {
        // 1. Let DclRec be envRec.[[DeclarativeRecord]]
        // 2. If ! DclRec.HasBinding(N) is true, throw a TypeError exception
        if (declarativeRecord.hasBinding(bindingName)) {
            // FIXME: Throw TypeError exception.
            throw new RuntimeException("TypeError");
        }
        // 3. Return declarativeRecord.createMutableBinding(N, D).
        declarativeRecord.createMutableBinding(bindingName, canDelete);
    }

    @Override
    public void createImmutableBinding(String bindingName, boolean strictBinding) {
        // 1. Let DclRec be envRec.[[DeclarativeRecord]]
        // 2. If ! DclRec.HasBinding(N) is true, throw a TypeError exception
        if (declarativeRecord.hasBinding(bindingName)) {
            // FIXME: Throw TypeError exception.
            throw new RuntimeException("TypeError");
        }
        // 3. Return declarativeRecord.createImmutableBinding(N, D).
        declarativeRecord.createImmutableBinding(bindingName, strictBinding);
    }

    @Override
    public void initializeBinding(String bindingName, Value value) {
        // 1. Let DclRec be envRec.[[DeclarativeRecord]]
        // 2. If ! DclRec.HasBinding(N) is true, then
        if (declarativeRecord.hasBinding(bindingName)) {
            // a. Return ! DclRec.InitializeBinding(N, v)
            declarativeRecord.initializeBinding(bindingName, value);
            return;
        }
        // 3. Assert: If the binding exists, it must be in the Object Environment Record [NOTE: Implicit]
        // 4. Let ObjRec be envRec.[[ObjectRecord]]
        // 5. Return ? ObjRec.InitializeBinding(N, V);
        objectRecord.initializeBinding(bindingName, value);
    }

    @Override
    public void setMutableBinding(String bindingName, Value value, boolean shouldThrowExceptions) {
        // 1. Let DclRec be envRec.[[DeclarativeRecord]].
        // 2. If ! DclRec.HasBinding(N) is true, then
        if (declarativeRecord.hasBinding(bindingName)) {
            // a. Return ? DclRec.SetMutableBinding(N, V, S).
            declarativeRecord.setMutableBinding(bindingName, value, shouldThrowExceptions);
        }
        // 3. Let ObjRec be envRec.[[ObjectRecord]]
        // 4. Return ? ObjRec.SetMutableBinding(N, V, S)
        objectRecord.setMutableBinding(bindingName, value, shouldThrowExceptions);
    }

    @Override
    public Value getBindingValue(String bindingName, boolean shouldThrowReferenceErrorIfBindingDoesNotExist) {
        // 1. Let DclRec be envRec.[[DeclarativeRecord]].
        // 2. If ! DclRec.HasBinding(N) is true, then
        if (declarativeRecord.hasBinding(bindingName)) {
            // a. Return ? DclRec.GetBindingValue(N, S).
            return declarativeRecord.getBindingValue(bindingName, shouldThrowReferenceErrorIfBindingDoesNotExist);
        }
        // 3. Let ObjRec be envRec.[[ObjectRecord]]
        // 4. Return ? ObjRec.GetBindingValue(N, S)
        return objectRecord.getBindingValue(bindingName, shouldThrowReferenceErrorIfBindingDoesNotExist);
    }

    @Override
    public boolean deleteBinding(String bindingName) {
        // 1. Let DclRec be envRec.[[DeclarativeRecord]].
        // 2. If ! DclRec.HasBinding(N) is true, then
        if (declarativeRecord.hasBinding(bindingName)) {
            // a. Return ! DclRec.DeleteBinding(N).
            return declarativeRecord.deleteBinding(bindingName) ;
        }
        // 3. Let ObjRec be envRec.[[ObjectRecord]].
        // 4. Let globalObject be ObjRec.[[BindingObject]].
        JsObject globalObject = objectRecord.getBindingObject();
        // 5. Let existingProp be ? HasOwnProperty(globalObject, N).
        Value existingProp = globalObject.hasOwnProperty(bindingName);
        // 6. If existingProp is true, then
        if (existingProp.asBoolean()) {
            // a. Let status be ? ObjRec.DeleteBinding(N).
            boolean status = objectRecord.deleteBinding(bindingName);
            // b. If status is true, then
            if (status) {
                // i. Let varNames be envRec.[[VarNames]].
                // ii. If N is an element of varNames, remove that element from the varNames.
                variableNames.remove(bindingName);
            }
            // c. Return status.
            return status;
        }
        // 7. Return true.
        return true;
    }

    @Override
    public boolean hasThisBinding() {
        // 1. Return true.
        return true;
    }

    @Override
    public boolean hasSuperBinding() {
        // 1. Return false.
        return false;
    }

    @Override
    public Value withBaseObject() {
        // 1. Return undefined.
        return Value.undefined();
    }

    public Value getThisBinding() {
        // 1. Return envRec.[[GlobalThisValue]]
        return Value.object(globalThisValue);
    }

    public boolean hasVariableDeclaration(String variableName) {
        // 1. Let varDeclaredNames be envRec.[[VarNames]]
        // 2. If varDeclaredNames.contains(N), return true
        // 3. Return false.
        return variableNames.contains(variableName);
    }

    public boolean hasLexicalDeclaration(String lexicalDeclarationName) {
        // 1. Let DclRec be envRec.[[DeclarativeRecord]].
        // 2. Return ! DclRec.HasBinding(N).
        return declarativeRecord.hasBinding(lexicalDeclarationName);
    }

    public boolean hasRestrictedGlobalProperty(String restrictedGlobalProperty) {
        // 1. Let ObjRec be envRec.[[ObjectRecord]].
        // 2. Let globalObject be ObjRec.[[BindingObject]].
        JsObject globalObject = objectRecord.getBindingObject();
        // 3. Let existingProp be ? globalObject.[[GetOwnProperty]](N).
        Optional<PropertyDescriptor> existingProp = globalObject.getOwnProperty(restrictedGlobalProperty);
        // 4. If existingProp is undefined, return false.
        if (existingProp.isEmpty()) {
            return false;
        }
        // 5. If existingProp.[[Configurable]] is true, return false.
        if (existingProp.get().isConfigurable()) {
            return false;
        }
        // 6. Return true.
        return true;
    }

    public boolean canDeclareGlobalVariable(String variableName) {
        // 1. Let ObjRec be envRec.[[ObjectRecord]].
        // 2. Let globalObject be ObjRec.[[BindingObject]].
        JsObject globalObject = objectRecord.getBindingObject();
        // 3. Let hasProperty be ? HasOwnProperty(globalObject, N).
        boolean hasProperty = globalObject.hasProperty(variableName);
        // 4. If hasProperty is true, return true.
        if (hasProperty) {
            return true;
        }
        // 5. Return ? IsExtensible(globalObject).
        return globalObject.isExtensible();
    }

    public boolean canDeclareGlobalFunction(String functionName) {
        // 1. Let ObjRec be envRec.[[ObjectRecord]].
        // 2. Let globalObject be ObjRec.[[BindingObject]].
        JsObject globalObject = objectRecord.getBindingObject();
        // 3. Let existingProp be ? globalObject.[[GetOwnProperty]](N).
        Optional<PropertyDescriptor> existingProp = globalObject.getOwnProperty(functionName);
        // 4. If existingProp is undefined, return ? IsExtensible(globalObject).
        if (existingProp.isEmpty()) {
            return globalObject.isExtensible();
        }
        // 5. If existingProp.[[Configurable]] is true, return true.
        if (existingProp.get().isConfigurable()) {
            return true;
        }
        // 6. If IsDataDescriptor(existingProp) is true and existingProp has attribute values { [[Writable]]: true, [[Enumerable]]: true }, return true.
        // 7. Return false.
        return existingProp.get().isDataDescriptor() && existingProp.get().isWritable() && existingProp.get().isEnumerable();
    }

    public void createGlobalVariableBinding(String variableName, boolean canDelete) {
        // 1. Let ObjRec be envRec.[[ObjectRecord]].
        // 2. Let globalObject be ObjRec.[[BindingObject]].
        JsObject globalObject = objectRecord.getBindingObject();
        // 3. Let hasProperty be ? HasOwnProperty(globalObject, N).
        Value hasProperty = globalObject.hasOwnProperty(variableName);
        // 4. Let extensible be ? IsExtensible(globalObject).
        boolean extensible = globalObject.isExtensible();
        // 5. If hasProperty is false and extensible is true, then
        if (!hasProperty.asBoolean() && extensible) {
            // a. Perform ? ObjRec.CreateMutableBinding(N, D).
            objectRecord.createMutableBinding(variableName, canDelete);
            // b. Perform ? ObjRec.InitializeBinding(N, undefined).
            objectRecord.initializeBinding(variableName, Value.undefined());
        }
        // 6. Let varDeclaredNames be envRec.[[VarNames]].
        // 7. If varDeclaredNames does not contain N, then
        if (!variableNames.contains(variableName)) {
            // a. Append N to varDeclaredNames.
            variableNames.add(variableName);
        }
        // 8. Return unused.
    }

    public void createGlobalFunctionBinding(String variableName, Value initializationValue, boolean canDelete) {
        // 1. Let ObjRec be envRec.[[ObjectRecord]].
        // 2. Let globalObject be ObjRec.[[BindingObject]].
        JsObject globalObject = objectRecord.getBindingObject();
        // 3. Let existingProp be ? globalObject.[[GetOwnProperty]](N).
        Optional<PropertyDescriptor> existingProp = globalObject.getOwnProperty(variableName);
        // 4. If existingProp is undefined or existingProp.[[Configurable]] is true, then
        PropertyDescriptor desc;
        if (existingProp.isEmpty() || existingProp.get().isConfigurable()) {
            // a. Let desc be the PropertyDescriptor { [[Value]]: V, [[Writable]]: true, [[Enumerable]]: true, [[Configurable]]: D }.
            desc = new PropertyDescriptor(initializationValue, true, true, canDelete);
        } else {
            // 5. Else,
            // a. Let desc be the PropertyDescriptor { [[Value]]: V }.
            desc = new PropertyDescriptor(initializationValue);
        }
        // 6. Perform ? DefinePropertyOrThrow(globalObject, N, desc).
        globalObject.definePropertyOrThrow(variableName, desc);
        // 7. Perform ? Set(globalObject, N, V, false).
        globalObject.set(variableName, initializationValue, false);
        // 8. Let varDeclaredNames be envRec.[[VarNames]].
        // 9. If varDeclaredNames does not contain N, then
        if (!variableNames.contains(variableName)) {
            // a. Append N to varDeclaredNames.
            variableNames.add(variableName);
        }
        // 10. Return unused.
    }
}

