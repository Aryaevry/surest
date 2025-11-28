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

few postman collection result for Admin Role

<img width="720" height="445" alt="image" src="https://github.com/user-attachments/assets/b6cd438c-d41f-446d-bb24-ef130f94fdc8" />
<img width="677" height="435" alt="image" src="https://github.com/user-attachments/assets/c73949c1-b221-4069-b95f-6942eb62c35b" />

<img width="673" height="430" alt="image" src="https://github.com/user-attachments/assets/83e39bde-a158-4fea-a795-27bea4147bf0" />
<img width="673" height="425" alt="image" src="https://github.com/user-attachments/assets/5c5dde03-abf3-4faf-a465-1b88df79bce6" />

few postman collection result for User Role
<img width="704" height="452" alt="image" src="https://github.com/user-attachments/assets/d96bc6b3-4c37-4bcc-a22d-2ba1b96c9bbc" />
<img width="672" height="426" alt="image" src="https://github.com/user-attachments/assets/b87685c7-af5c-403d-9dbe-29972c47b715" />
<img width="649" height="434" alt="image" src="https://github.com/user-attachments/assets/d3fe281d-3ee6-47bd-917e-d5d9608cc2bf" />









