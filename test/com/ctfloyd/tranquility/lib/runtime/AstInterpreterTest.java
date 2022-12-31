package com.ctfloyd.tranquility.lib.runtime;

import com.ctfloyd.tranquility.lib.ast.*;
import com.ctfloyd.tranquility.lib.test.Suite;
import com.ctfloyd.tranquility.lib.test.Test;

import java.util.Collections;

import static com.ctfloyd.tranquility.lib.common.Assert.assertTrue;

@Suite
public class AstInterpreterTest {

    @Test
    public void interpret_givenAdditionAst_expectCorrectValue() throws Exception {
        Program program = new Program();
        BinaryExpression expression = new BinaryExpression(new NumericLiteral(100), new NumericLiteral(50), BinaryExpressionOperator.PLUS);
        ExpressionStatement statement = new ExpressionStatement(expression);
        program.addStatement(statement);

        Runtime interpreter = new Runtime();
        Value finalValue = program.execute();
        assertTrue("Value is not a number", finalValue.isNumber());
        assertTrue("Value is not '150'", finalValue.asDouble() == 150D);
    }

    @Test
    public void interpret_givenFunctionAst_expectCorrectValue() throws Exception {
        /**
         * function foo() {
         *    return 1 + 2
         * }
         *    foo();
         */
        Program program = new Program();
        BlockStatement block = new BlockStatement();
        BinaryExpression expression = new BinaryExpression(new NumericLiteral(1), new NumericLiteral(2), BinaryExpressionOperator.PLUS);
        ExpressionStatement statement = new ExpressionStatement(expression);
        ReturnStatement returnStatement = new ReturnStatement(statement);
        block.addChild(returnStatement);
        FunctionDeclaration functionDeclaration = new FunctionDeclaration("foo", Collections.emptyList(), block);

        CallExpression callExpression = new CallExpression(new Identifier("foo"), Collections.emptyList());
        ExpressionStatement callStatement = new ExpressionStatement(callExpression);

        program.addStatement(functionDeclaration);
        program.addStatement(callStatement);

        Runtime runtime = new Runtime();
        Value result = program.execute();
        assertTrue("Value is not a number", result.isNumber());
        assertTrue("Value is not '3'", result.asDouble() == 3D);
    }
}
