package com.ctfloyd.tranquility.lib.interpret;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class StringPrototype extends JsObject {

    public StringPrototype() {
        putNativeFunction("split", this::split);
        putNativeFunction("at", this::at);
        putNativeFunction("charAt", this::charAt);
        putNativeFunction("endsWith", this::endsWith);
        putNativeFunction("includes", this::includes);
    }

    private Value split(AstInterpreter interpreter, List<Value> arguments) {
        Value unknown = interpreter.getThisValue();
        ASSERT(unknown.isObject());
        ASSERT(unknown.asObject().isStringObject());

        StringObject stringObject = (StringObject) unknown.asObject();
        String string = stringObject.getString();

        Value separatorValue = arguments.size() >= 1 ? arguments.get(0) : Value.undefined();
        ASSERT(separatorValue.isUndefined() || separatorValue.isString());
        String separator = "undefined";
        if (!separatorValue.isUndefined()){
            separator = separatorValue.asString();
        }

        // 4. If limit is undefined, let lim be 2^32 - 1; else let lim be limit.
        int limit = Integer.MAX_VALUE;
        Value limitValue = arguments.size() == 2 ? arguments.get(1) : Value.undefined();
        if (!limitValue.isUndefined()) {
            limit = limitValue.asInteger();
        }

        // 6. If lim = 0 then return CreateArrayFromList(<< >>);
        if (limit == 0) {
            return Value.object(ArrayObject.create(interpreter, Collections.emptyList()));
        }

        // 7. If separator is undefined, then
        if (separator.equals("undefined")) {
            return Value.object(ArrayObject.create(interpreter, Collections.singletonList(Value.string(string))));
        }

        // 8. Let separatorLength be then length of R
        int separatorLength = separator.length();

        // 9. If separator length is 0, then
        if (separatorLength == 0) {
            // a. Let head be the substring of S from 0 to limit
            String head = string.substring(0, Math.min(string.length(), limit));
            // b. Let codeUnits be a List consisting of the sequence of code units that are elements of head.
            List<Value> codeUnits = new ArrayList<>();
            for (int i = 0; i < head.length(); i++) {
                codeUnits.add(Value.string("" + head.charAt(i)));
            }
            // c. return CreateArrayFromList(codeUnits);
            return Value.object(ArrayObject.create(interpreter, codeUnits));
        }

        // 10. If S is the empty String, return CreateArrayFromList(<< S >>).
        if (string.isEmpty()) {
            return Value.object(ArrayObject.create(interpreter, Collections.singletonList(Value.string(string))));
        }

        // 11. Let substrings be a new empty List
        List<Value> substrings = new ArrayList<>();

        // 12. Let i be 0.
        int i = 0;

        // 13. Let j be StringIndexOf(S, R, 0).
        int j = string.indexOf(separator, 0);

        // 14. Repeat, while j is not -1
        while (j != -1) {
            // a. Let T be the substring of string from i to j.
            String substring = string.substring(i, j);
            // b. Append T to substring
            substrings.add(Value.string(substring));
            // c. If the number of elements of substring is limit, return CreateArrayFromList(substrings);
            if (substrings.size() == limit) {
                return Value.object(ArrayObject.create(interpreter, substrings));
            }
            // d. Set i to j + separatorLength
            i = j + separatorLength;
            // e. Set j to StringIndexOf(S, R, i)
            j = string.indexOf(separator, i);
        }

        // 15. Let T be the substring of S from i
        String substring = string.substring(i);
        // 16. Append T to substrings
        substrings.add(Value.string(substring));
        // 17. Return CreateArrayFromList(substrings);
        return Value.object(ArrayObject.create(interpreter, substrings));
    }

    // https://tc39.es/ecma262/#sec-string.prototype.at
    private Value at(AstInterpreter interpreter, List<Value> arguments) {
        // 1.  Let O be the RequireObjectCoercible (this value).
        Value unknown = interpreter.getThisValue();
        ASSERT(unknown.isObject());
        ASSERT(unknown.asObject().isStringObject());
        StringObject stringObject = (StringObject) unknown.asObject();
        // 2. Let S be ? ToString(O);
        String string = stringObject.getString();
        // 3. Let len be the length of S
        int len = string.length();
        // FIXME: Infinity isn't implemented yet
        // 4. Let relativeIndex be ? ToIntegerOrInfinity(index)
        int relativeIndex = arguments.size() >= 1 ? arguments.get(0).asInteger() : Integer.MAX_VALUE;
        // 5. If relativeIndex >= 0, then
        int k;
        if (relativeIndex >= 0) {
            // a. let k be relativeIndex
            k = relativeIndex;
        } else {
            // 6. Else, a. Let k ben len + relativeIndex
            k = len + relativeIndex;
        }
        // 7. If k < 0 or k >= len, return undefined
        if (k < 0 || k >= len) {
            return Value.undefined();
        }
        // 8. Return the substring of S from k to k + 1
        return Value.string(string.substring(k, k + 1));
    }

    // https://tc39.es/ecma262/#sec-string.prototype.charAt
    private Value charAt(AstInterpreter interpreter, List<Value> arguments) {
        // 1.  Let O be the RequireObjectCoercible (this value).
        Value unknown = interpreter.getThisValue();
        ASSERT(unknown.isObject());
        ASSERT(unknown.asObject().isStringObject());
        StringObject stringObject = (StringObject) unknown.asObject();
        // 2. Let S be ? ToString(O);
        String string = stringObject.getString();
        // FIXME: Infinity isn't implemented yet
        // 3. Let position be ? ToIntegerOrInfinity(pos)
        int position = arguments.size() >= 1 ? arguments.get(0).asInteger() : Integer.MAX_VALUE;
        // 4. Let size be the length of S
        int size = string.length();
        // 5. If position < 0 or position >= size, return the empty String.
        if (position < 0 || position >= size) {
            return Value.string("");
        }
        // 6. Return the substring of S from position to position + 1
        return Value.string(string.substring(position, position + 1));
    }

    // https://tc39.es/ecma262/#sec-string.prototype.endsWith
    private Value endsWith(AstInterpreter interpreter, List<Value> arguments) {
        // 1.  Let O be the RequireObjectCoercible (this value).
        Value unknown = interpreter.getThisValue();
        ASSERT(unknown.isObject());
        ASSERT(unknown.asObject().isStringObject());
        StringObject stringObject = (StringObject) unknown.asObject();
        // 2. Let S be ? ToString(O);
        String string = stringObject.getString();
        // FIXME: 3 and 4 ask about RegExp
        // 5. Let searchStr be ? ToString(searchString);
        Value searchStringArgument = arguments.size() >= 1 ? arguments.get(0) : Value.undefined();
        String searchString = searchStringArgument._toString(interpreter);
        // 6. Let len be the length of S
        int len = string.length();
        // FIXME: Infinity is not implemented yet
        // 7. If endPosition is undefined, let pos be len; else let pos b ? ToIntegerOrInfinity(endPosition);
        Value endPositionValue = arguments.size() == 2 ? arguments.get(1) : Value.undefined();
        int pos;
        if (endPositionValue.isUndefined()) {
            pos = len;
        } else {
            pos = endPositionValue.asInteger();
        }
        // 8. Let end be the result of clamping pos between 0 and len.
        int end = Math.max(0, Math.min(pos, len));
        // 9. Let searchLength be the length of searchString
        int searchLength = searchString.length();
        // 10. If searchLength = 0, return true
        if (searchLength == 0) {
            return Value._boolean(true);
        }
        // 11. Let start be end - searchLength
        int start = end - searchLength;
        // 12. If start < 0, return false
        if (start < 0) {
            return Value._boolean(false);
        }
        // 13. Let substring be the substring of S from start to end
        String substring = string.substring(start, end);
        // 14. If substring is searchStr, return true
        return Value._boolean(substring.equals(searchString));
    }

    // https://tc39.es/ecma262/#sec-string.prototype.includes
    private Value includes(AstInterpreter interpreter, List<Value> arguments) {
        // 1.  Let O be the RequireObjectCoercible (this value).
        Value unknown = interpreter.getThisValue();
        ASSERT(unknown.isObject());
        ASSERT(unknown.asObject().isStringObject());
        StringObject stringObject = (StringObject) unknown.asObject();
        // 2. Let S be ? ToString(O);
        String string = stringObject.getString();
        // FIXME: 3 and 4 ask about RegExp
        // 5. Let searchStr be ? ToString(searchString);
        String searchString = arguments.get(0).asString();
        // 6. Let pos be ? ToIntegerOrInfinity(position);
        // FIXME: Infinity not implemented
        int pos = arguments.size() == 2 ? arguments.get(1).isUndefined() ? 0 : arguments.get(1).asInteger() : 0;
        // 7. Assert: If position is undefined, then pos is 0
        if (arguments.size() < 2 || arguments.get(1).isUndefined()) {
            ASSERT(pos == 0);
        }
        // 8. Let len be the length of S
        int len = string.length();
        // 9. Let start be the result of clamping pos between 0 and len
        int start = Math.max(0, Math.min(pos, len));
        // 10. Let index be StringIndexOf(S, searchStr, start)
        int index = string.indexOf(searchString, start);
        // 11. If is not -1 return true - 12. Return false
        return Value._boolean(index != -1);
    }
}
