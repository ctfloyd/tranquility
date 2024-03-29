package com.ctfloyd.tranquility.lib.parse.ast;

import com.ctfloyd.tranquility.lib.runtime.*;
import com.ctfloyd.tranquility.lib.runtime.Runtime;

import java.util.Collections;
import java.util.List;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class CallExpression extends Expression {

    private final AstNode callee;
    private final List<AstNode> arguments;

    public CallExpression(AstNode callee, List<AstNode> arguments) {
        ASSERT(callee != null);
        ASSERT(arguments != null);
        this.callee = callee;
        this.arguments = arguments;
    }

    @Override
    public Value execute() {
        Value unknownValue;
        if (callee.isIdentifier()){
           unknownValue = ((Identifier) callee).getReference().getValue(getRealm());
        } else {
            unknownValue = callee.execute();
        }
        ASSERT(unknownValue.isObject());
        JsObject object = unknownValue.asObject();
        ASSERT(object.isFunction() || object.isNativeFunction());

        Value thisValue = Value.undefined();
        if (callee.isMemberExpression()) {
            thisValue = ((MemberExpression)callee).getObject().getReference().getValue(getRealm());
        }

        if (!thisValue.isUndefined()) {
            getRuntime().setThisValue(thisValue);
        }

        ArgumentList evaluatedArguments = new ArgumentList(Collections.emptyList());
        for (AstNode node : arguments) {
            if (node.isIdentifier()) {
               Reference reference = ((Identifier) node).getReference();
               evaluatedArguments.addArgument(reference.getValue(getRealm()));
            } else {
                evaluatedArguments.addArgument(node.execute());
            }
        }

        Value returnValue;
        if (object.isNativeFunction()) {
            returnValue = ((NativeFunction)object).call(evaluatedArguments);
        } else {
            Function function = (Function) object;
            returnValue = function.call(evaluatedArguments);
        }

        if (!thisValue.isUndefined()) {
            getRuntime().setThisValue(Value.undefined());
        }

        return returnValue;
    }

    @Override
    public void setRuntime(Runtime runtime) {
        super.setRuntime(runtime);
        callee.setRuntime(runtime);
        for (AstNode child : arguments) {
            child.setRuntime(runtime);
        }
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("CallExpression (");
        printIndent(indent + 1);
        System.out.println("[Callee] {");
        callee.dump(indent + 1);
        printIndent(indent + 1);
        System.out.println("}");
        printIndent(indent + 1);
        System.out.println("[Arguments] {");
        arguments.forEach(argument -> argument.dump(indent + 1));
        printIndent(indent + 1);
        System.out.println("}");
        printIndent(indent);
        System.out.println(")");
    }
}
