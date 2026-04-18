package com.codereviewer.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.codereviewer.dto.LineIssue;
@Component
public class LanguageAnalyzerRouter {

    public List<String> analyzeSecurityByLanguage(String code, String language) {
        List<String> issues = new ArrayList<>();
        // FIX 1: normalize language to lowercase so callers passing "Python" or "C++"
        // are handled correctly by every branch in this class.
        String lang = language.toLowerCase();

        if (lang.equals("python")) {
            analyzePythonSecurity(code, issues);
        } else if (lang.equals("cpp") || lang.equals("c++")) {
            analyzeCppSecurity(code, issues);
        }

        return issues;
    }

    public List<LineIssue> analyzeLineIssuesByLanguage(String code, String language) {
        List<LineIssue> issues = new ArrayList<>();
        // FIX 1: normalize language to lowercase.
        String lang = language.toLowerCase();
        String[] lines = code.split("\n");

        if (lang.equals("python")) {
            analyzePythonLines(lines, issues);
        } else if (lang.equals("cpp") || lang.equals("c++")) {
            analyzeCppLines(lines, issues);
        }

        return issues;
    }

    public String estimateComplexityByLanguage(String code, String language) {
        int loopCount = 0;
        int maxDepth = 0;
        int currentDepth = 0;

        // FIX 1: normalize language to lowercase.
        String lang = language.toLowerCase();
        String[] lines = code.split("\n");

        for (String line : lines) {
            String trimmed = line.trim();

            boolean isLoop = false;

            if (lang.equals("python")) {
                isLoop = trimmed.startsWith("for ") || trimmed.startsWith("while ");
            } else if (lang.equals("cpp") || lang.equals("c++")) {
                isLoop = trimmed.startsWith("for(") || trimmed.startsWith("for (")
                        || trimmed.startsWith("while(") || trimmed.startsWith("while (");
            }

            if (isLoop) {
                loopCount++;
                currentDepth++;
                maxDepth = Math.max(maxDepth, currentDepth);
            }

            // FIX 2: the original check reset depth on any non-empty, non-indented line,
            // which incorrectly zeroed depth mid-block (e.g. on "return x" at top level).
            // Only reset when we see a top-level dedent: a line whose indentation level
            // is zero AND that is not itself a loop opener.
            if (lang.equals("python") && !trimmed.isEmpty() && currentDepth > 0) {
                int leadingSpaces = line.length() - line.stripLeading().length();
                if (leadingSpaces == 0 && !isLoop) {
                    currentDepth = 0;
                }
            }

            if ((lang.equals("cpp") || lang.equals("c++"))
                    && trimmed.equals("}")) {
                currentDepth = Math.max(0, currentDepth - 1);
            }
        }

        if (maxDepth == 0) return "O(1)";
        if (maxDepth == 1) return "O(n)";
        if (maxDepth == 2) return "O(n^2)";
        return "O(n^" + maxDepth + ")";
    }

    private void analyzePythonSecurity(String code, List<String> issues) {
        if (code.contains("eval(")) {
            issues.add("eval() detected — high risk of code injection in Python.");
        }
        if (code.contains("exec(")) {
            issues.add("exec() detected — avoid executing dynamic code strings.");
        }
        if (code.contains("password") && code.contains("=")
                && code.contains("\"")) {
            issues.add("Possible hardcoded password detected. Use os.environ instead.");
        }
        if (code.contains("pickle.load")) {
            issues.add("pickle.load() is unsafe with untrusted data — risk of arbitrary code execution.");
        }
        if (code.contains("subprocess.call") || code.contains("os.system")) {
            issues.add("Shell command execution detected. Validate all inputs carefully.");
        }
        if (code.contains("random.random()") || code.contains("random.randint")) {
            issues.add("Use secrets module instead of random for security-sensitive operations.");
        }
    }

    private void analyzeCppSecurity(String code, List<String> issues) {
        if (code.contains("gets(")) {
            issues.add("gets() is unsafe — causes buffer overflow. Use fgets() instead.");
        }
        if (code.contains("strcpy(")) {
            issues.add("strcpy() has no bounds checking — use strncpy() or std::string.");
        }
        if (code.contains("sprintf(")) {
            issues.add("sprintf() can overflow buffer — use snprintf() instead.");
        }
        if (code.contains("scanf(")) {
            issues.add("scanf() without width limit — potential buffer overflow.");
        }
        if (code.contains("malloc(") && !code.contains("free(")) {
            issues.add("malloc() used without free() — potential memory leak.");
        }
        if (code.contains("new ") && !code.contains("delete ")) {
            issues.add("new used without delete — potential memory leak.");
        }
        if (code.contains("system(")) {
            issues.add("system() call detected — risk of command injection.");
        }
    }

    private void analyzePythonLines(String[] lines, List<LineIssue> issues) {
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            int lineNum = i + 1;

            // FIX 3: use contains() instead of startsWith() for consistency with
            // analyzePythonSecurity() and to catch mid-line eval() calls.
            if (line.contains("eval(")) {
                issues.add(new LineIssue(lineNum, "CRITICAL", "SECURITY",
                        "eval() is dangerous — code injection risk."));
            }
            if (line.contains("except:") || line.contains("except Exception:")) {
                issues.add(new LineIssue(lineNum, "WARNING", "RELIABILITY",
                        "Bare except catches everything. Specify exception type."));
            }
            if (line.startsWith("for ") || line.startsWith("while ")) {
                issues.add(new LineIssue(lineNum, "INFO", "COMPLEXITY",
                        "Loop detected at line " + lineNum + "."));
            }
            if (line.contains("print(")) {
                issues.add(new LineIssue(lineNum, "INFO", "MAINTAINABILITY",
                        "Consider using logging instead of print()."));
            }
        }
    }

    private void analyzeCppLines(String[] lines, List<LineIssue> issues) {
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            int lineNum = i + 1;

            if (line.contains("gets(")) {
                issues.add(new LineIssue(lineNum, "CRITICAL", "SECURITY",
                        "gets() causes buffer overflow. Use fgets()."));
            }
            if (line.contains("strcpy(")) {
                issues.add(new LineIssue(lineNum, "CRITICAL", "SECURITY",
                        "strcpy() has no bounds check. Use strncpy()."));
            }
            if (line.contains("malloc(") && !line.contains("free(")) {
                issues.add(new LineIssue(lineNum, "WARNING", "MEMORY",
                        "Verify malloc() has corresponding free() to avoid memory leak."));
            }
            if (line.startsWith("for(") || line.startsWith("for (")) {
                issues.add(new LineIssue(lineNum, "INFO", "COMPLEXITY",
                        "Loop detected at line " + lineNum + "."));
            }
        }
    }
}