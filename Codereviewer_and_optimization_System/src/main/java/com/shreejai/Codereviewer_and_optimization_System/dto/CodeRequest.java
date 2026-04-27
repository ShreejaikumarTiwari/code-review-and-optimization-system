package com.shreejai.Codereviewer_and_optimization_System.dto;

public class CodeRequest {

    private String code;
    private String language;

    public CodeRequest() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getLanguageOrDefault() {
        if (language == null || language.isBlank()) return "java";
        
        if(language == "cpp" )
            return "c++";
        return language.toLowerCase().trim();
    }
}