package com.ctfloyd.tranquility.lib.runtime;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

// https://tc39.es/ecma262/#sec-completion-record-specification-type
public class CompletionRecord {

    private static final String EMPTY_TARGET = "";

    private final CompletionRecordType type;
    private final Object value;
    private final String target;

    public static CompletionRecord normal(Object value) {
        return new CompletionRecord(CompletionRecordType.NORMAL, value, EMPTY_TARGET);
    }

    public static CompletionRecord _throw(Object value) {
        return new CompletionRecord(CompletionRecordType.THROW, value, EMPTY_TARGET);
    }

    private CompletionRecord(CompletionRecordType type, Object value, String target) {
        ASSERT(type != null);
        ASSERT(value != null);
        ASSERT(target != null);
        this.type = type;
        this.value = value;
        this.target = target;
    }

    public boolean isThrow() {
        return type == CompletionRecordType.THROW;
    }

    public CompletionRecordType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public String getTarget() {
        return target;
    }
}
