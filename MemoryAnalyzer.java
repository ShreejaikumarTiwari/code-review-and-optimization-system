package com.codereviewer.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class MemoryAnalyzer {

    public double calculateMemory(CompilationUnit cu) {

        double memory = 0;

        // Object creation
        for (ObjectCreationExpr obj : cu.findAll(ObjectCreationExpr.class)) {

            String type = obj.getType().getNameAsString();

            if (type.equals("ArrayList")) {
                memory += 64;
            }
            else if (type.equals("HashMap")) {
                memory += 128;
            }
            else if (type.equals("String")) {
                memory += 40;
            }
            else {
                memory += 32;
            }
        }

        // Array creation
        for (ArrayCreationExpr arr : cu.findAll(ArrayCreationExpr.class)) {

            String type = arr.getElementType().asString();
            int elementSize = getPrimitiveSize(type);

            memory += 16 + (elementSize * 10);
        }

        // Variable declarations
        for (VariableDeclarator var : cu.findAll(VariableDeclarator.class)) {

            String type = var.getType().asString();
            memory += getPrimitiveSize(type);
        }

        return memory;
    }

    private int getPrimitiveSize(String type) {

        if (type.equals("byte")) return 1;
        if (type.equals("boolean")) return 1;
        if (type.equals("short")) return 2;
        if (type.equals("char")) return 2;
        if (type.equals("int")) return 4;
        if (type.equals("float")) return 4;
        if (type.equals("long")) return 8;
        if (type.equals("double")) return 8;

        return 4;
    }

    public String formatMemory(double bytes) {

        if (bytes < 1024) {
            return bytes + " Bytes";
        }

        if (bytes < 1024 * 1024) {
            return (bytes / 1024) + " KB";
        }

        return (bytes / (1024 * 1024)) + " MB";
    }
}