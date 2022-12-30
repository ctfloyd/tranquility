package com.ctfloyd.tranquility.lib.runtime;

import com.ctfloyd.tranquility.lib.ast.BlockStatement;

import java.util.List;
import java.util.Optional;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class Function extends JsObject {

    private final String name;
    private List<String> argumentNames;
    private final BlockStatement body;
    private JsObject homeObject;

    public Function(String name, List<String> argumentNames, BlockStatement body) {
        super();
        ASSERT(name != null);
        ASSERT(!name.isEmpty());
        ASSERT(argumentNames != null);
        this.name = name;
        this.body = body;
        this.argumentNames = argumentNames;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    public Optional<JsObject> getHomeObject() {
        return Optional.ofNullable(homeObject);
    }

    public void setHomeObject(JsObject homeObject) {
        this.homeObject = homeObject;
    }

    public BlockStatement getBody() {
       return body;
    }

    public int getNumberOfArguments() {
        return argumentNames.size();
    }

    public String getArgumentNameAt(int i) {
        return argumentNames.get(i);
    }

    public Value call(ArgumentList arguments) {
        Environment outerEnvironment = getRuntime().getCurrentExecutionContext().getLexicalEnvironment();
        Environment environment = new DeclarativeEnvironment(outerEnvironment);
        getRuntime().getCurrentExecutionContext().setLexicalEnvironment(environment);
        for (int i = 0; i < arguments.size(); i++) {
            environment.createMutableBinding(getArgumentNameAt(i), true);
            environment.initializeBinding(getArgumentNameAt(i), arguments.getArgumentAt(i));
        }
        Value value =  body.execute();
        for (int i = 0; i < arguments.size(); i++) {
            environment.deleteBinding(getArgumentNameAt(i));
        }
        getRuntime().getCurrentExecutionContext().setLexicalEnvironment(environment.getOuterEnvironment());
        return value;
    }

    public Value call() {
        return call(new ArgumentList());
    }
}
