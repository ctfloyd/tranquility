package com.ctfloyd.tranquility.lib.parse.ast;

import com.ctfloyd.tranquility.lib.runtime.Reference;
import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.Value;

import java.util.List;
import java.util.StringJoiner;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class VariableDeclaration extends AstNode implements BoundName {

    private final BindingIdentifier bindingIdentifier;
    private final AstNode initializer;

    public VariableDeclaration(String name) {
        ASSERT(name != null);
        ASSERT(!name.isEmpty());
        this.bindingIdentifier = new BindingIdentifier(name);
        this.initializer = null;
    }

    public VariableDeclaration(String name, AstNode initializer) {
        ASSERT(name != null);
        ASSERT(!name.isEmpty());
        ASSERT(initializer != null);
        this.bindingIdentifier = new BindingIdentifier(name);
        this.initializer = initializer;
    }

    // https://tc39.es/ecma262/#sec-variable-statement-runtime-semantics-evaluation
    @Override
    public Value execute() {
        if (initializer != null) {
            // 1. Let bindingId be StringValue of BindingIdentifier.
            String bindingId = bindingIdentifier.getStringValue();
            // 2. Let lhs be ? ResolveBinding(bindingId).
            Reference leftHandSide = getRuntime().resolveBinding(bindingId);
            // FIXME: 3. If IsAnonymousFunctionDefinition(initializer) is true, then
            // 3a. Let value be ? NamedEvaluation of Initializer with argument bindingId.
            // 4. Else
            // 4a. Let rhs be ? Evaluation of Initializer.
            // 4b. Let value be ? GetValue(rightHandSize).
            Value value = initializer.execute();
            // 5. Perform ? PutValue(lhs, value).
            leftHandSide.putValue(getRealm(), value);
            // 6. Return empty.
            return Value.undefined();
        }
        // Else: Return empty
        return Value.undefined();
    }

    @Override
    public List<String> getBoundNames() {
        return bindingIdentifier.getBoundNames();
    }

    @Override
    public void setRuntime(Runtime runtime) {
        super.setRuntime(runtime);
        initializer.setRuntime(runtime);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("VariableDeclaration (");
        printIndent(indent + 1);
        System.out.println("[BindingIdentifier] (" + bindingIdentifier + ")");
        printIndent(indent + 1);
        System.out.println("[Initializer] {");
        if (initializer != null) {
            initializer.dump(indent + 1);
        } else {
            printIndent(indent + 1);
            System.out.println("undefined");
        }
        printIndent(indent + 1);
        System.out.println("}");
        printIndent(indent);
        System.out.println(")");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VariableDeclaration.class.getSimpleName() + "[", "]")
                .add("bindingIdentifier='" + bindingIdentifier + "'")
                .add("initializer=" + initializer)
                .toString();
    }
}
