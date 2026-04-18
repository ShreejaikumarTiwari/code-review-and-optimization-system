package com.codereviewer.analyzer;

import com.codereviewer.dto.AnalysisResponse;

 interface Codeanalyzer

 {
     boolean supports(String criteria); 
    void analyze(String code, AnalysisResponse response);
}