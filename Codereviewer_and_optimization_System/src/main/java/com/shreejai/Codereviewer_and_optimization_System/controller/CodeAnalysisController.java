package com.shreejai.Codereviewer_and_optimization_System.controller;
import com.shreejai.Codereviewer_and_optimization_System.dto.ComparisonRequest;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.AIOptimizationEngine;
import com.shreejai.Codereviewer_and_optimization_System.dto.AnalysisResponse;
import com.shreejai.Codereviewer_and_optimization_System.dto.CodeRequest;
import com.shreejai.Codereviewer_and_optimization_System.service.CodeAnalysisService;
import com.shreejai.Codereviewer_and_optimization_System.service.GeminiOptimizationService;
import com.shreejai.Codereviewer_and_optimization_System.validator.InputValidator;
import org.springframework.web.bind.annotation.*;
import com.shreejai.Codereviewer_and_optimization_System.dto.ComparisonRequest;
import com.shreejai.Codereviewer_and_optimization_System.dto.ComparisonResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analyze")
public class CodeAnalysisController {

    private final CodeAnalysisService service;
    private final InputValidator validator;
    private final AIOptimizationEngine aiEngine;
    private final GeminiOptimizationService gemini;

    public CodeAnalysisController(
            CodeAnalysisService service,
            InputValidator validator,
            GeminiOptimizationService gemini) {
        this.service = service;
        this.validator = validator;
        this.aiEngine = new AIOptimizationEngine();
        this.gemini = gemini;
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

        // Use Gemini for levels 1 and 2
        // Use rule-based for levels 3, 4, 5
        String optimized;
        String engine;

        if (level <= 2) {
            optimized = gemini.optimize(
                    request.getCode(), lang, level);
            engine = "Gemini AI";
        } else {
            optimized = aiEngine.optimizeAtLevel(
                    request.getCode(), lang, level);
            engine = "Rule-based engine";
        }

        return Map.of(
                "level", String.valueOf(level),
                "language", lang,
                "engine", engine,
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
        String code = request.getCode();

        // Level 1 — Gemini maximum
        String level1 = gemini.optimize(code, lang, 1);
        try {
    Thread.sleep(15000); // 15 sec gap — safe for free tier
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
        // Level 2 — Gemini strong
        String level2 = gemini.optimize(code, lang, 2);
try {
    Thread.sleep(15000); // 15 sec gap — safe for free tier
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
        // Levels 3-5 — rule based
        String level3 = aiEngine.optimizeAtLevel(
                code, lang, 3);
        String level4 = aiEngine.optimizeAtLevel(
                code, lang, 4);

        return Map.of(
                "language", lang,
                "levels", Map.of(
                        1, level1,
                        2, level2,
                        3, level3,
                        4, level4,
                        5, code
                )
        );
    }

    @PostMapping("/ai-review")
    public Map<String, String> aiReview(
            @RequestBody CodeRequest request) {

        List<String> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(
                    String.join(", ", errors));
        }

        String review = gemini.analyzeAndSuggest(
                request.getCode(),
                request.getLanguageOrDefault());

        return Map.of(
                "language", request.getLanguageOrDefault(),
                "aiReview", review
        );
    }

    private String getLevelDescription(int level) {
        return switch (level) {
            case 1 ->
                "Maximum (Gemini AI) — all fixes applied";
             
            case 2 ->
                "Strong (Gemini AI) — security + performance";
        
            case 3 ->
                "Moderate (rule-based) — critical fixes";
            case 4 ->
                "Minimal (rule-based) — comments only";
            case 5 -> "Original — no changes";
            default -> "Unknown";
        };
    }
   @PostMapping("/compare")
public ComparisonResponse compareCode(
        @RequestBody ComparisonRequest request) {

    if (request.getCodeA() == null || request.getCodeA().isBlank() ||
        request.getCodeB() == null || request.getCodeB().isBlank()) {
        throw new IllegalArgumentException(
                "Both codeA and codeB are required.");
    }

    String lang = request.getLanguageOrDefault();
    String raw = gemini.compareCode(
            request.getCodeA(), request.getCodeB(), lang);

    if (raw == null) {
        throw new RuntimeException("Gemini comparison unavailable.");
    }

    try {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(raw);

        int scoreA = node.path("scoreA").asInt(5);
        int scoreB = node.path("scoreB").asInt(5);

        AnalysisResponse analysisA = new AnalysisResponse();
        analysisA.setSummary(node.path("analysisA").asText());
        analysisA.setScore(scoreA);

        AnalysisResponse analysisB = new AnalysisResponse();
        analysisB.setSummary(node.path("analysisB").asText());
        analysisB.setScore(scoreB);

        return new ComparisonResponse(
                analysisA,
                analysisB,
                node.path("winner").asText(),
                node.path("reason").asText(),
                Math.abs(scoreA - scoreB)
        );

    } catch (Exception e) {
        throw new RuntimeException(
                "Failed to parse Gemini response: " + e.getMessage());
    }
}
}