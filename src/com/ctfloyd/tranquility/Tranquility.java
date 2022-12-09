package com.ctfloyd.tranquility;

import com.ctfloyd.tranquility.lib.ast.Program;
import com.ctfloyd.tranquility.lib.interpret.AstInterpreter;
import com.ctfloyd.tranquility.lib.parse.Parser;
import com.ctfloyd.tranquility.lib.tokenize.TokenStream;
import com.ctfloyd.tranquility.lib.tokenize.Tokenizer;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Tranquility {

    public static void main(String[] args) throws Exception {
        String fileContents = Files.readString(Paths.get("input/test.js"));
        Tokenizer tokenizer = new Tokenizer(fileContents.toCharArray());
        Parser parser = new Parser(new TokenStream(tokenizer.tokenize()));
        AstInterpreter astInterpreter = new AstInterpreter();
        Program program = parser.parse();
        System.out.println(program.interpret(astInterpreter));
    }

}
