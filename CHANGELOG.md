# Changelog
All notable changes to this project will be documented in this file.

The format is based on *Keep a Changelog* and this project adheres to *Semantic Versioning*.

## [1.4.0] - 2026-01-08
### Added
- Public REST endpoints (v1): `/api/v1/personal` and `/api/v1/contact`.
- REST DTOs + MapStruct mapper for API responses.
- OpenAPI/Swagger via springdoc (Swagger UI at `/api/v1/swagger-ui`, OpenAPI at `/api/v1/openapi`).
- Global exception handler returning ProblemDetail payloads.
- Integration tests for controllers using MockMvc + Testcontainers + JSON seed.

## [1.3.0] - 2026-01-08
### Added
- Versioned JSON seed files under `src/main/resources/data/`.
- `CvSeedRunner` (conditional) to load JSON into PostgreSQL on startup.
- Idempotent seed strategy (single-row tables for single CV profile).
- Contact persistence (JPA entity/repository/adapter/mapper) + application service.
- Integration test validating seed behavior with Testcontainers.

## [1.2.0] - 2026-01-08
### Added
- PostgreSQL integration (Spring Data JPA).
- MapStruct configured for Spring component model.
- Persistence adapter example (PersonalInfo) implementing a domain port.
- Integration test using Testcontainers (PostgreSQL).
- Lombok (provided scope)


## [1.1.0] - 2026-01-07
### Added
- Spring Boot 3 + Java 21 + Maven baseline.
- Hexagonal/DDD package structure: domain / application / infrastructure.
- Actuator health endpoint (`/actuator/health`).

