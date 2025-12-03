package com.skyhigh.casa.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for Audit Log endpoints
 */
public class AuditLogApiTest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api/v1/logs";
    }

    @Test(priority = 1, description = "Test get all logs")
    public void testGetAllLogs() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("content", notNullValue())
                .body("totalElements", greaterThanOrEqualTo(0));
    }

    @Test(priority = 2, description = "Test get logs by status")
    public void testGetLogsByStatus() {
        given()
                .pathParam("status", "BLOCKED")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/status/{status}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test(priority = 3, description = "Test get statistics")
    public void testGetStatistics() {
        given()
                .when()
                .get("/statistics")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("totalUploads", greaterThanOrEqualTo(0))
                .body("allowedUploads", greaterThanOrEqualTo(0))
                .body("blockedUploads", greaterThanOrEqualTo(0));
    }

    @Test(priority = 4, description = "Test get recent logs")
    public void testGetRecentLogs() {
        given()
                .when()
                .get("/recent")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", lessThanOrEqualTo(10));
    }

    @Test(priority = 5, description = "Test get blocked uploads")
    public void testGetBlockedUploads() {
        given()
                .when()
                .get("/blocked")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test(priority = 6, description = "Test get high risk uploads")
    public void testGetHighRiskUploads() {
        given()
                .queryParam("threshold", 70)
                .when()
                .get("/highrisk")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test(priority = 7, description = "Test pagination")
    public void testPagination() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 5)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size", lessThanOrEqualTo(5))
                .body("number", equalTo(0));
    }
}
