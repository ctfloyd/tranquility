package com.ctfloyd.tranquility.lib.parse;

import com.ctfloyd.tranquility.lib.ast.AstNode;
import com.ctfloyd.tranquility.lib.ast.BinaryExpression;
import com.ctfloyd.tranquility.lib.ast.BinaryExpressionOperator;
import com.ctfloyd.tranquility.lib.ast.BlockStatement;
import com.ctfloyd.tranquility.lib.ast.CallExpression;
import com.ctfloyd.tranquility.lib.ast.ExpressionStatement;
import com.ctfloyd.tranquility.lib.ast.FunctionDeclaration;
import com.ctfloyd.tranquility.lib.ast.Identifier;
import com.ctfloyd.tranquility.lib.ast.NumericLiteral;
import com.ctfloyd.tranquility.lib.ast.Program;
import com.ctfloyd.tranquility.lib.ast.ReturnStatement;
import com.ctfloyd.tranquility.lib.ast.StringLiteral;
import com.ctfloyd.tranquility.lib.tokenize.Token;
import com.ctfloyd.tranquility.lib.tokenize.TokenStream;
import com.ctfloyd.tranquility.lib.tokenize.TokenType;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

public class Parser {

    private final TokenStream tokenStream;
    private Token currentToken;
    private Token previousToken;

    public Parser(TokenStream tokenStream) {
        ASSERT(tokenStream != null);
        this.tokenStream = tokenStream;
    }

    public Program parse() {
        Program program = new Program();
        consume();
        while (!done()) {
            AstNode node = parseStatement();
            program.addChild(node);
        }
        return program;
    }

    // FIXME: Instead of returning AstNode everywhere, we should make stricter constraints about which *type* of AstNode
    // is being returned
    private AstNode parseStatement() {
        if (matchesExpression()) {
            AstNode expression = parseExpression();
            return new ExpressionStatement(expression);
        }

        TokenType type = currentToken.getType();
        if (type == TokenType.FUNCTION) {
            return parseFunctionDeclaration();
        } else if (type == TokenType.LEFT_CURLY_BRACE) {
            return parseBlockStatement();
        } else if (type == TokenType.RETURN) {
            return parseReturnStatement();
        } else {
            ASSERT(false, "Do not know how to handle token: " + currentToken + " for parse statement.");
            return null;
        }
    }

    private FunctionDeclaration parseFunctionDeclaration() {
        consume(TokenType.FUNCTION);
        String functionName = consume(TokenType.IDENTIFIER).getValue();
        consume(TokenType.LEFT_PARENTHESIS);
        // FIXME: Support parsing function parameters
        consume(TokenType.RIGHT_PARENTHESIS);
        BlockStatement functionBody = parseBlockStatement();
        return new FunctionDeclaration(functionName, functionBody);
    }

    private BlockStatement parseBlockStatement() {
        BlockStatement block = new BlockStatement();
        consume(TokenType.LEFT_CURLY_BRACE);
        while (!done() && !match(TokenType.RIGHT_CURLY_BRACE)) {
            // FIXME: Support consuming semicolons
            if (matchesStatement()) {
                block.addChild(parseStatement());
            } else {
                ASSERT(false, "Should only match statements in block statements. Couldn't match: " + currentToken);
            }
        }
        consume(TokenType.RIGHT_CURLY_BRACE);
        return block;
    }

    private ReturnStatement parseReturnStatement() {
        consume(TokenType.RETURN);
        if (matchesExpression()) {
            return new ReturnStatement(parseExpression());
        }
        return new ReturnStatement(null);
    }

    private CallExpression parseCallExpression(AstNode leftHandSide) {
        // FIXME: allow arguments
        consume(TokenType.LEFT_PARENTHESIS);
        consume(TokenType.RIGHT_PARENTHESIS);

        if (leftHandSide.isIdentifier()) {
            return new CallExpression((Identifier) leftHandSide);
        }

        ASSERT(false, "Do not know how to handle call expression for things that aren't identifiers.");
        return null;
    }

    private AstNode parseExpression() {
        AstNode expression = parsePrimaryExpression();
        if (matchesSecondaryExpression()) {
            expression = parseSecondaryExpression(expression);
        }
        return expression;
    }

    private AstNode parsePrimaryExpression() {
        TokenType type = currentToken.getType();
        if (type == TokenType.LEFT_PARENTHESIS) {
            consume(TokenType.LEFT_PARENTHESIS);
            AstNode expression = parseExpression();
            consume(TokenType.RIGHT_PARENTHESIS);
            return expression;
        } else if (type == TokenType.IDENTIFIER) {
            return new Identifier(consume().getValue());
        } else if (type == TokenType.NUMERIC_LITERAL) {
            return new NumericLiteral(Double.parseDouble(consume().getValue()));
        } else if (type == TokenType.STRING_LITERAL){
            return new StringLiteral(consume().getValue());
        } else {
            ASSERT(false, "Not implemented, cannot handle token: " + currentToken + " for primary expression.");
            return null;
        }
    }

    private AstNode parseSecondaryExpression(AstNode leftHandSide) {
        TokenType type = currentToken.getType();
        if (type == TokenType.PLUS) {
            consume();
            return new BinaryExpression(leftHandSide, parseExpression(), BinaryExpressionOperator.PLUS);
        } else if (type == TokenType.MINUS) {
            consume();
            return new BinaryExpression(leftHandSide, parseExpression(), BinaryExpressionOperator.MINUS);
        } else if (type == TokenType.LEFT_PARENTHESIS) {
            return parseCallExpression(leftHandSide);
        } else {
            ASSERT(false, "Do not know how to handle token: " + currentToken + " for parsing secondary expression.");
            return null;
        }
    }

    private boolean match(TokenType tokenType) {
        ASSERT(currentToken != null);
        return currentToken.getType() == tokenType;
    }

    private Token consume(TokenType tokenType) {
        consume();
        ASSERT(previousToken.getType() == tokenType);
        return previousToken;
    }

    private Token consume() {
        previousToken = currentToken;
        currentToken = tokenStream.take();
        return previousToken;
    }

    private boolean matchesExpression() {
        ASSERT(currentToken != null);
        TokenType type = currentToken.getType();
        return type == TokenType.BOOLEAN_LITERAL ||
                type == TokenType.NUMERIC_LITERAL ||
                type == TokenType.STRING_LITERAL ||
                type == TokenType.NULL_LITERAL ||
                type == TokenType.IDENTIFIER ||
                type == TokenType.NEW ||
                type == TokenType.LEFT_CURLY_BRACE ||
                type == TokenType.LEFT_SQUARE_BRACKET ||
                type == TokenType.LEFT_PARENTHESIS;
    }

    private boolean matchesSecondaryExpression() {
        ASSERT(currentToken != null);
        TokenType type = currentToken.getType();
        return type == TokenType.PLUS ||
                type == TokenType.MINUS ||
                type == TokenType.MULTIPLY ||
                type == TokenType.COMMENT ||
                type == TokenType.EQUALITY ||
                type == TokenType.LEFT_PARENTHESIS;
    }

    private boolean matchesStatement() {
        ASSERT(currentToken != null);
        TokenType type = currentToken.getType();
        return matchesExpression() ||
                type == TokenType.FUNCTION ||
                type == TokenType.RETURN ||
                type == TokenType.VAR;
    }

    private boolean done() {
        ASSERT(currentToken != null);
        return currentToken.getType() == TokenType.EOF;
    }
}
