package com.ctfloyd.tranquility.lib.parse;

import com.ctfloyd.tranquility.lib.ast.*;
import com.ctfloyd.tranquility.lib.common.NumberUtils;
import com.ctfloyd.tranquility.lib.common.StringUtils;
import com.ctfloyd.tranquility.lib.tokenize.Token;
import com.ctfloyd.tranquility.lib.tokenize.TokenStream;
import com.ctfloyd.tranquility.lib.tokenize.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            if (match(TokenType.SEMICOLON) || match(TokenType.COMMENT) || match(TokenType.MULTI_LINE_COMMENT)) {
                consume();
                if (done()) {
                    break;
                }
            }

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
        } else if (type == TokenType.VAR) {
            return parseVariableDeclarator();
        } else if (type == TokenType.FOR) {
            return parseForStatement();
        } else if (type == TokenType.IF) {
            return parseIfStatement();
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

    private ForStatement parseForStatement() {
        consume(TokenType.FOR);
        consume(TokenType.LEFT_PARENTHESIS);
        AstNode initializer = parseStatement();
        consume(TokenType.SEMICOLON);
        AstNode test = parseExpression();
        consume(TokenType.SEMICOLON);
        AstNode update = parseStatement();
        consume(TokenType.RIGHT_PARENTHESIS);
        BlockStatement body = parseBlockStatement();
        return new ForStatement(initializer, test, update, body);
    }

    private IfStatement parseIfStatement() {
        consume(TokenType.IF);
        consume(TokenType.LEFT_PARENTHESIS);
        AstNode test = parseExpression();
        consume(TokenType.RIGHT_PARENTHESIS);
        AstNode body = parseBlockStatement();
        AstNode alternate = null;
        if (match(TokenType.ELSE)) {
            alternate = parseElseStatement();
        }
        return new IfStatement(test, body, alternate);
    }

    private AstNode parseElseStatement() {
        consume(TokenType.ELSE);
        if (match(TokenType.IF)) {
            return parseIfStatement();
        } else {
            return parseBlockStatement();
        }
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
        boolean computed = false;
        if (match(TokenType.PERIOD)) {
            consume(TokenType.PERIOD);
        } else {
            computed = true;
            consume(TokenType.LEFT_SQUARE_BRACKET);
        }

        Identifier identifier = null;
        if (match(TokenType.IDENTIFIER)) {
            identifier = new Identifier(consume(TokenType.IDENTIFIER).getValue());
        } else if (match(TokenType.NUMERIC_LITERAL)) {
            identifier = new Identifier(consume(TokenType.NUMERIC_LITERAL).getValue());
        }

        if (match(TokenType.RIGHT_SQUARE_BRACKET)) {
            consume(TokenType.RIGHT_SQUARE_BRACKET);
        }

        MemberExpression memberExpression = new MemberExpression(leftHandSide, identifier, computed);
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
            Number value = NumberUtils.parse(consume().getValue());
            ASSERT(value != null);
            return new NumericLiteral(value.doubleValue());
        } else if (type == TokenType.STRING_LITERAL) {
            String value = StringUtils.parse(consume().getValue());
            return new StringLiteral(value);
        } else if (type == TokenType.BOOLEAN_LITERAL) {
            return new BooleanLiteral(Boolean.parseBoolean(consume().getValue()));
        } else if (type == TokenType.LEFT_CURLY_BRACE) {
            return parseObjectExpression();
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
        } else if (type == TokenType.LESS_THAN) {
            consume(TokenType.LESS_THAN);
            return new BinaryExpression(leftHandSide, parseExpression(), BinaryExpressionOperator.LESS_THAN);
        } else if (type == TokenType.LESS_THAN_EQUALS) {
            consume(TokenType.LESS_THAN_EQUALS);
            return new BinaryExpression(leftHandSide, parseExpression(), BinaryExpressionOperator.LESS_THAN_EQUALS);
        } else if (type == TokenType.GREATER_THAN) {
            consume(TokenType.GREATER_THAN);
            return new BinaryExpression(leftHandSide, parseExpression(), BinaryExpressionOperator.GREATER_THAN);
        } else if (type == TokenType.GREATER_THAN_EQUALS) {
            consume(TokenType.GREATER_THAN_EQUALS);
            return new BinaryExpression(leftHandSide, parseExpression(), BinaryExpressionOperator.GREATER_THAN_EQUALS);
        } else if (type == TokenType.ASSIGNMENT) {
            consume(TokenType.ASSIGNMENT);
            ASSERT(leftHandSide.isIdentifier());
            return new AssignmentExpression((Identifier) leftHandSide, parseExpression(), AssignmentExpressionOperator.EQUALS);
        } else if (type == TokenType.LEFT_PARENTHESIS) {
            return parseCallExpression(leftHandSide);
        } else if (type == TokenType.PERIOD || type == TokenType.LEFT_SQUARE_BRACKET) {
            return parseMemberExpression(leftHandSide);
        } else {
            ASSERT(false, "Do not know how to handle token: " + currentToken + " for parsing secondary expression.");
            return null;
        }
    }

    private AstNode parseObjectExpression() {
        consume(TokenType.LEFT_CURLY_BRACE);

        Map<Identifier, AstNode> properties = new HashMap<>();
        while (!done() && !match(TokenType.RIGHT_CURLY_BRACE)) {
            parseObjectProperty(properties);
            if (match(TokenType.COMMA)) {
                consume(TokenType.COMMA);
            }
        }
        consume(TokenType.RIGHT_CURLY_BRACE);
        return new ObjectExpression(properties);
    }

    private void parseObjectProperty(Map<Identifier, AstNode> properties) {
        // FIXME: This shouldn't be as strict as an identifier
        Identifier property = null;
        if (match(TokenType.IDENTIFIER)){
            property = new Identifier(consume(TokenType.IDENTIFIER).getValue());
        } else if (match(TokenType.STRING_LITERAL)) {
            property = new Identifier(consume(TokenType.STRING_LITERAL).getValue());
        }
        ASSERT(property != null);
        consume(TokenType.COLON);
        AstNode value = parseExpression();
        properties.put(property, value);
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
                type == TokenType.EQUALITY ||
                type == TokenType.PERIOD ||
                type == TokenType.LEFT_SQUARE_BRACKET ||
                type == TokenType.LEFT_PARENTHESIS ||
                type == TokenType.LESS_THAN ||
                type == TokenType.LESS_THAN_EQUALS ||
                type == TokenType.GREATER_THAN ||
                type == TokenType.GREATER_THAN_EQUALS ||
                type == TokenType.ASSIGNMENT;
    }

    private boolean matchesStatement() {
        ASSERT(currentToken != null);
        TokenType type = currentToken.getType();
        return matchesExpression() ||
                type == TokenType.FUNCTION ||
                type == TokenType.RETURN ||
                type == TokenType.VAR ||
                type == TokenType.IF ||
                type == TokenType.FOR;
    }

    private boolean done() {
        ASSERT(currentToken != null);
        return currentToken.getType() == TokenType.EOF;
    }
}
