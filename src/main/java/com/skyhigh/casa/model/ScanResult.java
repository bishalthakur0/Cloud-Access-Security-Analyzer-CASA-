package com.skyhigh.casa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of sensitive data scanning
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanResult {

    private boolean containsSensitiveData;

    @Builder.Default
    private List<String> sensitiveTypes = new ArrayList<>();

    private int riskScore;

    @Builder.Default
    private List<SensitiveDataMatch> matches = new ArrayList<>();

    private String fileName;

    private long fileSize;

    private String contentType;

    /**
     * Represents a single match of sensitive data
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SensitiveDataMatch {
        private String type;
        private String pattern;
        private String matchedValue;
        private int position;
        private int severity; // 1-10
    }

    /**
     * Calculate overall risk score based on matches
     */
    public void calculateRiskScore() {
        if (matches.isEmpty()) {
            this.riskScore = 0;
            return;
        }

        int totalSeverity = matches.stream()
                .mapToInt(SensitiveDataMatch::getSeverity)
                .sum();

        int avgSeverity = totalSeverity / matches.size();
        int matchCount = matches.size();

        // Risk score formula: average severity * 10 + (match count * 5)
        this.riskScore = Math.min(100, (avgSeverity * 10) + (matchCount * 5));
    }

    /**
     * Add a sensitive data match
     */
    public void addMatch(SensitiveDataMatch match) {
        this.matches.add(match);
        if (!this.sensitiveTypes.contains(match.getType())) {
            this.sensitiveTypes.add(match.getType());
        }
        this.containsSensitiveData = true;
    }
}
