package com.codereviewer.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;

public class RecursionAnalyzer {

    public List<String> detect(CompilationUnit cu) {

        List<String> findings = new ArrayList<>();

        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {

            String methodName = method.getNameAsString();

            boolean callsSelf = method.findAll(MethodCallExpr.class)
                    .stream()
                    .anyMatch(call -> call.getNameAsString().equals(methodName));

            if (callsSelf) {
                findings.add("Recursive method detected: '"
                        + methodName
                        + "()'. Ensure base case exists. "
                        + "Consider iterative approach or memoization.");
            }
        }

        return findings;
    }

    public boolean hasRecursion(CompilationUnit cu) {
        return !detect(cu).isEmpty();
    }
}