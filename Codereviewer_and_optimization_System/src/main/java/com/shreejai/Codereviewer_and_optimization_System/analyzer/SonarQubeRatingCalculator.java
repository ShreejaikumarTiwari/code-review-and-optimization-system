package com.shreejai.Codereviewer_and_optimization_System.analyzer;

public class SonarQubeRatingCalculator {

    public String calculateRating(int performanceScore, int securityIssueCount,
                                   int cyclomaticComplexity, int codeSmellCount) {

        // Security critical issues instantly drop to E
        if (securityIssueCount >= 5) return "E";
        if (securityIssueCount >= 3) return "D";

        // Combine all signals into rating
        int ratingScore = performanceScore;
        ratingScore -= (cyclomaticComplexity > 10 ? 20 : 0);
        ratingScore -= (codeSmellCount * 3);
        ratingScore -= (securityIssueCount * 5);

        ratingScore = Math.max(0, Math.min(100, ratingScore));

        if (ratingScore >= 90) return "A";
        if (ratingScore >= 75) return "B";
        if (ratingScore >= 60) return "C";
        if (ratingScore >= 40) return "D";
        return "E";
    }

    public String getRatingDescription(String rating) {
        return switch (rating) {
            case "A" -> "Excellent — production ready";
            case "B" -> "Good — minor improvements recommended";
            case "C" -> "Fair — significant issues present";
            case "D" -> "Poor — major refactoring needed";
            case "E" -> "Critical — do not deploy";
            default  -> "Unknown";
        };
    }
}