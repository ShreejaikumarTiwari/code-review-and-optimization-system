package com.codereviewer.dto;

public class LineIssue {

    private final int line;
    private final String severity;
    private final String category;
    private final String message;

    public LineIssue(int line, String severity, String category, String message) {
        this.line = line;
        this.severity = severity;
        this.category = category;
        this.message = message;
    }

    public int getLine() { return line; }
    public String getSeverity() { return severity; }
    public String getCategory() { return category; }
    public String getMessage() { return message; }
}