package com.codereviewer.analyzer;

public class PerformanceScoreCalculator {

    public int calculate(int maxDepth, int cyclomatic, double energy,
                         double memoryBytes, int maintainabilityIndex,
                         int securityIssueCount, int codeSmellCount,
                         boolean hasRecursion, int duplicateCount) {

        // Each component scored 0-100, then weighted
        int timeScore = scoreFromDepth(maxDepth);
        int cyclomaticScore = scoreFromCyclomatic(cyclomatic);
        int energyScore = scoreFromEnergy(energy);
        int memoryScore = scoreFromMemory(memoryBytes);
        int maintScore = maintainabilityIndex;
        int securityScore = scoreFromIssues(securityIssueCount, 10);
        int smellScore = scoreFromIssues(codeSmellCount, 5);
        int recursionPenalty = hasRecursion ? 10 : 0;
        int duplicatePenalty = duplicateCount * 3;

        // Weighted formula
        double weighted =
                (timeScore       * 0.20) +
                (cyclomaticScore * 0.15) +
                (energyScore     * 0.15) +
                (memoryScore     * 0.10) +
                (maintScore      * 0.15) +
                (securityScore   * 0.15) +
                (smellScore      * 0.10);

        int finalScore = (int) weighted - recursionPenalty - duplicatePenalty;
        return Math.max(0, Math.min(100, finalScore));
    }

    private int scoreFromDepth(int depth) {
        if (depth == 0) return 100;
        if (depth == 1) return 85;
        if (depth == 2) return 65;
        if (depth == 3) return 45;
        return 25;
    }

    private int scoreFromCyclomatic(int cc) {
        if (cc <= 2)  return 100;
        if (cc <= 5)  return 80;
        if (cc <= 10) return 60;
        if (cc <= 20) return 40;
        return 20;
    }

    private int scoreFromEnergy(double energy) {
        if (energy <= 10)  return 100;
        if (energy <= 50)  return 80;
        if (energy <= 100) return 60;
        if (energy <= 200) return 40;
        return 20;
    }

    private int scoreFromMemory(double bytes) {
        if (bytes <= 64)   return 100;
        if (bytes <= 256)  return 80;
        if (bytes <= 1024) return 60;
        if (bytes <= 4096) return 40;
        return 20;
    }

    private int scoreFromIssues(int count, int maxPenaltyAt) {
        if (count == 0) return 100;
        int penalty = (count * 100) / maxPenaltyAt;
        return Math.max(0, 100 - penalty);
    }
}