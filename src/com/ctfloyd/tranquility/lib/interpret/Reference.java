package com.ctfloyd.tranquility.lib.interpret;

import com.ctfloyd.tranquility.lib.common.StringUtils;

import java.util.StringJoiner;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class Reference {

    private final Value base;
    // FIXME: 6.2.5 This could also be a Symbol or a Private name
    private final String referencedName;
    private final boolean strict;
    private final Value thisValue;

    public Reference(Value base, String referencedName, boolean strict, Value thisValue) {
        ASSERT(base != null);
        ASSERT(StringUtils.isNotBlank(referencedName));
        this.base = base;
        this.referencedName = referencedName;
        this.strict = strict;
        this.thisValue = thisValue;
    }

    public Value getBase() {
        return base;
    }

    public String getReferencedName() {
        return referencedName;
    }

    public boolean isStrict() {
        return strict;
    }

    public Value getThisValue() {
        return thisValue;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Reference.class.getSimpleName() + "[", "]")
                .add("base=" + base)
                .add("referencedName='" + referencedName + "'")
                .add("strict=" + strict)
                .add("thisValue=" + thisValue)
                .toString();
    }
}
