package com.codereviewer.service;

import com.codereviewer.dto.CodeRequest;
import com.codereviewer.dto.CodeResponse;
import org.springframework.stereotype.Service;

@Service
public class CodeReviewService {

    public CodeResponse review(CodeRequest request) {

        String code = request.getCode();

        int lines = countLines(code);
        int loops = countLoops(code);

        String suggestion;

        if (loops >= 3) {
            suggestion = "High loop nesting detected. Consider optimizing logic.";
        } else if (loops == 2) {
            suggestion = "Moderate complexity. Review nested loops.";
        } else {
            suggestion = "Code structure looks good.";
        }

        return new CodeResponse(lines, loops, suggestion);
    }

    private int countLines(String code) {
        return code.split("\n").length;
    }

    private int countLoops(String code) {
        int count = 0;
        if (code.contains("for(") || code.contains("for (")) count++;
        if (code.contains("while(") || code.contains("while (")) count++;
        return count;
    }
}