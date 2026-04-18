package com.codereviewer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codereviewer.model.ScoreHistoryEntry;

@Repository
public interface ScoreHistoryRepository extends JpaRepository<ScoreHistoryEntry, Long> {

    List<ScoreHistoryEntry> findTop10ByOrderByAnalyzedAtDesc();

    List<ScoreHistoryEntry> findByLanguageOrderByAnalyzedAtDesc(String language);
}
