# 🛡️ CASA - Cloud Access Security Analyzer

### *A Lightweight Cloud Data Security & Automated Testing Platform*

**Author:** Bishal Thakur  
**Target Role:** Associate Software Engineer – Skyhigh Security  
**Tech Stack:** Java, Spring Boot, Docker, MinIO/S3, Selenium, TestNG, Rest Assured, GitHub Actions  
**Version:** 1.0

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Problem Statement](#-problem-statement)
- [Key Features](#-key-features)
- [System Architecture](#-system-architecture)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Project Structure](#-project-structure)
- [Roadmap](#-roadmap)
- [Why This Project](#-why-this-project)

---

## 🎯 Project Overview

CASA is a **mini CASB-like system** designed to **scan, classify, and secure files** before they are uploaded to cloud storage. The system prevents sensitive data leakage by applying **Zero Trust security policies**, performing **content inspection**, and generating **audit logs & alerts**.

This project demonstrates enterprise-grade security practices aligned with Skyhigh Security's CASB/SASE solutions, including:

✅ **Sensitive Data Detection** - Regex-based scanning for PII, credentials, tokens  
✅ **Zero Trust Policy Enforcement** - Block/allow based on content & context  
✅ **Cloud Storage Integration** - MinIO/S3 compatible storage  
✅ **Comprehensive Audit Logging** - Full compliance trail  
✅ **Test Automation Framework** - API + UI testing with CI/CD  

---

## 🔍 Problem Statement

Organizations frequently upload files to the cloud (S3, Azure Blob, GDrive) without visibility into:

- Whether the file contains **sensitive or regulated data**
- Whether the upload is allowed based on **Zero Trust rules**
- Whether logs and audit trails exist for compliance
- Whether existing systems are **automatically tested** for security regressions

**CASA solves these problems** with a lightweight, developer-friendly Zero Trust scanning engine + automated testing suite.

---

## ✨ Key Features

### 🔐 Security Features

- **Sensitive Data Scanner**
  - Email addresses
  - Credit card numbers (Visa, MasterCard, Amex)
  - Aadhaar numbers (Indian ID)
  - AWS Access Keys & Secret Tokens
  - Password patterns with entropy analysis
  
- **Zero Trust Policy Engine**
  - File size restrictions
  - Content-based blocking
  - IP whitelisting
  - JWT authentication
  - Admin policy override

- **Audit & Compliance**
  - Complete audit trail for all operations
  - Real-time alerts for blocked uploads
  - Dashboard for security analysts
  - Exportable compliance reports

### 🧪 Testing Features

- **API Test Automation** (RestAssured)
  - Upload validation tests
  - Policy enforcement tests
  - Boundary & negative tests
  - Authentication tests
  
- **UI Test Automation** (Selenium)
  - File upload workflows
  - Dashboard interactions
  - Alert rendering tests
  
- **Data-Driven Testing**
  - CSV/JSON test data
  - Parameterized test execution
  - TestNG data providers

- **Code Coverage**
  - JaCoCo integration
  - 85%+ coverage target
  - Automated reporting

---

## 🏗️ System Architecture

```
┌─────────────┐
│   User UI   │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────────┐
│     Spring Boot Backend                 │
│  ┌────────────────────────────────┐    │
│  │  File Upload Controller        │    │
│  └────────┬───────────────────────┘    │
│           ▼                             │
│  ┌────────────────────────────────┐    │
│  │  Sensitive Data Scanner        │    │
│  │  (Regex Pattern Matching)      │    │
│  └────────┬───────────────────────┘    │
│           ▼                             │
│  ┌────────────────────────────────┐    │
│  │  Zero Trust Policy Engine      │    │
│  └────────┬───────────────────────┘    │
│           ▼                             │
│     ┌─────┴─────┐                      │
│     │  Allowed? │                      │
│     └─────┬─────┘                      │
│      YES  │  NO                         │
│     ┌─────▼─────┐  ┌──────────────┐   │
│     │  MinIO/S3 │  │  Quarantine  │   │
│     └───────────┘  └──────────────┘   │
│           │              │              │
│           ▼              ▼              │
│  ┌────────────────────────────────┐    │
│  │  Audit Log Service (H2/MySQL)  │    │
│  └────────────────────────────────┘    │
└─────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────┐
│   Test Automation Framework             │
│  ┌──────────────┐  ┌──────────────┐    │
│  │ RestAssured  │  │   Selenium   │    │
│  │  API Tests   │  │   UI Tests   │    │
│  └──────────────┘  └──────────────┘    │
└─────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────┐
│   CI/CD Pipeline (GitHub Actions)       │
│   Build → Test → Coverage → Deploy     │
└─────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

### Backend
- **Java 17** - Core language
- **Spring Boot 3.x** - Application framework
- **Spring Data JPA** - Database access
- **Spring Security** - Authentication & authorization
- **Apache Tika** - File content extraction
- **H2/MySQL** - Database

### Storage
- **MinIO** - S3-compatible object storage
- **AWS S3 SDK** - Cloud storage integration

### Frontend
- **HTML5/CSS3** - Structure & styling
- **Bootstrap 5** - Responsive design
- **JavaScript (Fetch API)** - API communication

### Testing
- **JUnit 5** - Unit testing
- **RestAssured** - API testing
- **Selenium WebDriver** - UI testing
- **TestNG** - Test orchestration
- **JaCoCo** - Code coverage

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **GitHub Actions** - CI/CD pipeline
- **Maven** - Build automation

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker & Docker Compose
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/casa.git
   cd casa
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run with Docker Compose** (Recommended)
   ```bash
   docker-compose up -d
   ```
   
   This starts:
   - CASA application on `http://localhost:8080`
   - MinIO on `http://localhost:9000`
   - MinIO Console on `http://localhost:9001`

4. **Or run locally**
   ```bash
   mvn spring-boot:run
   ```

### Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database (H2 for development)
spring.datasource.url=jdbc:h2:mem:casadb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop

# MinIO Configuration
minio.endpoint=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
minio.bucket-name=casa-files

# File Upload Limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Security
jwt.secret=your-secret-key-here
jwt.expiration=86400000
```

---

## 📡 API Documentation

### 1. Upload File

**Endpoint:** `POST /api/v1/upload`

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/upload \
  -H "Authorization: Bearer <token>" \
  -F "file=@document.pdf" \
  -F "metadata={\"userId\":\"user123\"}"
```

**Response:**
```json
{
  "status": "ALLOWED",
  "fileId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "fileName": "document.pdf",
  "riskScore": 22,
  "sensitiveDataFound": false,
  "sensitiveTypes": [],
  "policyAction": "UPLOAD",
  "storageLocation": "s3://casa-files/2024/01/document.pdf",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Blocked Response:**
```json
{
  "status": "BLOCKED",
  "fileId": "f47ac10b-58cc-4372-a567-0e02b2c3d480",
  "fileName": "sensitive.txt",
  "riskScore": 95,
  "sensitiveDataFound": true,
  "sensitiveTypes": ["EMAIL", "CREDIT_CARD", "AWS_TOKEN"],
  "policyAction": "QUARANTINE",
  "reason": "File contains sensitive data: Credit Card, AWS Access Key",
  "timestamp": "2024-01-15T10:31:00Z"
}
```

### 2. Get Audit Logs

**Endpoint:** `GET /api/v1/logs`

**Request:**
```bash
curl -X GET "http://localhost:8080/api/v1/logs?page=0&size=20&status=BLOCKED" \
  -H "Authorization: Bearer <token>"
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "fileId": "f47ac10b-58cc-4372-a567-0e02b2c3d480",
      "fileName": "sensitive.txt",
      "userId": "user123",
      "status": "BLOCKED",
      "riskScore": 95,
      "sensitiveTypes": ["EMAIL", "CREDIT_CARD"],
      "timestamp": "2024-01-15T10:31:00Z",
      "ipAddress": "192.168.1.100"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0
}
```

### 3. Validate Policy

**Endpoint:** `POST /api/v1/policy/validate`

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/policy/validate \
  -H "Content-Type: application/json" \
  -d '{
    "fileSize": 5242880,
    "sensitiveDataFound": true,
    "sensitiveTypes": ["EMAIL"],
    "userId": "user123"
  }'
```

**Response:**
```json
{
  "allowed": false,
  "reason": "File contains sensitive data",
  "action": "BLOCK",
  "policyRules": ["SENSITIVE_DATA_BLOCK"]
}
```

### 4. Health Check

**Endpoint:** `GET /actuator/health`

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "minio": {"status": "UP"},
    "diskSpace": {"status": "UP"}
  }
}
```

---

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run API Tests Only

```bash
mvn test -Dtest=*ApiTest
```

### Run UI Tests Only

```bash
mvn test -Dtest=*UITest
```

### Generate Coverage Report

```bash
mvn verify
```

Coverage report available at: `target/site/jacoco/index.html`

### Test Structure

```
src/test/java/
├── api/
│   ├── FileUploadApiTest.java
│   ├── PolicyValidationApiTest.java
│   └── AuditLogApiTest.java
├── ui/
│   ├── FileUploadUITest.java
│   └── DashboardUITest.java
├── integration/
│   └── EndToEndTest.java
└── utils/
    ├── DataProviders.java
    └── TestDataGenerator.java

src/test/resources/
├── testdata/
│   ├── sensitive-files.csv
│   ├── policy-scenarios.json
│   └── sample-files/
│       ├── clean-document.txt
│       ├── with-email.txt
│       └── with-credit-card.txt
```

### Sample Test Data (CSV)

**testdata/sensitive-files.csv:**
```csv
fileName,expectedStatus,sensitiveTypes,riskScore
clean-document.txt,ALLOWED,[],0
with-email.txt,BLOCKED,EMAIL,45
with-credit-card.txt,BLOCKED,CREDIT_CARD,90
with-aadhaar.txt,BLOCKED,AADHAAR,85
```

---

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

The project includes automated CI/CD pipeline that:

1. ✅ Builds the application
2. ✅ Runs unit tests
3. ✅ Runs API tests
4. ✅ Starts Docker Compose (app + MinIO)
5. ✅ Runs Selenium UI tests
6. ✅ Generates JaCoCo coverage report
7. ✅ Uploads coverage artifacts
8. ✅ Builds Docker image
9. ✅ Pushes to container registry (optional)

**Workflow file:** `.github/workflows/ci-cd.yml`

### Manual Docker Build

```bash
# Build image
docker build -t casa:latest .

# Run container
docker run -p 8080:8080 \
  -e MINIO_ENDPOINT=http://minio:9000 \
  casa:latest
```

---

## 📁 Project Structure

```
casa/
├── src/
│   ├── main/
│   │   ├── java/com/skyhigh/casa/
│   │   │   ├── CasaApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── FileUploadController.java
│   │   │   │   ├── AuditLogController.java
│   │   │   │   └── PolicyController.java
│   │   │   ├── service/
│   │   │   │   ├── FileProcessingService.java
│   │   │   │   ├── AuditLogService.java
│   │   │   │   └── CloudStorageService.java
│   │   │   ├── scanner/
│   │   │   │   ├── SensitiveDataScanner.java
│   │   │   │   └── PatternDefinitions.java
│   │   │   ├── policy/
│   │   │   │   ├── PolicyEngine.java
│   │   │   │   └── PolicyRule.java
│   │   │   ├── model/
│   │   │   │   ├── FileUploadRequest.java
│   │   │   │   ├── ScanResult.java
│   │   │   │   └── AuditLog.java
│   │   │   ├── repository/
│   │   │   │   └── AuditLogRepository.java
│   │   │   ├── config/
│   │   │   │   ├── MinioConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   └── exception/
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       │   ├── index.html
│   │       │   ├── dashboard.html
│   │       │   ├── css/styles.css
│   │       │   └── js/app.js
│   │       └── templates/
│   └── test/
│       ├── java/com/skyhigh/casa/
│       │   ├── api/
│       │   ├── ui/
│       │   ├── integration/
│       │   └── utils/
│       └── resources/
│           └── testdata/
├── .github/
│   └── workflows/
│       └── ci-cd.yml
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

---

## 🗺️ Roadmap

### Version 1.0 (Current) ✅
- File upload API
- Sensitive data scanner
- Zero Trust policy engine
- Basic UI
- Audit logging
- MinIO integration
- Test automation framework

### Version 1.1 (Planned)
- AWS S3 integration
- Azure Blob Storage support
- Enhanced policy dashboard
- Role-based access control (RBAC)

### Version 1.2 (Future)
- Machine Learning-based classification
- OCR for image scanning
- Real-time threat intelligence integration
- Advanced analytics dashboard

### Version 2.0 (Vision)
- Multi-cloud support
- Kubernetes deployment
- API rate limiting & throttling
- Webhook notifications
- SaaS multi-tenancy

---

## 💡 Why This Project

This project demonstrates skills directly aligned with **Skyhigh Security** roles:

### Technical Alignment
✅ **Zero Trust Architecture** - Core to Skyhigh's SASE platform  
✅ **Cloud Data Protection** - CASB functionality  
✅ **Policy Enforcement** - DLP & security controls  
✅ **Test Automation** - Quality assurance practices  
✅ **Microservices** - Scalable architecture  

### Skills Demonstrated
- Backend development (Spring Boot, REST APIs)
- Security engineering (data scanning, policy enforcement)
- Test automation (RestAssured, Selenium, TestNG)
- DevOps (Docker, CI/CD, GitHub Actions)
- Data-driven testing
- Cloud storage integration
- Audit & compliance logging

### Interview Talking Points
1. **Zero Trust Implementation** - How policies are enforced
2. **Regex Pattern Matching** - Sensitive data detection algorithms
3. **Test Automation Strategy** - API + UI + data-driven approach
4. **Scalability** - Stateless design, horizontal scaling
5. **Security Best Practices** - JWT auth, input validation, audit trails

---

## 📞 Contact

**Bishal Thakur**  
📧 Email: your.email@example.com  
💼 LinkedIn: [linkedin.com/in/yourprofile](https://linkedin.com/in/yourprofile)  
🐙 GitHub: [github.com/yourusername](https://github.com/yourusername)

---

## 📄 License

This project is created for educational and portfolio purposes.

---

## 🙏 Acknowledgments

- Inspired by Skyhigh Security's CASB platform
- Built with industry best practices
- Designed for interview demonstration

---

**⭐ If you find this project helpful, please give it a star!**
