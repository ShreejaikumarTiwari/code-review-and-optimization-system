package com.shreejai.Codereviewer_and_optimization_System.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.util.Set;

public class SpaceComplexityAnalyzer {

    private static final Set<String> LINEAR_COLLECTIONS = Set.of(
            "ArrayList", "LinkedList", "Stack", "Queue",
            "Vector", "ArrayDeque", "PriorityQueue"
    );

    private static final Set<String> MAP_COLLECTIONS = Set.of(
            "HashMap", "TreeMap", "LinkedHashMap",
            "HashSet", "TreeSet", "LinkedHashSet"
    );

    public String analyze(CompilationUnit cu, boolean hasRecursion) {

        int arrays = cu.findAll(ArrayCreationExpr.class).size();

        int loops = cu.findAll(ForStmt.class).size()
                + cu.findAll(WhileStmt.class).size()
                + cu.findAll(ForEachStmt.class).size();

        int linearCollections = 0;
        int mapCollections = 0;

        for (ObjectCreationExpr obj : cu.findAll(ObjectCreationExpr.class)) {
            String type = obj.getType().getNameAsString();
            if (LINEAR_COLLECTIONS.contains(type)) linearCollections++;
            if (MAP_COLLECTIONS.contains(type)) mapCollections++;
        }

        if (hasRecursion) {
            return "O(n) — recursive call stack";
        }

        if (arrays > 0 || linearCollections > 0) {
            if (loops >= 2) return "O(n^2) — collections inside nested loops";
            return "O(n) — array or collection allocation";
        }

        if (mapCollections > 0) {
            return "O(n) — hash-based collection";
        }

        return "O(1) — no significant heap allocation";
    }

    public int scoreFromSpaceComplexity(String spaceComplexity) {
        if (spaceComplexity.startsWith("O(1)")) return 95;
        if (spaceComplexity.startsWith("O(n)")) return 75;
        if (spaceComplexity.startsWith("O(n^2)")) return 50;
        return 40;
    }
} 