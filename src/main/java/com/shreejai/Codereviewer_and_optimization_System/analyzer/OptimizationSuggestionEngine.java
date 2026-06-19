package com.shreejai.Codereviewer_and_optimization_System.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.util.ArrayList;
import java.util.List;

public class OptimizationSuggestionEngine {

    public List<String> suggest(CompilationUnit cu, int maxDepth, int cyclomatic, double energy) {

        List<String> suggestions = new ArrayList<>();

        // Nested loop suggestion
        if (maxDepth >= 2) {
            suggestions.add("Nested loops detected. Consider using HashMap for O(1) lookups instead of O(n²) search.");
        }

        // High energy
        if (energy > 50) {
            suggestions.add("High energy consumption estimated. Reduce object creation and loop complexity.");
        }

        // Object creation inside loops
        boolean objectInLoop = false;
        for (ForStmt loop : cu.findAll(ForStmt.class)) {
            if (!loop.findAll(ObjectCreationExpr.class).isEmpty()) {
                objectInLoop = true;
            }
        }
        for (WhileStmt loop : cu.findAll(WhileStmt.class)) {
            if (!loop.findAll(ObjectCreationExpr.class).isEmpty()) {
                objectInLoop = true;
            }
        }
        if (objectInLoop) {
            suggestions.add("Object creation detected inside loop. Move object creation outside the loop to reduce memory pressure.");
        }

        // High cyclomatic complexity
        if (cyclomatic >= 7) {
            suggestions.add("High cyclomatic complexity. Extract complex logic into smaller, focused methods.");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("Code looks reasonably optimized. No major issues detected.");
        }

        return suggestions;
    }
}