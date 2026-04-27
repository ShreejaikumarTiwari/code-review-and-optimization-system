package com.shreejai.Codereviewer_and_optimization_System.controller;

import com.shreejai.Codereviewer_and_optimization_System.dto.AnalysisResponse;
import com.shreejai.Codereviewer_and_optimization_System.dto.CodeRequest;
import com.shreejai.Codereviewer_and_optimization_System.service.CodeAnalysisService;
import com.shreejai.Codereviewer_and_optimization_System.service.ReportExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/export")
public class ReportExportController {

    private final CodeAnalysisService analysisService;
    private final ReportExportService exportService;

    public ReportExportController(CodeAnalysisService analysisService,
                                   ReportExportService exportService) {
        this.analysisService = analysisService;
        this.exportService = exportService;
    }

    @PostMapping("/json")
    public ResponseEntity<String> exportJson(
            @RequestBody CodeRequest request) {

        AnalysisResponse result = analysisService.analyze(request);
        String json = exportService.toJson(result);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=analysis-report.json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }

    @PostMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestBody CodeRequest request) {

        AnalysisResponse result = analysisService.analyze(request);
        byte[] pdf = exportService.toPdf(result);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=analysis-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}