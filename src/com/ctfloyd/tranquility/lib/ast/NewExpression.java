package com.ctfloyd.tranquility.lib.ast;

import com.ctfloyd.tranquility.lib.interpret.ArgumentList;
import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Constructor;
import com.ctfloyd.tranquility.lib.interpret.Value;

import java.util.ArrayList;
import java.util.List;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

// https://tc39.es/ecma262/#sec-new-operator
public class NewExpression extends AstNode {

    private final AstNode callee;
    private final List<AstNode> arguments;

    public NewExpression(AstNode callee, List<AstNode> arguments) {
        ASSERT(callee != null);
        ASSERT(arguments != null);
        this.callee = callee;
        this.arguments = arguments;
    }

    // https://tc39.es/ecma262/#sec-evaluatenew
    @Override
    public Value interpret(AstInterpreter interpreter) throws RuntimeException {
        // 1. Let ref be ? Evaluation of constructExpr
        Value value = callee.interpret(interpreter);
        // FIXME: Not actually doing GetValue currently
        // 2. Let constructor be ? GetValue(ref)
        Value constructor = value;
        // 3. If arguments is empty, let argList be a new empty List
        List<Value> argList = new ArrayList<>();
        // 4. Else, a. Let argList be ArgumentListEvaluation of arguments
        if (!arguments.isEmpty()) {
            for (AstNode astNode: arguments)  {
                argList.add(astNode.interpret(interpreter));
            }
        }
        // FIXME: Throw the error
        // 5. If IsConstructor(constructor) is false, throw a TypeError Exception
        ASSERT(constructor.isObject());
        if (!value.toObject(interpreter).isConstructor()) {
            throw new RuntimeException("TypeError: Attempted to call 'new' on something that is not a constructor.");
        }
        // 6. Return ? Constructor(constructor, argumentList);
        Constructor constructorObject = (Constructor) value.toObject(interpreter);
        return Value.object(constructorObject.construct(interpreter, new ArgumentList(argList), null));
    }

    @Override
    public void dump(int indent) {
        super.dump(indent);
    }
}
