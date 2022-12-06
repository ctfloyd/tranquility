package com.ctfloyd.tranquility.lib.tokenize;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tokenizer {

    // tab, vertical tab, form feed, space
    private static final Set<Character> WHITESPACE = Stream.of((char) 0x09, (char) 0x0B, (char) 0x0C, (char) 0x20)
            .collect(Collectors.toSet());
    private static final Set<Character> LINE_TERMINATORS = Stream.of((char) 0x0A, (char) 0x0D)
            .collect(Collectors.toSet());
    private static final Set<Character> COMMENT = Stream.of('/', '*').collect(Collectors.toSet());
    private static final Set<String> KEYWORDS = Stream.of("break", "for", "new", "var", "continue", "function", "return",
            "void", "delete", "if", "this", "while", "else", "in", "typeof", "with").collect(Collectors.toSet());
    private static final Set<String> RESERVED_KEYWORDS = Stream.of("case", "debugger", "export", "super", "catch",
            "default", "extends", "switch", "class", "do", "finally", "throw", "const", "enum", "import", "try").collect(Collectors.toSet());

    private static final int END_OF_FILE_CHARACTER = -1;

    // input
    private char[] sourceCharacters;
    private int index;

    // state
    private final StringBuilder stringBuffer;
    private final StringBuilder scratch;
    private boolean inMultilineComment;
    private boolean inComment;
    // keyword, reserved keyword, identifier, or literals, all require 'scratch' space
    private boolean inScratchSpace;


    private boolean tokenized;
    private List<Token> tokens;

    public Tokenizer(char[] sourceCharacters) {
        this.sourceCharacters = sourceCharacters;
        this.index = 0;
        this.tokenized = false;
        this.stringBuffer = new StringBuilder(1024);
        this.scratch = new StringBuilder(128);
        this.tokens = new ArrayList<>();
    }

    public List<Token> tokenize() {
        if (tokenized) {
            this.index = 0;
            this.tokenized = false;
        }

        List<Token> tokens = tokenizeInternal();

        this.tokenized = true;
        return tokens;
    }


    private List<Token> tokenizeInternal() {
        for (;;) {
            int token = getCharacter();

            if (!inCommentOfAnyType() && isComment(token) && isComment(peek())) {
                if (peek() == '*') {
                    inMultilineComment = true;
                } else {
                    inComment = true;
                }
                skip();
                continue;
            }

            if (inCommentOfAnyType()) {
                if (inComment) {
                    if (isLineTerminator(token) || token == END_OF_FILE_CHARACTER) {
                        emit(TokenType.COMMENT, finishString());
                        inComment = false;
                        inMultilineComment = false;
                        continue;
                    } else {
                        append(token);
                        continue;
                    }
                } else if (inMultilineComment) {
                    if (token == '*' && peek() == '/') {
                        emit(TokenType.MULTI_LINE_COMMENT, finishString());
                        index += 1;
                        inComment = false;
                        inMultilineComment = false;
                        continue;
                    } else {
                        append(token);
                        continue;
                    }
                }
            }

            if (scratchHasContent() && isScratchSpaceTerminator(token)) {
                if (scratchHasKeywordOrReservedWord()){
                    String scratch = finishScratch();
                    emit(getTokenTypeForKeywordOrReservedWord(scratch));
                    continue;
                }

                if (scratchHasNumericLiteral()) {
                    // Just hand off the string representation to the parser to deal with.
                    String scratch = finishScratch();
                    emit(TokenType.NUMERIC_LITERAL, scratch);
                    continue;
                }

                if (scratchHasStringLiteral()) {
                    String scratch = finishScratch();
                    // Chop off the quotes
                    emit(TokenType.STRING_LITERAL, scratch.substring(1, scratch.length() - 1));
                    continue;
                }

                if (scratchHasBooleanLiteral())  {
                    String scratch = finishScratch();
                    emit(TokenType.BOOLEAN_LITERAL, scratch);
                    continue;
                }

                if (scratchHasNullLiteral()) {
                    String scratch = finishScratch();
                    emit(TokenType.NULL_LITERAL);
                    continue;
                }

                String identifier = finishScratch();
                emit(TokenType.IDENTIFIER, identifier);
                inScratchSpace = false;
                // continue parsing because the current token hasn't been handled
            }

            if (isWhitespace(token)) {
                // eat whitespace that aren't part of comments
                continue;
            }

            if (isStartOfPunctuator(token)) {
                TokenType punctuatorType = determinePunctuator(token);
                emit(punctuatorType);
                continue;
            }

            if (!inScratchSpace && (isStartOfKeywordOrReservedWord(token) || isValidIdentifier(token) || isValidStartOfLiteral(token))) {
                inScratchSpace = true;
                appendScratch(token);
                continue;
            }

            if (inScratchSpace && isStartOfKeywordOrReservedWord(token) || isValidIdentifier(token) || isValidStartOfLiteral(token)) {
                appendScratch(token);
                continue;
            }

            if (token == END_OF_FILE_CHARACTER) {
                break;
            }

            // FIXME: It would be nice to know line numbers and line indexes from the original input that caused the failure.
            throw new IllegalStateException("Did not know how to process token: " + ((char) token) + " at index: " + index);
        }

        List<Token> someTokens = tokens;
        tokens = new ArrayList<>();
        return someTokens;
    }

    private void emit(TokenType tokenType, String tokenValue) {
        tokens.add(new Token(tokenType, tokenValue));
    }

    private void emit(TokenType tokenType) {
        tokens.add(new Token(tokenType));
    }

    private void append(int token) {
        stringBuffer.append((char) token);
    }

    private void appendScratch(int token) {
        scratch.append((char) token);
    }

    private String finishString() {
        String tmp = stringBuffer.toString();
        stringBuffer.setLength(0);
        return tmp;
    }

    private boolean isScratchSpaceTerminator(int token) {
        return isStartOfPunctuator(token) || isWhitespace(token) || token == END_OF_FILE_CHARACTER;
    }

    private boolean scratchHasContent() {
        return scratch.length() > 0;
    }

    private boolean scratchHasNumericLiteral() {
        // FIXME: Only supports positive decimal literals for now, but should eventually support hexadecimal, octal, signed numbers,
        // exponents, etc.
        String currentScratch = scratch.toString();
        if (currentScratch.isEmpty()) {
            return false;
        }

        for (int i = 0; i < currentScratch.length(); i++) {
            if (!Character.isDigit(currentScratch.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean scratchHasBooleanLiteral() {
        String currentScratch = scratch.toString();
        return currentScratch.equals("true") || currentScratch.equals("false");
    }

    private boolean scratchHasNullLiteral() {
        String currentScratch = scratch.toString();
        return currentScratch.equals("null");
    }

    private boolean scratchHasStringLiteral() {
        if (scratch.length() == 1) {
            return false;
        }

        // FIXME: There are more edge cases defined in the specification: escaped characters, unicode codepoints, etc.
        return scratch.charAt(0) == '"' && scratch.charAt(scratch.length() - 1) == '"';
    }

    private boolean scratchHasKeywordOrReservedWord() {
        String currentScratch = scratch.toString();
        return KEYWORDS.contains(currentScratch) || RESERVED_KEYWORDS.contains(currentScratch);
    }

    private TokenType getTokenTypeForKeywordOrReservedWord(String word) {
        if (RESERVED_KEYWORDS.contains(word)) {
            return TokenType.RESERVED;
        }

        for (TokenType tokenType : TokenType.values()) {
            if (tokenType.name().equalsIgnoreCase(word)) {
                return tokenType;
            }
        }

        throw new IllegalStateException("Could not find corresponding token type for keyword: " + word);
    }

    private TokenType determinePunctuator(int token) {
        if (token == '=') {
            if (peek() == '=') {
                skip();
                return TokenType.EQUALITY;
            }
            return TokenType.ASSIGNMENT;
        }

        if (token == '>') {
            if (peek() == '>') {
                if (peek(2) == '>') {
                    if (peek(3) == '=') {
                        skip(3);
                        return TokenType.UNSIGNED_BIT_SHIFT_RIGHT_EQUALS;
                    }

                    skip(2);
                    return TokenType.UNSIGNED_BIT_SHIFT_RIGHT;
                }

                if (peek(2) == '=') {
                    skip(2);
                    return TokenType.BIT_SHIFT_RIGHT_EQUALS;
                }

                skip();
                return TokenType.BIT_SHIFT_RIGHT;
            }

            if (peek() == '=') {
                skip();
                return TokenType.GREATER_THAN_EQUALS;
            }

            return TokenType.GREATER_THAN;
        }

        if (token == '<') {
            if (peek() == '<') {
                if (peek(2) == '=') {
                    skip(2);
                    return TokenType.BIT_SHIFT_LEFT_EQUALS;
                }

                skip();
                return TokenType.BIT_SHIFT_LEFT;
            }

            if (peek() == '=') {
                skip();
                return TokenType.LESS_THAN_EQUALS;
            }

            return TokenType.LESS_THAN;
        }

        if (token == '!') {
            if (peek() == '=') {
                skip();
                return TokenType.DOES_NOT_EQUAL;
            }

            return TokenType.EXCLAMATION;
        }

        if (token == ',') {
            return TokenType.COMMA;
        }

        if (token == '~') {
            return TokenType.TILDE;
        }

        if (token == '?') {
            return TokenType.QUESTION_MARK;
        }

        if (token == ':') {
            return TokenType.COLON;
        }

        if (token == '&') {
            if (peek() == '&') {
                skip();
                return TokenType.AND;
            }

            if (peek() == '=') {
                skip();
                return TokenType.BITWISE_AND_EQUALS;
            }

            return TokenType.BITWISE_AND;
        }

        if (token == '|') {
            if (peek() == '|') {
                skip();
                return TokenType.OR;
            }

            if (peek() == '=') {
                skip();
                return TokenType.BITWISE_OR_EQUALS;
            }

            return TokenType.BITWISE_OR;
        }

        if (token == '+') {
            if (peek() == '+') {
                skip();
                return TokenType.INCREMENT;
            }

            if (peek() == '=') {
                skip();
                return TokenType.PLUS_EQUALS;
            }

            return TokenType.PLUS;
        }

        if (token == '-') {
            if (peek() == '-') {
                skip();
                return TokenType.DECREMENT;
            }

            if (peek() == '=') {
                skip();
                return TokenType.MINUS_EQUALS;
            }

            return TokenType.MINUS;
        }

        if (token == '*') {
            if (peek() == '=') {
                skip();
                return TokenType.MULTIPLY_EQUALS;
            }

            return TokenType.MULTIPLY;
        }

        if (token == '/') {
            if (peek() == '=') {
                skip();
                return TokenType.DIVIDE_EQUALS;
            }

            return TokenType.DIVIDE;
        }

        if (token == '^') {
            if (peek() == '=') {
                skip();
                return TokenType.BITWISE_XOR_EQUALS;
            }

            return TokenType.BITWISE_XOR;
        }

        if (token == '(') {
            return TokenType.LEFT_PARENTHESIS;
        }

        if (token == ')') {
            return TokenType.RIGHT_PARENTHESIS;
        }

        if (token == '{') {
            return TokenType.LEFT_CURLY_BRACE;
        }

        if (token == '}') {
            return TokenType.RIGHT_CURLY_BRACE;
        }

        if (token == '[') {
            return TokenType.LEFT_SQUARE_BRACKET;
        }

        if (token == ']') {
            return TokenType.RIGHT_SQUARE_BRACKET;
        }

        if (token == ';') {
            return TokenType.SEMICOLON;
        }

        if (token == '.') {
            return TokenType.PERIOD;
        }

        if (token == '%'){
            if (peek() == '=') {
                skip();
                return TokenType.MODULO_EQUALS;
            }

            return TokenType.MODULO;
        }

        throw new IllegalStateException("Could not determine punctuation for start token: " + token + " at index: " + index);
    }

    private String finishScratch() {
        String tmp = scratch.toString();
        scratch.setLength(0);
        return tmp;
    }

    private void skip() {
        skip(1);
    }

    private void skip(int amount) {
        index += amount;
    }

    private int getCharacter() {
        if (index >= sourceCharacters.length) {
            return -1;
        }

        return sourceCharacters[index++];
    }

    private int peek() {
        return peek(1);
    }

    private int peek(int amount) {
        if (index + amount - 1 >= sourceCharacters.length) {
            return -1;
        }

        return sourceCharacters[index + amount - 1];
    }

    private boolean inCommentOfAnyType() {
        return inMultilineComment || inComment;
    }

    private boolean isWhitespace(int ch) {
        return WHITESPACE.contains((char) ch);
    }

    private boolean isComment(int ch) {
        return COMMENT.contains((char) ch);
    }

    private boolean isLineTerminator(int ch) {
        return LINE_TERMINATORS.contains((char) ch);
    }

    private boolean isValidIdentifier(int ch) {
        return Character.isLetter(ch) || Character.isDigit(ch) || ch == '$' || ch == '_';
    }

    private boolean isValidStartOfLiteral(int ch) {
        return Character.isLetter(ch) || Character.isDigit(ch) || ch == '"';
    }

    private boolean isStartOfPunctuator(int ch) {
        return ch == '=' || ch == '>' || ch == '<' || ch == '!' || ch == ',' || ch == '~' || ch == '?' || ch == ':' ||
                ch == '.' || ch == '&' || ch == '|' || ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^'
                || ch == '(' || ch == ')' || ch == '{' || ch == '}' || ch == '[' || ch == ']' || ch == ';' || ch == '%';
    }

    private boolean isStartOfKeywordOrReservedWord(int ch) {
        for (String keyword : KEYWORDS) {
            if (keyword.charAt(0) == ch) {
                return true;
            }
        }
        for (String keyword : RESERVED_KEYWORDS) {
            if (keyword.charAt(0) == ch) {
                return true;
            }
        }
        return false;
    }
}
