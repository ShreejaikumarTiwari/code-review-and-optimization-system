import { useState } from "react";
import axios from "axios";
import {
  RadarChart, Radar, PolarGrid, PolarAngleAxis,
  PolarRadiusAxis, ResponsiveContainer, BarChart,
  Bar, XAxis, YAxis, Tooltip, CartesianGrid
} from "recharts";

const API = "https://code-review-and-optimization-system-production.up.railway.app/api/v1";

const COLORS = {
  A: "#27ae60", "A+": "#1e8449",
  B: "#2980b9", "B+": "#1a6fa8",
  C: "#f39c12", D: "#e67e22", E: "#e74c3c"
};

export default function App() {
  const [code, setCode] = useState("");
  const [language, setLanguage] = useState("java");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState("analysis");
  const [optimizeLevel, setOptimizeLevel] = useState(1);
  const [optimizedCode, setOptimizedCode] = useState(null);
  const [compareCodeA, setCompareCodeA] = useState("");
  const [compareCodeB, setCompareCodeB] = useState("");
  const [compareResult, setCompareResult] = useState(null);
  const [history, setHistory] = useState([]);

  const analyze = async () => {
    if (!code.trim()) {
      setError("Please enter some code.");
      return;
    }
    setLoading(true);
    setError(null);
    setResult(null);
    try {
      const res = await axios.post(`${API}/analyze`, {
        code, language
      });
      setResult(res.data);
    } catch (e) {
      setError(e.response?.data?.message || "Analysis failed.");
    } finally {
      setLoading(false);
    }
  };

  const optimize = async () => {
    if (!code.trim()) return;
    setLoading(true);
    try {
      const res = await axios.post(
        `${API}/analyze/optimize/${optimizeLevel}`,
        { code, language }
      );
      setOptimizedCode(res.data);
    } catch (e) {
      setError(e.response?.data?.message || "Optimization failed.");
    } finally {
      setLoading(false);
    }
  };

  const compare = async () => {
    if (!compareCodeA.trim() || !compareCodeB.trim()) {
      setError("Both code snippets are required.");
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const res = await axios.post(`${API}/compare`, {
        language,
        codeA: compareCodeA,
        codeB: compareCodeB
      });
      setCompareResult(res.data);
    } catch (e) {
      setError(e.response?.data?.message || "Comparison failed.");
    } finally {
      setLoading(false);
    }
  };

  const loadHistory = async () => {
    try {
      const res = await axios.get(`${API}/history`);
      setHistory(res.data);
    } catch (e) {
      setError("Failed to load history.");
    }
  };

  const exportJson = async () => {
    if (!code.trim()) return;
    const res = await axios.post(`${API}/export/json`,
      { code, language },
      { responseType: "blob" }
    );
    const url = window.URL.createObjectURL(
      new Blob([res.data]));
    const a = document.createElement("a");
    a.href = url;
    a.download = "analysis-report.json";
    a.click();
  };

  const exportHtml = async () => {
    if (!code.trim()) return;
    const res = await axios.post(`${API}/export/pdf`,
      { code, language },
      { responseType: "blob" }
    );
    const url = window.URL.createObjectURL(
      new Blob([res.data], { type: "text/html" }));
    window.open(url, "_blank");
  };

  const radarData = result ? [
    { metric: "Score", value: result.score },
    { metric: "Maintain.", value: result.maintainabilityIndex },
    {
      metric: "Security",
      value: Math.max(0, 100 - (
        result.securityIssues?.length || 0) * 15)
    },
    {
      metric: "Energy",
      value: Math.max(0, 100 - Math.min(
        result.energyConsumption, 100))
    },
    {
      metric: "Complexity",
      value: Math.max(0, 100 - (
        result.cyclomaticComplexity * 8))
    },
  ] : [];

  return (
    <div style={styles.app}>
      {/* Header */}
      <div style={styles.header}>
        <h1 style={styles.headerTitle}>
          Code Reviewer & Optimizer
        </h1>
        <p style={styles.headerSub}>
          AI-powered static code analysis
        </p>
      </div>

      {/* Tabs */}
      <div style={styles.tabs}>
        {["analysis", "optimize", "compare", "history"]
          .map(tab => (
            <button
              key={tab}
              style={{
                ...styles.tab,
                ...(activeTab === tab
                  ? styles.tabActive : {})
              }}
              onClick={() => {
                setActiveTab(tab);
                if (tab === "history") loadHistory();
              }}
            >
              {tab.charAt(0).toUpperCase() + tab.slice(1)}
            </button>
          ))}
      </div>

      <div style={styles.main}>

        {/* ANALYSIS TAB */}
        {activeTab === "analysis" && (
          <div>
            <div style={styles.inputRow}>
              <select
                value={language}
                onChange={e => setLanguage(e.target.value)}
                style={styles.select}
              >
                <option value="java">Java</option>
                <option value="python">Python</option>
                <option value="cpp">C++</option>
              </select>
              <button
                onClick={analyze}
                disabled={loading}
                style={styles.btnPrimary}
              >
                {loading ? "Analyzing..." : "Analyze Code"}
              </button>
              <button
                onClick={exportJson}
                style={styles.btnSecondary}
              >
                Export JSON
              </button>
              <button
                onClick={exportHtml}
                style={styles.btnSecondary}
              >
                Export Report
              </button>
            </div>

            <textarea
              value={code}
              onChange={e => setCode(e.target.value)}
              placeholder={
                language === "java"
                  ? "Paste your Java code here..."
                  : language === "python"
                  ? "Paste your Python code here..."
                  : "Paste your C++ code here..."
              }
              style={styles.textarea}
            />

            {error && (
              <div style={styles.errorBox}>{error}</div>
            )}

            {result && (
              <div>
                {/* Grade + Score */}
                <div style={styles.gradeRow}>
                  <div style={{
                    ...styles.gradeCard,
                    background: COLORS[result.grade] || "#888"
                  }}>
                    <div style={styles.gradeLabel}>Grade</div>
                    <div style={styles.gradeValue}>
                      {result.grade}
                    </div>
                  </div>
                  <div style={styles.scoreCard}>
                    <div style={styles.gradeLabel}>
                      Performance Score
                    </div>
                    <div style={styles.scoreValue}>
                      {result.score}
                      <span style={styles.scoreMax}>/100</span>
                    </div>
                  </div>
                  <div style={styles.scoreCard}>
                    <div style={styles.gradeLabel}>
                      Maintainability
                    </div>
                    <div style={styles.scoreValue}>
                      {result.maintainabilityIndex}
                      <span style={styles.scoreMax}>/100</span>
                    </div>
                  </div>
                  <div style={styles.scoreCard}>
                    <div style={styles.gradeLabel}>Language</div>
                    <div style={{
                      ...styles.scoreValue,
                      fontSize: 24
                    }}>
                      {result.language?.toUpperCase()}
                    </div>
                  </div>
                </div>

                {/* Metrics grid */}
                <div style={styles.metricsGrid}>
                  {[
                    {
                      label: "Time Complexity",
                      value: result.timeComplexity
                    },
                    {
                      label: "Space Complexity",
                      value: result.spaceComplexity
                    },
                    {
                      label: "Big-O Pattern",
                      value: result.bigOPattern
                    },
                    {
                      label: "Cyclomatic Complexity",
                      value: result.cyclomaticComplexity
                    },
                    {
                      label: "Energy",
                      value: result.energyConsumption + " nJ"
                    },
                    {
                      label: "Memory",
                      value: result.memoryUsage
                    },
                    {
                      label: "Total Loops",
                      value: result.totalLoops
                    },
                    {
                      label: "Max Depth",
                      value: result.maxDepth
                    },
                    {
                      label: "Cache",
                      value: result.cacheFriendliness
                    },
                  ].map((m, i) => (
                    <div key={i} style={styles.metricCard}>
                      <div style={styles.metricLabel}>
                        {m.label}
                      </div>
                      <div style={styles.metricValue}>
                        {m.value}
                      </div>
                    </div>
                  ))}
                </div>

                {/* Radar Chart */}
                <div style={styles.chartBox}>
                  <h3 style={styles.sectionTitle}>
                    Performance Radar
                  </h3>
                  <ResponsiveContainer width="100%" height={300}>
                    <RadarChart data={radarData}>
                      <PolarGrid />
                      <PolarAngleAxis dataKey="metric" />
                      <PolarRadiusAxis domain={[0, 100]} />
                      <Radar
                        dataKey="value"
                        stroke="#3498db"
                        fill="#3498db"
                        fillOpacity={0.4}
                      />
                    </RadarChart>
                  </ResponsiveContainer>
                </div>

                {/* Security Issues */}
                {result.securityIssues?.length > 0 && (
                  <div style={styles.section}>
                    <h3 style={{
                      ...styles.sectionTitle,
                      color: "#e74c3c"
                    }}>
                      Security Issues (
                      {result.securityIssues.length})
                    </h3>
                    {result.securityIssues.map((s, i) => (
                      <div key={i} style={styles.issueCard}>
                        {s}
                      </div>
                    ))}
                  </div>
                )}

                {/* Line Issues */}
                {result.lineIssues?.length > 0 && (
                  <div style={styles.section}>
                    <h3 style={styles.sectionTitle}>
                      Line Issues
                    </h3>
                    <table style={styles.table}>
                      <thead>
                        <tr>
                          <th style={styles.th}>Line</th>
                          <th style={styles.th}>Severity</th>
                          <th style={styles.th}>Category</th>
                          <th style={styles.th}>Message</th>
                        </tr>
                      </thead>
                      <tbody>
                        {result.lineIssues.map((issue, i) => (
                          <tr key={i}>
                            <td style={styles.td}>
                              {issue.line}
                            </td>
                            <td style={{
                              ...styles.td,
                              color: issue.severity === "CRITICAL"
                                ? "#e74c3c"
                                : issue.severity === "WARNING"
                                ? "#f39c12" : "#3498db",
                              fontWeight: "bold"
                            }}>
                              {issue.severity}
                            </td>
                            <td style={styles.td}>
                              {issue.category}
                            </td>
                            <td style={styles.td}>
                              {issue.message}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}

                {/* Suggestions */}
                {result.suggestions?.length > 0 && (
                  <div style={styles.section}>
                    <h3 style={{
                      ...styles.sectionTitle,
                      color: "#27ae60"
                    }}>
                      Optimization Suggestions
                    </h3>
                    {result.suggestions.map((s, i) => (
                      <div key={i} style={styles.suggCard}>
                        {s}
                      </div>
                    ))}
                  </div>
                )}

                {/* Recursion */}
                {result.recursionFindings?.length > 0 && (
                  <div style={styles.section}>
                    <h3 style={styles.sectionTitle}>
                      Recursion Findings
                    </h3>
                    {result.recursionFindings.map((r, i) => (
                      <div key={i} style={styles.infoCard}>
                        {r}
                      </div>
                    ))}
                  </div>
                )}

                {/* Duplicate Code */}
                {result.duplicateCodeFindings?.length > 0 && (
                  <div style={styles.section}>
                    <h3 style={styles.sectionTitle}>
                      Duplicate Code
                    </h3>
                    {result.duplicateCodeFindings.map((d, i) => (
                      <div key={i} style={styles.infoCard}>
                        {d}
                      </div>
                    ))}
                  </div>
                )}

                {/* Optimized Code */}
                {result.optimizedCode && (
                  <div style={styles.section}>
                    <h3 style={styles.sectionTitle}>
                      Auto-Optimized Code
                    </h3>
                    <pre style={styles.codeBlock}>
                      {result.optimizedCode}
                    </pre>
                  </div>
                )}
              </div>
            )}
          </div>
        )}

        {/* OPTIMIZE TAB */}
        {activeTab === "optimize" && (
          <div>
            <div style={styles.inputRow}>
              <select
                value={language}
                onChange={e => setLanguage(e.target.value)}
                style={styles.select}
              >
                <option value="java">Java</option>
                <option value="python">Python</option>
                <option value="cpp">C++</option>
              </select>
              <div style={styles.levelSelector}>
                <span style={styles.levelLabel}>
                  Optimization Level:
                </span>
                {[1, 2, 3, 4, 5].map(l => (
                  <button
                    key={l}
                    onClick={() => setOptimizeLevel(l)}
                    style={{
                      ...styles.levelBtn,
                      ...(optimizeLevel === l
                        ? styles.levelBtnActive : {})
                    }}
                  >
                    {l}
                  </button>
                ))}
              </div>
              <button
                onClick={optimize}
                disabled={loading}
                style={styles.btnPrimary}
              >
                {loading ? "Optimizing..." : "Optimize"}
              </button>
            </div>

            <div style={styles.levelDesc}>
              {optimizeLevel === 1 &&
                "Level 1 — Maximum: all security, performance and style fixes applied"}
              {optimizeLevel === 2 &&
                "Level 2 — Strong: security and performance fixes applied"}
              {optimizeLevel === 3 &&
                "Level 3 — Moderate: critical security fixes only"}
              {optimizeLevel === 4 &&
                "Level 4 — Minimal: improvement comments added only"}
              {optimizeLevel === 5 &&
                "Level 5 — Original: no changes made"}
            </div>

            <textarea
              value={code}
              onChange={e => setCode(e.target.value)}
              placeholder="Paste your code here to optimize..."
              style={styles.textarea}
            />

            {optimizedCode && (
              <div style={styles.section}>
                <h3 style={styles.sectionTitle}>
                  Level {optimizedCode.level} —{" "}
                  {optimizedCode.description}
                </h3>
                <pre style={styles.codeBlock}>
                  {optimizedCode.optimizedCode}
                </pre>
              </div>
            )}
          </div>
        )}

        {/* COMPARE TAB */}
        {activeTab === "compare" && (
          <div>
            <div style={styles.inputRow}>
              <select
                value={language}
                onChange={e => setLanguage(e.target.value)}
                style={styles.select}
              >
                <option value="java">Java</option>
                <option value="python">Python</option>
                <option value="cpp">C++</option>
              </select>
              <button
                onClick={compare}
                disabled={loading}
                style={styles.btnPrimary}
              >
                {loading ? "Comparing..." : "Compare"}
              </button>
            </div>

            <div style={styles.compareRow}>
              <div style={styles.compareCol}>
                <h3 style={styles.sectionTitle}>Code A</h3>
                <textarea
                  value={compareCodeA}
                  onChange={e => setCompareCodeA(e.target.value)}
                  placeholder="Paste Code A here..."
                  style={styles.compareTextarea}
                />
              </div>
              <div style={styles.compareCol}>
                <h3 style={styles.sectionTitle}>Code B</h3>
                <textarea
                  value={compareCodeB}
                  onChange={e => setCompareCodeB(e.target.value)}
                  placeholder="Paste Code B here..."
                  style={styles.compareTextarea}
                />
              </div>
            </div>

            {error && (
              <div style={styles.errorBox}>{error}</div>
            )}

            {compareResult && (
              <div>
                {/* Winner banner */}
                <div style={{
                  ...styles.winnerBanner,
                  background: compareResult.winner === "Code A"
                    ? "#2980b9"
                    : compareResult.winner === "Code B"
                    ? "#27ae60" : "#888"
                }}>
                  <div style={styles.winnerTitle}>
                    {compareResult.winner === "Tie"
                      ? "It's a Tie!"
                      : `Winner: ${compareResult.winner}`}
                  </div>
                  <div style={styles.winnerReason}>
                    {compareResult.reason}
                  </div>
                  <div style={styles.winnerDiff}>
                    Score difference: {compareResult.scoreDifference} points
                  </div>
                </div>

                {/* Side by side scores */}
                <div style={styles.compareScores}>
                  <div style={styles.compareScoreCard}>
                    <h3>Code A</h3>
                    <div style={{
                      ...styles.gradeValue,
                      color: COLORS[
                        compareResult.analysisA?.grade] || "#888"
                    }}>
                      {compareResult.analysisA?.grade}
                    </div>
                    <div style={styles.scoreValue}>
                      {compareResult.analysisA?.score}/100
                    </div>
                    <div style={styles.metricLabel}>
                      {compareResult.analysisA?.timeComplexity}
                    </div>
                    <div style={styles.metricLabel}>
                      Cyclomatic:{" "}
                      {compareResult.analysisA?.cyclomaticComplexity}
                    </div>
                    <div style={styles.metricLabel}>
                      Energy:{" "}
                      {compareResult.analysisA?.energyConsumption} nJ
                    </div>
                    <div style={styles.metricLabel}>
                      Security issues:{" "}
                      {compareResult.analysisA?.securityIssues?.length}
                    </div>
                  </div>

                  {/* Bar chart comparison */}
                  <div style={{ flex: 2 }}>
                    <ResponsiveContainer width="100%" height={250}>
                      <BarChart data={[
                        {
                          name: "Score",
                          A: compareResult.analysisA?.score,
                          B: compareResult.analysisB?.score
                        },
                        {
                          name: "Maintainability",
                          A: compareResult.analysisA
                            ?.maintainabilityIndex,
                          B: compareResult.analysisB
                            ?.maintainabilityIndex
                        },
                        {
                          name: "Cyclomatic",
                          A: compareResult.analysisA
                            ?.cyclomaticComplexity,
                          B: compareResult.analysisB
                            ?.cyclomaticComplexity
                        },
                      ]}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="name" />
                        <YAxis />
                        <Tooltip />
                        <Bar dataKey="A" fill="#3498db"
                          name="Code A" />
                        <Bar dataKey="B" fill="#27ae60"
                          name="Code B" />
                      </BarChart>
                    </ResponsiveContainer>
                  </div>

                  <div style={styles.compareScoreCard}>
                    <h3>Code B</h3>
                    <div style={{
                      ...styles.gradeValue,
                      color: COLORS[
                        compareResult.analysisB?.grade] || "#888"
                    }}>
                      {compareResult.analysisB?.grade}
                    </div>
                    <div style={styles.scoreValue}>
                      {compareResult.analysisB?.score}/100
                    </div>
                    <div style={styles.metricLabel}>
                      {compareResult.analysisB?.timeComplexity}
                    </div>
                    <div style={styles.metricLabel}>
                      Cyclomatic:{" "}
                      {compareResult.analysisB?.cyclomaticComplexity}
                    </div>
                    <div style={styles.metricLabel}>
                      Energy:{" "}
                      {compareResult.analysisB?.energyConsumption} nJ
                    </div>
                    <div style={styles.metricLabel}>
                      Security issues:{" "}
                      {compareResult.analysisB?.securityIssues?.length}
                    </div>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* HISTORY TAB */}
        {activeTab === "history" && (
          <div>
            <div style={styles.inputRow}>
              <h3 style={styles.sectionTitle}>
                Score Trend History
              </h3>
              <button
                onClick={loadHistory}
                style={styles.btnSecondary}
              >
                Refresh
              </button>
            </div>

            {history.length > 0 && (
              <div>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={history.slice().reverse()}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="language" />
                    <YAxis domain={[0, 100]} />
                    <Tooltip />
                    <Bar dataKey="score" fill="#3498db"
                      name="Score" />
                  </BarChart>
                </ResponsiveContainer>

                <table style={styles.table}>
                  <thead>
                    <tr>
                      <th style={styles.th}>Language</th>
                      <th style={styles.th}>Score</th>
                      <th style={styles.th}>Grade</th>
                      <th style={styles.th}>Complexity</th>
                      <th style={styles.th}>Cyclomatic</th>
                      <th style={styles.th}>Time</th>
                    </tr>
                  </thead>
                  <tbody>
                    {history.map((h, i) => (
                      <tr key={i}>
                        <td style={styles.td}>
                          {h.language?.toUpperCase()}
                        </td>
                        <td style={styles.td}>{h.score}</td>
                        <td style={{
                          ...styles.td,
                          color: COLORS[h.grade] || "#888",
                          fontWeight: "bold"
                        }}>
                          {h.grade}
                        </td>
                        <td style={styles.td}>
                          {h.timeComplexity}
                        </td>
                        <td style={styles.td}>
                          {h.cyclomaticComplexity}
                        </td>
                        <td style={styles.td}>
                          {new Date(h.analyzedAt)
                            .toLocaleTimeString()}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {history.length === 0 && (
              <div style={styles.emptyState}>
                No analysis history yet.
                Run some analyses first.
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

const styles = {
  app: {
    fontFamily: "'Segoe UI', sans-serif",
    background: "#f0f2f5",
    minHeight: "100vh"
  },
  header: {
    background: "linear-gradient(135deg, #1a1a2e, #16213e)",
    color: "white",
    padding: "24px 40px",
    boxShadow: "0 2px 8px rgba(0,0,0,0.3)"
  },
  headerTitle: {
    margin: 0,
    fontSize: 28,
    fontWeight: 700
  },
  headerSub: {
    margin: "4px 0 0",
    opacity: 0.7,
    fontSize: 14
  },
  tabs: {
    display: "flex",
    background: "#fff",
    borderBottom: "2px solid #e0e0e0",
    padding: "0 40px"
  },
  tab: {
    padding: "14px 24px",
    border: "none",
    background: "none",
    cursor: "pointer",
    fontSize: 15,
    color: "#666",
    borderBottom: "3px solid transparent",
    marginBottom: -2
  },
  tabActive: {
    color: "#3498db",
    borderBottom: "3px solid #3498db",
    fontWeight: 600
  },
  main: {
    padding: "24px 40px",
    maxWidth: 1200,
    margin: "0 auto"
  },
  inputRow: {
    display: "flex",
    gap: 12,
    alignItems: "center",
    marginBottom: 16,
    flexWrap: "wrap"
  },
  select: {
    padding: "10px 16px",
    borderRadius: 8,
    border: "1px solid #ddd",
    fontSize: 15,
    background: "#fff"
  },
  btnPrimary: {
    padding: "10px 24px",
    background: "#3498db",
    color: "white",
    border: "none",
    borderRadius: 8,
    fontSize: 15,
    cursor: "pointer",
    fontWeight: 600
  },
  btnSecondary: {
    padding: "10px 20px",
    background: "#fff",
    color: "#3498db",
    border: "2px solid #3498db",
    borderRadius: 8,
    fontSize: 15,
    cursor: "pointer",
    fontWeight: 600
  },
  textarea: {
    width: "100%",
    height: 220,
    padding: 16,
    fontFamily: "'Courier New', monospace",
    fontSize: 13,
    border: "1px solid #ddd",
    borderRadius: 8,
    resize: "vertical",
    background: "#1e1e1e",
    color: "#d4d4d4",
    boxSizing: "border-box"
  },
  errorBox: {
    background: "#ffeaea",
    border: "1px solid #e74c3c",
    color: "#c0392b",
    padding: 12,
    borderRadius: 8,
    marginTop: 12
  },
  gradeRow: {
    display: "flex",
    gap: 16,
    marginTop: 24,
    flexWrap: "wrap"
  },
  gradeCard: {
    padding: "20px 32px",
    borderRadius: 12,
    color: "white",
    textAlign: "center",
    minWidth: 100
  },
  scoreCard: {
    padding: "20px 32px",
    borderRadius: 12,
    background: "#fff",
    textAlign: "center",
    minWidth: 140,
    boxShadow: "0 2px 8px rgba(0,0,0,0.08)"
  },
  gradeLabel: {
    fontSize: 12,
    opacity: 0.8,
    marginBottom: 6,
    textTransform: "uppercase",
    letterSpacing: 1
  },
  gradeValue: {
    fontSize: 42,
    fontWeight: 800,
    lineHeight: 1
  },
  scoreValue: {
    fontSize: 32,
    fontWeight: 700,
    color: "#2c3e50"
  },
  scoreMax: {
    fontSize: 16,
    color: "#999"
  },
  metricsGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(200px, 1fr))",
    gap: 12,
    marginTop: 20
  },
  metricCard: {
    background: "#fff",
    padding: "16px 20px",
    borderRadius: 10,
    boxShadow: "0 2px 6px rgba(0,0,0,0.06)"
  },
  metricLabel: {
    fontSize: 12,
    color: "#999",
    marginBottom: 4,
    textTransform: "uppercase"
  },
  metricValue: {
    fontSize: 16,
    fontWeight: 600,
    color: "#2c3e50",
    wordBreak: "break-word"
  },
  chartBox: {
    background: "#fff",
    borderRadius: 12,
    padding: 20,
    marginTop: 20,
    boxShadow: "0 2px 8px rgba(0,0,0,0.06)"
  },
  section: {
    marginTop: 20
  },
  sectionTitle: {
    fontSize: 17,
    fontWeight: 600,
    color: "#2c3e50",
    marginBottom: 10
  },
  issueCard: {
    background: "#ffeaea",
    border: "1px solid #f5c6c6",
    borderLeft: "4px solid #e74c3c",
    padding: "10px 14px",
    borderRadius: 6,
    marginBottom: 8,
    fontSize: 14,
    color: "#c0392b"
  },
  suggCard: {
    background: "#eafaf1",
    border: "1px solid #a9dfbf",
    borderLeft: "4px solid #27ae60",
    padding: "10px 14px",
    borderRadius: 6,
    marginBottom: 8,
    fontSize: 14,
    color: "#1e8449"
  },
  infoCard: {
    background: "#eaf4fb",
    border: "1px solid #aed6f1",
    borderLeft: "4px solid #3498db",
    padding: "10px 14px",
    borderRadius: 6,
    marginBottom: 8,
    fontSize: 14,
    color: "#1a6fa8"
  },
  codeBlock: {
    background: "#1e1e1e",
    color: "#d4d4d4",
    padding: 20,
    borderRadius: 8,
    fontSize: 13,
    overflow: "auto",
    fontFamily: "'Courier New', monospace",
    lineHeight: 1.6
  },
  table: {
    width: "100%",
    borderCollapse: "collapse",
    background: "#fff",
    borderRadius: 8,
    overflow: "hidden",
    boxShadow: "0 2px 8px rgba(0,0,0,0.06)"
  },
  th: {
    background: "#3498db",
    color: "white",
    padding: "10px 14px",
    textAlign: "left",
    fontSize: 13
  },
  td: {
    padding: "10px 14px",
    borderBottom: "1px solid #eee",
    fontSize: 13,
    color: "#444"
  },
  levelSelector: {
    display: "flex",
    alignItems: "center",
    gap: 6
  },
  levelLabel: {
    fontSize: 14,
    color: "#666"
  },
  levelBtn: {
    width: 36,
    height: 36,
    borderRadius: "50%",
    border: "2px solid #ddd",
    background: "#fff",
    cursor: "pointer",
    fontWeight: 600,
    fontSize: 14
  },
  levelBtnActive: {
    background: "#3498db",
    color: "white",
    border: "2px solid #3498db"
  },
  levelDesc: {
    background: "#eaf4fb",
    padding: "10px 16px",
    borderRadius: 8,
    fontSize: 13,
    color: "#1a6fa8",
    marginBottom: 12
  },
  compareRow: {
    display: "flex",
    gap: 16,
    marginTop: 8
  },
  compareCol: {
    flex: 1
  },
  compareTextarea: {
    width: "100%",
    height: 200,
    padding: 14,
    fontFamily: "'Courier New', monospace",
    fontSize: 13,
    border: "1px solid #ddd",
    borderRadius: 8,
    resize: "vertical",
    background: "#1e1e1e",
    color: "#d4d4d4",
    boxSizing: "border-box"
  },
  winnerBanner: {
    padding: "24px 32px",
    borderRadius: 12,
    color: "white",
    marginTop: 20,
    textAlign: "center"
  },
  winnerTitle: {
    fontSize: 28,
    fontWeight: 800,
    marginBottom: 8
  },
  winnerReason: {
    fontSize: 15,
    opacity: 0.9,
    marginBottom: 6
  },
  winnerDiff: {
    fontSize: 13,
    opacity: 0.8
  },
  compareScores: {
    display: "flex",
    gap: 16,
    marginTop: 20,
    alignItems: "center"
  },
  compareScoreCard: {
    background: "#fff",
    padding: 20,
    borderRadius: 12,
    textAlign: "center",
    minWidth: 160,
    boxShadow: "0 2px 8px rgba(0,0,0,0.08)"
  },
  emptyState: {
    textAlign: "center",
    color: "#999",
    padding: 60,
    fontSize: 16
  }
};
