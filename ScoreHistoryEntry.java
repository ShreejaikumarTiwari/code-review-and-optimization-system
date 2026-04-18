package com.codereviewer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class ScoreHistoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String language;
    private int score;
    private int cyclomaticComplexity;
    private double energyConsumption;
    private String timeComplexity;
    private String grade;
    private LocalDateTime analyzedAt;

    public ScoreHistoryEntry() {}

    public ScoreHistoryEntry(String language, int score, int cyclomaticComplexity,
                              double energyConsumption, String timeComplexity,
                              String grade) {
        this.language = language;
        this.score = score;
        this.cyclomaticComplexity = cyclomaticComplexity;
        this.energyConsumption = energyConsumption;
        this.timeComplexity = timeComplexity;
        this.grade = grade;
        this.analyzedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getLanguage() { return language; }
    public int getScore() { return score; }
    public int getCyclomaticComplexity() { return cyclomaticComplexity; }
    public double getEnergyConsumption() { return energyConsumption; }
    public String getTimeComplexity() { return timeComplexity; }
    public String getGrade() { return grade; }
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
}