package com.ctfloyd.tranqulity.lib.interpret;

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
import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.JsValue;

import static com.ctfloyd.tranquility.lib.common.Assert.assertTrue;

public class AstInterpreterTest {

    private void interpret_givenAdditionAst_expectCorrectValue() throws Exception {
        Program program = new Program();
        BinaryExpression expression = new BinaryExpression(new NumericLiteral(100), new NumericLiteral(50), BinaryExpressionOperator.PLUS);
        ExpressionStatement statement = new ExpressionStatement(expression);
        program.addChild(statement);

        AstInterpreter interpreter = new AstInterpreter();
        JsValue finalValue = program.interpret(interpreter);
        assertTrue("Value is not a number", finalValue.isNumber());
        assertTrue("Value is not '150'", finalValue.asDouble() == 150D);
    }

    private void interpret_givenFunctionAst_expectCorrectValue() throws Exception {
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
        FunctionDeclaration functionDeclaration = new FunctionDeclaration("foo", block);

        CallExpression callExpression = new CallExpression(new Identifier("foo"));
        ExpressionStatement callStatement = new ExpressionStatement(callExpression);

        program.addChild(functionDeclaration);
        program.addChild(callStatement);

        AstInterpreter interpreter = new AstInterpreter();
        JsValue result = program.interpret(interpreter);
        assertTrue("Value is not a number", result.isNumber());
        assertTrue("Value is not '3'", result.asDouble() == 3D);
    }

    private void test() throws Exception {
        interpret_givenAdditionAst_expectCorrectValue();
        interpret_givenFunctionAst_expectCorrectValue();
    }

    public static void main(String[] args) throws Exception {
        new AstInterpreterTest().test();
    }
}
