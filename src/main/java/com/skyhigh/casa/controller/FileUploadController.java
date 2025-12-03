package com.skyhigh.casa.controller;

import com.skyhigh.casa.model.FileUploadResponse;
import com.skyhigh.casa.service.FileProcessingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for file upload operations
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileProcessingService fileProcessingService;

    /**
     * Upload file endpoint
     */
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", required = false) String userId,
            HttpServletRequest request) {

        log.info("Received file upload request: {}", file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(FileUploadResponse.error(file.getOriginalFilename(), "File is empty"));
        }

        // Get client IP address
        String ipAddress = getClientIpAddress(request);

        // Process file
        FileUploadResponse response = fileProcessingService.processFile(file, userId, ipAddress);

        // Return appropriate HTTP status
        if ("ALLOWED".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else if ("BLOCKED".equals(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("CASA is running");
    }

    /**
     * Get client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
