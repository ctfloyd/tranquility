package com.ctfloyd.tranquility.lib.ast;

import java.util.ArrayList;
import java.util.List;

public class Scope extends AstNode {

    private final List<String> lexicallyDeclaredNames = new ArrayList<>();
    private final List<String> variableDeclaredNames = new ArrayList<>();
    private final List<AstNode> variableScopedDeclarations = new ArrayList<>();

    public List<String> getLexicallyDeclaredNames() {
        return lexicallyDeclaredNames;
    }

    public List<String> getVariableDeclaredNames() {
        return variableDeclaredNames;
    }

    public String addLexicallyDeclaredName(String lexicallyDeclaredName) {
        lexicallyDeclaredNames.add(lexicallyDeclaredName);
        return lexicallyDeclaredName;
    }

    public String addVariableDeclaredNames(String variableDeclaredName) {
        variableDeclaredNames.add(variableDeclaredName);
        return variableDeclaredName;
    }

    public AstNode addVariableScopeDeclaration(AstNode node) {
        variableScopedDeclarations.add(node);
        return node;
    }

    public List<AstNode> getVariableScopedDeclarations() {
        return variableScopedDeclarations;
    }

    @Override
    public boolean isScopeNode() {
        return true;
    }
}
