package com.ctfloyd.tranquility.lib.parse;

import com.ctfloyd.tranquility.lib.ast.AstNode;
import com.ctfloyd.tranquility.lib.ast.BinaryExpression;
import com.ctfloyd.tranquility.lib.ast.BinaryExpressionOperator;
import com.ctfloyd.tranquility.lib.ast.BlockStatement;
import com.ctfloyd.tranquility.lib.ast.BooleanLiteral;
import com.ctfloyd.tranquility.lib.ast.CallExpression;
import com.ctfloyd.tranquility.lib.ast.ExpressionStatement;
import com.ctfloyd.tranquility.lib.ast.FunctionDeclaration;
import com.ctfloyd.tranquility.lib.ast.Identifier;
import com.ctfloyd.tranquility.lib.ast.MemberExpression;
import com.ctfloyd.tranquility.lib.ast.NumericLiteral;
import com.ctfloyd.tranquility.lib.ast.Program;
import com.ctfloyd.tranquility.lib.ast.ReturnStatement;
import com.ctfloyd.tranquility.lib.ast.StringLiteral;
import com.ctfloyd.tranquility.lib.ast.VariableDeclarator;
import com.ctfloyd.tranquility.lib.tokenize.Token;
import com.ctfloyd.tranquility.lib.tokenize.TokenStream;
import com.ctfloyd.tranquility.lib.tokenize.TokenType;

import java.util.ArrayList;
import java.util.List;

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
            if (match(TokenType.SEMICOLON)) {
                consume(TokenType.SEMICOLON);
            }
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
        } else if (type == TokenType.VAR) {
            return parseVariableDeclarator();
        } else {
            ASSERT(false, "Do not know how to handle token: " + currentToken + " for parse statement.");
            return null;
        }
    }

    private FunctionDeclaration parseFunctionDeclaration() {
        consume(TokenType.FUNCTION);
        String functionName = consume(TokenType.IDENTIFIER).getValue();
        consume(TokenType.LEFT_PARENTHESIS);

        List<String> arguments = new ArrayList<>();
        while(match(TokenType.IDENTIFIER)) {
            arguments.add(consume().getValue());
            if (match(TokenType.COMMA)) {
                consume(TokenType.COMMA);
            }
        }
        consume(TokenType.RIGHT_PARENTHESIS);
        BlockStatement functionBody = parseBlockStatement();
        return new FunctionDeclaration(functionName, arguments, functionBody);
    }

    private VariableDeclarator parseVariableDeclarator() {
        consume(TokenType.VAR);
        String variableName = consume(TokenType.IDENTIFIER).getValue();

        if (match(TokenType.ASSIGNMENT)) {
            consume(TokenType.ASSIGNMENT);
            AstNode expression = parseExpression();
            return new VariableDeclarator(variableName, expression);
        }

        return new VariableDeclarator(variableName);
    }

    private BlockStatement parseBlockStatement() {
        BlockStatement block = new BlockStatement();
        consume(TokenType.LEFT_CURLY_BRACE);
        while (!done() && !match(TokenType.RIGHT_CURLY_BRACE)) {
            if (matchesStatement()) {
                block.addChild(parseStatement());
            } else if (match(TokenType.SEMICOLON)) {
                consume(TokenType.SEMICOLON);
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
        consume(TokenType.LEFT_PARENTHESIS);
        List<AstNode> arguments = new ArrayList<>();
        while (matchesExpression()) {
            arguments.add(parseExpression());
            if (match(TokenType.COMMA)) {
                consume(TokenType.COMMA);
            }
        }
        consume(TokenType.RIGHT_PARENTHESIS);
        return new CallExpression(leftHandSide, arguments);
    }

    private AstNode parseMemberExpression(AstNode leftHandSide) {
        consume(TokenType.PERIOD);
        Identifier identifier = new Identifier(consume(TokenType.IDENTIFIER).getValue());
        MemberExpression memberExpression = new MemberExpression(leftHandSide, identifier);
        if (matchesSecondaryExpression()) {
            return parseSecondaryExpression(memberExpression);
        }
        return memberExpression;
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
        } else if (type == TokenType.STRING_LITERAL) {
            return new StringLiteral(consume().getValue());
        } else if (type == TokenType.BOOLEAN_LITERAL) {
            return new BooleanLiteral(Boolean.parseBoolean(consume().getValue()));
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
        } else if (type == TokenType.PERIOD) {
            return parseMemberExpression(leftHandSide);
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
        ASSERT(previousToken.getType() == tokenType, "Expected token type to be " + tokenType + " but got " + previousToken.getType() + " instead. ");
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
                type == TokenType.PERIOD ||
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
