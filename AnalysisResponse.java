package com.codereviewer.dto;

import java.util.List;

public class AnalysisResponse {

    private int totalLoops;
    private int maxDepth;
    private String timeComplexity;
    private String spaceComplexity;
    private String bigOPattern;
    private int cyclomaticComplexity;
    private double energyConsumption;
    private String memoryUsage;
    private int maintainabilityIndex;
    private String grade;
    private String cacheFriendliness;
    private String language;
    private String optimizedCode;
    private String summary;        // ← ADDED (controller uses setSummary)
    private List<LineIssue> lineIssues;
    private List<String> recursionFindings;
    private List<String> codeSmells;
    private List<String> suggestions;
    private List<String> securityIssues;
    private List<String> duplicateCodeFindings;
    private int score;

    // No-arg constructor (required by controller)
    public AnalysisResponse() {}

    // Full constructor (keep for existing usages)
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

    // Setters (required by controller)
    public void setSummary(String summary) { this.summary = summary; }
    public void setScore(int score) { this.score = score; }
    public void setTotalLoops(int totalLoops) { this.totalLoops = totalLoops; }
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }
    public void setTimeComplexity(String timeComplexity) { this.timeComplexity = timeComplexity; }
    public void setSpaceComplexity(String spaceComplexity) { this.spaceComplexity = spaceComplexity; }
    public void setBigOPattern(String bigOPattern) { this.bigOPattern = bigOPattern; }
    public void setCyclomaticComplexity(int cyclomaticComplexity) { this.cyclomaticComplexity = cyclomaticComplexity; }
    public void setEnergyConsumption(double energyConsumption) { this.energyConsumption = energyConsumption; }
    public void setMemoryUsage(String memoryUsage) { this.memoryUsage = memoryUsage; }
    public void setMaintainabilityIndex(int maintainabilityIndex) { this.maintainabilityIndex = maintainabilityIndex; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setCacheFriendliness(String cacheFriendliness) { this.cacheFriendliness = cacheFriendliness; }
    public void setLanguage(String language) { this.language = language; }
    public void setOptimizedCode(String optimizedCode) { this.optimizedCode = optimizedCode; }
    public void setLineIssues(List<LineIssue> lineIssues) { this.lineIssues = lineIssues; }
    public void setRecursionFindings(List<String> recursionFindings) { this.recursionFindings = recursionFindings; }
    public void setCodeSmells(List<String> codeSmells) { this.codeSmells = codeSmells; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    public void setSecurityIssues(List<String> securityIssues) { this.securityIssues = securityIssues; }
    public void setDuplicateCodeFindings(List<String> duplicateCodeFindings) { this.duplicateCodeFindings = duplicateCodeFindings; }

    // All existing getters stay exactly the same
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
    public String getSummary() { return summary; }
    public List<LineIssue> getLineIssues() { return lineIssues; }
    public List<String> getRecursionFindings() { return recursionFindings; }
    public List<String> getCodeSmells() { return codeSmells; }
    public List<String> getSuggestions() { return suggestions; }
    public List<String> getSecurityIssues() { return securityIssues; }
    public List<String> getDuplicateCodeFindings() { return duplicateCodeFindings; }
    public int getScore() { return score; }
}