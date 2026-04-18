package com.codereviewer.controller;

import com.codereviewer.model.ScoreHistoryEntry;
import com.codereviewer.repository.ScoreHistoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/history")
public class ScoreHistoryController {

    private final ScoreHistoryRepository repository;

    public ScoreHistoryController(ScoreHistoryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ScoreHistoryEntry> getHistory() {
        return repository.findTop10ByOrderByAnalyzedAtDesc();
    }

    @GetMapping("/{language}")
    public List<ScoreHistoryEntry> getByLanguage(@PathVariable String language) {
        return repository.findByLanguageOrderByAnalyzedAtDesc(language);
    }

    @DeleteMapping
    public void clearHistory() {
        repository.deleteAll();
    }
}
