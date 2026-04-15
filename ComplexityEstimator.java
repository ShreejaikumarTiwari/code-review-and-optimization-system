package com.codereviewer.analyzer;

public class ComplexityEstimator {

    public String estimate(int depth) {
        if (depth == 0) return "O(1)";
        if (depth == 1) return "O(n)";
        if (depth == 2) return "O(n^2)";
        return "O(n^" + depth + ")";
    }

    public int calculateScore(int depth) {
        return Math.max(100 - (depth * 20), 0);
    }
}