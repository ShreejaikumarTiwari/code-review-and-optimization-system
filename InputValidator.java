package com.codereviewer.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.codereviewer.dto.CodeRequest;

@Component
public class InputValidator {

    private static final int MAX_CODE_LENGTH = 50_000;
    private static final int MIN_CODE_LENGTH = 5;
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of(
            "java", "python", "cpp", "C++"
    );

    public List<String> validate(CodeRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("Request cannot be null.");
            return errors;
        }

        if (request.getCode() == null
                || request.getCode().isBlank()) {
            errors.add("Code cannot be empty.");
            return errors;
        }

        String code = request.getCode().trim();

        if (code.length() < MIN_CODE_LENGTH) {
            errors.add("Code is too short. Minimum "
                    + MIN_CODE_LENGTH
                    + " characters required.");
        }

        if (code.length() > MAX_CODE_LENGTH) {
            errors.add("Code is too large. Maximum "
                    + MAX_CODE_LENGTH
                    + " characters allowed.");
        }

        String lang = request.getLanguageOrDefault();
        if (!SUPPORTED_LANGUAGES.contains(lang)) {
            errors.add("Unsupported language '"
                    + lang
                    + "'. Supported: java, python, cpp.");
        }

        return errors;
    }

    public boolean isValid(CodeRequest request) {
        return validate(request).isEmpty();
    }
}