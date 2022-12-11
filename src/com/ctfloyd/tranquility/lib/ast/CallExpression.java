package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.Argument;
import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Function;
import com.ctfloyd.tranquility.lib.interpret.JsObject;
import com.ctfloyd.tranquility.lib.interpret.NativeFunction;
import com.ctfloyd.tranquility.lib.interpret.Value;

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
    public Value interpret(AstInterpreter interpreter) throws Exception {
        Value unknownValue = callee.interpret(interpreter);
        ASSERT(unknownValue.isObject());
        JsObject object = unknownValue.asObject();
        ASSERT(object.isFunction() || object.isNativeFunction());

        if (object.isNativeFunction()) {
            List<Value> jsValues = arguments.stream().map(argument -> {
                try {
                    return argument.interpret(interpreter);
                } catch (Exception e) {
                    return Value.undefined();
                }
            }).collect(Collectors.toList());
            return ((NativeFunction)object).call(jsValues);
        }


        Function function = (Function) object;
        ASSERT(function.getNumberOfArguments() == arguments.size());
        interpreter.enterScope();
        for (int i = 0; i < arguments.size(); i++) {
            AstNode node = arguments.get(i);
            interpreter.setIdentifier(function.getArgumentNameAt(i), Optional.ofNullable(node.interpret(interpreter)));
        }
        Value returnValue = ((Function)object).getBody().interpret(interpreter);
        interpreter.leaveScope();
        return returnValue;
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("CallExpression (");
        callee.dump(indent + 1);
        printIndent(indent);
        System.out.println(")");
    }
}
