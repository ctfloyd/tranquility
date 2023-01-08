package com.ctfloyd.tranquility.lib.runtime;

import com.ctfloyd.tranquility.lib.ast.BlockStatement;

import java.util.List;
import java.util.Optional;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class Function extends JsObject {

    private List<String> parameterList;
    private BlockStatement body;
    private JsObject homeObject;
    private Environment environment;
    private Environment privateEnvironment;
    private Realm realm;
    private ThisMode thisMode;
    private boolean strict;

    public static Function ordinaryFunctionCreate(
            JsObject functionPrototype,
            String sourceText,
            List<String> parameterList,
            BlockStatement body,
            ThisMode thisMode,
            Environment environment,
            Environment privateEnvironment,
            ExecutionContext executionContext)
    {
        Function function = new Function();
        function.setPrototypeOf(functionPrototype);
        function.setParameterList(parameterList);
        function.setBody(body);
        function.setStrict(false);
        function.setThisMode(thisMode);
        function.setEnvironment(environment);
        function.setPrivateEnvironment(privateEnvironment);
        function.setRealm(executionContext.getRealm());
        return function;
    };

    Function() {
        super();
    }

    public Value call(ArgumentList arguments) {
        ExecutionContext callerContext = getRuntime().getCurrentExecutionContext();
        ExecutionContext calleeContext = prepareForOrdinaryCall(null);
        ASSERT(calleeContext == getRuntime().getCurrentExecutionContext());
        // FIXME: 4
        ordinaryCallBindThis(calleeContext, getRuntime().getThisValue());

        // TODO: Technically this could throw an error and we don't handle that appropriately.
        Value result = ordinaryCallEvaluateBody(arguments);
        getRuntime().popExecutionContext();
        getRuntime().pushExecutionContext(callerContext);
        return result;
    }

    private Value ordinaryCallEvaluateBody(ArgumentList arguments) {
        // TODO: This should formally call FunctionDeclarationInstantiation, but we will inline part of that code for now.
        Environment environment = getRuntime().getCurrentExecutionContext().getLexicalEnvironment();

        for (String parameter : parameterList) {
            boolean alreadyDeclared = environment.hasBinding(parameter);
            if (!alreadyDeclared) {
                environment.createMutableBinding(parameter, false);;
            }
        }

        for (int i = 0; i < parameterList.size(); i++) {
            String name = parameterList.get(i);
            Value value = arguments.getArgumentAt(i);
            environment.initializeBinding(name, value);
        }

        return body.execute();
    }

    private void ordinaryCallBindThis(ExecutionContext calleeContext, Value thisArgument) {
        if (thisMode == ThisMode.LEXICAL_THIS) {
            return;
        }
        Environment localEnvironment = calleeContext.getLexicalEnvironment();
        Value thisValue;
        if (strict) {
            thisValue = thisArgument;
        } else {
            if (thisArgument.isUndefined() || thisArgument.isNull()) {
                Optional<GlobalEnvironment> globalEnvironment = realm.getGlobalEnvironment();
                ASSERT(globalEnvironment.isPresent());
                thisValue = globalEnvironment.get().getThisBinding();
            } else {
                thisValue = Value.object(thisArgument.toObject(realm));
            }
        }
        ASSERT(localEnvironment instanceof FunctionEnvironment);
        ((FunctionEnvironment) localEnvironment).bindThisValue(thisValue);
    }

    private ExecutionContext prepareForOrdinaryCall(JsObject newTarget)  {
        ExecutionContext callerContext = getRuntime().getCurrentExecutionContext();
        ExecutionContext calleeContext = new ExecutionContext();
        calleeContext.setFunction(this);
        calleeContext.setRealm(realm);
        // FIXME: More things are supposed to be set
        FunctionEnvironment localEnvironment = new FunctionEnvironment(this, newTarget);
        calleeContext.setLexicalEnvironment(localEnvironment);
        calleeContext.setVariableEnvironment(localEnvironment);
        // FIXME: Set private environment, 10, 11
        getRuntime().pushExecutionContext(calleeContext);
        return calleeContext;
    }

    public List<String> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<String> parameterList) {
        this.parameterList = parameterList;
    }

    public BlockStatement getBody() {
        return body;
    }

    public void setBody(BlockStatement body) {
        this.body = body;
    }

    public Optional<JsObject> getHomeObject() {
        return Optional.ofNullable(homeObject);
    }

    public void setHomeObject(JsObject homeObject) {
        this.homeObject = homeObject;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getPrivateEnvironment() {
        return privateEnvironment;
    }

    public void setPrivateEnvironment(Environment privateEnvironment) {
        this.privateEnvironment = privateEnvironment;
    }

    @Override
    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    public ThisMode getThisMode() {
        return thisMode;
    }

    public void setThisMode(ThisMode thisMode) {
        this.thisMode = thisMode;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

}
