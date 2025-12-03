package com.skyhigh.casa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity class for Audit Logs
 * Stores all file upload attempts and their results
 */
@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UploadStatus status;

    @Column(nullable = false)
    private Integer riskScore;

    private Boolean sensitiveDataFound;

    @ElementCollection
    @CollectionTable(name = "sensitive_types", joinColumns = @JoinColumn(name = "audit_log_id"))
    @Column(name = "type")
    private List<String> sensitiveTypes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PolicyAction policyAction;

    private String storageLocation;

    private String blockReason;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 1000)
    private String metadata;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public enum UploadStatus {
        ALLOWED,
        BLOCKED,
        QUARANTINED,
        ERROR
    }

    public enum PolicyAction {
        UPLOAD,
        BLOCK,
        QUARANTINE,
        ALERT
    }
}
