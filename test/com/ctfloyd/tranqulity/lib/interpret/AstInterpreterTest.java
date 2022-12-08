package com.ctfloyd.tranqulity.lib.interpret;

import com.ctfloyd.tranquility.lib.ast.BinaryExpression;
import com.ctfloyd.tranquility.lib.ast.BinaryExpressionOperator;
import com.ctfloyd.tranquility.lib.ast.ExpressionStatement;
import com.ctfloyd.tranquility.lib.ast.NumericLiteral;
import com.ctfloyd.tranquility.lib.ast.Program;
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

    private void test() throws Exception {
        interpret_givenAdditionAst_expectCorrectValue();
    }

    public static void main(String[] args) throws Exception {
        new AstInterpreterTest().test();
    }
}
