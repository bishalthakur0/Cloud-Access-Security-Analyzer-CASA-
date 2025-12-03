package com.skyhigh.casa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * CASA - Cloud Access Security Analyzer
 * Main Application Entry Point
 * 
 * @author Bishal Thakur
 * @version 1.0
 */
@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties
public class CasaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasaApplication.class, args);
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   CASA - Cloud Access Security Analyzer                  ║");
        System.out.println("║   Application Started Successfully                        ║");
        System.out.println("║   Access UI: http://localhost:8080                        ║");
        System.out.println("║   API Docs: http://localhost:8080/actuator/health         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }
}
