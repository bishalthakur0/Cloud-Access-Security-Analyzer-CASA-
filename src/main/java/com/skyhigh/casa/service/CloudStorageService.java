package com.skyhigh.casa.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

/**
 * Cloud Storage Service using MinIO
 * Falls back to local storage if MinIO is not available
 */
@Service
@Slf4j
public class CloudStorageService {

    @Autowired(required = false)
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${policy.quarantine-path:./storage}")
    private String localStoragePath;

    /**
     * Upload file to cloud storage or local storage
     */
    public String uploadFile(MultipartFile file, String fileId) throws Exception {
        if (minioClient != null) {
            return uploadToMinio(file, fileId);
        } else {
            return uploadToLocal(file, fileId);
        }
    }

    /**
     * Upload to MinIO
     */
    private String uploadToMinio(MultipartFile file, String fileId) throws Exception {
        try {
            String objectName = generateObjectName(file.getOriginalFilename(), fileId);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            String storageLocation = String.format("s3://%s/%s", bucketName, objectName);
            log.info("File uploaded to MinIO: {}", storageLocation);
            return storageLocation;

        } catch (Exception e) {
            log.error("Failed to upload file to MinIO: {}", e.getMessage(), e);
            throw new Exception("Failed to upload file to cloud storage", e);
        }
    }

    /**
     * Upload to local storage (fallback)
     */
    private String uploadToLocal(MultipartFile file, String fileId) throws Exception {
        try {
            Path storageDir = Paths.get(localStoragePath);
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }

            String filename = fileId + "-" + file.getOriginalFilename();
            Path filePath = storageDir.resolve(filename);
            file.transferTo(filePath.toFile());

            String storageLocation = "local://" + filePath.toString();
            log.info("File uploaded to local storage: {}", storageLocation);
            return storageLocation;

        } catch (Exception e) {
            log.error("Failed to upload file to local storage: {}", e.getMessage(), e);
            throw new Exception("Failed to upload file to local storage", e);
        }
    }

    /**
     * Download file from cloud storage
     */
    public InputStream downloadFile(String objectName) throws Exception {
        if (minioClient != null) {
            try {
                return minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build());
            } catch (Exception e) {
                log.error("Failed to download file from MinIO: {}", e.getMessage(), e);
                throw new Exception("Failed to download file from cloud storage", e);
            }
        } else {
            throw new Exception("MinIO not configured");
        }
    }

    /**
     * Delete file from cloud storage
     */
    public void deleteFile(String objectName) throws Exception {
        if (minioClient != null) {
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build());
                log.info("File deleted successfully: {}", objectName);
            } catch (Exception e) {
                log.error("Failed to delete file from MinIO: {}", e.getMessage(), e);
                throw new Exception("Failed to delete file from cloud storage", e);
            }
        }
    }

    /**
     * Generate object name with date-based path
     */
    private String generateObjectName(String originalFilename, String fileId) {
        LocalDate now = LocalDate.now();
        String datePath = String.format("%d/%02d", now.getYear(), now.getMonthValue());

        String sanitizedFilename = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");

        return String.format("%s/%s-%s", datePath, fileId, sanitizedFilename);
    }
}
