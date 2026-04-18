package com.codereviewer.analyzer;
import org.springframework.stereotype.Component;

@Component
public class MaintainabilityCalculator {

    public int calculate(int linesOfCode, int cyclomatic, int maxDepth) {
        // Simplified Maintainability Index formula
        int score = 100;
        score -= (cyclomatic * 3);
        score -= (maxDepth * 5);
        score -= (linesOfCode / 10);
        return Math.max(0, Math.min(100, score));
    }

    public String grade(int index) {
        if (index >= 90) return "A+";
        if (index >= 80) return "A";
        if (index >= 70) return "B+";
        if (index >= 60) return "B";
        if (index >= 50) return "C";
        return "D";
    }
}