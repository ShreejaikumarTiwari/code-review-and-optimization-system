package com.codereviewer.dto;

public class CodeResponse {

    private int lines;
    private int loops;
    private String suggestion;

    public CodeResponse(int lines, int loops, String suggestion) {
        this.lines = lines;
        this.loops = loops;
        this.suggestion = suggestion;
    }

    public int getLines() {
        return lines;
    }

    public int getLoops() {
        return loops;
    }

    public String getSuggestion() {
        return suggestion;
    }
}