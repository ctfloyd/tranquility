package com.ctfloyd.tranquility.lib.runtime;

import java.util.StringJoiner;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class NumberObject extends JsObject {

    private final double value;
    private final boolean positiveInfinity;
    private final boolean negativeInfinity;
    private final boolean notANumber;

    private NumberObject(double value) {
        this.value = value;
        this.positiveInfinity = false;
        this.negativeInfinity = false;
        this.notANumber = false;
    }

    private NumberObject(boolean positiveInfinity, boolean negativeInfinity) {
        if (positiveInfinity || negativeInfinity) {
            ASSERT(positiveInfinity != negativeInfinity);
        }
        this.value = 0;
        this.positiveInfinity = positiveInfinity;
        this.negativeInfinity = negativeInfinity;
        this.notANumber = false;
    }

    private NumberObject(boolean positiveInfinity, boolean negativeInfinity, boolean notANumber) {
        if (positiveInfinity || negativeInfinity) {
            ASSERT(positiveInfinity != negativeInfinity);
        }
        this.value = 0;
        this.positiveInfinity = positiveInfinity;
        this.negativeInfinity = negativeInfinity;
        this.notANumber = notANumber;
    }

    public static NumberObject create(Realm realm, double _double) {
        NumberObject number = new NumberObject(_double);
        number.setPrototypeOf(realm.getIntrinsics().get(Intrinsic.NUMBER));
        return number;
    }

    public static NumberObject createPositiveInfinity(Realm realm) {
        NumberObject number = new NumberObject(true, false);
        number.setPrototypeOf(realm.getIntrinsics().get(Intrinsic.NUMBER));
        return number;
    }

    public static NumberObject createNegativeInfinity(Realm realm) {
        NumberObject number = new NumberObject(false, true);
        number.setPrototypeOf(realm.getIntrinsics().get(Intrinsic.NUMBER));
        return number;
    }

    public static NumberObject createNaN(Realm realm) {
        NumberObject number = new NumberObject(false, false, true);
        number.setPrototypeOf(realm.getIntrinsics().get(Intrinsic.NUMBER));
        return number;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean isNumberObject() {
        return true;
    }

    public boolean isInfinite() {
        return positiveInfinity || negativeInfinity;
    }

    public boolean isNan() {
        return notANumber;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NumberObject.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .add("positiveInfinity=" + positiveInfinity)
                .add("negativeInfinity=" + negativeInfinity)
                .toString();
    }
}
