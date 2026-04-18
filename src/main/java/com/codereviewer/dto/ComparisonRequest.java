package com.codereviewer.dto;

public class ComparisonRequest {

    private String codeA;
    private String codeB;
    private String language;

    public ComparisonRequest() {}

    public String getCodeA() { return codeA; }
    public void setCodeA(String codeA) { this.codeA = codeA; }

    public String getCodeB() { return codeB; }
    public void setCodeB(String codeB) { this.codeB = codeB; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguageOrDefault() {
        if (language == null || language.isBlank()) return "java";
        return language.toLowerCase().trim();
    }
}