package com.codereviewer.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;

import java.util.ArrayList;
import java.util.List;

public class CodeSmellDetector {

    public List<String> detect(CompilationUnit cu, int maxDepth, int cyclomatic) {

        List<String> smells = new ArrayList<>();

        // Deep nesting
        if (maxDepth >= 3) {
            smells.add("Deep nested loops detected (depth=" + maxDepth + "). Refactor logic.");
        }

        // High cyclomatic complexity
        if (cyclomatic >= 10) {
            smells.add("High cyclomatic complexity (" + cyclomatic + "). Consider splitting the method.");
        }

        // Too many if statements
        int ifCount = cu.findAll(IfStmt.class).size();
        if (ifCount > 5) {
            smells.add("Too many conditionals (" + ifCount + "). Consider using polymorphism or strategy pattern.");
        }

        // Long methods
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            long lines = method.getRange()
                    .map(r -> (long)(r.end.line - r.begin.line))
                    .orElse(0L);
            if (lines > 30) {
                smells.add("Method '" + method.getNameAsString() + "' is too long (" + lines + " lines). Consider breaking it up.");
            }
        }

        // Infinite loop risk
        for (WhileStmt w : cu.findAll(WhileStmt.class)) {
            if (w.getCondition().toString().equals("true")) {
                smells.add("Potential infinite loop detected: while(true).");
            }
        }

        return smells;
    }
}