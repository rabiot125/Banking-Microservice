# Banking-Microservice
🏦 **Banking Platform – Microservices Architecture**

This project implements a simplified microservices-based banking platform that enables customers to access card services. It consists of three microservices: **Customer Service**, **Account Service**, and **Card Service**, each handling specific domain responsibilities.

---

## 📦 Microservices Overview

### 1. 🔹 Customer Service
Manages customer bio data.

### 2. 🔸 Account Service
Handles customer account details.

### 3. 🟢 Card Service
Manages card information tied to accounts.

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **PostgreSQL** (Relational Database)
- **Maven** (Build tool)
- **JUnit 5 & Mockito** (Testing)
- **Spring Validation**
- **Docker** (Optional)

---

## 🚀 How to Run

### Prerequisites

- Java 17 or higher
- Maven 3 or higher
- PostgreSQL (Ensure the database server is running)
- IDE (IntelliJ, VS Code, etc.)

### Setup and Build

```bash
git clone https://github.com/rabiot125/Banking-Microservice.git
cd banking-platform

---
# create a database called bank-db or edit application.properties accordingly

cd customer-service
mvn spring-boot:run

# Swagger UI: http://localhost:8081/swagger-ui/index.html

cd ../account-service
mvn spring-boot:run

# Swagger UI: http://localhost:8090/swagger-ui/index.html

cd ../card-service
mvn spring-boot:run

# Swagger UI: http://localhost:8080/swagger-ui/index.html


###Using Docker Compose
# Make sure you are in the root of the project (banking-platform)

# Build and start the database and all microservices
docker-compose up --build

# This will start:
# - PostgreSQL database on port 5432
# - Customer Service on port 8081
# - Account Service on port 8090
# - Card Service on port 8080

# Access Swagger UIs at:
# http://localhost:8081/swagger-ui/index.html
# http://localhost:8090/swagger-ui/index.html
# http://localhost:8080/swagger-ui/index.html



 

