package com.skyhigh.casa.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for File Upload using RestAssured
 * 
 * @author Bishal Thakur
 */
public class FileUploadApiTest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api/v1";
    }

    @Test(priority = 1, description = "Test successful file upload with clean file")
    public void testUploadCleanFile() {
        File cleanFile = new File("src/test/resources/testdata/sample-files/clean-document.txt");

        given()
                .multiPart("file", cleanFile)
                .multiPart("userId", "testuser")
                .when()
                .post("/upload")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo("ALLOWED"))
                .body("sensitiveDataFound", equalTo(false))
                .body("riskScore", lessThan(40))
                .body("policyAction", equalTo("UPLOAD"));
    }

    @Test(priority = 2, description = "Test file upload with email address")
    public void testUploadFileWithEmail() {
        File emailFile = new File("src/test/resources/testdata/sample-files/with-email.txt");

        given()
                .multiPart("file", emailFile)
                .multiPart("userId", "testuser")
                .when()
                .post("/upload")
                .then()
                .statusCode(anyOf(is(200), is(403)))
                .contentType(ContentType.JSON)
                .body("sensitiveDataFound", equalTo(true))
                .body("sensitiveTypes", hasItem("EMAIL"));
    }

    @Test(priority = 3, description = "Test file upload with credit card")
    public void testUploadFileWithCreditCard() {
        File ccFile = new File("src/test/resources/testdata/sample-files/with-credit-card.txt");

        given()
                .multiPart("file", ccFile)
                .multiPart("userId", "testuser")
                .when()
                .post("/upload")
                .then()
                .statusCode(403)
                .contentType(ContentType.JSON)
                .body("status", equalTo("BLOCKED"))
                .body("sensitiveDataFound", equalTo(true))
                .body("sensitiveTypes", hasItem("CREDIT_CARD"))
                .body("riskScore", greaterThan(70));
    }

    @Test(priority = 4, description = "Test empty file upload")
    public void testUploadEmptyFile() {
        File emptyFile = new File("src/test/resources/testdata/sample-files/empty.txt");

        given()
                .multiPart("file", emptyFile)
                .multiPart("userId", "testuser")
                .when()
                .post("/upload")
                .then()
                .statusCode(anyOf(is(400), is(200)));
    }

    @Test(priority = 5, description = "Test large file upload")
    public void testUploadLargeFile() {
        // This test would need a large file
        // For now, we'll skip or create a mock
    }

    @Test(priority = 6, description = "Test health endpoint")
    public void testHealthEndpoint() {
        given()
                .when()
                .get("/health")
                .then()
                .statusCode(200)
                .body(containsString("CASA"));
    }

    @Test(priority = 7, description = "Test upload without user ID")
    public void testUploadWithoutUserId() {
        File cleanFile = new File("src/test/resources/testdata/sample-files/clean-document.txt");

        given()
                .multiPart("file", cleanFile)
                .when()
                .post("/upload")
                .then()
                .statusCode(anyOf(is(200), is(403)))
                .contentType(ContentType.JSON);
    }
}
