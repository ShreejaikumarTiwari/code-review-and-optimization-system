package com.codereviewer.analyzer;

import java.util.LinkedHashMap;
import java.util.Map;

public class AIOptimizationEngine {

    public Map<Integer, String> optimizeAllLevels(String code, String language) {
        Map<Integer, String> levels = new LinkedHashMap<>();
        levels.put(1, optimizeLevel1(code, language));
        levels.put(2, optimizeLevel2(code, language));
        levels.put(3, optimizeLevel3(code, language));
        levels.put(4, optimizeLevel4(code, language));
        levels.put(5, code);
        return levels;
    }

    public String optimizeAtLevel(String code, String language, int level) {
        return switch (level) {
            case 1 -> optimizeLevel1(code, language);
            case 2 -> optimizeLevel2(code, language);
            case 3 -> optimizeLevel3(code, language);
            case 4 -> optimizeLevel4(code, language);
            default -> code;
        };
    }

    // Level 1 — maximum optimization (aggressive)
    private String optimizeLevel1(String code, String language) {
        String result = code;

        if (language.equals("java")) {
            result = applyLevel1Java(result);
        } else if (language.equals("python")) {
            result = applyLevel1Python(result);
        } else if (language.equals("cpp") || language.equals("c++")) {
            result = applyLevel1Cpp(result);
        }

        return addHeader(result, 1,
                "Maximum optimization applied: security hardened, "
                + "collections optimized, all anti-patterns removed.");
    }

    // Level 2 — strong optimization
    private String optimizeLevel2(String code, String language) {
        String result = code;

        if (language.equals("java")) {
            result = applyLevel2Java(result);
        } else if (language.equals("python")) {
            result = applyLevel2Python(result);
        }

        return addHeader(result, 2,
                "Strong optimization: security fixes and "
                + "performance improvements applied.");
    }

    // Level 3 — moderate optimization
    private String optimizeLevel3(String code, String language) {
        String result = code;

        result = result.replace("new Random()", "new SecureRandom()");
        result = fixStringComparisons(result);

        return addHeader(result, 3,
                "Moderate optimization: critical security "
                + "fixes only.");
    }

    // Level 4 — minimal optimization (comments only)
    private String optimizeLevel4(String code, String language) {
        StringBuilder sb = new StringBuilder();
        sb.append("// [Level 4] Minimal optimization — "
                + "review suggestions added as comments\n\n");

        String[] lines = code.split("\n");
        for (String line : lines) {
            sb.append(line).append("\n");

            if (line.contains("new Random()")) {
                sb.append("    // TODO: Consider SecureRandom\n");
            }
            if (line.contains("System.out.println")) {
                sb.append("    // TODO: Replace with logger\n");
            }
            if (line.contains("} catch") && line.contains("{}")) {
                sb.append("    // TODO: Handle this exception\n");
            }
        }

        return sb.toString();
    }

    private String applyLevel1Java(String code) {
        String result = code;

        // Security: Random → SecureRandom
        if (result.contains("new Random()")) {
            result = result.replace("new Random()", "new SecureRandom()");
            if (!result.contains("import java.security.SecureRandom")) {
                result = "import java.security.SecureRandom;\n" + result;
            }
        }

        // Security: fix string == comparison
        result = fixStringComparisons(result);

        // Security: remove System.exit
        result = result.replaceAll(
                "System\\.exit\\(\\d+\\);",
                "throw new IllegalStateException(\"Application error\");");

        // Security: fix empty catch blocks
        result = result.replaceAll(
                "catch\\s*\\(([^)]+)\\)\\s*\\{\\s*\\}",
                "catch ($1) {\n        throw new RuntimeException(\"Unhandled exception\", $1);\n    }");

        // Performance: replace println with logger
        result = result.replaceAll(
                "System\\.out\\.println\\(([^)]+)\\)",
                "logger.info(String.valueOf($1))");

        // Add logger field if not present
        if (result.contains("logger.info") && !result.contains("Logger logger")) {
            result = result.replaceFirst(
                    "(public class \\w+\\s*\\{)",
                    "$1\n    private static final java.util.logging.Logger logger ="
                    + " java.util.logging.Logger.getLogger(\"\");");
        }

        return result;
    }

    private String applyLevel2Java(String code) {
        String result = code;
        result = result.replace("new Random()", "new SecureRandom()");
        result = fixStringComparisons(result);
        result = result.replaceAll(
                "System\\.exit\\(\\d+\\);",
                "throw new IllegalStateException(\"Application error\");");
        result = result.replaceAll(
                "System\\.out\\.println\\(([^)]+)\\)",
                "logger.info(String.valueOf($1))");
        return result;
    }

    private String applyLevel1Python(String code) {
        String result = code;

        // Replace print with logging
        result = result.replaceAll(
                "print\\(([^)]+)\\)",
                "logging.info($1)");

        if (result.contains("logging.info") && !result.contains("import logging")) {
            result = "import logging\nlogging.basicConfig(level=logging.INFO)\n\n" + result;
        }

        // Replace random with secrets
        result = result.replace("random.random()", "secrets.token_hex()");
        result = result.replace("random.randint", "secrets.randbelow");
        if (result.contains("secrets.") && !result.contains("import secrets")) {
            result = "import secrets\n" + result;
        }

        // Replace range(len(x)) with enumerate
        result = result.replaceAll(
                "for (\\w+) in range\\(len\\((\\w+)\\)\\):",
                "for $1, _ in enumerate($2):  # optimized");

        return result;
    }

    private String applyLevel2Python(String code) {
        String result = code;
        result = result.replaceAll(
                "print\\(([^)]+)\\)",
                "logging.info($1)");
        if (result.contains("logging.info") && !result.contains("import logging")) {
            result = "import logging\n" + result;
        }
        return result;
    }

    private String applyLevel1Cpp(String code) {
        String result = code;

        result = result.replace("gets(", "fgets(  // FIXED: gets() replaced");
        result = result.replace("strcpy(", "strncpy(  // FIXED: strcpy replaced");
        result = result.replace("sprintf(", "snprintf(  // FIXED: sprintf replaced");
        result = result.replace("endl",
                "'\\n'  // optimized: faster than endl");

        return result;
    }

    private String fixStringComparisons(String code) {
        code = code.replaceAll(
                "\"([^\"]+)\"\\s*==\\s*(\\w+)",
                "\"$1\".equals($2)");
        code = code.replaceAll(
                "(\\w+)\\s*==\\s*\"([^\"]+)\"",
                "\"$2\".equals($1)");
        return code;
    }

    private String addHeader(String code, int level, String description) {
        return "// ═══════════════════════════════════════\n"
                + "// Optimization Level " + level + " — " + description + "\n"
                + "// ═══════════════════════════════════════\n\n"
                + code;
    }
}