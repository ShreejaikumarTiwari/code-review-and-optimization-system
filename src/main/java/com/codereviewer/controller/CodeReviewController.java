package com.codereviewer.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codereviewer.dto.CodeRequest;
import com.codereviewer.dto.CodeResponse;
import com.codereviewer.service.CodeReviewService;

@RestController
@RequestMapping("/api/review")
public class CodeReviewController {

    private final CodeReviewService service;

    public CodeReviewController(CodeReviewService service) {
        this.service = service;
    }

    @PostMapping
    public CodeResponse review(@RequestBody CodeRequest request) {
        return service.review(request);
    }
}