# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-02-12

### Added
- **Hexagonal Architecture (Ports & Adapters)** implementation with clear separation of concerns
- **Domain-Driven Design (DDD)** with well-defined aggregates, entities, and value objects
- **REST API** endpoints for CV management:
  - Personal profile (personal info, contact info)
  - Professional experience
  - Education
  - Technical skills with search capabilities
  - Language skills
  - Soft skills
  - Complementary training
- **Internationalization (i18n)** support for Spanish and English
- **Comprehensive logging system** with correlation IDs for request tracing
  - Structured JSON logging in production
  - Human-readable logging in development
  - See [docs/logging-system.md](docs/logging-system.md) for details
- **Exception handling** with global controller advice and consistent error responses
- **OpenAPI/Swagger documentation** available at `/swagger-ui.html`
- **JSON-based persistence** layer for data storage
- **Spring Boot Actuator** for monitoring and health checks
- **Comprehensive test suite**:
  - Unit tests for domain layer
  - Integration tests for application layer
  - Test coverage reports with JaCoCo
- **MapStruct** for entity-DTO mapping
- **Validation** with Hibernate Validator
- **Production-ready configuration**:
  - Environment-specific application.yml
  - Logback configuration
  - CORS configuration for web clients

### Documentation
- Detailed README with architecture overview
- Architecture documentation in [docs/architecture.md](docs/architecture.md)
- Logging system documentation in [docs/logging-system.md](docs/logging-system.md)
- Postman collection for API testing
- OpenAPI/Swagger interactive documentation

### Technical Stack
- Java 21
- Spring Boot 3.4.1
- Maven build system
- MapStruct 1.5.5
- Lombok 1.18.30
- SpringDoc OpenAPI 2.5.0
- Logstash Logback Encoder 7.4
- JaCoCo for code coverage
- JUnit 5, Mockito, AssertJ for testing

[1.0.0]: https://github.com/JB2DEV/jac/releases/tag/v1.0.0

