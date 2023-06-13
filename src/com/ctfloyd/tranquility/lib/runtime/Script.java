package com.ctfloyd.tranquility.lib.runtime;

import com.ctfloyd.tranquility.lib.parse.ast.AstNode;
import com.ctfloyd.tranquility.lib.parse.ast.FunctionDeclaration;
import com.ctfloyd.tranquility.lib.parse.ast.Program;
import com.ctfloyd.tranquility.lib.parse.Parser;
import com.ctfloyd.tranquility.lib.parse.tokenize.Token;
import com.ctfloyd.tranquility.lib.parse.tokenize.TokenStream;
import com.ctfloyd.tranquility.lib.parse.tokenize.Tokenizer;

import java.util.ArrayList;
import java.util.List;

import static com.ctfloyd.tranquility.lib.common.Assert.ASSERT;

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

    public static Script fromProgram(Program program, Realm realm, Runtime runtime) {
        return new Script(program, false, realm, runtime);
    }

    private static Program parseText(char[] sourceText, String goalSymbol) {
        Tokenizer tokenizer = new Tokenizer(sourceText);
        List<Token> tokens = tokenizer.tokenize();
        Parser parser = new Parser(new TokenStream(tokens));
        return parser.parse();
    }

    public Value evaluate() {
        program.setRuntime(runtime);
        GlobalEnvironment globalEnvironment = realm.getGlobalEnvironment().get();
        ExecutionContext scriptContext = new ExecutionContext();
        scriptContext.setFunction(null);
        scriptContext.setRealm(realm);
        scriptContext.setVariableEnvironment(globalEnvironment);
        scriptContext.setLexicalEnvironment(globalEnvironment);
        runtime.pushExecutionContext(scriptContext);
        globalDeclarationInstantiation(globalEnvironment);
        return program.execute();
    }

    public void dump() {
        program.dump(0);
    }

    private void globalDeclarationInstantiation(GlobalEnvironment environment) {

        for (String name : program.getLexicallyDeclaredNames()) {
            if (environment.hasVariableDeclaration(name)) {
                throw new RuntimeException("SyntaxError");
            }

            if (environment.hasLexicalDeclaration(name)) {
                throw new RuntimeException("SyntaxError");
            }

            if (environment.hasRestrictedGlobalProperty(name)) {
                throw new RuntimeException("SyntaxError");
            }
        }

        for (String name: program.getVariableDeclaredNames()) {
            if (environment.hasLexicalDeclaration(name)) {
                throw new RuntimeException("SyntaxError");
            }
        }

        List<FunctionDeclaration> functionsToInitialize = new ArrayList<>();
        List<String> declaredFunctionNames = new ArrayList<>();
        for (AstNode node : program.getVariableScopedDeclarations()) {
            // FIXME: Skipping check if node is a for binding, variable declaration, or binding identifier
            // FIXME: It could be more than just a function declaration, i.e. GeneratorDeclaration, AsyncFunctionDeclaration, ...
            ASSERT(node.isFunctionDeclaration());
            FunctionDeclaration functionDeclaration = (FunctionDeclaration) node;
            String fn = functionDeclaration.getStringValue();
            if (!declaredFunctionNames.contains(fn)) {
                boolean fnDefinable = environment.canDeclareGlobalFunction(fn);
                if (!fnDefinable) {
                    throw new RuntimeException("TypeError");
                }
                declaredFunctionNames.add(fn);
                functionsToInitialize.add(functionDeclaration);
            }
        }

        List<String> declaredVariableNames = new ArrayList<>();
        for (String variableName : program.getVariableDeclaredNames()) {
            if (!declaredFunctionNames.contains(variableName)) {
                // FIXME: Skipped some steps in the algorithm
                if (environment.canDeclareGlobalVariable(variableName)) {
                    declaredVariableNames.add(variableName);
                }
            }
        }

        // FIXME: This is two loops in the specification, because work is supposed to occur between the two.
        for (String variableName : declaredVariableNames) {
            environment.createGlobalVariableBinding(variableName, false);
        }

        for (String d : program.getLexicallyDeclaredNames()) {
            environment.createMutableBinding(d, false);;
        }

        Environment privateEnv = null;
        for (FunctionDeclaration f : functionsToInitialize) {
            String fn = f.getStringValue();
            JsObject fo = f.instantiate(environment, privateEnv);
            fo.setRuntime(runtime);
            environment.createGlobalFunctionBinding(fn, Value.object(fo), false);
        }
    }
}
