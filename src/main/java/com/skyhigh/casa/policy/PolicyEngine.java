package com.skyhigh.casa.policy;

import com.skyhigh.casa.model.PolicyDecision;
import com.skyhigh.casa.model.ScanResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Zero Trust Policy Engine
 * Enforces security policies on file uploads
 * 
 * @author Bishal Thakur
 */
@Service
@Slf4j
public class PolicyEngine {

    @Value("${policy.max-file-size}")
    private long maxFileSize;

    @Value("${policy.block-sensitive-data}")
    private boolean blockSensitiveData;

    @Value("${policy.require-authentication}")
    private boolean requireAuthentication;

    @Value("${policy.allowed-file-types}")
    private String allowedFileTypes;

    /**
     * Evaluate policy for file upload
     */
    public PolicyDecision evaluate(ScanResult scanResult, String userId, String ipAddress) {
        log.info("Evaluating policy for file: {}", scanResult.getFileName());

        List<String> violatedRules = new ArrayList<>();

        // Rule 1: File size check
        if (scanResult.getFileSize() > maxFileSize) {
            violatedRules.add("FILE_SIZE_EXCEEDED");
            log.warn("File size {} exceeds maximum allowed size {}",
                    scanResult.getFileSize(), maxFileSize);
            return PolicyDecision.block(
                    String.format("File size (%d bytes) exceeds maximum allowed size (%d bytes)",
                            scanResult.getFileSize(), maxFileSize),
                    violatedRules);
        }

        // Rule 2: Sensitive data check
        if (blockSensitiveData && scanResult.isContainsSensitiveData()) {
            violatedRules.add("SENSITIVE_DATA_DETECTED");
            log.warn("Sensitive data detected in file: {}", scanResult.getSensitiveTypes());

            String reason = String.format("File contains sensitive data: %s",
                    String.join(", ", scanResult.getSensitiveTypes()));

            // High risk files are blocked, medium risk are quarantined
            if (scanResult.getRiskScore() >= 70) {
                return PolicyDecision.block(reason, violatedRules);
            } else if (scanResult.getRiskScore() >= 40) {
                return PolicyDecision.quarantine(reason);
            }
        }

        // Rule 3: File type check
        if (!isAllowedFileType(scanResult.getFileName())) {
            violatedRules.add("INVALID_FILE_TYPE");
            log.warn("File type not allowed: {}", scanResult.getFileName());
            return PolicyDecision.block(
                    "File type not allowed. Allowed types: " + allowedFileTypes,
                    violatedRules);
        }

        // Rule 4: Authentication check
        if (requireAuthentication && (userId == null || userId.isEmpty())) {
            violatedRules.add("AUTHENTICATION_REQUIRED");
            log.warn("Authentication required but user ID not provided");
            return PolicyDecision.block("Authentication required", violatedRules);
        }

        // Rule 5: IP whitelist check (optional)
        if (!isAllowedIpAddress(ipAddress)) {
            violatedRules.add("IP_NOT_WHITELISTED");
            log.warn("IP address not whitelisted: {}", ipAddress);
            return PolicyDecision.block("IP address not whitelisted", violatedRules);
        }

        // Rule 6: Risk score threshold
        if (scanResult.getRiskScore() > 90) {
            violatedRules.add("HIGH_RISK_SCORE");
            log.warn("Risk score {} exceeds threshold", scanResult.getRiskScore());
            return PolicyDecision.block(
                    String.format("Risk score (%d) too high", scanResult.getRiskScore()),
                    violatedRules);
        }

        // All checks passed
        log.info("Policy evaluation passed for file: {}", scanResult.getFileName());
        return PolicyDecision.allow();
    }

    /**
     * Check if file type is allowed
     */
    private boolean isAllowedFileType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return false;
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        String[] allowed = allowedFileTypes.split(",");

        for (String allowedType : allowed) {
            if (extension.equals(allowedType.trim().toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if IP address is whitelisted
     * For demo purposes, all IPs are allowed
     * In production, implement actual whitelist logic
     */
    private boolean isAllowedIpAddress(String ipAddress) {
        // For demo, allow all IPs
        // In production, check against whitelist
        return true;
    }

    /**
     * Admin override - allow upload despite policy violations
     */
    public PolicyDecision adminOverride(String reason, String adminId) {
        log.warn("Admin override applied by: {} - Reason: {}", adminId, reason);
        return PolicyDecision.builder()
                .allowed(true)
                .action(com.skyhigh.casa.model.AuditLog.PolicyAction.UPLOAD)
                .reason("Admin override: " + reason)
                .severity(0)
                .build();
    }

    /**
     * Get policy summary
     */
    public String getPolicySummary() {
        return String.format(
                "Policy Configuration:\n" +
                        "- Max File Size: %d bytes\n" +
                        "- Block Sensitive Data: %s\n" +
                        "- Require Authentication: %s\n" +
                        "- Allowed File Types: %s",
                maxFileSize, blockSensitiveData, requireAuthentication, allowedFileTypes);
    }
}
