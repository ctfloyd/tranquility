package com.ctfloyd.tranquility;

import com.ctfloyd.tranquility.lib.ast.Program;
import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.interpret.Value;
import com.ctfloyd.tranquility.lib.parse.Parser;
import com.ctfloyd.tranquility.lib.tokenize.TokenStream;
import com.ctfloyd.tranquility.lib.tokenize.Tokenizer;

public class Tranquility {

    public static void main(String[] args) throws Exception {
        String programString = "function foo() { return 1 + 2 }\nfoo()";
        Tokenizer tokenizer = new Tokenizer(programString.toCharArray());
        Parser parser = new Parser(new TokenStream(tokenizer.tokenize()));
        AstInterpreter astInterpreter = new AstInterpreter();
        Program program = parser.parse();
        program.dump(0);
        Value v = program.interpret(astInterpreter);
        System.out.println(v);
    }

}
