package com.codereviewer.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.WhileStmt;

public class CyclomaticComplexityAnalyzer {

    public int calculate(CompilationUnit cu) {

        int complexity = 1;

        complexity += cu.findAll(IfStmt.class).size();
        complexity += cu.findAll(ForStmt.class).size();
        complexity += cu.findAll(ForEachStmt.class).size();
        complexity += cu.findAll(WhileStmt.class).size();
        complexity += cu.findAll(DoStmt.class).size();
        complexity += cu.findAll(SwitchEntry.class).size();
        complexity += cu.findAll(CatchClause.class).size();

        complexity += cu.findAll(BinaryExpr.class).stream()
                .filter(expr ->
                        expr.getOperator() == BinaryExpr.Operator.AND ||
                        expr.getOperator() == BinaryExpr.Operator.OR)
                .count();

        return complexity;
    }
}