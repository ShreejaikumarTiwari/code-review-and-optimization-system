package com.codereviewer.controller;

import com.codereviewer.dto.BatchRequest;
import com.codereviewer.dto.CodeRequest;
import com.codereviewer.service.CodeAnalysisService;
import com.codereviewer.service.WebhookService;
import com.codereviewer.validator.InputValidator;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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