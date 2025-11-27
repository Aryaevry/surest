Surest Member Management

Overview

This project is a Spring Boot–based Member Management System that provides secured REST APIs for managing members. It uses PostgreSQL, Spring Data JPA, JWT authentication, role-based access control, caching, pagination, and adheres to a clean Git workflow.


Tech Stack

Java 17+

Spring Boot

Spring Data JPA

Spring Security + JWT

PostgreSQL

Gradle

Flyway (or SQL migration scripts)

JUnit 5 + Mockito

MapStruct (optional bonus)

SLF4J/Logback



Features
✔ Authentication

JWT-based login using /auth/login

Validates username & password

Generates JWT token for securing all APIs

✔ Member Management

CRUD operations for members

Pagination, sorting, and optional filtering

In-memory caching for GET /members/{id}

Cache eviction on update/delete


| Role      | Permissions          |
| --------- | -------------------- |
| **ADMIN** | Full CRUD on members |
| **USER**  | Read-only access     |



Testing

./gradlew test


Generate JaCoCo report:

./gradlew jacocoTestReport

 Run the application

 ./gradlew bootRun

Run the application using podman

./gradlew build  

podman build -t surest-app:latest -f Dockerfile .

podman play kube surest-pod.yaml


Swagger Ui
<img width="956" height="476" alt="image" src="https://github.com/user-attachments/assets/554aba80-7ddf-4f43-b8fa-94cf204c7153" />

postman collection result



