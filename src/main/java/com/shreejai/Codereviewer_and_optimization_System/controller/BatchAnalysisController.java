package com.shreejai.Codereviewer_and_optimization_System.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shreejai.Codereviewer_and_optimization_System.dto.BatchRequest;
import com.shreejai.Codereviewer_and_optimization_System.dto.CodeRequest;
import com.shreejai.Codereviewer_and_optimization_System.service.CodeAnalysisService;
import com.shreejai.Codereviewer_and_optimization_System.service.WebhookService;
import com.shreejai.Codereviewer_and_optimization_System.validator.InputValidator;

@RestController
@RequestMapping("/api/v1/batch")
public class BatchAnalysisController {

    private final CodeAnalysisService service;
    private final WebhookService webhookService;
    private final InputValidator validator;

    public BatchAnalysisController(CodeAnalysisService service,
                                    WebhookService webhookService,
                                    InputValidator validator) {
        this.service = service;
        this.webhookService = webhookService;
        this.validator = validator;
    }

    @PostMapping
    public Map<String, Object> analyzeBatch(
            @RequestBody BatchRequest request) {

        if (request.getCodes() == null
                || request.getCodes().isEmpty()) {
            throw new IllegalArgumentException(
                    "No code snippets provided.");
        }
        if (request.getCodes().size() > 10) {
            throw new IllegalArgumentException(
                    "Maximum 10 snippets per batch request.");
        }

        List<Map<String, Object>> results = new ArrayList<>();

        for (int i = 0; i < request.getCodes().size(); i++) {
            CodeRequest code = request.getCodes().get(i);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("index", i + 1);

            List<String> errors = validator.validate(code);
            if (!errors.isEmpty()) {
                result.put("status", "SKIPPED");
                result.put("errors", errors);
            } else {
                try {
                    result.put("status", "SUCCESS");
                    result.put("analysis", service.analyze(code));
                } catch (Exception e) {
                    result.put("status", "FAILED");
                    result.put("error", e.getMessage());
                }
            }
            results.add(result);
        }

        long processed = results.stream()
                .filter(r -> "SUCCESS".equals(r.get("status")))
                .count();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalSubmitted", request.getCodes().size());
        response.put("totalProcessed", processed);
        response.put("results", results);

        if (request.getWebhookUrl() != null
                && !request.getWebhookUrl().isBlank()) {
            webhookService.notify(request.getWebhookUrl(), response);
            response.put("webhookNotified", true);
        }

        return response;
    }
}