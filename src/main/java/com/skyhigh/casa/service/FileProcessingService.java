package com.skyhigh.casa.service;

import com.skyhigh.casa.model.*;
import com.skyhigh.casa.policy.PolicyEngine;
import com.skyhigh.casa.scanner.SensitiveDataScanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Main file processing service
 * Orchestrates scanning, policy evaluation, and storage
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessingService {

    private final SensitiveDataScanner scanner;
    private final PolicyEngine policyEngine;
    private final CloudStorageService cloudStorageService;
    private final AuditLogService auditLogService;

    @Value("${policy.quarantine-path}")
    private String quarantinePath;

    /**
     * Process uploaded file
     */
    public FileUploadResponse processFile(MultipartFile file, String userId, String ipAddress) {
        String fileId = UUID.randomUUID().toString();

        log.info("Processing file upload: {} (ID: {})", file.getOriginalFilename(), fileId);

        try {
            // Step 1: Scan file for sensitive data
            ScanResult scanResult = scanner.scanFile(file);
            log.info("Scan complete - Risk Score: {}, Sensitive Data: {}",
                    scanResult.getRiskScore(), scanResult.isContainsSensitiveData());

            // Step 2: Evaluate policy
            PolicyDecision policyDecision = policyEngine.evaluate(scanResult, userId, ipAddress);
            log.info("Policy decision: {} - {}", policyDecision.getAction(), policyDecision.getReason());

            // Step 3: Create audit log
            AuditLog auditLog = createAuditLog(fileId, file, scanResult, policyDecision, userId, ipAddress);

            // Step 4: Handle based on policy decision
            if (policyDecision.isAllowed()) {
                return handleAllowedUpload(file, fileId, auditLog);
            } else if (policyDecision.getAction() == AuditLog.PolicyAction.QUARANTINE) {
                return handleQuarantinedUpload(file, fileId, auditLog);
            } else {
                return handleBlockedUpload(auditLog);
            }

        } catch (IOException e) {
            log.error("Error processing file: {}", e.getMessage(), e);
            auditLogService.createLog(createErrorLog(fileId, file, userId, ipAddress, e.getMessage()));
            return FileUploadResponse.error(file.getOriginalFilename(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing file: {}", e.getMessage(), e);
            auditLogService.createLog(createErrorLog(fileId, file, userId, ipAddress, e.getMessage()));
            return FileUploadResponse.error(file.getOriginalFilename(), "Internal server error");
        }
    }

    /**
     * Handle allowed upload
     */
    private FileUploadResponse handleAllowedUpload(MultipartFile file, String fileId, AuditLog auditLog) {
        try {
            // Upload to cloud storage
            String storageLocation = cloudStorageService.uploadFile(file, fileId);
            auditLog.setStorageLocation(storageLocation);
            auditLog.setStatus(AuditLog.UploadStatus.ALLOWED);

            // Save audit log
            auditLogService.createLog(auditLog);

            log.info("File upload allowed and stored: {}", storageLocation);
            return FileUploadResponse.success(auditLog);

        } catch (Exception e) {
            log.error("Failed to upload file to cloud storage: {}", e.getMessage(), e);
            auditLog.setStatus(AuditLog.UploadStatus.ERROR);
            auditLog.setBlockReason("Cloud storage error: " + e.getMessage());
            auditLogService.createLog(auditLog);
            return FileUploadResponse.error(file.getOriginalFilename(), "Failed to upload to cloud storage");
        }
    }

    /**
     * Handle quarantined upload
     */
    private FileUploadResponse handleQuarantinedUpload(MultipartFile file, String fileId, AuditLog auditLog) {
        try {
            // Save to quarantine directory
            String quarantineLocation = saveToQuarantine(file, fileId);
            auditLog.setStorageLocation(quarantineLocation);
            auditLog.setStatus(AuditLog.UploadStatus.QUARANTINED);

            // Save audit log
            auditLogService.createLog(auditLog);

            log.warn("File quarantined: {}", quarantineLocation);
            return FileUploadResponse.blocked(auditLog);

        } catch (IOException e) {
            log.error("Failed to quarantine file: {}", e.getMessage(), e);
            auditLog.setStatus(AuditLog.UploadStatus.ERROR);
            auditLogService.createLog(auditLog);
            return FileUploadResponse.error(file.getOriginalFilename(), "Failed to quarantine file");
        }
    }

    /**
     * Handle blocked upload
     */
    private FileUploadResponse handleBlockedUpload(AuditLog auditLog) {
        auditLog.setStatus(AuditLog.UploadStatus.BLOCKED);
        auditLogService.createLog(auditLog);

        log.warn("File upload blocked: {}", auditLog.getBlockReason());
        return FileUploadResponse.blocked(auditLog);
    }

    /**
     * Save file to quarantine directory
     */
    private String saveToQuarantine(MultipartFile file, String fileId) throws IOException {
        // Create quarantine directory if it doesn't exist
        Path quarantineDir = Paths.get(quarantinePath);
        if (!Files.exists(quarantineDir)) {
            Files.createDirectories(quarantineDir);
        }

        // Save file
        String filename = fileId + "-" + file.getOriginalFilename();
        Path filePath = quarantineDir.resolve(filename);
        file.transferTo(filePath.toFile());

        return filePath.toString();
    }

    /**
     * Create audit log from scan and policy results
     */
    private AuditLog createAuditLog(String fileId, MultipartFile file, ScanResult scanResult,
            PolicyDecision policyDecision, String userId, String ipAddress) {
        return AuditLog.builder()
                .fileId(fileId)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .userId(userId != null ? userId : "anonymous")
                .ipAddress(ipAddress != null ? ipAddress : "unknown")
                .riskScore(scanResult.getRiskScore())
                .sensitiveDataFound(scanResult.isContainsSensitiveData())
                .sensitiveTypes(scanResult.getSensitiveTypes())
                .policyAction(policyDecision.getAction())
                .blockReason(policyDecision.getReason())
                .build();
    }

    /**
     * Create error audit log
     */
    private AuditLog createErrorLog(String fileId, MultipartFile file, String userId,
            String ipAddress, String errorMessage) {
        return AuditLog.builder()
                .fileId(fileId)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .userId(userId != null ? userId : "anonymous")
                .ipAddress(ipAddress != null ? ipAddress : "unknown")
                .status(AuditLog.UploadStatus.ERROR)
                .riskScore(0)
                .sensitiveDataFound(false)
                .policyAction(AuditLog.PolicyAction.BLOCK)
                .blockReason("Error: " + errorMessage)
                .build();
    }
}
