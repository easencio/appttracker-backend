---
inclusion: always
---

# Appointment Tracker — Project Context

## Project Overview
A full-stack appointment tracking application with a Java Spring Boot backend and an Angular frontend.

## Workspace Structure
```
appttracker/
├── backend/       # Spring Boot REST API
├── frontend/      # Angular application (to be scaffolded)
└── appttracker.code-workspace
```

## Backend (Spring Boot)

### Tech Stack
- Java 21
- Spring Boot 3.3.0
- Spring Web (REST API)
- Spring Boot DevTools (hot reload)
- Lombok
- H2 in-memory database
- Maven build tool

### Package / Group ID
- Group ID: `com.appttracker`
- Base package: `com.appttracker`

### Key Files
- `pom.xml` — Maven config with all dependencies
- `src/main/java/com/appttracker/AppttrackerApplication.java` — main entry point (`@SpringBootApplication`)
- `src/main/java/com/appttracker/TestController.java` — smoke-test REST controller
- `src/main/resources/application.properties` — app configuration

### Running the Backend
```bash
cd backend
mvn spring-boot:run
```
Server starts on `http://localhost:8080`

### Endpoints
| Method | Path       | Description                       |
|--------|------------|-----------------------------------|
| GET    | /api/hello | Returns "Hello from Spring Boot!" |

### H2 Console
Accessible at `http://localhost:8080/h2-console` during development.
- JDBC URL: `jdbc:h2:mem:appttracker`
- Username: `sa`
- Password: *(empty)*

### CORS
Backend is configured to allow requests from `http://localhost:4200` (Angular dev server).

## Frontend (Angular)
- Not yet scaffolded. Intended to run on `http://localhost:4200`.

## Notes
- Maven was not pre-installed on the system; install with `sudo apt install maven` if needed.
- The workspace is configured via `appttracker.code-workspace` to open both `backend` and `frontend`
  as a single multi-root workspace in Kiro.
