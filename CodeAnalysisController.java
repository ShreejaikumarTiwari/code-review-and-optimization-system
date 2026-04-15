package com.codereviewer.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codereviewer.analyzer.AIOptimizationEngine;
import com.codereviewer.dto.AnalysisResponse;
import com.codereviewer.dto.CodeRequest;
import com.codereviewer.service.CodeAnalysisService;
import com.codereviewer.validator.InputValidator;

@RestController
@RequestMapping("/api/v1/analyze")
public class CodeAnalysisController {

    private final CodeAnalysisService service;
    private final InputValidator validator;
    private final AIOptimizationEngine aiEngine;

    public CodeAnalysisController(
            CodeAnalysisService service,
            InputValidator validator) {
        this.service = service;
        this.validator = validator;
        this.aiEngine = new AIOptimizationEngine();
    }

    @PostMapping
    public AnalysisResponse analyze(
            @RequestBody CodeRequest request) {

        List<String> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(
                    String.join(", ", errors));
        }
        return service.analyze(request);
    }

    @PostMapping("/optimize/{level}")
    public Map<String, String> optimizeAtLevel(
            @RequestBody CodeRequest request,
            @PathVariable int level) {

        if (level < 1 || level > 5) {
            throw new IllegalArgumentException(
                    "Level must be between 1 and 5.");
        }

        List<String> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(
                    String.join(", ", errors));
        }

        String lang = request.getLanguageOrDefault();
        String optimized = aiEngine.optimizeAtLevel(
                request.getCode(), lang, level);

        return Map.of(
                "level", String.valueOf(level),
                "language", lang,
                "description", getLevelDescription(level),
                "optimizedCode", optimized
        );
    }

    @PostMapping("/optimize/all")
    public Map<String, Object> optimizeAllLevels(
            @RequestBody CodeRequest request) {

        List<String> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(
                    String.join(", ", errors));
        }

        String lang = request.getLanguageOrDefault();
        Map<Integer, String> levels =
                aiEngine.optimizeAllLevels(
                        request.getCode(), lang);

        return Map.of(
                "language", lang,
                "levels", levels
        );
    }

    private String getLevelDescription(int level) {
        return switch (level) {
            case 1 -> "Maximum — all fixes applied";
            case 2 -> "Strong — security and performance fixes";
            case 3 -> "Moderate — critical security fixes only";
            case 4 -> "Minimal — comments added only";
            case 5 -> "Original — no changes";
            default -> "Unknown";
        };
    }
}
