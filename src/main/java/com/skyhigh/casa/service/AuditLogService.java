package com.skyhigh.casa.service;

import com.skyhigh.casa.model.AuditLog;
import com.skyhigh.casa.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing audit logs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Create audit log entry
     */
    @Transactional
    public AuditLog createLog(AuditLog auditLog) {
        log.info("Creating audit log for file: {}", auditLog.getFileName());
        return auditLogRepository.save(auditLog);
    }

    /**
     * Get all logs with pagination
     */
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    /**
     * Get logs by status
     */
    public Page<AuditLog> getLogsByStatus(AuditLog.UploadStatus status, Pageable pageable) {
        return auditLogRepository.findByStatus(status, pageable);
    }

    /**
     * Get logs by user
     */
    public Page<AuditLog> getLogsByUser(String userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable);
    }

    /**
     * Get logs by date range
     */
    public Page<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return auditLogRepository.findByTimestampBetween(start, end, pageable);
    }

    /**
     * Get blocked uploads
     */
    public List<AuditLog> getBlockedUploads() {
        return auditLogRepository.findBlockedUploads();
    }

    /**
     * Get high risk uploads
     */
    public List<AuditLog> getHighRiskUploads(int threshold) {
        return auditLogRepository.findHighRiskUploads(threshold);
    }

    /**
     * Get statistics
     */
    public AuditStatistics getStatistics() {
        long totalUploads = auditLogRepository.count();
        long allowedUploads = auditLogRepository.countByStatus(AuditLog.UploadStatus.ALLOWED);
        long blockedUploads = auditLogRepository.countByStatus(AuditLog.UploadStatus.BLOCKED);
        long quarantinedUploads = auditLogRepository.countByStatus(AuditLog.UploadStatus.QUARANTINED);

        return new AuditStatistics(totalUploads, allowedUploads, blockedUploads, quarantinedUploads);
    }

    /**
     * Get recent logs
     */
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop10ByOrderByTimestampDesc();
    }

    /**
     * Statistics DTO
     */
    public record AuditStatistics(
            long totalUploads,
            long allowedUploads,
            long blockedUploads,
            long quarantinedUploads) {
    }
}
