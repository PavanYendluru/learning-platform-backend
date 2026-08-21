# Learning Platform Backend - Complete Project

This is the complete Spring Boot backend for the **Learning Resource & Technology Roadmap Platform**, built phase-by-phase.

## Tech Stack
- Java 21
- Spring Boot 3.3.4
- Spring Security + JWT
- Spring Data JPA + Hibernate
- MySQL
- Springdoc OpenAPI (Swagger)
- Maven

## How to Run

1. Create database:
   ```sql
   CREATE DATABASE learning_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. Set environment variables (optional):
   ```bash
   export DB_USERNAME=root
   export DB_PASSWORD=yourpassword
   export JWT_SECRET=your-long-random-secret-at-least-32-chars
   export YOUTUBE_API_KEY=your-youtube-api-key   # optional
   ```

3. Run:
   ```bash
   mvn spring-boot:run
   ```

4. Swagger UI: http://localhost:8080/swagger-ui.html

## Project Structure

```
com.learningplatform.backend
├── config/          → SecurityConfig, OpenApiConfig, CorsConfig
├── controller/      → All REST controllers
├── dto/             → Request / Response objects
├── entity/          → JPA entities
├── enums/           → All enums
├── exception/       → Custom exceptions + GlobalExceptionHandler
├── repository/      → Spring Data repositories
├── security/        → JwtService, JwtAuthenticationFilter
├── service/         → Business logic interfaces
│   └── impl/        → Implementations
└── client/          → YouTubeClient, CrossrefClient
```

## Important Notes

- All code from Phases 1A to 6 has been provided in the conversation.
- Copy the Java classes from the previous messages into the corresponding packages.
- The skeleton (pom.xml, application.properties, enums, exceptions, main class) is already in this ZIP.

## Next Steps for You

1. Copy all the service, controller, entity, dto, repository, security, and client classes from the chat history into this project.
2. Restart the application.
3. Test using Swagger or Postman.

If you want me to generate the remaining files one package at a time (entities → repositories → services → controllers), just tell me which package you want next.
