package com.shreejai.Codereviewer_and_optimization_System.controller;

import com.shreejai.Codereviewer_and_optimization_System.model.ScoreHistoryEntry;
import com.shreejai.Codereviewer_and_optimization_System.repository.ScoreHistoryRepository;
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
