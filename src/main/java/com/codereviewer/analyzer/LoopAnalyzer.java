package com.codereviewer.analyzer;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;

public class LoopAnalyzer extends VoidVisitorAdapter<Void> {

    private int totalLoops = 0;
    private int currentDepth = 0;
    private int maxDepth = 0;

    @Override
    public void visit(ForStmt n, Void arg) {
        totalLoops++;
        currentDepth++;
        maxDepth = Math.max(maxDepth, currentDepth);
        super.visit(n, arg);
        currentDepth--;
    }

    @Override
    public void visit(WhileStmt n, Void arg) {
        totalLoops++;
        currentDepth++;
        maxDepth = Math.max(maxDepth, currentDepth);
        super.visit(n, arg);
        currentDepth--;
    }

    @Override
    public void visit(ForEachStmt n, Void arg) {
        totalLoops++;
        currentDepth++;
        maxDepth = Math.max(maxDepth, currentDepth);
        super.visit(n, arg);
        currentDepth--;
    }

    public int getTotalLoops() {
        return totalLoops;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}