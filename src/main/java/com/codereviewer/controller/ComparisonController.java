package com.codereviewer.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codereviewer.dto.AnalysisResponse;
import com.codereviewer.dto.CodeRequest;
import com.codereviewer.dto.ComparisonRequest;
import com.codereviewer.dto.ComparisonResponse;
import com.codereviewer.service.CodeAnalysisService;

@RestController
@RequestMapping("/api/v1/compare")
public class ComparisonController {

    private final CodeAnalysisService service;

    public ComparisonController(CodeAnalysisService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> compare(
            @RequestBody ComparisonRequest request) {

        // Manual validation — no InputValidator used here
        if (request == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            "Request body is required."));
        }
        if (request.getCodeA() == null
                || request.getCodeA().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            "codeA cannot be empty."));
        }
        if (request.getCodeB() == null
                || request.getCodeB().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            "codeB cannot be empty."));
        }

        try {
            String lang = request.getLanguageOrDefault();

            // Build two independent requests
            CodeRequest reqA = new CodeRequest();
            reqA.setCode(request.getCodeA().trim());
            reqA.setLanguage(lang);

            CodeRequest reqB = new CodeRequest();
            reqB.setCode(request.getCodeB().trim());
            reqB.setLanguage(lang);

            // Analyze both directly — no validator
            AnalysisResponse a = service.analyze(reqA);
            AnalysisResponse b = service.analyze(reqB);

            // Determine winner
            String winner;
            String reason;
            int diff = Math.abs(a.getScore() - b.getScore());

            if (a.getScore() > b.getScore()) {
                winner = "Code A";
                reason = buildReason(a, b);
            } else if (b.getScore() > a.getScore()) {
                winner = "Code B";
                reason = buildReason(b, a);
            } else {
                winner = "Tie";
                reason = "Both snippets have equal scores.";
            }

            return ResponseEntity.ok(
                    new ComparisonResponse(
                            a, b, winner, reason, diff));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Analysis failed",
                            "message", e.getMessage()));
        }
    }

    private String buildReason(AnalysisResponse better,
                                AnalysisResponse worse) {
        StringBuilder sb = new StringBuilder();
        sb.append("Wins by ")
          .append(better.getScore() - worse.getScore())
          .append(" points. ");

        if (better.getCyclomaticComplexity()
                < worse.getCyclomaticComplexity()) {
            sb.append("Lower cyclomatic complexity. ");
        }
        if (better.getEnergyConsumption()
                < worse.getEnergyConsumption()) {
            sb.append("Lower energy consumption. ");
        }
        if (better.getSecurityIssues() != null
                && worse.getSecurityIssues() != null
                && better.getSecurityIssues().size()
                < worse.getSecurityIssues().size()) {
            sb.append("Fewer security issues. ");
        }
        if (better.getMaxDepth() < worse.getMaxDepth()) {
            sb.append("Lower nesting depth. ");
        }

        return sb.toString().trim();
    }
}