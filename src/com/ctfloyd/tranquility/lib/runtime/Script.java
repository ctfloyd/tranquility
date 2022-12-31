package com.ctfloyd.tranquility.lib.runtime;

import com.ctfloyd.tranquility.lib.ast.Program;
import com.ctfloyd.tranquility.lib.parse.Parser;
import com.ctfloyd.tranquility.lib.tokenize.Token;
import com.ctfloyd.tranquility.lib.tokenize.TokenStream;
import com.ctfloyd.tranquility.lib.tokenize.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class Script {

    private final Program program;
    private final boolean isStrict;
    private final Realm realm;

    // TODO: Add [[EcmaScriptCode]] [[LoadedModules]] [[HostDefined]]
    private final Runtime runtime;

    public Script(Program program, boolean isStrict, Realm realm, Runtime runtime) {
        this.program = program;
        this.isStrict = isStrict;
        this.realm = realm;
        this.runtime = runtime;
    }

    public static Script parseScript(char[] sourceText, Realm realm, Runtime runtime) {
        return new Script(parseText(sourceText, "script"), false, realm, runtime);
    }

    private static Program parseText(char[] sourceText, String goalSymbol) {
        Tokenizer tokenizer = new Tokenizer(sourceText);
        List<Token> tokens = tokenizer.tokenize();
        Parser parser = new Parser(new TokenStream(tokens));
        return parser.parse();
    }

    public Value evaluate() {
        GlobalEnvironment globalEnvironment = realm.getGlobalEnvironment().get();
        ExecutionContext scriptContext = new ExecutionContext();
        scriptContext.setFunction(null);
        scriptContext.setRealm(realm);
        scriptContext.setVariableEnvironment(globalEnvironment);
        scriptContext.setLexicalEnvironment(globalEnvironment);
        runtime.pushExecutionContext(scriptContext);
        globalDeclarationInstantiation(globalEnvironment);
        program.setRuntime(runtime);
        return program.execute();
    }

    private void globalDeclarationInstantiation(GlobalEnvironment environment) {
        List<String> declaredVariableNames = new ArrayList<>();
        for (String variableName : program.getVariableDeclaredNames()) {
            if (environment.canDeclareGlobalVariable(variableName)) {
                declaredVariableNames.add(variableName);
            }
        }

        // FIXME: This is two loops in the specification, because work is supposed to occur between the two.
        for (String variableName : declaredVariableNames) {
            environment.createGlobalVariableBinding(variableName, false);
        }
    }
}
