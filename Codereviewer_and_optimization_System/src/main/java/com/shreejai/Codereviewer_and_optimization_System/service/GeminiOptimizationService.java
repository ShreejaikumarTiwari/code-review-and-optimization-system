package com.shreejai.Codereviewer_and_optimization_System.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.Optional;

@Service
public class GeminiOptimizationService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private static final int MAX_RETRIES = 3;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public String optimize(String code, String language, int level) {
        String prompt = buildPrompt(code, language, level);
        return callGemini(prompt)
                .orElse("// Gemini unavailable — falling back to rule-based\n\n" + code);
    }

    public String analyzeAndSuggest(String code, String language) {
        String prompt = "Analyze this " + language + " code and provide:\n" +
                "1. Time complexity analysis\n" +
                "2. Space complexity analysis\n" +
                "3. Security vulnerabilities\n" +
                "4. Top 5 specific improvements\n" +
                "5. Optimized version of the code\n\n" +
                "Be concise and technical.\n\n" +
                "Code:\n```" + language + "\n" + code + "\n```";

        return callGemini(prompt)
                .orElse("Gemini analysis unavailable.");
    }

   private Optional<String> callGemini(String prompt) {
    for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
        try {
            String body = buildRequestBody(prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return Optional.of(extractTextFromResponse(response.body()));
            }

            if (response.statusCode() == 429) {
                System.out.println("Gemini 429 on attempt " + attempt + ", retrying...");
                long delay = (long) Math.pow(2, attempt) * 1000L;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return Optional.empty();
                }
                continue;
            }

            // Non-retryable error (4xx, 5xx)
            System.out.println("Gemini error: HTTP " + response.statusCode()
                    + " — " + response.body());
            return Optional.empty();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (Exception e) {
            System.out.println("Gemini exception attempt " + attempt + ": " + e.getMessage());
            if (attempt == MAX_RETRIES - 1) return Optional.empty();
        }
    }
    return Optional.empty();
}

    private String buildRequestBody(String prompt) throws Exception {
        ObjectNode root = MAPPER.createObjectNode();
        ArrayNode contents = root.putArray("contents");
        ObjectNode content = contents.addObject();
        ArrayNode parts = content.putArray("parts");
        parts.addObject().put("text", prompt);

        ObjectNode config = root.putObject("generationConfig");
        config.put("temperature", 0.3);
        config.put("maxOutputTokens", 4096);

        return MAPPER.writeValueAsString(root);
    }

    private String extractTextFromResponse(String responseBody) {
        try {
            JsonNode root = MAPPER.readTree(responseBody);
            String text = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText();

            return text
                    .replaceAll("```[a-zA-Z]*\\n", "")
                    .replaceAll("```", "")
                    .trim();

        } catch (Exception e) {
            return "// Error parsing Gemini response: " + e.getMessage();
        }
    }

    private String buildPrompt(String code, String language, int level) {
        String levelDesc = switch (level) {
            case 1 -> "Apply ALL possible optimizations: fix every security vulnerability," +
                    " reduce time complexity, improve memory usage, fix all code smells," +
                    " add proper error handling, replace deprecated patterns." +
                    " Rewrite nested loops with better algorithms where possible.";
            case 2 -> "Apply security fixes and performance improvements." +
                    " Fix security issues, reduce unnecessary complexity, improve readability.";
            case 3 -> "Apply only critical security fixes. Fix hardcoded secrets," +
                    " SQL injection risks, weak random, empty catch blocks.";
            case 4 -> "Add improvement comments only. Do not change the code." +
                    " Just add TODO comments where improvements are needed.";
            default -> "Return the code unchanged.";
        };

        return "You are an expert " + language + " code optimizer.\n\n" +
                "Task: " + levelDesc + "\n\n" +
                "Rules:\n" +
                "- Return ONLY the optimized code\n" +
                "- No explanations before or after\n" +
                "- Keep the same class/function names\n" +
                "- Add short inline comments for major changes\n" +
                "- Start with optimization summary as code comment\n\n" +
                "Code to optimize:\n" +
                "```" + language + "\n" + code + "\n```\n\n" +
                "Return only the optimized code:";
    }
   public String compareCode(String codeA, String codeB, String language) {
    String prompt = "Compare these two " + language + " code snippets.\n\n" +
            "Respond in this exact JSON format with no extra text:\n" +
            "{\n" +
            "  \"winner\": \"Code A\" or \"Code B\" or \"Tie\",\n" +
            "  \"reason\": \"brief explanation\",\n" +
            "  \"scoreA\": number 1-10,\n" +
            "  \"scoreB\": number 1-10,\n" +
            "  \"analysisA\": \"performance, security, readability summary for Code A\",\n" +
            "  \"analysisB\": \"performance, security, readability summary for Code B\"\n" +
            "}\n\n" +
            "Code A:\n```" + language + "\n" + codeA + "\n```\n\n" +
            "Code B:\n```" + language + "\n" + codeB + "\n```";

    return callGemini(prompt)
            .orElse(null);
}

}