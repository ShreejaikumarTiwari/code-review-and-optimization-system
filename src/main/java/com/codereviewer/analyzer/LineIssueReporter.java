package com.codereviewer.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.codereviewer.dto.LineIssue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

public class LineIssueReporter {

    private static final Set<String> SENSITIVE_NAMES = Set.of(
            "password", "passwd", "pwd", "secret", "apikey",
            "api_key", "token", "privatekey", "credentials"
    );

    public List<LineIssue> report(CompilationUnit cu) {

        List<LineIssue> issues = new ArrayList<>();

        reportNestedLoops(cu, issues);
        reportHardcodedSecrets(cu, issues);
        reportEmptyCatch(cu, issues);
        reportSystemExit(cu, issues);
        reportStringEquality(cu, issues);
        reportWeakRandom(cu, issues);
        reportDangerousExec(cu, issues);
        reportLongMethods(cu, issues);
        reportRecursion(cu, issues);
        reportSqlInjection(cu, issues);

        return issues;
    }

    private void reportNestedLoops(CompilationUnit cu, List<LineIssue> issues) {
        for (ForStmt outer : cu.findAll(ForStmt.class)) {
            if (!outer.findAll(ForStmt.class).isEmpty()
                    || !outer.findAll(WhileStmt.class).isEmpty()) {
                int line = outer.getBegin().map(p -> p.line).orElse(-1);
                issues.add(new LineIssue(line, "WARNING", "COMPLEXITY",
                        "Nested loop detected. Consider reducing nesting depth."));
            }
        }
    }

    private void reportHardcodedSecrets(CompilationUnit cu, List<LineIssue> issues) {
        for (VariableDeclarator var : cu.findAll(VariableDeclarator.class)) {
            String name = var.getNameAsString().toLowerCase();
            boolean sensitive = SENSITIVE_NAMES.stream().anyMatch(name::contains);
            if (sensitive) {
                var.getInitializer().ifPresent(init -> {
                    if (init instanceof StringLiteralExpr s && !s.asString().isEmpty()) {
                        int line = var.getBegin().map(p -> p.line).orElse(-1);
                        issues.add(new LineIssue(line, "CRITICAL", "SECURITY",
                                "Hardcoded secret in '" + var.getNameAsString()
                                        + "'. Use environment variables."));
                    }
                });
            }
        }
    }

    private void reportEmptyCatch(CompilationUnit cu, List<LineIssue> issues) {
        for (CatchClause c : cu.findAll(CatchClause.class)) {
            if (c.getBody().isEmpty()) {
                int line = c.getBegin().map(p -> p.line).orElse(-1);
                issues.add(new LineIssue(line, "WARNING", "RELIABILITY",
                        "Empty catch block silently swallows exceptions."));
            }
        }
    }

    private void reportSystemExit(CompilationUnit cu, List<LineIssue> issues) {
        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (call.getNameAsString().equals("exit")) {
                call.getScope().ifPresent(scope -> {
                    if (scope.toString().equals("System")) {
                        int line = call.getBegin().map(p -> p.line).orElse(-1);
                        issues.add(new LineIssue(line, "WARNING", "RELIABILITY",
                                "System.exit() should not be used in production code."));
                    }
                });
            }
        }
    }

    private void reportStringEquality(CompilationUnit cu, List<LineIssue> issues) {
        for (BinaryExpr expr : cu.findAll(BinaryExpr.class)) {
            if (expr.getOperator() == BinaryExpr.Operator.EQUALS
                    || expr.getOperator() == BinaryExpr.Operator.NOT_EQUALS) {
                if (expr.getLeft() instanceof StringLiteralExpr
                        || expr.getRight() instanceof StringLiteralExpr) {
                    int line = expr.getBegin().map(p -> p.line).orElse(-1);
                    issues.add(new LineIssue(line, "ERROR", "BUG",
                            "String compared with == instead of .equals()."));
                }
            }
        }
    }

    private void reportWeakRandom(CompilationUnit cu, List<LineIssue> issues) {
        for (ObjectCreationExpr obj : cu.findAll(ObjectCreationExpr.class)) {
            if (obj.getType().getNameAsString().equals("Random")) {
                int line = obj.getBegin().map(p -> p.line).orElse(-1);
                issues.add(new LineIssue(line, "WARNING", "SECURITY",
                        "Use SecureRandom instead of Random for security-sensitive operations."));
            }
        }
    }

    private void reportDangerousExec(CompilationUnit cu, List<LineIssue> issues) {
        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (call.getNameAsString().equals("exec")) {
                int line = call.getBegin().map(p -> p.line).orElse(-1);
                issues.add(new LineIssue(line, "CRITICAL", "SECURITY",
                        "exec() call detected. High risk of remote code execution."));
            }
        }
    }

    private void reportLongMethods(CompilationUnit cu, List<LineIssue> issues) {
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            long lines = method.getRange()
                    .map(r -> (long) (r.end.line - r.begin.line))
                    .orElse(0L);
            if (lines > 30) {
                int line = method.getBegin().map(p -> p.line).orElse(-1);
                issues.add(new LineIssue(line, "INFO", "MAINTAINABILITY",
                        "Method '" + method.getNameAsString()
                                + "' is too long (" + lines + " lines). Consider splitting."));
            }
        }
    }

    private void reportRecursion(CompilationUnit cu, List<LineIssue> issues) {
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            String name = method.getNameAsString();
            boolean callsSelf = method.findAll(MethodCallExpr.class)
                    .stream().anyMatch(c -> c.getNameAsString().equals(name));
            if (callsSelf) {
                int line = method.getBegin().map(p -> p.line).orElse(-1);
                issues.add(new LineIssue(line, "INFO", "COMPLEXITY",
                        "Recursive method '" + name
                                + "()'. Verify base case. Consider memoization."));
            }
        }
    }

    private void reportSqlInjection(CompilationUnit cu, List<LineIssue> issues) {
        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            String methodName = call.getNameAsString();
            if (Set.of("executeQuery", "executeUpdate", "execute").contains(methodName)) {
                boolean hasConcat = call.getArguments().stream()
                        .anyMatch(arg -> arg instanceof BinaryExpr b
                                && b.getOperator() == BinaryExpr.Operator.PLUS);
                if (hasConcat) {
                    int line = call.getBegin().map(p -> p.line).orElse(-1);
                    issues.add(new LineIssue(line, "CRITICAL", "SECURITY",
                            "SQL injection risk: string concatenation in query. Use PreparedStatement."));
                }
            }
        }
    }
}