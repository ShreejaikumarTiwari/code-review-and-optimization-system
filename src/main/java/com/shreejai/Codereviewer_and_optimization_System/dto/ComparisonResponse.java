package com.shreejai.Codereviewer_and_optimization_System.dto;

public class ComparisonResponse {

    private final AnalysisResponse analysisA;
    private final AnalysisResponse analysisB;
    private final String winner;
    private final String reason;
    private final int scoreDifference;

    public ComparisonResponse(AnalysisResponse analysisA,
                               AnalysisResponse analysisB,
                               String winner, String reason,
                               int scoreDifference) {
        this.analysisA = analysisA;
        this.analysisB = analysisB;
        this.winner = winner;
        this.reason = reason;
        this.scoreDifference = scoreDifference;
    }

    public AnalysisResponse getAnalysisA() { return analysisA; }
    public AnalysisResponse getAnalysisB() { return analysisB; }
    public String getWinner() { return winner; }
    public String getReason() { return reason; }
    public int getScoreDifference() { return scoreDifference; }
}