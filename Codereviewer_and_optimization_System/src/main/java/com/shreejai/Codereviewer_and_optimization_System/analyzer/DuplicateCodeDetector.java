package com.shreejai.Codereviewer_and_optimization_System.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

public class DuplicateCodeDetector {

    public List<String> detect(CompilationUnit cu) {

        List<String> findings = new ArrayList<>();
        Map<String, Integer> statementFrequency = new HashMap<>();

        for (BlockStmt block : cu.findAll(BlockStmt.class)) {
            for (Statement stmt : block.getStatements()) {

                String normalized = normalize(stmt.toString());
                if (normalized.length() < 10) continue;

                statementFrequency.merge(normalized, 1, Integer::sum);
            }
        }

        statementFrequency.forEach((stmt, count) -> {
            if (count >= 2) {
                String preview = stmt.length() > 60
                        ? stmt.substring(0, 60) + "..."
                        : stmt;
                findings.add("Duplicate code block detected ("
                        + count + " occurrences): '"
                        + preview
                        + "'. Consider extracting into a method.");
            }
        });

        return findings;
    }

    private String normalize(String code) {
        return code.replaceAll("\\s+", " ").trim();
    }
}