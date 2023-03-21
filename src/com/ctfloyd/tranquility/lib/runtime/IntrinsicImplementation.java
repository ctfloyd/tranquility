package com.ctfloyd.tranquility.lib.runtime;

import com.ctfloyd.tranquility.lib.common.NumberUtils;

public class IntrinsicImplementation {

    // https://tc39.es/ecma262/#sec-isfinite-number
    public static Value isFinite(Realm realm, ArgumentList argumentList) {
        Value number = argumentList.getFirstArgument();
        // 1. Let num be ? ToNumber(number).
        NumberObject num = number._toNumber(realm);
        // 2. If num is not finite, return false.
        // 3. Otherwise, return true.
        return Value._boolean(!num.isInfinite());
    }

    // https://tc39.es/ecma262/#sec-isnan-number
    public static Value isNaN(Realm realm, ArgumentList argumentList) {
        Value number = argumentList.getFirstArgument();
        // 1. Let num be ? ToNumber(number).
        NumberObject num = number._toNumber(realm);
        // 2. If num is NaN, return true.
        // 3. Otherwise, return false.
        return Value._boolean(num.isNan());
    }

    // https://tc39.es/ecma262/#sec-parsefloat-string
    public static Value parseFloat(Realm realm, ArgumentList argumentList) {
        Value string = argumentList.getFirstArgument();
        // 1. Let inputString be ToString(string)
        Value inputString = string._toString();
        // 2. Let trimmedString be TrimString(inputString, start)
        Value trimmedString = inputString.trimString(TrimStringSpecifier.START);

        // 3. If neither trimmedString nor any prefix of trimmedString satisfies the syntax of a StrDecimalLiteral, return NaN.
        // 4. Let numberString be the longest prefix of trimmedString, which might be trimmedString itself, that satisfies
        // the syntax of a StrDecimalLiteral.

        // OPTIMIZATION: Keep track of the longest prefix as we go.
        String javaString = trimmedString.asString();
        String numberString = null;
        for (int i = 0; i <= javaString.length(); i++) {
            String prefix = javaString.substring(0, i);
            if (NumberUtils.isParseable(prefix)) {
                numberString = prefix;
            }
        }

        if (numberString == null) {
            return Value.object(NumberObject.createNaN(realm));
        }

        // TODO: Implement this piece properly.
        // 5. Let parsedNumber be ParseText(StringToCodePoints(numberString), StrDecimalLiteral).
        // 6. Assert: parsedNumber is a Parse Node.
        // 7. Return StringNumericValue of parsedNumber.
        return Value.number(NumberUtils.parse(numberString).doubleValue());
    }

}
