package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class CallExpression extends AstNode {

    private final AstNode callee;
    private final List<AstNode> arguments;

    public CallExpression(AstNode callee, List<AstNode> arguments) {
        ASSERT(callee != null);
        ASSERT(arguments != null);
        this.callee = callee;
        this.arguments = arguments;
    }

    @Override
    public Value interpret(AstInterpreter interpreter) {
        Value unknownValue = callee.interpret(interpreter);
        ASSERT(unknownValue.isObject());
        JsObject object = unknownValue.asObject();
        ASSERT(object.isFunction() || object.isNativeFunction());

        Value thisValue = Value.undefined();
        if (callee.isMemberExpression()) {
            thisValue = ((MemberExpression)callee).getObject().interpret(interpreter);
            ASSERT(thisValue.isObject());
        }

        if (!thisValue.isUndefined()) {
            interpreter.pushThisValue(thisValue);
        }

        if (object.isNativeFunction()) {
            List<Value> jsValues = arguments.stream().map(argument -> {
                try {
                    return argument.interpret(interpreter);
                } catch (Exception e) {
                    return Value.undefined();
                }
            }).collect(Collectors.toList());
            Value result = ((NativeFunction)object).call(interpreter, jsValues);
            if (!thisValue.isUndefined()) {
                interpreter.popThisValue();
            }
            return result;
        }


        Function function = (Function) object;
        ASSERT(function.getNumberOfArguments() == arguments.size());
        interpreter.enterScope();
        for (int i = 0; i < arguments.size(); i++) {
            AstNode node = arguments.get(i);
            interpreter.setIdentifier(function.getArgumentNameAt(i), Optional.ofNullable(node.interpret(interpreter)));
        }
        Value returnValue = ((Function)object).getBody().interpret(interpreter);
        if (!thisValue.isUndefined()) {
            interpreter.popThisValue();
        }
        interpreter.leaveScope();
        return returnValue;
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
