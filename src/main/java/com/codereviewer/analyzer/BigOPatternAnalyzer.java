package com.codereviewer.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.util.Set;

public class BigOPatternAnalyzer {

    private static final Set<String> LOG_N_METHODS = Set.of(
            "binarySearch", "floor", "ceiling", "higher",
            "lower", "headSet", "tailSet"
    );

    private static final Set<String> SORTING_METHODS = Set.of(
            "sort", "parallelSort"
    );

    private static final Set<String> LINEAR_SEARCH_METHODS = Set.of(
            "contains", "indexOf", "lastIndexOf", "find"
    );

    public String detect(CompilationUnit cu, int loopDepth, boolean hasRecursion) {

        boolean hasLogN = cu.findAll(MethodCallExpr.class).stream()
                .anyMatch(c -> LOG_N_METHODS.contains(c.getNameAsString()));

        boolean hasSorting = cu.findAll(MethodCallExpr.class).stream()
                .anyMatch(c -> SORTING_METHODS.contains(c.getNameAsString()));

        boolean hasLinearSearch = cu.findAll(MethodCallExpr.class).stream()
                .anyMatch(c -> LINEAR_SEARCH_METHODS.contains(c.getNameAsString()));

        int forLoops = cu.findAll(ForStmt.class).size();
        int whileLoops = cu.findAll(WhileStmt.class).size();
        int totalLoops = forLoops + whileLoops;

        // Sorting inside a loop = O(n log n) per iteration = O(n^2 log n)
        if (hasSorting && totalLoops >= 1) {
            return "O(n^2 log n) — sorting inside loop";
        }

        // Standalone sorting
        if (hasSorting) {
            return "O(n log n) — sorting operation";
        }

        // Binary search or tree operations
        if (hasLogN && totalLoops == 0) {
            return "O(log n) — logarithmic search";
        }

        // Binary search inside a loop
        if (hasLogN && totalLoops >= 1) {
            return "O(n log n) — log-n operation inside loop";
        }

        // Recursive without obvious pattern = O(2^n) risk
        if (hasRecursion && totalLoops == 0) {
            return "O(2^n) risk — recursive without memoization";
        }

        // Linear search
        if (hasLinearSearch) {
            return "O(n) — linear search operation";
        }

        // Fall back to loop-depth based
        if (loopDepth == 0) return "O(1)";
        if (loopDepth == 1) return "O(n)";
        if (loopDepth == 2) return "O(n^2)";
        return "O(n^" + loopDepth + ")";
    }
}