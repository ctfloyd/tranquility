package com.ctfloyd.tranquility.lib.parse.ast;

import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.*;

import java.util.Collections;
import java.util.List;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class FunctionDeclaration extends AstNode implements BoundName {

    private final BindingIdentifier bindingIdentifier;
    private final List<String> argumentNames;
    private final BlockStatement blockStatement;

    public FunctionDeclaration(String name, List<String> argumentNames, BlockStatement blockStatement) {
        ASSERT(name != null);
        ASSERT(!name.isEmpty());
        ASSERT(blockStatement != null);
        ASSERT(argumentNames != null);
        this.bindingIdentifier = new BindingIdentifier(name);
        this.blockStatement = blockStatement;
        this.argumentNames = argumentNames;
    }

    public String getStringValue() {
        return bindingIdentifier.getStringValue();
    }

    @Override
    public List<String> getBoundNames() {
        return bindingIdentifier.getBoundNames();
    }

    // https://tc39.es/ecma262/#sec-runtime-semantics-instantiateordinaryfunctionobject
    public JsObject instantiate(Environment environment, Environment privateEnvironment) {
        // we have no source text currently
        JsObject function = Function.ordinaryFunctionCreate(JsObject.create(getRealm(), Collections.emptyMap()),
                "", argumentNames, blockStatement,
                ThisMode.NON_LEXICAL_THIS, environment, privateEnvironment, getRuntime().getCurrentExecutionContext());
//        f.setName(name);
        return function;
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("FunctionDeclaration (");
        printIndent(indent + 1);
        System.out.println("Name: " + getStringValue());
        blockStatement.dump(indent + 1);
        printIndent(indent);
        System.out.println(")");
    }

    @Override
    public Value execute() {
        throw new RuntimeException("NO.");
    }

    @Override
    public void setRuntime(Runtime runtime) {
        super.setRuntime(runtime);
        blockStatement.setRuntime(runtime);
    }

    @Override
    public boolean isFunctionDeclaration() {
        return true;
    }
}
