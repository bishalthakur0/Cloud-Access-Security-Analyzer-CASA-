package com.skyhigh.casa.controller;

import com.skyhigh.casa.model.AuditLog;
import com.skyhigh.casa.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for audit log operations
 */
@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * Get all logs with pagination
     */
    @GetMapping
    public ResponseEntity<Page<AuditLog>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AuditLog> logs = auditLogService.getAllLogs(pageable);

        return ResponseEntity.ok(logs);
    }

    /**
     * Get logs by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<AuditLog>> getLogsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            AuditLog.UploadStatus uploadStatus = AuditLog.UploadStatus.valueOf(status.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
            Page<AuditLog> logs = auditLogService.getLogsByStatus(uploadStatus, pageable);
            return ResponseEntity.ok(logs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get logs by user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AuditLog>> getLogsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLog> logs = auditLogService.getLogsByUser(userId, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get logs by date range
     */
    @GetMapping("/daterange")
    public ResponseEntity<Page<AuditLog>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLog> logs = auditLogService.getLogsByDateRange(start, end, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get blocked uploads
     */
    @GetMapping("/blocked")
    public ResponseEntity<List<AuditLog>> getBlockedUploads() {
        List<AuditLog> logs = auditLogService.getBlockedUploads();
        return ResponseEntity.ok(logs);
    }

    /**
     * Get high risk uploads
     */
    @GetMapping("/highrisk")
    public ResponseEntity<List<AuditLog>> getHighRiskUploads(
            @RequestParam(defaultValue = "70") int threshold) {
        List<AuditLog> logs = auditLogService.getHighRiskUploads(threshold);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<AuditLogService.AuditStatistics> getStatistics() {
        AuditLogService.AuditStatistics stats = auditLogService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get recent logs
     */
    @GetMapping("/recent")
    public ResponseEntity<List<AuditLog>> getRecentLogs() {
        List<AuditLog> logs = auditLogService.getRecentLogs();
        return ResponseEntity.ok(logs);
    }
}
