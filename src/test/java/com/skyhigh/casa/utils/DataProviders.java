package com.skyhigh.casa.utils;

import org.testng.annotations.DataProvider;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Providers for Data-Driven Testing
 * Reads test data from CSV files
 */
public class DataProviders {

    /**
     * Provides test data for sensitive file uploads
     * Format: fileName, expectedStatus, sensitiveTypes, riskScore
     */
    @DataProvider(name = "sensitiveFilesData")
    public static Object[][] getSensitiveFilesData() {
        return readCSV("src/test/resources/testdata/sensitive-files.csv");
    }

    /**
     * Provides test data for policy scenarios
     */
    @DataProvider(name = "policyData")
    public static Object[][] getPolicyData() {
        return new Object[][] {
                { "clean-document.txt", "ALLOWED", 0 },
                { "with-email.txt", "BLOCKED", 45 },
                { "with-credit-card.txt", "BLOCKED", 90 },
                { "with-aadhaar.txt", "BLOCKED", 85 }
        };
    }

    /**
     * Provides test data for file size limits
     */
    @DataProvider(name = "fileSizeData")
    public static Object[][] getFileSizeData() {
        return new Object[][] {
                { 1024, true }, // 1 KB - allowed
                { 1048576, true }, // 1 MB - allowed
                { 5242880, true }, // 5 MB - allowed (at limit)
                { 10485760, false } // 10 MB - blocked
        };
    }

    /**
     * Provides test data for user scenarios
     */
    @DataProvider(name = "userData")
    public static Object[][] getUserData() {
        return new Object[][] {
                { "user1", "192.168.1.100", true },
                { "user2", "192.168.1.101", true },
                { "", "192.168.1.102", true }, // Anonymous
                { null, "192.168.1.103", true } // No user ID
        };
    }

    /**
     * Read CSV file and return as Object[][]
     */
    private static Object[][] readCSV(String filePath) {
        List<Object[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] values = line.split(",");
                data.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data.toArray(new Object[0][]);
    }
}
