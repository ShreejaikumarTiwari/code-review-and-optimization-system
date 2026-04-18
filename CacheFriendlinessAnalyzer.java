package com.codereviewer.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.stmt.ForStmt;

import java.util.List;

public class CacheFriendlinessAnalyzer {

    public String analyze(CompilationUnit cu) {

        List<ForStmt> loops = cu.findAll(ForStmt.class);
        List<ArrayAccessExpr> arrayAccesses = cu.findAll(ArrayAccessExpr.class);

        if (arrayAccesses.isEmpty()) {
            return "N/A — no array access detected";
        }

        boolean hasNestedLoops = loops.size() >= 2;

        if (!hasNestedLoops) {
            return "Good — sequential or simple access pattern";
        }

        boolean potentialColumnMajor = arrayAccesses.stream()
                .anyMatch(access -> {
                    String accessStr = access.toString();
                    return accessStr.contains("[j]") && accessStr.contains("[i]");
                });

        if (potentialColumnMajor) {
            return "Poor — possible column-major access (arr[j][i])."
                    + " Swap loop order for better cache performance.";
        }

        return "Moderate — verify loop order matches array storage layout.";
    }
}