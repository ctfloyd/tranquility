package com.ctfloyd.tranquility.lib.tokenize;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class TokenStream {

    private final List<Token> tokens;
    private int index;

    public TokenStream(List<Token> tokens) {
        ASSERT(tokens != null);
        this.tokens = tokens;
    }

    public boolean hasTokens() {
        return index < tokens.size();
    }

    public Token take() {
        return tokens.get(index++);
    }

    public Token take(TokenType tokenType) {
        Token token = take();
        ASSERT(token.getType() == tokenType, "Unexpected token type! Expected: " + tokenType + " but received " + token.getType());
        return token;
    }

    public Token peek() {
        return peek(1);
    }

    public Token peek(int amount) {
        return tokens.get(index + amount - 1);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TokenStream.class.getSimpleName() + "[", "]")
                .add("tokens=" + tokens)
                .add("index=" + index)
                .toString();
    }
}
