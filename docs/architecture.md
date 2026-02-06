# Architecture Documentation - jb2dev-cv-api

## Table of Contents

- [Overview](#overview)
- [Architectural Style](#architectural-style)
- [Hexagonal Architecture Implementation](#hexagonal-architecture-implementation)
- [Domain-Driven Design (DDD)](#domain-driven-design-ddd)
- [Layer Architecture](#layer-architecture)
- [Dependency Rules](#dependency-rules)
- [Component Diagram](#component-diagram)
- [Request Flow](#request-flow)
- [Exception Handling Strategy](#exception-handling-strategy)
- [Data Flow](#data-flow)
- [Technology Boundaries](#technology-boundaries)
- [Design Decisions](#design-decisions)

---

## Overview

The **jb2dev-cv-api** is designed as a microservice following **Hexagonal Architecture (Ports & Adapters)** combined with **Domain-Driven Design (DDD)** principles. This architectural approach ensures:

- **Technology independence**: The business logic (domain) is completely isolated from frameworks and infrastructure concerns.
- **Testability**: Each layer can be tested independently with appropriate strategies.
- **Maintainability**: Clear separation of concerns makes the codebase easier to understand and modify.
- **Flexibility**: Infrastructure components (database, REST API, messaging) can be replaced without affecting business logic.

---

## Architectural Style

### Primary Pattern: Hexagonal Architecture

The hexagonal architecture, also known as **Ports and Adapters**, organizes the application into three main layers:

```
┌──────────────────────────────────────────────────────────────────┐
│                         ADAPTERS (IN/OUT)                        │
│                      Infrastructure Layer                        │
│  ┌────────────┐                                ┌──────────────┐  │
│  │   REST     │                                │     JSON     │  │
│  │ Controller │◄───────────┐      ┌───────────►│   Adapter    │  │
│  │  Adapter   │            │      │            │ (Repository) │  │
│  └────────────┘            │      │            └──────────────┘  │
└────────────────────────────┼──────┼──────────────────────────────┘
                             │      │
┌────────────────────────────┼──────┼──────────────────────────────┐
│                            ▼      ▼                              │
│                    ┌─────────────────────┐                       │
│                    │                     │                       │
│                    │   APPLICATION       │                       │
│                    │   Use Cases /       │                       │
│                    │   Interactors       │                       │
│                    │                     │                       │
│                    └─────────────────────┘                       │
│                             │                                    │
│                             ▼                                    │
│                    ┌─────────────────────┐                       │
│                    │                     │                       │
│                    │      DOMAIN         │                       │
│                    │  ┌───────────────┐  │                       │
│                    │  │   Entities    │  │                       │
│                    │  │ Value Objects │  │                       │
│                    │  │   Aggregates  │  │                       │
│                    │  │     Ports     │  │                       │
│                    │  └───────────────┘  │                       │
│                    │                     │                       │
│                    └─────────────────────┘                       │
│                     CORE BUSINESS LOGIC                          │
└──────────────────────────────────────────────────────────────────┘
```

### Key Characteristics

1. **Domain at the center**: Business logic is the heart of the application.
2. **Ports as contracts**: Interfaces define what the domain needs, not how it's implemented.
3. **Adapters as implementations**: Infrastructure components implement domain ports.
4. **Dependency direction**: Always pointing inward (Infrastructure → Application → Domain).

---

## Hexagonal Architecture Implementation

### Ports (Interfaces)

Ports are **interfaces defined in the domain layer** that abstract external dependencies. They represent contracts between the domain and the outside world.

#### Primary Ports (Driving)
These are the **use cases** that drive the application logic:

```java
// Example: Use Case as Primary Port
@FunctionalInterface
public interface GetEducationUseCase {
  EducationItem execute(int id, Language language);
}
```

**Location**: `com.jb2dev.cv.application.*`

**Implemented by**: Interactors in `application.*.impl`

**Driven by**: REST Controllers (infrastructure layer)

#### Secondary Ports (Driven)
These are **repository interfaces** that the domain needs but doesn't implement:

```java
// Example: Repository as Secondary Port
public interface EducationRepository {
  List<EducationItem> findAllEducations(Language language);
  Optional<EducationItem> findEducationById(int id, Language language);
}
```

**Location**: `com.jb2dev.cv.domain.*.ports`

**Implemented by**: JSON adapters in `infrastructure.json.*`

### Adapters

#### Input Adapters (Primary/Driving)
Handle external requests and delegate to use cases:

**REST Controllers**:
```java
@RestController
@RequestMapping("/api/v1/education")
public class EducationController {
  private final GetEducationUseCase getEducationUseCase;
  
  @GetMapping("/{id}")
  public ResponseEntity<EducationResponse> getById(@PathVariable int id) {
    // Adapter responsibility: Convert HTTP → Domain
    EducationItem item = getEducationUseCase.execute(id, language);
    // Adapter responsibility: Convert Domain → HTTP
    return ResponseEntity.ok(mapper.toResponse(item));
  }
}
```

**Location**: `com.jb2dev.cv.infrastructure.rest.controllers.*`

#### Output Adapters (Secondary/Driven)
Implement domain ports using specific technologies:

**JSON Repository Adapter**:
```java
@Component
public class JsonEducationQueryAdapter implements EducationRepository {
  private final ClasspathJsonReader reader;
  
  @Override
  public List<EducationItem> findAllEducations(Language language) {
    String path = language == Language.EN_EN 
        ? "data/en/education.json" 
        : "data/es/education.json";
    return reader.read(path, TYPE);
  }
}
```

**Location**: `com.jb2dev.cv.infrastructure.json.*`


---

## Domain-Driven Design (DDD)

### Bounded Contexts

The application is implicitly organized into **bounded contexts**:

```
┌─────────────────────────────────────────────────────────────┐
│                     CV MANAGEMENT CONTEXT                   │
│  ┌──────────┐  ┌────────────┐  ┌─────────┐  ┌──────────┐    │
│  │ Profile  │  │ Education  │  │  Skills │  │ Training │    │
│  │ Context  │  │  Context   │  │ Context │  │ Context  │    │
│  └──────────┘  └────────────┘  └─────────┘  └──────────┘    │
│       │              │               │             │        │
│  ┌──────────┐  ┌────────────┐  ┌─────────┐  ┌──────────┐    │
│  │Experience│  │            │  │         │  │          │    │
│  │ Context  │  │            │  │         │  │          │    │
│  └──────────┘  └────────────┘  └─────────┘  └──────────┘    │
└─────────────────────────────────────────────────────────────┘
```

Each context is self-contained with:
- **Model**: Entities and Value Objects
- **Ports**: Repository interfaces
- **Use Cases**: Operations within the context

### Aggregates

Aggregates define the consistency boundaries in the domain. In this project, aggregates are simple data structures (Java Records) without behavior, following an anemic domain model approach suitable for read-only operations:

#### Education Aggregate
```
EducationItem (Aggregate Root)
├── id (Identity)
├── title
├── institution
├── location
├── startDate
├── endDate
└── details
```

**Design Approach**:
- Implemented as an immutable Java Record
- No explicit validation in the domain entity
- Data integrity guaranteed by the data source (JSON files are pre-validated)
- Simple data carrier pattern (anemic domain model for read-only operations)

**Note**: Since this is a read-only CV application with static data, complex domain invariants are not enforced in code. The simplicity principle ("Make it Simple") is applied - validation would be over-engineering for this use case.

#### Experience Aggregate
```
ExperienceItem (Aggregate Root)
├── id (Identity)
├── role
├── company
├── location
├── startDate
├── endDate
├── current (boolean)
├── summary
└── description
```

**Design Approach**:
- Implemented as an immutable Java Record
- No explicit validation in the domain entity
- The `current` flag indicates if this is an ongoing position
- When `current=true`, `endDate` can be null (representing ongoing work)
- Data integrity guaranteed by the data source (JSON files are pre-validated)

**Note**: Following the "Make it Simple" principle, validation logic is not implemented for this read-only use case.

### Domain Design Approach

This project intentionally uses an **anemic domain model** for the following reasons:

- **Read-only operations**: The application only queries data (no writes, no state changes)
- **Static data source**: JSON files contain pre-validated, immutable CV information
- **Simplicity over complexity**: Complex domain logic would be over-engineering for this use case
- **Java Records**: Leverages modern Java features for immutable data carriers
- **No business rules to enforce**: CV data doesn't have complex business invariants to protect

**When would you need rich domain models?**
- Write operations (create, update, delete)
- Complex business rules and validations
- State transitions with invariants
- Domain events and side effects
- Transactional consistency requirements

For this portfolio project demonstrating Hexagonal Architecture, the focus is on **structural patterns** (layers, ports, adapters) rather than rich domain behavior.

### Entities vs Value Objects

**Entities** (have identity):
- `EducationItem`
- `ExperienceItem`
- `TrainingItem`
- `TechnicalSkill`
- `SoftSkill`
- `LanguageSkill`

**Value Objects** (defined by attributes):
- `Language` (enum: ES_ES, EN_EN)
- `TechnicalSkillCategory` (enum)
- `PersonalInfo` (record - immutable)
- `ContactInfo` (record - immutable)

### Ubiquitous Language

The code uses domain language consistently:

- **Education** (not "Study" or "Degree")
- **Experience** (not "Job" or "Work")
- **Training** (not "Course" or "Certification")
- **Use Case** (not "Service" or "Handler")
- **Interactor** (implementation of Use Case)
- **Port** (interface/contract)
- **Adapter** (implementation)

---

## Layer Architecture

### Domain Layer

**Package**: `com.jb2dev.cv.domain`

**Responsibilities**:
- Define business entities and value objects (as immutable data structures)
- Declare ports (repository interfaces)
- Define domain exceptions
- Provide a pure Java representation of the CV domain

**Note**: This project uses an anemic domain model (data carriers without behavior) since it's a read-only application. Complex business rules and validations are not required.

**Characteristics**:
- **Zero dependencies** on external frameworks
- **Pure Java** (Records, enums, interfaces)
- **No annotations** from Spring, Jackson, or any framework
- **No technical concerns** (logging, HTTP, JSON, SQL)

**Structure**:
```
domain/
├── education/
│   ├── model/
│   │   └── EducationItem.java          (Entity)
│   └── ports/
│       └── EducationRepository.java    (Port)
├── experience/
│   ├── model/
│   │   └── ExperienceItem.java
│   └── ports/
│       └── ExperienceRepository.java
├── profile/
│   ├── model/
│   │   ├── PersonalInfo.java           (Value Object)
│   │   └── ContactInfo.java            (Value Object)
│   └── ports/
│       ├── PersonalInfoRepository.java
│       └── ContactInfoRepository.java
├── skills/
│   ├── model/
│   │   ├── TechnicalSkill.java
│   │   ├── SoftSkill.java
│   │   ├── LanguageSkill.java
│   │   └── TechnicalSkillCategory.java (Value Object)
│   └── ports/
│       └── SkillsRepository.java
├── training/
│   ├── model/
│   │   └── TrainingItem.java
│   └── ports/
│       └── TrainingRepository.java
├── exception/
│   ├── DomainException.java            (Base)
│   ├── ResourceNotFoundException.java
│   ├── InvalidLanguageException.java
│   └── InvalidAggregateStateException.java
└── Language.java                        (Value Object)
```

### Application Layer

**Package**: `com.jb2dev.cv.application`

**Responsibilities**:
- Orchestrate business operations (use cases)
- Coordinate calls to domain ports
- Handle application-level exceptions
- Log business events
- Define input/output contracts (DTOs for queries)

**Characteristics**:
- Depends **only on domain**
- Uses domain ports (interfaces)
- **No knowledge** of infrastructure details
- Minimal Spring usage (`@Service` for DI only)

**Structure**:
```
application/
├── education/
│   ├── GetEducationUseCase.java        (Port)
│   ├── ListEducationsUseCase.java      (Port)
│   └── impl/
│       ├── GetEducationInteractor.java (Adapter)
│       └── ListEducationsInteractor.java
├── experience/
│   ├── GetExperienceUseCase.java
│   ├── ListExperiencesUseCase.java
│   └── impl/
│       ├── GetExperienceInteractor.java
│       └── ListExperiencesInteractor.java
├── profile/
│   ├── GetPersonalInfoUseCase.java
│   ├── GetContactInfoUseCase.java
│   └── impl/
│       ├── GetPersonalInfoInteractor.java
│       └── GetContactInfoInteractor.java
├── skills/
│   ├── SearchTechnicalSkillsUseCase.java
│   ├── GetLanguageSkillUseCase.java
│   ├── ListLanguageSkillsUseCase.java
│   ├── GetSoftSkillUseCase.java
│   ├── ListSoftSkillsUseCase.java
│   ├── query/
│   │   └── TechnicalSkillSearchCriteria.java (Input DTO)
│   └── impl/
│       ├── SearchTechnicalSkillsInteractor.java
│       ├── GetLanguageSkillInteractor.java
│       ├── ListLanguageSkillsInteractor.java
│       ├── GetSoftSkillInteractor.java
│       └── ListSoftSkillsInteractor.java
├── training/
│   ├── GetTrainingUseCase.java
│   ├── ListTrainingsUseCase.java
│   └── impl/
│       ├── GetTrainingInteractor.java
│       └── ListTrainingsInteractor.java
└── exception/
    ├── ApplicationException.java       (Base)
    └── UseCaseExecutionException.java
```

### Infrastructure Layer

**Package**: `com.jb2dev.cv.infrastructure`

**Responsibilities**:
- Implement domain ports using concrete technologies
- Expose REST API endpoints
- Handle HTTP concerns (validation, serialization, error handling)
- Manage logging, correlation, security
- Configure Spring Boot application

**Characteristics**:
- Depends on **application and domain**
- Contains **all technical details**
- Uses Spring Boot, Jackson, MapStruct, etc.
- Adapters can be replaced without affecting business logic

**Structure**:
```
infrastructure/
├── rest/
│   ├── controllers/
│   │   ├── education/
│   │   │   └── EducationController.java
│   │   ├── experience/
│   │   │   └── ExperienceController.java
│   │   ├── profile/
│   │   │   └── ProfileController.java
│   │   ├── skills/
│   │   │   └── SkillsController.java
│   │   └── training/
│   │       └── TrainingController.java
│   ├── dto/
│   │   ├── education/
│   │   │   └── EducationResponse.java
│   │   ├── experience/
│   │   │   └── ExperienceResponse.java
│   │   ├── profile/
│   │   │   ├── PersonalInfoResponse.java
│   │   │   └── ContactInfoResponse.java
│   │   ├── skills/
│   │   │   ├── TechnicalSkillDetailResponse.java
│   │   │   ├── LanguageSkillResponse.java
│   │   │   └── SoftSkillResponse.java
│   │   ├── training/
│   │   │   └── TrainingResponse.java
│   │   └── error/
│   │       └── ErrorResponse.java
│   ├── mappers/
│   │   ├── education/
│   │   │   └── EducationRestMapper.java (MapStruct)
│   │   ├── experience/
│   │   │   └── ExperienceRestMapper.java
│   │   ├── profile/
│   │   │   └── ProfileRestMapper.java
│   │   ├── skills/
│   │   │   └── SkillsRestMapper.java
│   │   └── training/
│   │       └── TrainingRestMapper.java
│   ├── exception/
│   │   └── GlobalExceptionHandler.java
│   └── config/
│       └── OpenApiConfig.java
├── json/
│   ├── education/
│   │   └── JsonEducationQueryAdapter.java (Implements EducationRepository)
│   ├── experience/
│   │   └── JsonExperienceQueryAdapter.java
│   ├── profile/
│   │   ├── JsonPersonalInfoQueryAdapter.java
│   │   └── JsonContactInfoQueryAdapter.java
│   ├── skills/
│   │   └── JsonSkillsQueryAdapter.java
│   ├── training/
│   │   └── JsonTrainingQueryAdapter.java
│   └── ClasspathJsonReader.java (Utility)
├── logging/
│   └── correlation/
│       ├── CorrelationIdFilter.java
│       ├── CorrelationIdHolder.java
│       └── CorrelationIdGenerator.java
└── exception/
    ├── InfrastructureException.java    (Base)
    ├── JsonReadException.java
    └── DataSourceException.java
```

---

## Dependency Rules

### The Dependency Rule

**Core Principle**: Dependencies must point **inward only**.

```
Infrastructure ──depends on──► Application ──depends on──► Domain
     (outer)                      (middle)                 (inner)
     
     ❌ Domain NEVER depends on Application or Infrastructure
     ❌ Application NEVER depends on Infrastructure
     ✅ Infrastructure can depend on both Application and Domain
```

### Allowed Dependencies

| Layer           | Can depend on      | Cannot depend on           |
|-----------------|--------------------|-----------------------------|
| Domain          | Nothing            | Application, Infrastructure |
| Application     | Domain             | Infrastructure              |
| Infrastructure  | Application, Domain| Nothing (outermost layer)   |

### Enforcement Mechanisms

1. **Package structure**: Clear separation prevents accidental cross-dependencies
2. **No framework annotations in domain**: Ensures domain purity
3. **Interface-based design**: Application uses domain ports (interfaces), not implementations

---

## Component Diagram

### High-Level Components

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                            │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐         │
│  │          │  │ POSTMAN  │  │          │  │          │         │
│  │          │  │          │  │          │  │          │         │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘         │
└───────┼────────────┼────────────┼────────────┼──────────────────┘
        │            │            │            │
        └────────────┴────────────┴────────────┘
                     │ HTTP/REST
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                         │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              REST API (Spring MVC)                       │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐                │   │
│  │  │Education │  │Experience│  │ Profile  │ ... [5 more]   │   │
│  │  │Controller│  │Controller│  │Controller│                │   │
│  │  └────┬─────┘  └────┬─────┘  └────┬─────┘                │   │
│  └───────┼─────────────┼─────────────┼──────────────────────┘   │
│          │             │             │                          │
│  ┌───────┼─────────────┼─────────────┼─────────────────────┐    │
│  │  ┌────▼─────┐  ┌────▼─────┐  ┌────▼─────┐               │    │
│  │  │DTO       │  │  Mapper  │  │Exception │               │    │
│  │  │Layer     │  │(MapStruct│  │ Handler  │               │    │
│  │  └──────────┘  └──────────┘  └──────────┘               │    │
│  └─────────────────────────────────────────────────────────┘    │
│          │             │             │                          │
│  ┌───────┼─────────────┼─────────────┼─────────────────────┐    │
│  │  ┌────▼─────────────▼─────────────▼─────┐               │    │
│  │  │      Cross-Cutting Concerns          │               │    │
│  │  │  - CorrelationId Filter (MDC)        │               │    │
│  │  │  - Logging (Logback + Logstash)      │               │    │
│  │  │  - Security (TODO: JWT)              │               │    │
│  │  └──────────────────────────────────────┘               │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────┬───────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                     APPLICATION LAYER                           │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              USE CASES (Interactors)                     │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐       │   │
│  │  │Get          │  │List         │  │Search       │ ...   │   │
│  │  │EducationUC  │  │EducationUC  │  │SkillsUC     │       │   │
│  │  │Interactor   │  │Interactor   │  │Interactor   │       │   │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘       │   │
│  └─────────┼─────────────────┼─────────────────┼────────────┘   │
└────────────┼─────────────────┼─────────────────┼────────────────┘
             │                 │                 │
             ▼                 ▼                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                        DOMAIN LAYER                             │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              PORTS (Repository Interfaces)               │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐       │   │
│  │  │Education    │  │Experience   │  │Skills       │ ...   │   │
│  │  │Repository   │  │Repository   │  │Repository   │       │   │
│  │  └──────▲──────┘  └──────▲──────┘  └──────▲──────┘       │   │
│  └─────────┼─────────────────┼─────────────────┼────────────┘   │
│            │                 │                 │                │
│  ┌─────────┼─────────────────┼─────────────────┼────────────┐   │
│  │  ┌──────┴──────┐  ┌───────┴──────┐  ┌──────┴──────┐      │   │
│  │  │  Entities   │  │Value Objects │  │ Aggregates  │      │   │
│  │  │             │  │              │  │             │      │   │
│  │  │EducationItem│  │   Language   │  │  (Business  │      │   │
│  │  │ExperienceIt.│  │PersonalInfo  │  │   Rules)    │      │   │
│  │  │TrainingItem │  │ContactInfo   │  │             │      │   │
│  │  └─────────────┘  └──────────────┘  └─────────────┘      │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
             ▲                 ▲                 ▲
             │                 │                 │
             └─────────────────┴─────────────────┘
                      Implemented by
┌─────────────────────────────────────────────────────────────────┐
│                INFRASTRUCTURE LAYER (Data Access)               │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │         JSON ADAPTERS (Repository Implementations)        │  │
│  │  ┌─────────────┐  ┌──────────────┐  ┌─────────────┐       │  │
│  │  │JsonEducation│  │JsonExperience│  │JsonSkills   │ ...   │  │
│  │  │QueryAdapter │  │QueryAdapter  │  │QueryAdapter │       │  │
│  │  └──────┬──────┘  └──────┬───────┘  └──────┬──────┘       │  │
│  └─────────┼─────────────────┼─────────────────┼─────────────┘  │
│            └─────────────────┴─────────────────┘                │
│                             │                                   │
│                    ┌────────▼────────┐                          │
│                    │ClasspathJson    │                          │
│                    │Reader (Jackson) │                          │
│                    └────────┬────────┘                          │
└─────────────────────────────┼───────────────────────────────────┘
                              ▼
                    ┌──────────────────┐
                    │  JSON Files      │
                    │  - data/en/*.json│
                    │  - data/es/*.json│
                    └──────────────────┘
```

---

## Request Flow

### Complete HTTP Request Flow

```
1. CLIENT REQUEST
   │
   │ GET /api/v1/education/3012
   │ Header: Accept-Language: en_EN
   │ Header: X-Correlation-Id: req-xyz-123
   │
   ▼
┌──────────────────────────────────────────────────────┐
│ 2. INFRASTRUCTURE: CorrelationIdFilter               │
│    - Extract/generate correlationId                  │
│    - Store in MDC for logging                        │
│    - Continue filter chain                           │
└────────────────────┬─────────────────────────────────┘
                     ▼
┌──────────────────────────────────────────────────────┐
│ 3. INFRASTRUCTURE: EducationController               │
│    @GetMapping("/{id}")                              │
│    - Extract path variable: id=3012                  │
│    - Extract header: Accept-Language=en_EN           │
│    - Convert to domain: Language.fromCode("en_EN")   │
│    - Validate parameters                             │
└────────────────────┬─────────────────────────────────┘
                     │ getEducationUseCase.execute(3012, EN_EN)
                     ▼
┌──────────────────────────────────────────────────────┐
│ 4. APPLICATION: GetEducationInteractor               │
│    - log.info("Executing GetEducation use case...")  │
│    - Call domain port: repository.findById(...)      │
└────────────────────┬─────────────────────────────────┘
                     │ findEducationById(3012, EN_EN)
                     ▼
┌──────────────────────────────────────────────────────┐
│ 5. INFRASTRUCTURE: JsonEducationQueryAdapter         │
│    - Determine file: "data/en/education.json"        │
│    - Call reader.read(path, TypeReference)           │
└────────────────────┬─────────────────────────────────┘
                     │ read("data/en/education.json")
                     ▼
┌──────────────────────────────────────────────────────┐
│ 6. INFRASTRUCTURE: ClasspathJsonReader               │
│    - Load resource from classpath                    │
│    - Parse JSON with Jackson ObjectMapper            │
│    - Return List<EducationItem>                      │
└────────────────────┬─────────────────────────────────┘
                     │ List<EducationItem>
                     ▼
┌──────────────────────────────────────────────────────┐
│ 7. INFRASTRUCTURE: JsonEducationQueryAdapter         │
│    - Filter list by id == 3012                       │
│    - Return Optional<EducationItem>                  │
└────────────────────┬─────────────────────────────────┘
                     │ Optional<EducationItem>
                     ▼
┌──────────────────────────────────────────────────────┐
│ 8. APPLICATION: GetEducationInteractor               │
│    - Check if Optional.isPresent()                   │
│    - If empty: throw ResourceNotFoundException       │
│    - If present: return EducationItem                │
│    - log.debug("Education retrieved: ...")           │
└────────────────────┬─────────────────────────────────┘
                     │ EducationItem (domain entity)
                     ▼
┌──────────────────────────────────────────────────────┐
│ 9. INFRASTRUCTURE: EducationController               │
│    - Call mapper.toResponse(educationItem)           │
│    - Convert domain entity → REST DTO                │
│    - Return ResponseEntity.ok(EducationResponse)     │
└────────────────────┬─────────────────────────────────┘
                     ▼
┌──────────────────────────────────────────────────────┐
│ 10. INFRASTRUCTURE: Spring MVC                       │
│     - Serialize DTO to JSON (Jackson)                │
│     - Apply snake_case naming strategy               │
│     - Add correlation-id to response headers         │
│     - Return HTTP 200 OK                             │
└────────────────────┬─────────────────────────────────┘
                     ▼
11. CLIENT RESPONSE
   │
   │ HTTP/1.1 200 OK
   │ Content-Type: application/json
   │ X-Correlation-Id: req-xyz-123
   │
   │ {
   │   "id": 3012,
   │   "title": "Bachelor of Computer Science",
   │   "institution": "University of Madrid",
   │   "location": "Madrid, Spain",
   │   "start_date": "2008-09-01",
   │   "end_date": "2012-06-30",
   │   "details": "Focused on software engineering..."
   │ }
```

---

## Exception Handling Strategy

### Exception Hierarchy

```
RuntimeException
│
├─ DomainException (abstract)
│  ├─ ResourceNotFoundException
│  ├─ InvalidLanguageException
│  └─ InvalidAggregateStateException
│
├─ ApplicationException (abstract)
│  └─ UseCaseExecutionException
│
└─ InfrastructureException (abstract)
   ├─ JsonReadException
   └─ DataSourceException
```

### Exception Flow

```
1. DOMAIN throws DomainException
   │
   ▼
2. APPLICATION catches or propagates
   │ - Logs business context
   │ - May wrap in ApplicationException
   │ - Propagates to infrastructure
   ▼
3. INFRASTRUCTURE: GlobalExceptionHandler
   │ @ExceptionHandler methods
   │
   ├─ ResourceNotFoundException → HTTP 404
   ├─ InvalidLanguageException → HTTP 400
   ├─ ApplicationException → HTTP 500
   └─ Exception (catch-all) → HTTP 500
   │
   ▼
4. ErrorResponse DTO
   {
     "timestamp": "2026-02-06T10:30:00",
     "status": 404,
     "error": "Not Found",
     "message": "Education with id '3012' not found",
     "path": "/api/v1/education/3012"
   }
```

### Exception Handling by Layer

| Layer           | Throws                    | Catches           | Responsibility                     |
|-----------------|---------------------------|-------------------|------------------------------------|
| Domain          | DomainException           | Never             | Business rule violations           |
| Application     | ApplicationException      | DomainException?  | Use case execution failures        |
| Infrastructure  | InfrastructureException   | All exceptions    | Technical failures, HTTP responses |

---

## Data Flow

### Domain Entity → REST Response DTO

```
┌───────────────────────────────────────────────────────────┐
│ DOMAIN ENTITY (Internal representation)                   │
│                                                           │
│ public record EducationItem(                              │
│     int id,                                               │
│     String title,                                         │
│     String institution,                                   │
│     String location,                                      │
│     LocalDate startDate,    // Java type                  │
│     LocalDate endDate,      // Java type                  │
│     String details                                        │
│ ) {}                                                      │
└─────────────────────────┬─────────────────────────────────┘
                          │
                          │ MapStruct mapper.toResponse()
                          ▼
┌───────────────────────────────────────────────────────────┐
│ REST RESPONSE DTO (External representation)               │
│                                                           │
│ public record EducationResponse(                          │
│     int id,                                               │
│     String title,                                         │
│     String institution,                                   │
│     String location,                                      │
│     @JsonFormat String start_date, // snake_case          │
│     @JsonFormat String end_date,   // snake_case          │
│     String details                                        │
│ ) {}                                                      │
└─────────────────────────┬─────────────────────────────────┘
                          │
                          │ Jackson serialization
                          ▼
┌───────────────────────────────────────────────────────────┐
│ JSON RESPONSE (Wire format)                               │
│                                                           │
│ {                                                         │
│   "id": 3012,                                             │
│   "title": "Bachelor of Computer Science",                │
│   "institution": "University of Madrid",                  │
│   "location": "Madrid, Spain",                            │
│   "start_date": "2008-09-01",                             │
│   "end_date": "2012-06-30",                               │
│   "details": "Focused on software engineering..."         │
│ }                                                         │
└───────────────────────────────────────────────────────────┘
```

### Mapping Strategy

- **MapStruct**: Compile-time code generation for type-safe mapping
- **No manual mapping**: Reduces boilerplate and errors
- **Convention over configuration**: Field names match when possible
- **Custom mappings**: Only when transformation logic is needed

---

## Technology Boundaries

### What Each Layer Can Use

#### Domain Layer
✅ **Allowed**:
- Pure Java (JDK 21)
- Java Records
- Java Enums
- Java Interfaces
- Java Exceptions

❌ **Forbidden**:
- Spring Framework (`@Component`, `@Service`, etc.)
- Jackson (`@JsonProperty`, `ObjectMapper`)
- Lombok (use sparingly, prefer Records)
- Any database libraries
- Any HTTP libraries

#### Application Layer
✅ **Allowed**:
- Everything from Domain
- SLF4J (logging abstraction)
- `@Service` annotation (only for DI)
- Java functional interfaces

❌ **Forbidden**:
- Spring MVC (`@RestController`, `@RequestMapping`)
- Jackson
- Database-specific code
- HTTP-specific code (servlets, requests, responses)

#### Infrastructure Layer
✅ **Allowed**:
- Everything (it's the outer layer)
- Spring Boot (all modules)
- Jackson
- MapStruct
- Logback
- Any external library

❌ **Forbidden**:
- Nothing (can use any technology)

---

## Design Decisions

### Why Java Records for Entities?

**Decision**: Use Java Records for all domain entities and value objects.

**Rationale**:
- **Immutability by default**: Records are implicitly final and all fields are final
- **Less boilerplate**: No need for constructors, getters, equals(), hashCode(), toString()
- **Clear intent**: A record signals it's a data carrier, not a behavior-rich class
- **Null safety**: Explicit constructor validation is centralized

**Trade-off**: Cannot use inheritance (records are final), but DDD prefers composition over inheritance.

### Why JSON Files Instead of Database?

**Decision**: Use JSON files as data source (read-only).

**Rationale**:
- **Make it Simple**: Following the principle of simplicity, I don't see the need to use a database for this use case. The application serves static CV data that rarely changes, and adding database complexity would be over-engineering.
- **Portfolio project**: Demonstrates architecture without unnecessary infrastructure complexity
- **Zero setup**: No database installation, configuration, or management required
- **Multi-language support**: Easy to manage Spanish and English data in separate files
- **Immutable data**: CV information is essentially read-only and doesn't require transactional integrity
- **Fast reads**: No network latency, perfect for small datasets
- **Version control**: Data changes are tracked in Git alongside code
- **Deployment simplicity**: Single JAR file contains everything needed to run

**Trade-off**: Not suitable if write operations or complex queries are needed in the future. However, the Hexagonal Architecture allows migrating to a database (PostgreSQL, MongoDB) by simply implementing new adapters without touching domain or application layers.

### Why Separate Use Case Interfaces and Implementations?

**Decision**: Define use cases as interfaces (`GetEducationUseCase`) with separate implementations (`GetEducationInteractor`).

**Rationale**:
- **Ports & Adapters**: Use case interface is a primary port
- **Testability**: Controllers can mock use case interfaces easily
- **SRP**: Interface defines "what", implementation defines "how"
- **Future flexibility**: Multiple implementations possible (e.g., cached, async)

**Trade-off**: More files, but clearer contracts.

### Why MapStruct Over Manual Mapping?

**Decision**: Use MapStruct for Domain ↔ DTO mapping.

**Rationale**:
- **Compile-time safety**: Errors detected at build time, not runtime
- **Performance**: No reflection, just plain method calls
- **Maintainability**: Changes to domain or DTOs caught by compiler
- **Less code**: Reduces boilerplate mapping methods

**Trade-off**: Additional build-time dependency, but worth it for safety.

### Why @Service in Application Layer?

**Decision**: Allow `@Service` annotation in Interactors despite "no framework" rule.

**Rationale**:
- **Pragmatic DDD**: Dependency injection is essential for real applications
- **Minimal intrusion**: `@Service` doesn't pollute business logic
- **Alternative is worse**: Manual bean configuration is more coupling
- **Compromise**: Only annotation allowed in application layer

**Trade-off**: Slight framework coupling, but keeps application testable and practical.

---

## Summary

The **jb2dev-cv-api** architecture is built on solid foundations:

- **Hexagonal Architecture** ensures technology independence and testability
- **Domain-Driven Design** keeps business logic clean and expressive
- **Strict layer separation** makes the codebase maintainable and scalable
- **Ports and Adapters** provide flexibility to swap implementations
- **Clear dependency rules** prevent architectural erosion
- **Simplicity first** - using JSON files instead of database complexity follows the "Make it Simple" principle

This architecture is **production-ready** and the clean separation of concerns allows evolving any infrastructure component without touching the core business logic in the domain layer.

---

**Last update**: 2026-02-06

**Related Documentation**:
- [README.md](../README.md) - Project overview and setup
- [Logging System](logging-system.md) - Detailed logging documentation

