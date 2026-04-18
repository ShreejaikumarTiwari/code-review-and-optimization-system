package com.codereviewer.service;

import com.codereviewer.dto.AnalysisResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportExportService {

    public String toJson(AnalysisResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        appendField(sb, "language", response.getLanguage());
        appendField(sb, "timeComplexity", response.getTimeComplexity());
        appendField(sb, "spaceComplexity", response.getSpaceComplexity());
        appendField(sb, "bigOPattern", response.getBigOPattern());
        appendFieldInt(sb, "cyclomaticComplexity",
                response.getCyclomaticComplexity());
        appendFieldDouble(sb, "energyConsumption",
                response.getEnergyConsumption());
        appendField(sb, "memoryUsage", response.getMemoryUsage());
        appendFieldInt(sb, "maintainabilityIndex",
                response.getMaintainabilityIndex());
        appendField(sb, "grade", response.getGrade());
        appendField(sb, "cacheFriendliness",
                response.getCacheFriendliness());
        appendFieldInt(sb, "score", response.getScore());
        appendList(sb, "securityIssues",
                response.getSecurityIssues());
        appendList(sb, "suggestions",
                response.getSuggestions());
        appendList(sb, "codeSmells",
                response.getCodeSmells());
        appendList(sb, "recursionFindings",
                response.getRecursionFindings());
        appendList(sb, "duplicateCodeFindings",
                response.getDuplicateCodeFindings());
        sb.append("  \"optimizedCode\": ")
          .append(jsonString(response.getOptimizedCode()))
          .append("\n");
        sb.append("}");
        return sb.toString();
    }

    public byte[] toPdf(AnalysisResponse response) {
        String html = buildHtml(response);
        return html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private String buildHtml(AnalysisResponse r) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head>")
          .append("<meta charset='UTF-8'>")
          .append("<title>Code Analysis Report</title>")
          .append("<style>")
          .append("body{font-family:Arial,sans-serif;margin:40px;color:#333}")
          .append("h1{color:#2c3e50;border-bottom:2px solid #3498db;")
          .append("padding-bottom:10px}")
          .append("h2{color:#2980b9;margin-top:30px}")
          .append("table{border-collapse:collapse;width:100%;margin:20px 0}")
          .append("th{background:#3498db;color:white;padding:10px;")
          .append("text-align:left}")
          .append("td{padding:8px 10px;border-bottom:1px solid #ddd}")
          .append("tr:nth-child(even){background:#f8f9fa}")
          .append(".issue{background:#ffeaea;padding:8px;margin:4px 0;")
          .append("border-left:3px solid #e74c3c;border-radius:3px}")
          .append(".suggestion{background:#eafaf1;padding:8px;margin:4px 0;")
          .append("border-left:3px solid #27ae60;border-radius:3px}")
          .append(".info{background:#eaf4fb;padding:8px;margin:4px 0;")
          .append("border-left:3px solid #3498db;border-radius:3px}")
          .append(".grade{font-size:48px;font-weight:bold;color:#2980b9}")
          .append("@media print{body{margin:20px}}")
          .append("</style></head><body>");

        sb.append("<h1>Code Analysis Report</h1>");

        sb.append("<div class='grade'>")
          .append(safe(r.getGrade()))
          .append("</div>");
        sb.append("<p><strong>Performance Score: </strong>")
          .append(r.getScore()).append(" / 100</p>");

        sb.append("<h2>Metrics Summary</h2>");
        sb.append("<table><tr><th>Metric</th><th>Value</th></tr>");
        addRow(sb, "Language", safe(r.getLanguage()));
        addRow(sb, "Time Complexity", safe(r.getTimeComplexity()));
        addRow(sb, "Space Complexity", safe(r.getSpaceComplexity()));
        addRow(sb, "Big-O Pattern", safe(r.getBigOPattern()));
        addRow(sb, "Cyclomatic Complexity",
                String.valueOf(r.getCyclomaticComplexity()));
        addRow(sb, "Energy Consumption",
                r.getEnergyConsumption() + " nJ");
        addRow(sb, "Memory Usage", safe(r.getMemoryUsage()));
        addRow(sb, "Maintainability Index",
                String.valueOf(r.getMaintainabilityIndex()));
        addRow(sb, "Cache Friendliness",
                safe(r.getCacheFriendliness()));
        addRow(sb, "Total Loops",
                String.valueOf(r.getTotalLoops()));
        addRow(sb, "Max Nesting Depth",
                String.valueOf(r.getMaxDepth()));
        sb.append("</table>");

        addSection(sb, "Security Issues",
                r.getSecurityIssues(), "issue");
        addSection(sb, "Optimization Suggestions",
                r.getSuggestions(), "suggestion");
        addSection(sb, "Code Smells",
                r.getCodeSmells(), "issue");
        addSection(sb, "Recursion Findings",
                r.getRecursionFindings(), "info");
        addSection(sb, "Duplicate Code",
                r.getDuplicateCodeFindings(), "info");

        if (r.getOptimizedCode() != null
                && !r.getOptimizedCode().isBlank()) {
            sb.append("<h2>Optimized Code</h2>");
            sb.append("<pre style='background:#f4f4f4;padding:16px;")
              .append("border-radius:4px;overflow:auto;font-size:13px'>")
              .append(escapeHtml(r.getOptimizedCode()))
              .append("</pre>");
        }

        sb.append("<p style='color:#999;font-size:12px;margin-top:40px'>")
          .append("Generated by Code Reviewer &amp; Optimizer")
          .append("</p>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private void addRow(StringBuilder sb, String key, String value) {
        sb.append("<tr><td><strong>").append(key)
          .append("</strong></td><td>").append(value)
          .append("</td></tr>");
    }

    private void addSection(StringBuilder sb, String title,
                              List<String> items, String cssClass) {
        if (items == null || items.isEmpty()) return;
        sb.append("<h2>").append(title).append("</h2>");
        for (String item : items) {
            sb.append("<div class='").append(cssClass).append("'>")
              .append(escapeHtml(item)).append("</div>");
        }
    }

    private void appendField(StringBuilder sb,
                               String key, String value) {
        sb.append("  \"").append(key).append("\": ")
          .append(jsonString(value)).append(",\n");
    }

    private void appendFieldInt(StringBuilder sb,
                                  String key, int value) {
        sb.append("  \"").append(key).append("\": ")
          .append(value).append(",\n");
    }

    private void appendFieldDouble(StringBuilder sb,
                                     String key, double value) {
        sb.append("  \"").append(key).append("\": ")
          .append(value).append(",\n");
    }

    private void appendList(StringBuilder sb,
                              String key, List<String> items) {
        sb.append("  \"").append(key).append("\": [");
        if (items != null && !items.isEmpty()) {
            sb.append("\n");
            for (int i = 0; i < items.size(); i++) {
                sb.append("    ").append(jsonString(items.get(i)));
                if (i < items.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ");
        }
        sb.append("],\n");
    }

    private String jsonString(String value) {
        if (value == null) return "null";
        return "\"" + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                + "\"";
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }

    private String safe(String value) {
        return value != null ? value : "N/A";
    }
}