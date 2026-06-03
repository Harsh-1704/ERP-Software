# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build
mvn clean package

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Run single test
mvn test -Dtest=ClassName

# Run single test method
mvn test -Dtest=ClassName#methodName
```

## Architecture Overview

**Stack:** Spring Boot 4.0.2, Java 17, PostgreSQL, Maven

**Core modules:**
- `com.erp.system` - Main application package
- `com.erp.system.auth` - Authentication/authorization (users, roles, parties)
- `com.erp.system.config.security` - JWT authentication, security configuration
- `com.erp.system.common` - Shared utilities and base classes

**Key patterns:**
- Repository pattern for data access (Spring Data JPA)
- Service layer for business logic
- REST controllers under `/api/*`
- Flyway migrations in `src/main/resources/db/migration/`
- Lombok for boilerplate reduction (`@Getter`, `@Setter`, `@RequiredArgsConstructor`)

**Security:**
- JWT-based authentication via `JwtAuthenticationFilter`
- BCrypt password encoding
- Stateless session management
- All endpoints require authentication by default (configured in `SecurityConfig`)

**Database:**
- PostgreSQL on `localhost:5432/erp_db`
- Server runs on port `8090`
- Base audit fields (`createdAt`, `updatedAt`) via `BaseAuditEntity` mapped superclass

**API endpoints:**
- `/api/auth/login` - JWT token generation
- `/api/users/*` - User management
- `/api/roles/*` - Role management
- `/api/parties/*` - Party (customer/vendor) management
