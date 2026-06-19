package com.shreejai.Codereviewer_and_optimization_System.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shreejai.Codereviewer_and_optimization_System.dto.CodeRequest;
import com.shreejai.Codereviewer_and_optimization_System.dto.CodeResponse;
import com.shreejai.Codereviewer_and_optimization_System.service.CodeReviewService;

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