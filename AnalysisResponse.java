package com.codereviewer.dto;

import java.util.List;

public class AnalysisResponse {

    private final int totalLoops;
    private final int maxDepth;
    private final String timeComplexity;
    private final String spaceComplexity;
    private final String bigOPattern;
    private final int cyclomaticComplexity;
    private final double energyConsumption;
    private final String memoryUsage;
    private final int maintainabilityIndex;
    private final String grade;
    private final String cacheFriendliness;
    private final String language;
    private final String optimizedCode;
    private final List<LineIssue> lineIssues;
    private final List<String> recursionFindings;
    private final List<String> codeSmells;
    private final List<String> suggestions;
    private final List<String> securityIssues;
    private final List<String> duplicateCodeFindings;
    private final int score;

    public AnalysisResponse(int totalLoops, int maxDepth,
                            String timeComplexity, String spaceComplexity,
                            String bigOPattern, int cyclomaticComplexity,
                            double energyConsumption, String memoryUsage,
                            int maintainabilityIndex, String grade,
                            String cacheFriendliness, String language,
                            String optimizedCode, List<LineIssue> lineIssues,
                            List<String> recursionFindings,
                            List<String> codeSmells, List<String> suggestions,
                            List<String> securityIssues,
                            List<String> duplicateCodeFindings, int score) {
        this.totalLoops = totalLoops;
        this.maxDepth = maxDepth;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
        this.bigOPattern = bigOPattern;
        this.cyclomaticComplexity = cyclomaticComplexity;
        this.energyConsumption = energyConsumption;
        this.memoryUsage = memoryUsage;
        this.maintainabilityIndex = maintainabilityIndex;
        this.grade = grade;
        this.cacheFriendliness = cacheFriendliness;
        this.language = language;
        this.optimizedCode = optimizedCode;
        this.lineIssues = lineIssues;
        this.recursionFindings = recursionFindings;
        this.codeSmells = codeSmells;
        this.suggestions = suggestions;
        this.securityIssues = securityIssues;
        this.duplicateCodeFindings = duplicateCodeFindings;
        this.score = score;
    }

    public int getTotalLoops() { return totalLoops; }
    public int getMaxDepth() { return maxDepth; }
    public String getTimeComplexity() { return timeComplexity; }
    public String getSpaceComplexity() { return spaceComplexity; }
    public String getBigOPattern() { return bigOPattern; }
    public int getCyclomaticComplexity() { return cyclomaticComplexity; }
    public double getEnergyConsumption() { return energyConsumption; }
    public String getMemoryUsage() { return memoryUsage; }
    public int getMaintainabilityIndex() { return maintainabilityIndex; }
    public String getGrade() { return grade; }
    public String getCacheFriendliness() { return cacheFriendliness; }
    public String getLanguage() { return language; }
    public String getOptimizedCode() { return optimizedCode; }
    public List<LineIssue> getLineIssues() { return lineIssues; }
    public List<String> getRecursionFindings() { return recursionFindings; }
    public List<String> getCodeSmells() { return codeSmells; }
    public List<String> getSuggestions() { return suggestions; }
    public List<String> getSecurityIssues() { return securityIssues; }
    public List<String> getDuplicateCodeFindings() { return duplicateCodeFindings; }
    public int getScore() { return score; }
}