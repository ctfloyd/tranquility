package com.ctfloyd.tranquility.lib.interpret;

import com.ctfloyd.tranquility.lib.ast.BlockStatement;

import java.util.Optional;

public class AstInterpreter {

    // FIXME: The interpreter should know about scopes and be able to enter / exit them to look up identifiers.
    // For now everything will live in the global namespace.

    private final GlobalObject globalObject;

    public AstInterpreter() {
        globalObject = new GlobalObject();
    }

    public void defineFunction(String functionName, BlockStatement body) {
        Function function = new Function(functionName, body);
        globalObject.put(functionName, JsValue.object(function));
    }

    public JsValue getIdentifier(String identifierName)  {
        return globalObject.get(identifierName);
    }

    public void setIdentifier(String identifier) {
        setIdentifier(identifier, Optional.empty());
    }

    public void setIdentifier(String identifier, Optional<JsValue> value) {
        if (value.isPresent()) {
            globalObject.put(identifier, value.get());
        } else {
            globalObject.put(identifier, JsValue.undefined());
        }
    }

    public GlobalObject getGlobalObject() {
        return globalObject;
    }

}
