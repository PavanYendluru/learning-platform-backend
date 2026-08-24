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
   export ADMIN_BOOTSTRAP_ENABLED=true
   export ADMIN_BOOTSTRAP_NAME=Platform Administrator
   export ADMIN_BOOTSTRAP_EMAIL=admin@example.com
   export ADMIN_BOOTSTRAP_PASSWORD=your-admin-password
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
└── client/          → External service clients (for example CrossrefClient)
```

## YouTube resources

YouTube content is maintained as ordinary learning resources. Create or update a resource through the admin resource APIs and provide:

- `externalUrl`: the HTTPS YouTube video or playlist URL
- `youtubeContentType`: `VIDEO` or `PLAYLIST`

Each resource belongs to a topic, so a topic can have any number of manually stored YouTube links. Published resources are returned by the authenticated `/api/resources` endpoints. No YouTube API key or external YouTube API call is required.

## Authentication and authorization

- `POST /api/auth/register` creates a `USER` account.
- `POST /api/auth/login` returns a JWT containing the user role.
- All `/api/**` endpoints require a Bearer JWT except registration, login, and Swagger documentation.
- `/api/admin/**` requires the `ADMIN` role.
- Normal users can read published domains, topics, and resources only.
- Administrators can manage content and use `/api/admin/resources` to view all resource statuses.

The optional admin bootstrap creates the first administrator once. If the configured email already exists, it does not change that user’s role.

For frontend integration, send `Authorization: Bearer <accessToken>` with every authenticated request. A `USER` client should use the published read endpoints; an `ADMIN` client may use the `/api/admin/**` management endpoints.

## Postman API testing guide

Set this variable in a Postman environment:

```text
baseUrl = http://localhost:8080
accessToken = <copy the accessToken returned by login>
adminToken = <copy the accessToken returned by an ADMIN login>
domainId = <created domain id>
topicId = <created topic id>
resourceId = <created resource id>
roadmapId = <created roadmap id>
technologyUpdateId = <created technology update id>
```

For every authenticated request, add this header:

```text
Authorization: Bearer {{accessToken}}
```

For admin requests, use `{{adminToken}}` instead. Requests with JSON bodies also need:

```text
Content-Type: application/json
```

Registration and login are the first requests to run. Copy the returned `accessToken` into the appropriate Postman variable.

### Authentication

| Method | Endpoint | Access | Expected result |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Creates a USER account; `201 Created` |
| POST | `/api/auth/login` | Public | Returns a JWT; `200 OK` |

Register body:

```json
{
  "name": "Test User",
  "email": "user@example.com",
  "password": "password123"
}
```

Login body:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

### Domains

| Method | Endpoint | Access | Expected result |
|---|---|---|---|
| GET | `/api/domains` | USER or ADMIN | Lists active domains |
| GET | `/api/domains/{{domainId}}` | USER or ADMIN | Gets one active domain |
| GET | `/api/admin/domains` | ADMIN | Lists all domains and statuses |
| GET | `/api/admin/domains/{{domainId}}` | ADMIN | Gets a domain in any status |
| POST | `/api/admin/domains` | ADMIN | Creates a domain; `201 Created` |
| PUT | `/api/admin/domains/{{domainId}}` | ADMIN | Updates a domain |
| DELETE | `/api/admin/domains/{{domainId}}` | ADMIN | Deletes a domain |

Domain body:

```json
{
  "name": "Java",
  "description": "Java programming resources",
  "iconUrl": "https://example.com/java.png",
  "bannerUrl": "https://example.com/java-banner.png",
  "difficulty": "BEGINNER",
  "status": "ACTIVE"
}
```

### Topics

| Method | Endpoint | Access | Expected result |
|---|---|---|---|
| GET | `/api/domains/{{domainId}}/topics` | USER or ADMIN | Lists active topics in a domain |
| GET | `/api/admin/domains/{{domainId}}/topics` | ADMIN | Lists all topics in a domain |
| GET | `/api/topics/{{topicId}}` | USER or ADMIN | Gets one active topic |
| GET | `/api/admin/topics/{{topicId}}` | ADMIN | Gets a topic in any status |
| POST | `/api/admin/domains/{{domainId}}/topics` | ADMIN | Creates a topic; `201 Created` |
| PUT | `/api/admin/topics/{{topicId}}` | ADMIN | Updates a topic |
| DELETE | `/api/admin/topics/{{topicId}}` | ADMIN | Deletes a topic |

Topic body:

```json
{
  "name": "Spring Core",
  "description": "Core Spring concepts",
  "sequenceNumber": 1,
  "difficulty": "BEGINNER",
  "status": "ACTIVE"
}
```

### Learning resources and YouTube links

| Method | Endpoint | Access | Expected result |
|---|---|---|---|
| GET | `/api/resources?page=0&size=12&sortBy=createdAt&direction=desc` | USER or ADMIN | Searches published resources |
| GET | `/api/resources?topicId={{topicId}}&resourceType=VIDEO&difficulty=BEGINNER&q=spring` | USER or ADMIN | Filters published resources |
| GET | `/api/resources/{{resourceId}}` | USER or ADMIN | Gets one published resource |
| GET | `/api/admin/resources` | ADMIN | Lists resources in all statuses |
| GET | `/api/admin/resources?status=DRAFT` | ADMIN | Lists resources by status |
| GET | `/api/admin/resources/{{resourceId}}` | ADMIN | Gets a resource in any status |
| POST | `/api/admin/resources` | ADMIN | Creates a resource; `201 Created` |
| PUT | `/api/admin/resources/{{resourceId}}` | ADMIN | Updates a resource |
| DELETE | `/api/admin/resources/{{resourceId}}` | ADMIN | Deletes a resource |
| POST | `/api/admin/resources/{{resourceId}}/submit-for-review` | ADMIN | Moves DRAFT/REJECTED to PENDING_REVIEW |
| POST | `/api/admin/resources/{{resourceId}}/approve` | ADMIN | Moves PENDING_REVIEW to PUBLISHED |
| POST | `/api/admin/resources/{{resourceId}}/reject` | ADMIN | Moves PENDING_REVIEW to REJECTED |
| PUT | `/api/admin/resources/{{resourceId}}/verification` | ADMIN | Updates verification status |

Resource body:

```json
{
  "topicId": 1,
  "title": "Spring Boot Course",
  "description": "Spring Boot learning resource",
  "resourceType": "VIDEO",
  "externalUrl": "https://www.youtube.com/watch?v=VIDEO_ID",
  "youtubeContentType": "VIDEO",
  "sequenceNumber": 1,
  "thumbnailUrl": "https://img.youtube.com/vi/VIDEO_ID/0.jpg",
  "author": "Learning Platform",
  "sourceName": "YouTube",
  "difficulty": "BEGINNER",
  "durationMinutes": 60,
  "language": "en",
  "status": "DRAFT"
}
```

For a playlist, use a URL such as `https://www.youtube.com/playlist?list=PLAYLIST_ID` and set:

```json
"youtubeContentType": "PLAYLIST"
```

Verification body:

```json
{
  "verificationStatus": "ACTIVE"
}
```

Valid `ResourceStatus` values are `DRAFT`, `PENDING_REVIEW`, `PUBLISHED`, `REJECTED`, and `ARCHIVED`. Valid `ResourceType` values are `VIDEO`, `BOOK`, `DOCUMENTATION`, `RESEARCH_PAPER`, `ARTICLE`, `COURSE`, `GITHUB`, `PRACTICE`, `PROJECT`, and `TECHNOLOGY_UPDATE`.

### Roadmaps

| Method | Endpoint | Access | Expected result |
|---|---|---|---|
| GET | `/api/roadmaps` | USER or ADMIN | Lists published roadmaps |
| GET | `/api/roadmaps/{{roadmapId}}` | USER or ADMIN | Gets a roadmap |
| POST | `/api/admin/roadmaps` | ADMIN | Creates a roadmap; `201 Created` |
| PUT | `/api/admin/roadmaps/{{roadmapId}}` | ADMIN | Updates a roadmap |
| DELETE | `/api/admin/roadmaps/{{roadmapId}}` | ADMIN | Deletes a roadmap |

Roadmap body:

```json
{
  "title": "Spring Boot Roadmap",
  "description": "A structured Spring Boot path",
  "domainId": 1,
  "status": "DRAFT",
  "steps": [
    {
      "topicId": 1,
      "title": "Learn Spring Core",
      "description": "Understand dependency injection",
      "stepOrder": 1
    }
  ]
}
```

### Technology updates

| Method | Endpoint | Access | Expected result |
|---|---|---|---|
| GET | `/api/technology-updates` | USER or ADMIN | Lists published updates |
| GET | `/api/domains/{{domainId}}/technology-updates` | USER or ADMIN | Lists published updates for a domain |
| GET | `/api/technology-updates/{{technologyUpdateId}}` | USER or ADMIN | Gets an update |
| POST | `/api/admin/technology-updates` | ADMIN | Creates an update; `201 Created` |
| PUT | `/api/admin/technology-updates/{{technologyUpdateId}}` | ADMIN | Updates an update |
| DELETE | `/api/admin/technology-updates/{{technologyUpdateId}}` | ADMIN | Deletes an update |

Technology update body:

```json
{
  "domainId": 1,
  "title": "Spring Boot 3.4 Released",
  "summary": "Release summary",
  "content": "Detailed update content",
  "sourceName": "Spring Blog",
  "sourceUrl": "https://spring.io/blog",
  "publishedDate": "2026-08-24",
  "technologyVersion": "3.4.0",
  "updateType": "RELEASE",
  "status": "DRAFT"
}
```

### User bookmarks and progress

These endpoints are already implemented as user-specific functionality and require authentication.

| Method | Endpoint | Access | Expected result |
|---|---|---|---|
| POST | `/api/users/bookmarks/{{resourceId}}` | Authenticated user | Adds a bookmark; `201 Created` |
| GET | `/api/users/bookmarks` | Authenticated user | Lists the current user’s bookmarks |
| DELETE | `/api/users/bookmarks/{{resourceId}}` | Authenticated user | Removes a bookmark |
| POST | `/api/users/progress/{{topicId}}` | Authenticated user | Saves topic progress |
| PUT | `/api/users/progress/{{topicId}}` | Authenticated user | Updates topic progress |
| GET | `/api/users/progress` | Authenticated user | Lists the current user’s progress |

Progress body:

```json
{
  "completed": false,
  "progressPercentage": 25
}
```

### Admin dashboard

| Method | Endpoint | Access | Expected result |
|---|---|---|---|
| GET | `/api/admin/dashboard/stats` | ADMIN | Returns platform statistics |

### Research search

| Method | Endpoint | Access | Expected result |
|---|---|---|---|
| GET | `/api/research/search?q=machine%20learning&rows=10` | USER or ADMIN | Searches research papers through Crossref |

### Negative authorization tests

Verify these cases in Postman:

1. Call `GET /api/domains` without a token and expect `401 Unauthorized`.
2. Call `POST /api/admin/domains` with a USER token and expect `403 Forbidden`.
3. Call `GET /api/admin/resources` with a USER token and expect `403 Forbidden`.
4. Call `GET /api/resources/{{resourceId}}` for a DRAFT resource with a USER token and expect `404 Not Found`.
5. Call `GET /api/admin/resources/{{resourceId}}` with an ADMIN token and confirm the DRAFT resource is returned.
6. Submit a resource with an invalid YouTube URL and expect `400 Bad Request`.
7. Call `POST /api/auth/register` twice with the same email and expect `409 Conflict` on the second request.

Swagger documentation is available at `http://localhost:8080/swagger-ui.html`.
