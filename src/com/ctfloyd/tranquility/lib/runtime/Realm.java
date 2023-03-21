package com.ctfloyd.tranquility.lib.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Stream;

// https://tc39.es/ecma262/#sec-code-realms
public class Realm {

    private Map<Intrinsic, JsObject> intrinsics = new HashMap<>();
    private JsObject globalObject = null;
    private GlobalEnvironment globalEnvironment = null;

    // FIXME: Eventually these needs to be implemented.
    // private List<Map<AstNode, List<JsObject>>> templateMap;
    // private List<Map<String, Object>> loadedModules

    // NOTE: There is a hostDefined property that allows us (the host) to put any additional information that's
    // needed into a realm. Tranquility treats this specification as 0...N additional class fields and methods in the
    // realm.

    // https://tc39.es/ecma262/#sec-initializehostdefinedrealm
    public static Realm initializeHostDefinedRealm(Stack<ExecutionContext> executionContextStack, boolean vanillaLogs) {
        // 1. Let realm be CreateRealm()
        Realm realm = createRealm();
        // 2. Let newContext be a new execution context.
        ExecutionContext newContext = new ExecutionContext();
        // 3. Set the Function of newContext to null.
        newContext.setFunction(null);
        // 4. Set the Reaml of newContext to realm.
        newContext.setRealm(realm);

        // TODO: 5 Set the ScriptOrModule of newContext to null.

        // 6. Push newContext onto the execution context stack; newContext is now the running execution context.
        executionContextStack.add(newContext);

        // 7. If the host requires use of an exotic object to serve as realm's global object, let global be such an object
        // created in a host-defined manner. Otherwise, let global be undefined, indicating that an ordinary object
        // should be created as the global object.
        JsObject global = GlobalObject.create(realm);

        // 8. If the host requires that the this binding in realm's global scope return an objet other than the global object,
        // let thisValue be such an objet created in a host-defined manner. Otherwise, let thisValue be undefined, indicating
        // that realm's global this binding should be the global object.
        JsObject thisValue = null;

        // 9 . Perform SetRealmGlobalObject(realm, global, thisValue).
        realm.setRealmGlobalObject(global, thisValue);

        // 10. Let globalObj be ? SetDefaultGlobalBindings(realm).
        JsObject globalObject = realm.setDefaultGlobalBindings();

        // 11. Create any host-defined global object properties on globalObj.
        globalObject.set("console", Value.object(new ConsoleObject(vanillaLogs)), false);

        return realm;
    }

    private static Realm createRealm() {
        // 1. Let realmRec be a new Realm Record.
        Realm realm = new Realm();
        // 2. Perform CreateIntrinsics(realmRec);
        createIntrinsics(realm);
        // NOTE: The following steps are done implicitly by the class definition.
        // 3. Set realmRec.[[GlobalObject]] to undefined.
        // 4. Set realmRec.[[GlobalEnv]] to undefined.
        // 5. Set realmRec.[[TemplateMap]] to a new empty List.
        // FIXME: Tranquility doesn't currently support template maps in realm records.
        // 6. Return realmRec;
        return realm;
    }

    // https://tc39.es/ecma262/#sec-createintrinsics
    private static void createIntrinsics(Realm realm) {
        // NOTE: Step 1 is done implicitly byy the class definition.
        // 1. Set realmRec.[[Intrinsics]] to a new Record.
        // 2. Set fields of realmRec.[[Intrinsics]] with the values listed in Table 6. The field names are the names...
        // FIXME: There's a lot of implementation details in the specification that are skipped over here.
        Stream.of(Intrinsic.values()).forEach(realm::addIntrinsic);
        // 3. Perform AddRestrictedFunctionProperties(realmRec.[[Intrinsics]].[[%Function.prototype%]], realmRec).
        // FIXME: This functionality depends on %ThrowTypeError% intrinsic, which Tranquility doesn't have yet.
        // 4. Return unused.
    }

    public Map<Intrinsic, JsObject> getIntrinsics() {
        return intrinsics;
    }

    public void setIntrinsics(Map<Intrinsic, JsObject> intrinsics) {
        this.intrinsics = intrinsics;
    }

    public void addIntrinsic(Intrinsic intrinsic) {
        intrinsics.put(intrinsic, intrinsic.getGlobalValue(this));
    }

    public Optional<JsObject> getGlobalObject() {
        return Optional.ofNullable(globalObject);
    }

    // https://tc39.es/ecma262/#sec-setrealmglobalobject
    public void setRealmGlobalObject(JsObject globalObject, JsObject thisValue) {
        // 1. If globalObj is undefined, then
        if (globalObject == null) {
            // a. Let intrinsics be realmRec.[[Intrinsics]].
            // b. Set globalObj to OrdinaryObjectCreate(intrinsics.[[%Object.prototype%]]).
            globalObject = JsObject.ordinaryObjectCreate(intrinsics.get(Intrinsic.OBJECT).getPrototypeOf().asObject());
        }
        // 2. Assert: globalObj is an Object. [Implicit]
        // 3. If thisValue is undefined, set thisValue to globalObj.
        if (thisValue == null) {
            thisValue = globalObject;
        }
        // 4. Set realmRec.[[GlobalObject]] to globalObj.
        this.globalObject = globalObject;
        // 5. Let newGlobalEnv be NewGlobalEnvironment(globalObj, thisValue).
        GlobalEnvironment newGlobalEnv = new GlobalEnvironment(globalObject, thisValue);
        // 6. Set realmRec.[[GlobalEnv]] to newGlobalEnv.
        this.globalEnvironment = newGlobalEnv;
        // 7. Return unused.
    }

    // https://tc39.es/ecma262/#sec-setdefaultglobalbindings
    public JsObject setDefaultGlobalBindings() {
        // FIXME: Implement this.
        // 1. Let global be realmRec.[[GlobalObject]].
        JsObject global = getGlobalObject().orElseThrow();
        // 2. For each property of the GlobalObject specified in clause 19, do
        // a. Let name be the String value of the property name.
        // b. Let desc the the fully populated data Property Descriptor for the property, containing the specified
        // attributes for the property. For properties listed in 19.2, 19.3, or 19.4 the value of the [[Value]] attribute
        // is the corresponding intrinsic object from realmRec.
        // 3. Return global.
        return global;
    }

    public Optional<GlobalEnvironment> getGlobalEnvironment() {
        return Optional.ofNullable(globalEnvironment);
    }

    public void setGlobalEnvironment(GlobalEnvironment globalEnvironment) {
        this.globalEnvironment = globalEnvironment;
    }
}
