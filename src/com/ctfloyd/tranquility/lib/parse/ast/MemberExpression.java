package com.ctfloyd.tranquility.lib.parse.ast;

import com.ctfloyd.tranquility.lib.runtime.JsObject;
import com.ctfloyd.tranquility.lib.runtime.Reference;
import com.ctfloyd.tranquility.lib.runtime.Runtime;
import com.ctfloyd.tranquility.lib.runtime.Value;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class MemberExpression extends Expression {

    private final Identifier object;
    private final Identifier property;
    private final boolean computed;

    public MemberExpression(Identifier object, Identifier property, boolean computed) {
        ASSERT(object != null);
        ASSERT(property != null);
        this.object = object;
        this.property = property;
        this.computed = computed;
    }

    public Identifier getObject() {
        return object;
    }

    private boolean isComputed() {
        return computed;
    }

    @Override
    public Value execute() {
        Reference reference = this.object.getReference();

//        Value propertyName;
//        if (isComputed()) {
//            propertyName = property.execute();
//        } else {
//            propertyName = Value.string(property.getName());
//        }
        JsObject object = reference.getValue(getRealm()).asObject();
        return object.get(property.getStringValue());
    }

    @Override
    public Reference getReference() {
        Reference baseReference = this.object.getReference();

        Value baseValue = baseReference.getValue(getRealm());

        Value propertyName;
        if (isComputed()) {
            propertyName = property.execute();
        } else {
            propertyName = Value.string(property.getStringValue());
        }

        return new Reference(baseValue, propertyName.asString(), false, null);
    }

    @Override
    public void setRuntime(Runtime runtime) {
        super.setRuntime(runtime);
        object.setRuntime(runtime);
        property.setRuntime(runtime);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("MemberExpression (");
        object.dump(indent + 1);
        property.dump(indent + 1);
        printIndent(indent);
        System.out.println(")");
    }

    @Override
    public boolean isMemberExpression() {
        return true;
    }
}
