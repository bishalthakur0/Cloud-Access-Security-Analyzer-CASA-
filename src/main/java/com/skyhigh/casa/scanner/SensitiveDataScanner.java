package com.skyhigh.casa.scanner;

import com.skyhigh.casa.model.ScanResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sensitive Data Scanner Engine
 * Scans files for sensitive information using regex patterns
 * 
 * @author Bishal Thakur
 */
@Service
@Slf4j
public class SensitiveDataScanner {

    private final Tika tika = new Tika();
    private final Map<String, ScanPattern> patterns = new HashMap<>();

    @Value("${scanner.patterns.email}")
    private String emailPattern;

    @Value("${scanner.patterns.credit-card}")
    private String creditCardPattern;

    @Value("${scanner.patterns.aadhaar}")
    private String aadhaarPattern;

    @Value("${scanner.patterns.aws-key}")
    private String awsKeyPattern;

    @Value("${scanner.patterns.password}")
    private String passwordPattern;

    /**
     * Initialize scanner patterns
     */
    public void initializePatterns() {
        patterns.put("EMAIL", new ScanPattern(emailPattern, 5, "Email Address"));
        patterns.put("CREDIT_CARD", new ScanPattern(creditCardPattern, 10, "Credit Card Number"));
        patterns.put("AADHAAR", new ScanPattern(aadhaarPattern, 9, "Aadhaar Number"));
        patterns.put("AWS_ACCESS_KEY", new ScanPattern(awsKeyPattern, 10, "AWS Access Key"));
        patterns.put("PASSWORD", new ScanPattern(passwordPattern, 8, "Password"));

        // Additional patterns
        patterns.put("SSN", new ScanPattern("\\b\\d{3}-\\d{2}-\\d{4}\\b", 10, "Social Security Number"));
        patterns.put("PHONE", new ScanPattern("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b", 3, "Phone Number"));
        patterns.put("IP_ADDRESS", new ScanPattern("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b", 4, "IP Address"));
        patterns.put("API_KEY", new ScanPattern(
                "(?i)(api[_-]?key|apikey)\\s*[:=]\\s*['\"]?([a-zA-Z0-9_\\-]{20,})['\"]?", 9, "API Key"));
        patterns.put("PRIVATE_KEY", new ScanPattern("-----BEGIN (RSA |EC )?PRIVATE KEY-----", 10, "Private Key"));

        log.info("Initialized {} sensitive data patterns", patterns.size());
    }

    /**
     * Scan file for sensitive data
     */
    public ScanResult scanFile(MultipartFile file) throws IOException {
        if (patterns.isEmpty()) {
            initializePatterns();
        }

        log.info("Scanning file: {} (size: {} bytes)", file.getOriginalFilename(), file.getSize());

        ScanResult result = ScanResult.builder()
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .build();

        try {
            // Extract text content using Apache Tika
            String content = tika.parseToString(file.getInputStream());
            log.debug("Extracted {} characters from file", content.length());

            // Scan for each pattern
            for (Map.Entry<String, ScanPattern> entry : patterns.entrySet()) {
                String patternType = entry.getKey();
                ScanPattern scanPattern = entry.getValue();

                Pattern pattern = Pattern.compile(scanPattern.getRegex());
                Matcher matcher = pattern.matcher(content);

                int matchCount = 0;
                while (matcher.find()) {
                    matchCount++;
                    String matchedValue = matcher.group();

                    // Mask sensitive value for logging
                    String maskedValue = maskSensitiveData(matchedValue);

                    ScanResult.SensitiveDataMatch match = ScanResult.SensitiveDataMatch.builder()
                            .type(patternType)
                            .pattern(scanPattern.getDescription())
                            .matchedValue(maskedValue)
                            .position(matcher.start())
                            .severity(scanPattern.getSeverity())
                            .build();

                    result.addMatch(match);

                    log.warn("Found {} at position {}: {}", patternType, matcher.start(), maskedValue);
                }

                if (matchCount > 0) {
                    log.info("Pattern {} matched {} times", patternType, matchCount);
                }
            }

            // Calculate overall risk score
            result.calculateRiskScore();

            log.info("Scan complete: {} sensitive data types found, risk score: {}",
                    result.getSensitiveTypes().size(), result.getRiskScore());

        } catch (Exception e) {
            log.error("Error scanning file: {}", e.getMessage(), e);
            throw new IOException("Failed to scan file: " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * Mask sensitive data for logging
     */
    private String maskSensitiveData(String value) {
        if (value == null || value.length() <= 4) {
            return "****";
        }

        int visibleChars = Math.min(4, value.length() / 4);
        String visible = value.substring(0, visibleChars);
        return visible + "****" + value.substring(value.length() - visibleChars);
    }

    /**
     * Validate credit card using Luhn algorithm
     */
    private boolean isValidCreditCard(String number) {
        String cleaned = number.replaceAll("[\\s-]", "");

        if (cleaned.length() < 13 || cleaned.length() > 19) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;

        for (int i = cleaned.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cleaned.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }

    /**
     * Inner class to hold pattern information
     */
    private static class ScanPattern {
        private final String regex;
        private final int severity;
        private final String description;

        public ScanPattern(String regex, int severity, String description) {
            this.regex = regex;
            this.severity = severity;
            this.description = description;
        }

        public String getRegex() {
            return regex;
        }

        public int getSeverity() {
            return severity;
        }

        public String getDescription() {
            return description;
        }
    }
}
