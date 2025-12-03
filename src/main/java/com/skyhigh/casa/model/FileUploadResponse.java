package com.skyhigh.casa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for file upload operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {

    private String status;
    private String fileId;
    private String fileName;
    private Integer riskScore;
    private Boolean sensitiveDataFound;
    private java.util.List<String> sensitiveTypes;
    private String policyAction;
    private String storageLocation;
    private String reason;
    private String timestamp;
    private String message;

    /**
     * Create success response
     */
    public static FileUploadResponse success(AuditLog auditLog) {
        return FileUploadResponse.builder()
                .status("ALLOWED")
                .fileId(auditLog.getFileId())
                .fileName(auditLog.getFileName())
                .riskScore(auditLog.getRiskScore())
                .sensitiveDataFound(auditLog.getSensitiveDataFound())
                .sensitiveTypes(auditLog.getSensitiveTypes())
                .policyAction(auditLog.getPolicyAction().name())
                .storageLocation(auditLog.getStorageLocation())
                .timestamp(auditLog.getTimestamp().toString())
                .message("File uploaded successfully")
                .build();
    }

    /**
     * Create blocked response
     */
    public static FileUploadResponse blocked(AuditLog auditLog) {
        return FileUploadResponse.builder()
                .status("BLOCKED")
                .fileId(auditLog.getFileId())
                .fileName(auditLog.getFileName())
                .riskScore(auditLog.getRiskScore())
                .sensitiveDataFound(auditLog.getSensitiveDataFound())
                .sensitiveTypes(auditLog.getSensitiveTypes())
                .policyAction(auditLog.getPolicyAction().name())
                .reason(auditLog.getBlockReason())
                .timestamp(auditLog.getTimestamp().toString())
                .message("File upload blocked by policy")
                .build();
    }

    /**
     * Create error response
     */
    public static FileUploadResponse error(String fileName, String reason) {
        return FileUploadResponse.builder()
                .status("ERROR")
                .fileName(fileName)
                .reason(reason)
                .timestamp(java.time.LocalDateTime.now().toString())
                .message("File upload failed")
                .build();
    }
}
