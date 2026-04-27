package com.shreejai.Codereviewer_and_optimization_System.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

public class EnergyAnalyzer {

    public double calculateEnergy(CompilationUnit cu) {

        int loops =
                cu.findAll(ForStmt.class).size()
                + cu.findAll(WhileStmt.class).size()
                + cu.findAll(DoStmt.class).size()
                + cu.findAll(ForEachStmt.class).size();

        int conditions =
                cu.findAll(IfStmt.class).size()
                + cu.findAll(SwitchStmt.class).size();

        int methodCalls = cu.findAll(MethodCallExpr.class).size();

        int memoryAllocations = cu.findAll(ObjectCreationExpr.class).size();

        int arithmeticOps = cu.findAll(BinaryExpr.class).size();

        /*
         Realistic approximate energy model (nanojoules)
         */

        double loopEnergy = loops * 1.0;
        double conditionEnergy = conditions * 0.2;
        double methodEnergy = methodCalls * 8.0;
        double memoryEnergy = memoryAllocations * 50.0;
        double arithmeticEnergy = arithmeticOps * 0.3;

        return loopEnergy
                + conditionEnergy
                + methodEnergy
                + memoryEnergy
                + arithmeticEnergy;
    }
}