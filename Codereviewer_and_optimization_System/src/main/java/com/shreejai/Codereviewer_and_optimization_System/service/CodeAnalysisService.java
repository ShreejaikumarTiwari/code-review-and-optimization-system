package com.shreejai.Codereviewer_and_optimization_System.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shreejai.Codereviewer_and_optimization_System.analyzer.BigOPatternAnalyzer;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.CacheFriendlinessAnalyzer;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.CodeOptimizerEngine;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.CodeSmellDetector;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.ComplexityEstimator;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.CyclomaticComplexityAnalyzer;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.DuplicateCodeDetector;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.EnergyAnalyzer;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.LanguageAnalyzerRouter;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.LineIssueReporter;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.LoopAnalyzer;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.MaintainabilityCalculator;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.MemoryAnalyzer;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.OptimizationSuggestionEngine;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.RecursionAnalyzer;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.SecurityVulnerabilityAnalyzer;
import com.shreejai.Codereviewer_and_optimization_System.analyzer.SpaceComplexityAnalyzer;
import com.shreejai.Codereviewer_and_optimization_System.dto.AnalysisResponse;
import com.shreejai.Codereviewer_and_optimization_System.dto.CodeRequest;
import com.shreejai.Codereviewer_and_optimization_System.dto.LineIssue;
import com.shreejai.Codereviewer_and_optimization_System.model.ScoreHistoryEntry;
import com.shreejai.Codereviewer_and_optimization_System.repository.ScoreHistoryRepository;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

@Service
public class CodeAnalysisService {

    private final ScoreHistoryRepository historyRepository;

    // FIX 4: inject shared collaborators rather than instantiating them inline in
    // every call. This respects Spring's DI model, allows mocking in tests, and
    // eliminates duplicate object allocation on every request.
    private final LanguageAnalyzerRouter languageAnalyzerRouter;
    private final CodeOptimizerEngine codeOptimizerEngine;
    private final MaintainabilityCalculator maintainabilityCalculator;

    public CodeAnalysisService(
            ScoreHistoryRepository historyRepository,
            LanguageAnalyzerRouter languageAnalyzerRouter,
            CodeOptimizerEngine codeOptimizerEngine,
            MaintainabilityCalculator maintainabilityCalculator) {
        this.historyRepository = historyRepository;
        this.languageAnalyzerRouter = languageAnalyzerRouter;
        this.codeOptimizerEngine = codeOptimizerEngine;
        this.maintainabilityCalculator = maintainabilityCalculator;
    }

    public AnalysisResponse analyze(CodeRequest request) {
        // FIX 1 (service side): normalize language to lowercase here so both
        // analyzeJava and analyzeNonJava (and the router inside it) always receive
        // a consistent lowercase value. Previously "Python" or "Java" from the UI
        // would fall through every branch silently.
        String language = request.getLanguageOrDefault().toLowerCase();
        String code = request.getCode();

        if (!language.equals("java")) {
            return analyzeNonJava(code, language);
        }

        return analyzeJava(code, language);
    }

    private AnalysisResponse analyzeJava(String code, String language) {

        CompilationUnit cu = StaticJavaParser.parse(code);

        LoopAnalyzer loopAnalyzer = new LoopAnalyzer();
        loopAnalyzer.visit(cu, null);
        int totalLoops = loopAnalyzer.getTotalLoops();
        int maxDepth = loopAnalyzer.getMaxDepth();

        RecursionAnalyzer recursionAnalyzer = new RecursionAnalyzer();
        List<String> recursionFindings = recursionAnalyzer.detect(cu);
        boolean hasRecursion = recursionAnalyzer.hasRecursion(cu);

        BigOPatternAnalyzer bigOAnalyzer = new BigOPatternAnalyzer();
        String bigOPattern = bigOAnalyzer.detect(cu, maxDepth, hasRecursion);

        ComplexityEstimator estimator = new ComplexityEstimator();
        String timeComplexity = estimator.estimate(maxDepth);
        int score = estimator.calculateScore(maxDepth);

        SpaceComplexityAnalyzer spaceAnalyzer = new SpaceComplexityAnalyzer();
        String spaceComplexity = spaceAnalyzer.analyze(cu, hasRecursion);

        CyclomaticComplexityAnalyzer cyclomaticAnalyzer = new CyclomaticComplexityAnalyzer();
        int cyclomatic = cyclomaticAnalyzer.calculate(cu);

        EnergyAnalyzer energyAnalyzer = new EnergyAnalyzer();
        double energy = energyAnalyzer.calculateEnergy(cu);

        MemoryAnalyzer memoryAnalyzer = new MemoryAnalyzer();
        double memoryBytes = memoryAnalyzer.calculateMemory(cu);
        String memoryUsage = memoryAnalyzer.formatMemory(memoryBytes);

        int linesOfCode = code.split("\n").length;
        int maintIndex = maintainabilityCalculator.calculate(linesOfCode, cyclomatic, maxDepth);
        String grade = maintainabilityCalculator.grade(maintIndex);

        CacheFriendlinessAnalyzer cacheAnalyzer = new CacheFriendlinessAnalyzer();
        String cacheFriendliness = cacheAnalyzer.analyze(cu);

        CodeSmellDetector smellDetector = new CodeSmellDetector();
        List<String> smells = smellDetector.detect(cu, maxDepth, cyclomatic);

        OptimizationSuggestionEngine suggestionEngine = new OptimizationSuggestionEngine();
        List<String> suggestions = suggestionEngine.suggest(cu, maxDepth, cyclomatic, energy);

        SecurityVulnerabilityAnalyzer securityAnalyzer = new SecurityVulnerabilityAnalyzer();
        List<String> securityIssues = securityAnalyzer.analyze(cu);

        DuplicateCodeDetector duplicateDetector = new DuplicateCodeDetector();
        List<String> duplicates = duplicateDetector.detect(cu);

        LineIssueReporter lineReporter = new LineIssueReporter();
        List<LineIssue> lineIssues = lineReporter.report(cu);

        String optimizedCode = codeOptimizerEngine.optimize(code, language);

        historyRepository.save(new ScoreHistoryEntry(
                language, score, cyclomatic, energy, timeComplexity, grade));

        return new AnalysisResponse(
                totalLoops, maxDepth, timeComplexity,
                spaceComplexity, bigOPattern, cyclomatic,
                energy, memoryUsage, maintIndex, grade,
                cacheFriendliness, language, optimizedCode,
                lineIssues, recursionFindings, smells,
                suggestions, securityIssues, duplicates, score);
    }

    private AnalysisResponse analyzeNonJava(String code, String language) {

        // language is already lowercase — router normalization is now redundant but
        // harmless; LanguageAnalyzerRouter also lowercases internally as a safety net.
        String timeComplexity = languageAnalyzerRouter.estimateComplexityByLanguage(code, language);
        List<String> securityIssues = languageAnalyzerRouter.analyzeSecurityByLanguage(code, language);
        List<LineIssue> lineIssues = languageAnalyzerRouter.analyzeLineIssuesByLanguage(code, language);

        String optimizedCode = codeOptimizerEngine.optimize(code, language);

        // Calculate base score from complexity.
        int score = timeComplexity.equals("O(1)") ? 95
                : timeComplexity.equals("O(n)") ? 80
                : timeComplexity.equals("O(n^2)") ? 60
                : 40;

        // FIX 5: cap the security deduction so score cannot drop below 0 due to an
        // unbounded penalty. Also enforce Math.max(0, ...) as a hard floor.
        int securityPenalty = Math.min(securityIssues.size() * 8, score);
        score = Math.max(0, score - securityPenalty);

        int linesOfCode = code.split("\n").length;

        int cyclomatic = 1;
        for (String line : code.split("\n")) {
            String t = line.trim();
            if (t.startsWith("if ") || t.startsWith("elif ")
                    || t.startsWith("for ") || t.startsWith("while ")
                    || t.startsWith("except") || t.contains(" and ")
                    || t.contains(" or ")) {
                cyclomatic++;
            }
        }

        int maintIndex = maintainabilityCalculator.calculate(linesOfCode, cyclomatic, 0);
        String grade = maintainabilityCalculator.grade(maintIndex);

        int objectCount = 0;
        for (String line : code.split("\n")) {
            String t = line.trim();
            if (t.contains("= []") || t.contains("= {}") || t.contains("= ()")
                    || t.contains("DataFrame") || t.contains("append(")) {
                objectCount++;
            }
        }
        String memoryUsage = (objectCount * 64) + " Bytes";

        double energy = linesOfCode * 0.5 + cyclomatic * 2.0;

        // FIX 6: derive spaceComplexity from the actual time complexity estimate
        // rather than hardcoding "O(n) — interpreted language" unconditionally.
        // For interpreted languages we conservatively mirror time complexity.
        String spaceComplexity = timeComplexity.equals("O(1)")
                ? "O(1)" : timeComplexity + " (estimated)";

        historyRepository.save(new ScoreHistoryEntry(
                language, score, cyclomatic, energy, timeComplexity, grade));

        return new AnalysisResponse(
                0, 0, timeComplexity,
                spaceComplexity,
                timeComplexity, cyclomatic,
                energy, memoryUsage,
                maintIndex, grade,
                "N/A — no array access",
                language, optimizedCode,
                lineIssues, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(),
                securityIssues, new ArrayList<>(), score);
    }
}