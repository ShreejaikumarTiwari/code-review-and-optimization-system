package com.codereviewer.analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
@Component
/**
 * Enterprise-grade CodeOptimizerEngine for comprehensive code analysis and optimization.
 * 
 * Features:
 * - Multi-language support (Java, Python, C/C++)
 * - Severity-based issue categorization (CRITICAL, HIGH, MEDIUM, LOW)
 * - Performance, Security, Maintainability, and Correctness analysis
 * - Code metrics tracking (complexity, style violations, patterns found)
 * - Extensible pattern-based detection system
 * 
 * Designed for production use in CI/CD pipelines and code review platforms.
 
 * @author CodeReviewer Team
 * @version 2.0
 */

public class CodeOptimizerEngine {
    
    // Severity levels for categorizing issues
    public enum Severity {
        CRITICAL(1), HIGH(2), MEDIUM(3), LOW(4);
        
        private final int priority;
        Severity(int priority) { this.priority = priority; }
        public int getPriority() { return priority; }
    }
    
    // Java patterns
    private static final Pattern JAVA_HARDCODED_SECRET_PATTERN = Pattern.compile(
            "(private|public|protected)?\\s*(static)?\\s*String\\s+(password|secret|apiKey|apiSecret|token|credential|privateKey)\\s*=\\s*\"[^\"]*\"");
    private static final Pattern JAVA_SQL_INJECTION_PATTERN = Pattern.compile(
            "\"\\s*\\+\\s*\\w+\\s*\\+\\s*\".*(?:SELECT|INSERT|UPDATE|DELETE|DROP)");
    private static final Pattern JAVA_THREAD_UNSAFE_PATTERN = Pattern.compile(
            "SimpleDateFormat|DecimalFormat|NumberFormat");
    private static final Pattern JAVA_RESOURCE_LEAK_PATTERN = Pattern.compile(
            "new\\s+(FileInputStream|FileOutputStream|Scanner|BufferedReader)(?!.*try-with-resources)");
    private static final Pattern JAVA_INEFFICIENT_STRING_PATTERN = Pattern.compile(
            "String\\s+\\w+\\s*=\\s*\"\"");
    
    // Python patterns
    private static final Pattern PYTHON_RANGE_LEN_PATTERN = Pattern.compile(
            "for\\s+(\\w+)\\s+in\\s+range\\s*\\(\\s*len\\s*\\(\\s*(\\w+)\\s*\\)\\s*\\)\\s*:");
    private static final Pattern PYTHON_GLOBAL_PATTERN = Pattern.compile("global\\s+\\w+");
    private static final Pattern PYTHON_WILDCARD_IMPORT = Pattern.compile("from\\s+\\w+\\s+import\\s+\\*");
    private static final Pattern PYTHON_EXEC_EVAL_PATTERN = Pattern.compile("\\b(exec|eval)\\s*\\(");
    
    // C++ patterns
    private static final Pattern CPP_ENDL_PATTERN = Pattern.compile("endl");
    private static final Pattern CPP_AUTO_PATTERN = Pattern.compile("\\bauto\\b");
    private static final Pattern CPP_CAST_PATTERN = Pattern.compile("\\(\\w+\\s*\\)");
    
    private final List<OptimizationIssue> issues = new ArrayList<>();
    private final Map<String, Integer> metrics = new HashMap<>();
    private int complexityScore = 0;

    /**
     * Analyzes and optimizes code based on the specified language.
     * 
     * @param code The source code to analyze
     * @param language The programming language (java, python, cpp, c++)
     * @return Optimized code with inline suggestions
     */
    public String optimize(String code, String language) {
        issues.clear();
        metrics.clear();
        complexityScore = 0;
        
        if (code == null || code.trim().isEmpty()) {
            return code;
        }
        
        language = normalizeLanguage(language);
        recordMetric("total_lines", code.split("\n").length);
        recordMetric("code_length", code.length());
        
        return switch (language) {
            case "java" -> optimizeJava(code);
            case "python" -> optimizePython(code);
            case "cpp" -> optimizeCpp(code);
            default -> code;
        };
    }
    
    /**
     * Returns all detected issues with severity levels.
     * @return List of optimization issues found
     */
    public List<OptimizationIssue> getIssues() {
        return Collections.unmodifiableList(issues);
    }
    
    /**
     * Returns code metrics collected during analysis.
     * @return Map of metric names to values
     */
    public Map<String, Integer> getMetrics() {
        return Collections.unmodifiableMap(metrics);
    }
    
    /**
     * Returns overall complexity score (0-100).
     * @return Complexity score
     */
    public int getComplexityScore() {
        return complexityScore;
    }
    
    private String normalizeLanguage(String language) {
        if (language == null) return "";
        language = language.toLowerCase().trim();
        return language.equals("c++") ? "cpp" : language;
    }
    
    private void recordIssue(String description, Severity severity, String category) {
        issues.add(new OptimizationIssue(description, severity, category));
    }
    
    private void recordMetric(String name, int value) {
        metrics.put(name, metrics.getOrDefault(name, 0) + value);
    }

    /**
     * Inner class representing an optimization issue found during analysis.
     */
    public static class OptimizationIssue {
        private final String description;
        private final Severity severity;
        private final String category;

        public OptimizationIssue(String description, Severity severity, String category) {
            this.description = description;
            this.severity = severity;
            this.category = category;
        }

        public String getDescription() { return description; }
        public Severity getSeverity() { return severity; }
        public String getCategory() { return category; }

        @Override
        public String toString() {
            return String.format("[%s] %s - %s", severity, category, description);
        }
    }

    /**
     * Optimizes code based on the specified language.
     * 
     * @param code The source code to optimize
     * @param language The programming language (java, python, cpp, c++)
     * @return Optimized code with improvement suggestions
     */
    private String optimizeJava(String code) {
        String optimized = code;

        // CRITICAL: Detect SQL injection vulnerabilities
        if (JAVA_SQL_INJECTION_PATTERN.matcher(optimized).find()) {
            recordIssue("SQL injection vulnerability: String concatenation in SQL query", Severity.CRITICAL, "Security");
            optimized = "// ⚠️ CRITICAL: SQL Injection Risk - Use PreparedStatement with parameterized queries\n" + optimized;
        }

        // CRITICAL: Detect hardcoded secrets
        if (JAVA_HARDCODED_SECRET_PATTERN.matcher(optimized).find()) {
            recordIssue("Hardcoded secrets detected", Severity.CRITICAL, "Security");
            optimized = optimized.replaceAll(
                    "((private|public|protected)?\\s*(static)?\\s*String\\s+(password|secret|apiKey|apiSecret|token|credential|privateKey)\\s*=\\s*\"[^\"]*\")",
                    "// ⚠️ CRITICAL SECURITY: Hardcoded secret - move to environment variable/config server\n    $1");
        }

        // HIGH: Thread safety - SimpleDateFormat, static collections
        if (JAVA_THREAD_UNSAFE_PATTERN.matcher(optimized).find()) {
            recordIssue("Thread-unsafe class used (SimpleDateFormat, etc)", Severity.HIGH, "Concurrency");
            optimized = "// WARNING: Thread-unsafe - use ThreadLocal or java.time API\n" + optimized;
        }

        // HIGH: Detect resource leaks
        if (JAVA_RESOURCE_LEAK_PATTERN.matcher(optimized).find()) {
            recordIssue("Potential resource leak - missing try-with-resources", Severity.HIGH, "Resource Management");
            optimized = "// WARNING: Use try-with-resources to ensure resource closure\n" + optimized;
        }

        // Replace new Random() with SecureRandom
        if (optimized.contains("new Random()")) {
            recordIssue("Weak random number generation", Severity.HIGH, "Security");
            optimized = optimized.replace("new Random()", "new SecureRandom()");
            if (!optimized.contains("java.security.SecureRandom")) {
                optimized = addJavaImport(optimized, "java.security.SecureRandom");
            }
        }

        // MEDIUM: Optimize string comparisons
        optimized = optimizeJavaStringComparison(optimized);

        // String concatenation in loops
        if (optimized.contains("+= \"") && optimized.contains("for ")) {
            recordIssue("String concatenation in loop - O(n²) complexity", Severity.HIGH, "Performance");
            optimized = optimized.replaceAll(
                    "for\\s*\\(",
                    "// PERF: Use StringBuilder instead of += strings (O(n) vs O(n²))\n    for (");
        }

        // Logging instead of System.out/err
        if (optimized.contains("System.out") || optimized.contains("System.err")) {
            recordIssue("System.out/err used instead of logger", Severity.MEDIUM, "Maintainability");
            optimized = optimized.replaceAll(
                    "System\\.(out|err)\\.",
                    "// TODO: Use SLF4J or Log4j\n        System.$1.");
        }

        // Empty catch blocks
        if (optimized.contains("catch")) {
            if (optimized.matches(".*catch\\s*\\([^)]+\\)\\s*\\{\\s*\\}.*")) {
                recordIssue("Empty catch block - suppressing exceptions", Severity.CRITICAL, "Error Handling");
                optimized = optimized.replaceAll(
                        "catch\\s*\\(\\s*([^)]+)\\s*\\)\\s*\\{\\s*\\}",
                        "catch ($1) {\n        log.error(\"Exception caught: \", $1);\n        // TODO: Handle appropriately\n    }");
            }
        }

        // N+1 query pattern
        if (optimized.contains("for") && optimized.contains(".findById")) {
            recordIssue("N+1 query pattern detected", Severity.HIGH, "Database");
            optimized = "// WARNING: N+1 query detected - use batch loading, JOIN, or fetch with one query\n" + optimized;
        }

        // MEDIUM: Optional without proper handling
        if (optimized.contains(".get()") && !optimized.contains("isPresent")) {
            recordIssue("Optional.get() without null check", Severity.MEDIUM, "Null Safety");
            optimized = "// WARNING: Optional.get() throws NoSuchElementException - use isPresent() or orElse()\n" + optimized;
        }

        // LOW: Avoid using == for object comparison
        if (optimized.matches(".*\\b\\w+\\s*==\\s*\\w+\\b.*") && !optimized.contains(".equals(")) {
            recordIssue("Object comparison using == instead of .equals()", Severity.MEDIUM, "Correctness");
        }

        // LOW: Inefficient string initialization
        if (JAVA_INEFFICIENT_STRING_PATTERN.matcher(optimized).find()) {
            recordIssue("Inefficient empty String initialization", Severity.LOW, "Performance");
        }

        complexityScore = Math.min(100, Math.max(0, 
                issues.size() * 10 + optimized.length() / 1000));
        
        return optimized;
    }
    
    private String optimizeJavaStringComparison(String code) {
        // Be careful not to apply in comments
        if (!code.contains("//") && !code.contains("/*")) {
            String result = code.replaceAll(
                    "\\b(\\w+)\\s*==\\s*\"([^\"]*)\"\\b",
                    "\"$2\".equals($1)");
            result = result.replaceAll(
                    "\\b\"([^\"]*)\"\\s*==\\s*(\\w+)\\b",
                    "\"$1\".equals($2)");
            if (!result.equals(code)) {
                recordIssue("String comparison using == instead of .equals()", Severity.MEDIUM, "Correctness");
            }
            return result;
        }
        return code;
    }
    
    private String addJavaImport(String code, String importStatement) {
        if (code.contains("import " + importStatement)) {
            return code;
        }
        int lastImportIndex = code.lastIndexOf("import ");
        if (lastImportIndex > 0) {
            int eol = code.indexOf("\n", lastImportIndex);
            return code.substring(0, eol) + "\nimport " + importStatement + ";" + code.substring(eol);
        }
        return code;
    }

    private String optimizePython(String code) {
        String optimized = code;
        int pythonIssueCount = 0;

        // CRITICAL: Detect eval/exec (code injection vulnerability)
        if (PYTHON_EXEC_EVAL_PATTERN.matcher(optimized).find()) {
            recordIssue("eval/exec usage - critical code injection vulnerability", Severity.CRITICAL, "Security");
            optimized = "# ⚠️ CRITICAL: Never use eval() or exec() with untrusted input - code injection risk\n" + optimized;
            pythonIssueCount++;
        }

        // CRITICAL: Detect wildcard imports (namespace pollution)
        if (PYTHON_WILDCARD_IMPORT.matcher(optimized).find()) {
            recordIssue("Wildcard import - namespace pollution and hidden dependencies", Severity.CRITICAL, "Maintainability");
            optimized = "# AVOID: from module import * - use explicit imports instead\n" + optimized;
            pythonIssueCount++;
        }

        // HIGH: Global variable usage (hard to test and maintain)
        if (PYTHON_GLOBAL_PATTERN.matcher(optimized).find()) {
            recordIssue("Global variable usage - difficult to test/maintain", Severity.HIGH, "Maintainability");
            optimized = "# WARNING: Global variables reduce code testability - use parameters or class attributes\n" + optimized;
            pythonIssueCount++;
        }

        // HIGH: Replace range(len(x)) with enumerate
        if (PYTHON_RANGE_LEN_PATTERN.matcher(optimized).find()) {
            recordIssue("Inefficient iteration with range(len())", Severity.HIGH, "Performance");
            optimized = optimized.replaceAll(
                    "for\\s+(\\w+)\\s+in\\s+range\\s*\\(\\s*len\\s*\\(\\s*(\\w+)\\s*\\)\\s*\\)\\s*:",
                    "for $1, item in enumerate($2):  # PERF: 2-3x faster than range(len())");
            pythonIssueCount++;
        }

        // HIGH: List append in loops (consider list comprehension)
        if (optimized.contains(".append(") && optimized.contains("for ")) {
            recordIssue("List append in loop - O(n²) instead of comprehension O(n)", Severity.HIGH, "Performance");
            optimized = "# PERF: Replace with list comprehension (5-10x faster)\n" + optimized;
            pythonIssueCount++;
        }

        // MEDIUM: Mutable default arguments
        if (optimized.contains("def ") && (optimized.contains("=[]") || optimized.contains("={}") || optimized.contains("=set()"))) {
            recordIssue("Mutable default argument - shared across all function calls", Severity.CRITICAL, "Correctness");
            optimized = "# BUG: Mutable defaults shared! Use None and initialize in function body\n" + optimized;
            pythonIssueCount++;
        }

        // MEDIUM: Bare except clause
        if (optimized.contains("except:")) {
            recordIssue("Bare except clause catches SystemExit, KeyboardInterrupt", Severity.MEDIUM, "Correctness");
            optimized = optimized.replaceAll(
                    "except:\\s*",
                    "except Exception as e:  # Catch specific exceptions\n        ");
            pythonIssueCount++;
        }

        // MEDIUM: print() instead of logging
        if (optimized.contains("print(")) {
            recordIssue("print() used instead of logging module", Severity.MEDIUM, "Maintainability");
            optimized = "import logging\nlogger = logging.getLogger(__name__)\n" + optimized;
            pythonIssueCount++;
        }

        // MEDIUM: String concatenation in loops
        if (optimized.contains("+= \"") && optimized.contains("for ")) {
            recordIssue("String concatenation in loop - O(n²) complexity", Severity.MEDIUM, "Performance");
            optimized = "# PERF: Use ''.join(list) or StringIO instead of += in loops\n" + optimized;
            pythonIssueCount++;
        }

        // LOW: Missing type hints
        if (!optimized.contains(": ") && !optimized.contains("->")) {
            recordIssue("Missing type hints - reduced IDE support and documentation", Severity.LOW, "Maintainability");
            optimized = "# TIP: Add type hints (def func(x: int) -> str:) for better IDE support\n" + optimized;
        }

        complexityScore = Math.min(100, issues.size() * 15 + pythonIssueCount * 5);
        return optimized;
    }

    private String optimizeCpp(String code) {
        String optimized = code;
        int cppIssueCount = 0;

        // CRITICAL: Detect raw pointer usage without safety checks
        if (optimized.contains(" new ") && !optimized.contains("unique_ptr") && !optimized.contains("shared_ptr")) {
            recordIssue("Raw pointer with new/delete - memory leak and exception safety risks", Severity.CRITICAL, "Memory Safety");
            optimized = "// CRITICAL: Replace with unique_ptr/shared_ptr for automatic memory management\n" + optimized;
            cppIssueCount++;
        }

        // HIGH: Detect C-style casts (unsafe type conversion)
        if (CPP_CAST_PATTERN.matcher(optimized).find() && optimized.contains("(")) {
            recordIssue("C-style cast detected - use static_cast/dynamic_cast/reinterpret_cast", Severity.HIGH, "Type Safety");
            optimized = "// WARNING: Use C++ casts (static_cast, dynamic_cast) instead of C-style\n" + optimized;
            cppIssueCount++;
        }

        // HIGH: endl instead of \n
        if (CPP_ENDL_PATTERN.matcher(optimized).find()) {
            recordIssue("endl used - causes buffer flush (50-100x slower than \\n)", Severity.HIGH, "Performance");
            optimized = optimized.replace("endl", "'\\n'  // PERF: 50-100x faster (no flush)");
            cppIssueCount++;
        }

        // HIGH: Missing include guards
        if (!optimized.contains("#pragma once") && !optimized.contains("#ifndef")) {
            recordIssue("Missing include guard - risk of multiple inclusion", Severity.HIGH, "Correctness");
            optimized = "#pragma once\n// Include guard added\n\n" + optimized;
            cppIssueCount++;
        }

        // MEDIUM: Pass by value for large objects
        if (optimized.contains("void ") && (optimized.contains("std::string ") || optimized.contains("std::vector"))) {
            recordIssue("Large objects passed by value instead of const reference", Severity.MEDIUM, "Performance");
            optimized = "// PERF: Pass large objects by const reference: const std::string&\n" + optimized;
            cppIssueCount++;
        }

        // MEDIUM: Auto keyword not used (missed performance/clarity opportunity)
        if (CPP_AUTO_PATTERN.matcher(optimized).find() && optimized.contains("for")) {
            recordIssue("auto keyword found - verify it's used correctly with iterators", Severity.LOW, "Maintainability");
        }

        // MEDIUM: Rule of Five not followed (custom destructor without copy/move)
        if (optimized.contains("~") && !optimized.contains("delete")) {
            recordIssue("Custom destructor but possibly missing copy/move constructors", Severity.MEDIUM, "Memory Safety");
            optimized = "// RULE OF FIVE: If defining ~, also define copy/move constructor/assignment\n" + optimized;
            cppIssueCount++;
        }

        // MEDIUM: Make_unique/make_shared not used
        if ((optimized.contains("unique_ptr") || optimized.contains("shared_ptr")) 
                && !optimized.contains("make_unique") && !optimized.contains("make_shared")) {
            recordIssue("unique_ptr/shared_ptr not constructed with make_unique/make_shared", Severity.MEDIUM, "Reliability");
            optimized = "// TIP: Use std::make_unique/make_shared for exception safety\n" + optimized;
            cppIssueCount++;
        }

        // LOW: Old C++ library instead of std equivalent
        if (optimized.contains("#include <cstdio>") || optimized.contains("#include <iostream.h>")) {
            recordIssue("Old-style C headers used - prefer modern C++ equivalents", Severity.LOW, "Modernization");
        }

        complexityScore = Math.min(100, issues.size() * 15 + cppIssueCount * 5);
        return optimized;
    }
}

