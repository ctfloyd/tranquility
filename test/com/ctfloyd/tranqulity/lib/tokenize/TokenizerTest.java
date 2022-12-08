package com.ctfloyd.tranqulity.lib.tokenize;

import com.ctfloyd.tranquility.lib.tokenize.Token;
import com.ctfloyd.tranquility.lib.tokenize.TokenType;
import com.ctfloyd.tranquility.lib.tokenize.Tokenizer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ctfloyd.tranquility.lib.common.Assert.assertNotReached;
import static com.ctfloyd.tranquility.lib.common.Assert.assertTrue;

public class TokenizerTest {

    private void tokenize_givenSimpleComment_shouldEmitComment() {
        String input = "//this is a comment";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        List<Token> tokens = tokenizer.tokenize();
        assertTrue("An unexpected number of tokens was emitted", tokens.size() == 1);
        Token expectedToken = tokens.get(0);
        assertTrue("An unexpected token type was emitted", expectedToken.getType() == TokenType.COMMENT);
        assertTrue("An unexpected token value was emitted", expectedToken.getValue().equals("this is a comment"));
    }

    private void tokenize_givenMultilineComment_shouldEmitMultiLineComment() {
        String input = "/*this is a multiline\ncomment*/";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        List<Token> tokens = tokenizer.tokenize();
        assertTrue("An unexpected number of tokens was emitted", tokens.size() == 1);
        Token expectedToken = tokens.get(0);
        assertTrue("An unexpected token type was emitted", expectedToken.getType() == TokenType.MULTI_LINE_COMMENT);
        assertTrue("An unexpected token value was emitted", expectedToken.getValue().equals("this is a multiline\ncomment"));
    }

    private void tokenize_givenKeyword_shouldEmitKeywordToken() {
        String input = "for";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        List<Token> tokens = tokenizer.tokenize();
        assertTrue("An unexpected number of tokens was emitted", tokens.size() == 1);
        Token expectedToken = tokens.get(0);
        assertTrue("An unexpected token type was emitted", expectedToken.getType() == TokenType.FOR);
    }

    private void tokenize_givenReservedKeyword_shouldEmitReservedToken() {
        String input = "debugger";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        List<Token> tokens = tokenizer.tokenize();
        assertTrue("An unexpected number of tokens was emitted", tokens.size() == 1);
        Token expectedToken = tokens.get(0);
        assertTrue("An unexpected token type was emitted", expectedToken.getType() == TokenType.RESERVED);
    }

    private void tokenize_givenTwoKeywords_shouldEmitTwoKeywords() {
        String input = "break continue";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        List<Token> tokens = tokenizer.tokenize();
        assertTrue("An unexpected number of tokens was emitted", tokens.size() == 2);
        Token breakToken = tokens.get(0);
        Token continueToken = tokens.get(1);
        assertTrue("Expected 'BREAK' token emitted, but another type of token was emitted instead", breakToken.getType() == TokenType.BREAK);
        assertTrue("Expected 'CONTINUE' token emitted, but another type of token was emitted instead", continueToken.getType() == TokenType.CONTINUE);
    }

    private void tokenize_givenIdentifier_shouldEmitIdentifierToken() {
        String input = "identifier";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        List<Token> tokens = tokenizer.tokenize();
        assertTrue("An unexpected number of tokens was emitted", tokens.size() == 1);
        Token identifierToken = tokens.get(0);
        assertTrue("Expected 'IDENTIFIER' token emitted, but another type of token was emitted instead", identifierToken.getType() == TokenType.IDENTIFIER);
        assertTrue("Expected IDENTIFIER value to be 'identifier'", identifierToken.getValue().equals("identifier"));
    }

    private void tokenize_givenIdentifierWithInvalidCharacter_shouldEmitError() {
        String input = "identifier!";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        try  {
            tokenizer.tokenize();
        } catch (Exception ex) {
            // expected
        }
    }

    private void tokenize_givenAllPunctuation_expectUniqueTokensForAll() {
        String input = "= > < == <= >= != , ! ~ ? : . && || ++ -- + - * / & | ^ % << >> >>> += -= *= /= &= |= ^= %= <<=" +
                " >>= >>>= ( ) { } [ ] ;";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        List<Token> tokens = tokenizer.tokenize();
        assertTrue("Expected 46 tokens", tokens.size() == 46);
        Set<TokenType> seenTypes = new HashSet<>();
        for (Token t : tokens) {
            if (seenTypes.contains(t.getType())) {
                assertNotReached("Token type: " + t.getType() + " was duplicated.");
            } else {
                seenTypes.add(t.getType());
            }
        }
    }

    private void tokenize_givenVarAndIdentifier_shouldEmitVarAndIdentifierToken() {
        String input = "var identifier";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        List<Token> tokens = tokenizer.tokenize();
        assertTrue("An unexpected number of tokens was emitted", tokens.size() == 2);
        Token varToken = tokens.get(0);
        assertTrue("Expected 'VAR' token emitted, but another type of token was emitted instead", varToken.getType() == TokenType.VAR);
        Token identifierToken = tokens.get(1);
        assertTrue("Expected 'IDENTIFIER' token emitted, but another type of token was emitted instead", identifierToken.getType() == TokenType.IDENTIFIER);
        assertTrue("Expected IDENTIFIER value to be 'identifier'", identifierToken.getValue().equals("identifier"));
    }

    private void tokenize_givenTrueBooleanLiteral_shouldEmitBooleanLiteral() {
        String input = "true";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        List<Token> tokens = tokenizer.tokenize();
        assertTrue("An unexpected number of tokens was emitted", tokens.size() == 1);
        Token booleanToken = tokens.get(0);
        assertTrue("Expected 'BOOLEAN_LITERAL' token emitted, but another type of token was emitted instead", booleanToken.getType() == TokenType.BOOLEAN_LITERAL);
        assertTrue("Expected 'BOOLEAN_LITERAL' token to have value 'true'", booleanToken.getValue().equals("true"));
    }

    private void tokenize_givenFalseBooleanLiteral_shouldEmitBooleanLiteral() {
        String input = "false";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        List<Token> tokens = tokenizer.tokenize();
        assertTrue("An unexpected number of tokens was emitted", tokens.size() == 1);
        Token booleanToken = tokens.get(0);
        assertTrue("Expected 'BOOLEAN_LITERAL' token emitted, but another type of token was emitted instead", booleanToken.getType() == TokenType.BOOLEAN_LITERAL);
        assertTrue("Expected 'BOOLEAN_LITERAL' token to have value 'true'", booleanToken.getValue().equals("false"));
    }

    private void tokenize_givenNullLiteral_shouldEmitNullLiteral() {
        String input = "null";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        List<Token> tokens = tokenizer.tokenize();
        assertTrue("An unexpected number of tokens was emitted", tokens.size() == 1);
        Token nullLiteral = tokens.get(0);
        assertTrue("Expected 'NULL_LITERAL' token emitted, but another type of token was emitted instead", nullLiteral.getType() == TokenType.NULL_LITERAL);
    }

    private void tokenize_givenStringLiteral_shouldEmitStringLiteral() {
        String input = "\"foo\"";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        List<Token> tokens = tokenizer.tokenize();
        assertTrue("An unexpected number of tokens was emitted", tokens.size() == 1);
        Token stringLiteral = tokens.get(0);
        assertTrue("Expected 'STRING_LITERAL' token emitted, but another type of token was emitted instead", stringLiteral.getType() == TokenType.STRING_LITERAL);
        assertTrue("Expected 'STRING_LITERAL' token to have value 'foo'", stringLiteral.getValue().equals("foo"));
    }

    private void tokenize_givenNumericLiteral_shouldEmitNumericLiteral() {
        String input = "1234";
        Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        List<Token> tokens = tokenizer.tokenize();
        assertTrue("An unexpected number of tokens was emitted", tokens.size() == 1);
        Token numericLiteral = tokens.get(0);
        assertTrue("Expected 'NUMERIC_LITERAL' token emitted, but another type of token was emitted instead", numericLiteral.getType() == TokenType.NUMERIC_LITERAL);
        assertTrue("Expected 'STRING_LITERAL' token to have value '1234'", numericLiteral.getValue().equals("1234"));
    }


    public void test() {
        tokenize_givenSimpleComment_shouldEmitComment();
        tokenize_givenMultilineComment_shouldEmitMultiLineComment();
        tokenize_givenKeyword_shouldEmitKeywordToken();
        tokenize_givenReservedKeyword_shouldEmitReservedToken();
        tokenize_givenTwoKeywords_shouldEmitTwoKeywords();
        tokenize_givenIdentifier_shouldEmitIdentifierToken();
        tokenize_givenVarAndIdentifier_shouldEmitVarAndIdentifierToken();
        tokenize_givenIdentifierWithInvalidCharacter_shouldEmitError();
        tokenize_givenAllPunctuation_expectUniqueTokensForAll();
        tokenize_givenNullLiteral_shouldEmitNullLiteral();
        tokenize_givenNumericLiteral_shouldEmitNumericLiteral();
        tokenize_givenTrueBooleanLiteral_shouldEmitBooleanLiteral();
        tokenize_givenFalseBooleanLiteral_shouldEmitBooleanLiteral();
        tokenize_givenStringLiteral_shouldEmitStringLiteral();
    }

    public static void main(String[] args) {
        new TokenizerTest().test();
    }

}
