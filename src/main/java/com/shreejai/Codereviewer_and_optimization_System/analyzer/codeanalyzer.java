package com.shreejai.Codereviewer_and_optimization_System.analyzer;


import com.shreejai.Codereviewer_and_optimization_System.dto.AnalysisResponse;

 interface Codeanalyzer

 {
     boolean supports(String criteria); 
    void analyze(String code, AnalysisResponse response);
}