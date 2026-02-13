# jb2dev-cv-api

[![Release](https://img.shields.io/github/v/release/JB2DEV/jac)](https://github.com/JB2DEV/jac/releases)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Coverage](https://codecov.io/gh/JB2DEV/jac/graph/badge.svg)](https://codecov.io/gh/JB2DEV/jac)

Curriculum Vitae REST API - Portfolio project implementing a Java backend based on Spring Boot 3, strictly following **Hexagonal Architecture (Ports & Adapters)** and **Domain-Driven Design (DDD)** principles.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Domain Model (DDD)](#domain-model-ddd)
  - [Aggregates and Entities](#aggregates-and-entities)
  - [Value Objects](#value-objects)
  - [Ports (Interfaces)](#ports-interfaces)
  - [Domain Exceptions](#domain-exceptions)
- [Use Cases (Application Layer)](#use-cases-application-layer)
  - [Organization](#organization)
  - [Naming Convention](#naming-convention)
  - [Execution Flow](#execution-flow)
- [Infrastructure](#infrastructure)
  - [REST Adapters](#rest-adapters)
  - [Persistence (JSON-backed)](#persistence-json-backed)
  - [Logging and Correlation](#logging-and-correlation)
  - [Security](#security)
- [Testing](#testing)
  - [Strategy by Layer](#strategy-by-layer)
  - [Running Tests](#running-tests)
  - [Coverage](#coverage)
- [DevOps](#devops)
- [Technologies](#technologies)
- [Configuration and Execution](#configuration-and-execution)
- [Main Endpoints](#main-endpoints)
- [Important Conventions and Rules](#important-conventions-and-rules)
- [How to Contribute](#how-to-contribute)

---

## Overview

**jb2dev-cv-api** is a REST API that exposes structured information from a curriculum vitae (personal profile, professional experience, education, technical skills, languages, complementary training).

### Business Purpose

Provide a technically robust, maintainable, and scalable backend that serves as:

- **Portfolio project** demonstrating capabilities in clean architecture, DDD, and Spring Boot.
- **Integration base** for modern frontends (React, Angular, Vue) or mobile applications.
- **Architectural reference** for enterprise backend projects.

### Technical Purpose

- Implement strict Hexagonal Architecture without compromises.
- Completely separate business logic from technical details.
- Demonstrate exhaustive testing across all layers.
- Apply structured logging with distributed correlation.
- Keep the domain completely isolated from external frameworks.

---

## Architecture

This project implements **Hexagonal Architecture (Ports & Adapters)** combined with **Domain-Driven Design (DDD)** principles to ensure clean separation of concerns, technology independence, and high testability.

### Key Architectural Principles

1. **Hexagonal Architecture**: The domain is at the center, isolated from all external technology. Adapters (REST, JSON) implement the ports defined by the domain.

2. **Domain-Driven Design**: Rich domain modeling with entities, value objects, aggregates, and domain services. The domain language (ubiquitous language) is reflected in the code.

3. **Dependency Inversion**: The domain layer depends on NOTHING. Application depends only on domain. Infrastructure depends on application and domain, but never the reverse.

4. **Separation of Concerns**: Each layer has clear responsibilities and cannot be bypassed.

### Quick Layer Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                      INFRASTRUCTURE LAYER                       │
│  REST Controllers, DTOs, Mappers, JSON Adapters, Logging        │
│  Dependencies: Spring Boot, Jackson, MapStruct, Logback         │
└────────────────────────────┬────────────────────────────────────┘
                             │ depends on
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      APPLICATION LAYER                          │
│  Use Cases/Interactors, Business Orchestration                  │
│  Dependencies: Domain only (+ minimal Spring for DI)            │
└────────────────────────────┬────────────────────────────────────┘
                             │ depends on
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                         DOMAIN LAYER                            │
│  Entities, Value Objects, Aggregates, Ports, Domain Exceptions  │
│  Dependencies: NONE (Pure Java)                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Layer Responsibilities Summary

- **Domain** (`com.jb2dev.cv.domain`): Pure business logic, entities (Records), value objects, repository interfaces (ports), domain exceptions. Zero external dependencies.

- **Application** (`com.jb2dev.cv.application`): Use case orchestration (Interactors), business operation coordination, logging of business events. Depends only on domain.

- **Infrastructure** (`com.jb2dev.cv.infrastructure`): Technical implementation details - REST controllers, DTOs, mappers (MapStruct), JSON adapters, logging filters, exception handlers, Spring Boot configuration.

### Detailed Architecture Documentation

For comprehensive architecture documentation including:
- Complete Hexagonal Architecture implementation details
- Domain-Driven Design patterns and bounded contexts
- Component diagrams and request flow
- Exception handling strategy
- Design decisions and rationale
- Future architecture evolution plans

**See**: [`docs/architecture.md`](docs/architecture.md)

---

## Project Structure

```
src/
├── main/
│   ├── java/com/jb2dev/cv/
│   │   ├── domain/
│   │   │   ├── education/
│   │   │   │   ├── model/          # EducationItem
│   │   │   │   └── ports/          # EducationRepository
│   │   │   ├── experience/
│   │   │   │   ├── model/          # ExperienceItem
│   │   │   │   └── ports/          # ExperienceRepository
│   │   │   ├── profile/
│   │   │   │   ├── model/          # PersonalInfo, ContactInfo
│   │   │   │   └── ports/          # PersonalInfoRepository, ContactInfoRepository
│   │   │   ├── skills/
│   │   │   │   ├── model/          # TechnicalSkill, SoftSkill, LanguageSkill, TechnicalSkillCategory
│   │   │   │   └── ports/          # SkillsRepository
│   │   │   ├── training/
│   │   │   │   ├── model/          # TrainingItem
│   │   │   │   └── ports/          # TrainingRepository
│   │   │   ├── exception/          # DomainException, ResourceNotFoundException, InvalidLanguageException
│   │   │   └── Language.java       # Value Object (enum)
│   │   │
│   │   ├── application/
│   │   │   ├── education/
│   │   │   │   ├── GetEducationUseCase.java
│   │   │   │   ├── ListEducationsUseCase.java
│   │   │   │   └── impl/           # GetEducationInteractor, ListEducationsInteractor
│   │   │   ├── experience/
│   │   │   │   ├── GetExperienceUseCase.java
│   │   │   │   ├── ListExperiencesUseCase.java
│   │   │   │   └── impl/           # Interactors
│   │   │   ├── profile/
│   │   │   │   ├── GetPersonalInfoUseCase.java
│   │   │   │   ├── GetContactInfoUseCase.java
│   │   │   │   └── impl/           # Interactors
│   │   │   ├── skills/
│   │   │   │   ├── SearchTechnicalSkillsUseCase.java
│   │   │   │   ├── GetLanguageSkillUseCase.java
│   │   │   │   ├── GetSoftSkillUseCase.java
│   │   │   │   ├── ListLanguageSkillsUseCase.java
│   │   │   │   ├── ListSoftSkillsUseCase.java
│   │   │   │   ├── query/          # TechnicalSkillSearchCriteria
│   │   │   │   └── impl/           # Interactors
│   │   │   ├── training/
│   │   │   │   ├── GetTrainingUseCase.java
│   │   │   │   ├── ListTrainingsUseCase.java
│   │   │   │   └── impl/           # Interactors
│   │   │   └── exception/          # ApplicationException, UseCaseExecutionException
│   │   │
│   │   ├── infrastructure/
│   │   │   ├── rest/
│   │   │   │   ├── controllers/    # EducationController, ExperienceController, ProfileController, SkillsController, TrainingController
│   │   │   │   ├── dto/            # Response DTOs (snake_case via Jackson)
│   │   │   │   ├── mappers/        # MapStruct mappers (Domain <-> REST DTO)
│   │   │   │   ├── exception/      # GlobalExceptionHandler
│   │   │   │   └── config/         # OpenApiConfig
│   │   │   ├── json/
│   │   │   │   ├── education/      # JsonEducationQueryAdapter
│   │   │   │   ├── experience/     # JsonExperienceQueryAdapter
│   │   │   │   ├── profile/        # JsonPersonalInfoQueryAdapter, JsonContactInfoQueryAdapter
│   │   │   │   ├── skills/         # JsonSkillsQueryAdapter
│   │   │   │   ├── training/       # JsonTrainingQueryAdapter
│   │   │   │   └── ClasspathJsonReader.java
│   │   │   ├── logging/
│   │   │   │   └── correlation/    # CorrelationIdFilter, CorrelationIdHolder, CorrelationIdGenerator
│   │   │   └── exception/          # InfrastructureException, JsonReadException, DataSourceException
│   │   │
│   │   └── CvApiApplication.java   # Spring Boot entry point
│   │
│   └── resources/
│       ├── application.yml         # Spring Boot configuration
│       ├── logback-spring.xml      # Logging configuration
│       └── data/
│           ├── en/                 # JSON data in English
│           ├── es/                 # JSON data in Spanish
│           └── commons/            # Common data (no i18n)
│
└── test/
    └── java/com/jb2dev/cv/
        ├── domain/                 # Pure unit tests (entities, value objects)
        ├── application/            # Unit tests with mocks (Mockito)
        └── infrastructure/         # Integration tests (@SpringBootTest, JSON adapters, E2E)
```

---

## Domain Model (DDD)

### Aggregates and Entities

The domain is organized into implicit **bounded contexts**:

1. **Education**: `EducationItem` - Formal education (degrees, postgraduate studies).
2. **Experience**: `ExperienceItem` - Professional experience.
3. **Profile**: `PersonalInfo`, `ContactInfo` - Personal and contact information.
4. **Skills**: `TechnicalSkill`, `SoftSkill`, `LanguageSkill` - Technical, soft skills, and languages.
5. **Training**: `TrainingItem` - Complementary training (certifications, courses).

All entities are modeled as **Java Records** (immutable by design).

#### Example: EducationItem

```java
public record EducationItem(
    int id,
    String title,
    String institution,
    String location,
    LocalDate startDate,
    LocalDate endDate,
    String details
) {}
```

### Value Objects

- **`Language`**: Enum representing supported languages (`ES_ES`, `EN_EN`). Includes validation and conversion from code.
- **`TechnicalSkillCategory`**: Classification of technical skills (frameworks, languages, tools, etc.).

Value Objects are **immutable** and **self-validating**. For example, `Language.fromCode(String)` throws `InvalidLanguageException` if the code is invalid.

### Ports (Interfaces)

Ports are **interfaces defined in the domain** that abstract external dependencies. They are implemented in the infrastructure layer.

#### Example: EducationRepository

```java
public interface EducationRepository {
  List<EducationItem> findAllEducations(Language language);
  Optional<EducationItem> findEducationById(int id, Language language);
}
```

#### Defined Ports

- `EducationRepository`
- `ExperienceRepository`
- `PersonalInfoRepository`
- `ContactInfoRepository`
- `SkillsRepository` (methods for technical, soft, and language skills)
- `TrainingRepository`

### Domain Exceptions

All domain exceptions inherit from `DomainException` (unchecked).

- **`ResourceNotFoundException`**: Resource not found by ID.
- **`InvalidLanguageException`**: Invalid language code.
- **`InvalidAggregateStateException`**: Invalid aggregate state (future use).

The domain **never** throws technical exceptions (SQL, JSON, HTTP).

---

## Use Cases (Application Layer)

### Organization

Use cases represent **explicit business intentions**. Each use case:

- Is a **functional interface** (`@FunctionalInterface`).
- Has an implementation in `impl/` called `*Interactor`.
- Uses domain ports (dependency injection).
- Performs logging of business operations.

### Naming Convention

**Pattern**: `{Verb}{Aggregate}UseCase` / `{Verb}{Aggregate}Interactor`

**Common verbs**:
- `Get` - Retrieve a single resource by ID.
- `List` - Retrieve collection of resources.
- `Search` - Search with criteria.

**Examples**:
- `GetEducationUseCase` / `GetEducationInteractor`
- `ListExperiencesUseCase` / `ListExperiencesInteractor`
- `SearchTechnicalSkillsUseCase` / `SearchTechnicalSkillsInteractor`

### Execution Flow

```
1. REST Controller receives HTTP request
   ↓
2. Validates parameters and extracts data (e.g., Accept-Language → Language enum)
   ↓
3. Invokes Use Case (e.g., getEducationUseCase.execute(id, language))
   ↓
4. Interactor:
   - Logs operation (INFO)
   - Calls domain port (repository)
   - Handles domain exceptions
   - Returns domain entity
   ↓
5. Controller:
   - Maps domain entity to DTO (MapStruct)
   - Returns ResponseEntity<DTO>
   ↓
6. GlobalExceptionHandler catches exceptions and returns ErrorResponse
```

### Use Case Example

```java
@FunctionalInterface
public interface GetEducationUseCase {
  EducationItem execute(int id, Language language);
}

@Service
@RequiredArgsConstructor
public class GetEducationInteractor implements GetEducationUseCase {
  private final EducationRepository educationRepository;
  
  @Override
  public EducationItem execute(int id, Language language) {
    log.info("Executing GetEducation use case: id={}, language={}", id, language);
    return educationRepository.findEducationById(id, language)
        .orElseThrow(() -> new ResourceNotFoundException("Education", String.valueOf(id)));
  }
}
```

---

## Infrastructure

### REST Adapters

**Location**: `infrastructure/rest/controllers/`

Responsibilities:
- Expose HTTP endpoints.
- Validate headers (e.g., `Accept-Language`).
- Map DTOs to/from domain entities.
- Delegate logic to Use Cases.
- OpenAPI documentation (Swagger).

**Conventions**:
- Base path: `/api/v1/{resource}`
- Response DTOs in `snake_case` (configured in Jackson).
- Standard headers (`X-Correlation-Id`, `Accept-Language`).

**Example**:
```java
@RestController
@RequestMapping("/api/v1/education")
public class EducationController {
  private final GetEducationUseCase getByIdUseCase;
  private final EducationRestMapper mapper;
  
  @GetMapping("/{id}")
  public ResponseEntity<EducationResponse> getById(
      @PathVariable int id,
      @RequestHeader(value = "Accept-Language", defaultValue = "es_ES") String lang) {
    Language language = Language.fromCode(lang);
    EducationItem item = getByIdUseCase.execute(id, language);
    return ResponseEntity.ok(mapper.toResponse(item));
  }
}
```

### Persistence (JSON-backed)

**Currently**: Adapters that read static JSON files from classpath (`src/main/resources/data/`).

**Location**: `infrastructure/json/`

Each adapter (e.g., `JsonEducationQueryAdapter`) implements the domain port (`EducationRepository`).

**Multi-language**: Data is separated by language:
- `data/en/education.json` (English)
- `data/es/education.json` (Spanish)

**ClasspathJsonReader**: Reusable utility that reads and parses JSON using Jackson.

### Logging and Correlation

The system implements **structured logging** with **distributed correlation**.

**Key components**:

1. **CorrelationIdFilter**: Intercepts each HTTP request, extracts or generates `X-Correlation-Id`, stores it in MDC, and propagates it in the response.

2. **MDC (Mapped Diagnostic Context)**: Each log automatically includes the `correlationId`.

3. **Layer-based configuration**:
   - **Domain**: No technical logging (pure logic).
   - **Application**: Business event logging (`log.info`, `log.debug`).
   - **Infrastructure**: Complete technical logging.

4. **Formats**:
   - **Development**: Human-readable logs in console.
   - **Production**: Structured JSON (Logstash encoder) for aggregators (ELK, Grafana Loki).

**Dynamic management**: Endpoint `/actuator/loggers` allows changing log levels at runtime.

See full documentation at: [`docs/logging-system.md`](docs/logging-system.md)

### Security

**Current status**: TODO


---

## Testing

### Strategy by Layer

#### 1. Unit Tests - Domain

- **Tools**: JUnit 5, AssertJ.
- **Scope**: Entities, Value Objects, domain validations.
- **Without**: Spring, Mockito, databases.
- **Example**: `LanguageTest`, `EducationItemTest`.

#### 2. Unit Tests - Application

- **Tools**: JUnit 5, Mockito, AssertJ.
- **Scope**: Interactors (Use Cases).
- **Strategy**: Mock domain ports (repositories).
- **Verify**: Orchestration logic, exception handling.
- **Example**: `GetEducationInteractorTest`.

```java
@ExtendWith(MockitoExtension.class)
class GetEducationInteractorTest {
  @Mock private EducationRepository repository;
  @InjectMocks private GetEducationInteractor interactor;
  
  @Test
  void shouldThrowExceptionWhenEducationNotFound() {
    when(repository.findEducationById(999, Language.EN_EN))
        .thenReturn(Optional.empty());
    
    assertThatThrownBy(() -> interactor.execute(999, Language.EN_EN))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
```

#### 3. Integration Tests - Infrastructure

- **Tools**: JUnit 5, `@SpringBootTest`, AssertJ.
- **Scope**: JSON adapters, controllers (E2E), filters.
- **Strategy**: Start complete Spring context.
- **Example**: `JsonEducationQueryAdapterIntegrationTest`, `EducationControllerE2ETest`.

```java
@SpringBootTest
class JsonEducationQueryAdapterIntegrationTest {
  @Autowired
  private JsonEducationQueryAdapter adapter;
  
  @Test
  void shouldFindAllEducationsInEnglish() {
    List<EducationItem> result = adapter.findAllEducations(Language.EN_EN);
    assertThat(result).isNotEmpty();
  }
}
```

#### 4. E2E Tests (End-to-End)

- **Tools**: `@SpringBootTest(webEnvironment = RANDOM_PORT)`, `TestRestTemplate`.
- **Scope**: Complete flow REST → Controller → Use Case → Repository → Response.
- **Example**: `EducationControllerE2ETest`.

### Running Tests

```bash
# All tests
mvn test

# Unit tests only (fast)
mvn test -Dgroups=unit

# Integration tests only
mvn test -Dgroups=integration

# With coverage (JaCoCo)
mvn clean test jacoco:report
```

### Coverage

**JaCoCo** configured in `pom.xml`.

Reports generated at: `target/site/jacoco/index.html`

**Goal**: >80% coverage in domain and application.

---

## DevOps

### CI/CD

**Current status**: TODO

---

## Technologies

### Core

- **Java 21** (LTS)
- **Spring Boot 3.4.1**
  - `spring-boot-starter-web` (REST)
  - `spring-boot-starter-actuator` (Observability)
- **Maven 3.9+**

### Libraries and Tools

- **MapStruct 1.5.5** - Domain ↔ DTOs mapping
- **Lombok 1.18.30** - Boilerplate reduction (limited use in domain)
- **Jackson** - JSON serialization
- **Logback + Logstash Encoder** - Structured logging
- **SpringDoc OpenAPI 2.5.0** - Automatic documentation (Swagger UI)
- **Hibernate Validator** - DTO validation

### Testing

- **JUnit 5** - Testing framework
- **Mockito** - Mocks and stubs
- **AssertJ** - Fluent assertions
- **JaCoCo** - Code coverage

---

## Configuration and Execution

### Prerequisites

- **Java 21** installed
- **Maven 3.9+** installed

### Local Execution

```bash
# Clone repository
git clone https://github.com/jb2dev/jac.git
cd jac

# Compile
mvn clean compile

# Run tests
mvn test

# Package
mvn package

# Run application
mvn spring-boot:run
# or
java -jar target/cv-api-1.1.0.jar
```

The application will be available at: `http://localhost:8080`

### Configuration

**File**: `src/main/resources/application.yml`

```yaml
server:
  port: 8080

logging:
  level:
    root: INFO
    com.jb2dev.cv: DEBUG

management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,loggers
```

### Actuator Endpoints

- `/actuator/health` - Application status
- `/actuator/info` - Build information
- `/actuator/loggers` - Dynamic log level management
- `/actuator/prometheus` - Metrics (TODO: integrate with Prometheus)

---

## Main Endpoints

### OpenAPI Documentation

- **Swagger UI**: `http://localhost:8080/api/v1/swagger-ui`
- **OpenAPI JSON**: `http://localhost:8080/api/v1/openapi`

### Resources

Base URL: `/api/v1`

| Resource     | Endpoint                        | Method | Description                          |
|--------------|---------------------------------|--------|--------------------------------------|
| Profile      | `/profile/personal`             | GET    | Personal information                 |
| Profile      | `/profile/contact`              | GET    | Contact information                  |
| Education    | `/education`                    | GET    | List of formal education             |
| Education    | `/education/{id}`               | GET    | Education detail by ID               |
| Experience   | `/experience`                   | GET    | List of professional experiences     |
| Experience   | `/experience/{id}`              | GET    | Experience detail by ID              |
| Skills       | `/skills/technical`             | GET    | Technical skills (with filters)      |
| Skills       | `/skills/languages`             | GET    | Languages                            |
| Skills       | `/skills/languages/{id}`        | GET    | Language detail                      |
| Skills       | `/skills/soft`                  | GET    | Soft skills                          |
| Skills       | `/skills/soft/{id}`             | GET    | Soft skill detail                    |
| Training     | `/training`                     | GET    | Complementary training               |
| Training     | `/training/{id}`                | GET    | Training detail                      |

### Common Headers

- **`Accept-Language`**: `es_ES` (Spanish) or `en_EN` (English). Default: `es_ES`.
- **`X-Correlation-Id`**: Optional. If not sent, it's automatically generated.

### Request Example

```bash
curl -H "Accept-Language: en_EN" \
     -H "X-Correlation-Id: req-12345" \
     http://localhost:8080/api/v1/education/3012
```

### Response Example

```json
{
  "id": 3012,
  "title": "Bachelor of Computer Science",
  "institution": "University of Madrid",
  "location": "Madrid, Spain",
  "start_date": "2008-09-01",
  "end_date": "2012-06-30",
  "details": "Focused on software engineering and distributed systems."
}
```

---
## Docker Quick Start

### Build and Run with Docker

```powershell
# Build the image
docker build -t jac-api:1.1.0 .

# Run in production mode
docker run -d -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod --name jac-api jac-api:1.1.0

# View logs
docker logs -f jac-api
```

### Run with Docker Compose

```powershell
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

**See [DOCKER.md](docs/docker.md) for complete documentation.**

---

## Related Documentation

- **[Architecture](docs/architecture.md)** - Comprehensive architecture documentation covering Hexagonal Architecture, DDD patterns, component diagrams, request flows, and design decisions
- **[Logging System](docs/logging-system.md)** - Detailed logging and observability documentation
- **[Docker Guide](docs/docker.md)** - Complete guide for building, running, and deploying with Docker

---
