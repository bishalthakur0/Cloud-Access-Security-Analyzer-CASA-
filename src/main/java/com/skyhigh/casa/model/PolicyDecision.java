package com.skyhigh.casa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Policy decision result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDecision {

    private boolean allowed;
    private String reason;
    private AuditLog.PolicyAction action;
    private java.util.List<String> violatedRules;
    private int severity; // 1-10

    /**
     * Create allow decision
     */
    public static PolicyDecision allow() {
        return PolicyDecision.builder()
                .allowed(true)
                .action(AuditLog.PolicyAction.UPLOAD)
                .reason("No policy violations detected")
                .severity(0)
                .build();
    }

    /**
     * Create block decision
     */
    public static PolicyDecision block(String reason, java.util.List<String> rules) {
        return PolicyDecision.builder()
                .allowed(false)
                .action(AuditLog.PolicyAction.BLOCK)
                .reason(reason)
                .violatedRules(rules)
                .severity(8)
                .build();
    }

    /**
     * Create quarantine decision
     */
    public static PolicyDecision quarantine(String reason) {
        return PolicyDecision.builder()
                .allowed(false)
                .action(AuditLog.PolicyAction.QUARANTINE)
                .reason(reason)
                .severity(6)
                .build();
    }
}
