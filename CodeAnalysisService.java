package com.codereviewer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.codereviewer.analyzer.BigOPatternAnalyzer;
import com.codereviewer.analyzer.CacheFriendlinessAnalyzer;
import com.codereviewer.analyzer.CodeOptimizerEngine;
import com.codereviewer.analyzer.CodeSmellDetector;
import com.codereviewer.analyzer.ComplexityEstimator;
import com.codereviewer.analyzer.CyclomaticComplexityAnalyzer;
import com.codereviewer.analyzer.DuplicateCodeDetector;
import com.codereviewer.analyzer.EnergyAnalyzer;
import com.codereviewer.analyzer.LanguageAnalyzerRouter;
import com.codereviewer.analyzer.LineIssueReporter;
import com.codereviewer.analyzer.LoopAnalyzer;
import com.codereviewer.analyzer.MaintainabilityCalculator;
import com.codereviewer.analyzer.MemoryAnalyzer;
import com.codereviewer.analyzer.OptimizationSuggestionEngine;
import com.codereviewer.analyzer.RecursionAnalyzer;
import com.codereviewer.analyzer.SecurityVulnerabilityAnalyzer;
import com.codereviewer.analyzer.SpaceComplexityAnalyzer;
import com.codereviewer.dto.AnalysisResponse;
import com.codereviewer.dto.CodeRequest;
import com.codereviewer.dto.LineIssue;
import com.codereviewer.model.ScoreHistoryEntry;
import com.codereviewer.repository.ScoreHistoryRepository;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

@Service
public class CodeAnalysisService {

    private final ScoreHistoryRepository historyRepository;

    public CodeAnalysisService(
            ScoreHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public AnalysisResponse analyze(CodeRequest request) {

        String language = request.getLanguageOrDefault();
        String code = request.getCode();

        if (!language.equals("java")) {
            return analyzeNonJava(code, language);
        }

        return analyzeJava(code, language);
    }

    private AnalysisResponse analyzeJava(String code,
                                          String language) {

        CompilationUnit cu = StaticJavaParser.parse(code);

        LoopAnalyzer loopAnalyzer = new LoopAnalyzer();
        loopAnalyzer.visit(cu, null);
        int totalLoops = loopAnalyzer.getTotalLoops();
        int maxDepth = loopAnalyzer.getMaxDepth();

        RecursionAnalyzer recursionAnalyzer =
                new RecursionAnalyzer();
        List<String> recursionFindings =
                recursionAnalyzer.detect(cu);
        boolean hasRecursion =
                recursionAnalyzer.hasRecursion(cu);

        BigOPatternAnalyzer bigOAnalyzer =
                new BigOPatternAnalyzer();
        String bigOPattern = bigOAnalyzer.detect(
                cu, maxDepth, hasRecursion);

        ComplexityEstimator estimator =
                new ComplexityEstimator();
        String timeComplexity = estimator.estimate(maxDepth);
        int score = estimator.calculateScore(maxDepth);

        SpaceComplexityAnalyzer spaceAnalyzer =
                new SpaceComplexityAnalyzer();
        String spaceComplexity =
                spaceAnalyzer.analyze(cu, hasRecursion);

        CyclomaticComplexityAnalyzer cyclomaticAnalyzer =
                new CyclomaticComplexityAnalyzer();
        int cyclomatic = cyclomaticAnalyzer.calculate(cu);

        EnergyAnalyzer energyAnalyzer = new EnergyAnalyzer();
        double energy = energyAnalyzer.calculateEnergy(cu);

        MemoryAnalyzer memoryAnalyzer = new MemoryAnalyzer();
        double memoryBytes =
                memoryAnalyzer.calculateMemory(cu);
        String memoryUsage =
                memoryAnalyzer.formatMemory(memoryBytes);

        int linesOfCode = code.split("\n").length;
        MaintainabilityCalculator maintCalc =
                new MaintainabilityCalculator();
        int maintIndex = maintCalc.calculate(
                linesOfCode, cyclomatic, maxDepth);
        String grade = maintCalc.grade(maintIndex);

        CacheFriendlinessAnalyzer cacheAnalyzer =
                new CacheFriendlinessAnalyzer();
        String cacheFriendliness =
                cacheAnalyzer.analyze(cu);

        CodeSmellDetector smellDetector =
                new CodeSmellDetector();
        List<String> smells =
                smellDetector.detect(cu, maxDepth, cyclomatic);

        OptimizationSuggestionEngine suggestionEngine =
                new OptimizationSuggestionEngine();
        List<String> suggestions =
                suggestionEngine.suggest(
                        cu, maxDepth, cyclomatic, energy);

        SecurityVulnerabilityAnalyzer securityAnalyzer =
                new SecurityVulnerabilityAnalyzer();
        List<String> securityIssues =
                securityAnalyzer.analyze(cu);

        DuplicateCodeDetector duplicateDetector =
                new DuplicateCodeDetector();
        List<String> duplicates =
                duplicateDetector.detect(cu);

        LineIssueReporter lineReporter =
                new LineIssueReporter();
        List<LineIssue> lineIssues =
                lineReporter.report(cu);

        CodeOptimizerEngine optimizer =
                new CodeOptimizerEngine();
        String optimizedCode =
                optimizer.optimize(code, language);

        historyRepository.save(new ScoreHistoryEntry(
                language, score, cyclomatic,
                energy, timeComplexity, grade));

        return new AnalysisResponse(
                totalLoops, maxDepth, timeComplexity,
                spaceComplexity, bigOPattern, cyclomatic,
                energy, memoryUsage, maintIndex, grade,
                cacheFriendliness, language, optimizedCode,
                lineIssues, recursionFindings, smells,
                suggestions, securityIssues, duplicates, score
        );
    }

    private AnalysisResponse analyzeNonJava(String code,
                                             String language) {

        LanguageAnalyzerRouter router =
                new LanguageAnalyzerRouter();

        String timeComplexity =
                router.estimateComplexityByLanguage(
                        code, language);
        List<String> securityIssues =
                router.analyzeSecurityByLanguage(
                        code, language);
        List<LineIssue> lineIssues =
                router.analyzeLineIssuesByLanguage(
                        code, language);

        CodeOptimizerEngine optimizer =
                new CodeOptimizerEngine();
        String optimizedCode =
                optimizer.optimize(code, language);

        int score = timeComplexity.equals("O(1)") ? 95
                : timeComplexity.equals("O(n)") ? 80
                : timeComplexity.equals("O(n^2)") ? 60
                : 40;

        historyRepository.save(new ScoreHistoryEntry(
                language, score, 0, 0.0,
                timeComplexity, "N/A"));

        return new AnalysisResponse(
                0, 0, timeComplexity, "N/A",
                timeComplexity, 0, 0.0, "N/A",
                0, "N/A", "N/A", language,
                optimizedCode, lineIssues,
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), securityIssues,
                new ArrayList<>(), score
        );
    }
}