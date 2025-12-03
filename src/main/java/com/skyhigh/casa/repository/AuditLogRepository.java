package com.skyhigh.casa.repository;

import com.skyhigh.casa.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Audit Log operations
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find logs by status
     */
    Page<AuditLog> findByStatus(AuditLog.UploadStatus status, Pageable pageable);

    /**
     * Find logs by user ID
     */
    Page<AuditLog> findByUserId(String userId, Pageable pageable);

    /**
     * Find logs by date range
     */
    Page<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * Find blocked uploads
     */
    @Query("SELECT a FROM AuditLog a WHERE a.status = 'BLOCKED' ORDER BY a.timestamp DESC")
    List<AuditLog> findBlockedUploads();

    /**
     * Find high risk uploads
     */
    @Query("SELECT a FROM AuditLog a WHERE a.riskScore >= :threshold ORDER BY a.riskScore DESC")
    List<AuditLog> findHighRiskUploads(@Param("threshold") int threshold);

    /**
     * Count uploads by status
     */
    long countByStatus(AuditLog.UploadStatus status);

    /**
     * Find recent logs
     */
    List<AuditLog> findTop10ByOrderByTimestampDesc();
}
