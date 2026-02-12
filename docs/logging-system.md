# Logging System - jb2dev-cv-api

## Table of Contents

1. [Introduction](#introduction)
2. [Logging System Architecture](#logging-system-architecture)
3. [Main Components](#main-components)
4. [Configuration](#configuration)
5. [Log Levels](#log-levels)
6. [Request Correlation](#request-correlation)
7. [Logging by Architecture Layer](#logging-by-architecture-layer)
8. [Runtime Logging Management](#runtime-logging-management)
9. [Output Formats](#output-formats)
10. [Implemented Best Practices](#implemented-best-practices)
11. [Integration with Observability Systems](#integration-with-observability-systems)
12. [Usage Examples](#usage-examples)

---

## Introduction

The jb2dev-cv-api logging system is designed to provide complete traceability, observability, and facilitate behavior analysis in development and production environments. The implementation strictly adheres to Hexagonal Architecture and Domain-Driven Design (DDD) principles, ensuring that each layer only logs information relevant to its level of abstraction.

### System Objectives

- **Complete traceability**: Correlation of all operations related to an HTTP request through unique identifiers.
- **Observability**: Structured information to facilitate analysis and metrics creation.
- **Separation of concerns**: Strict adherence to hexagonal architecture layers.
- **Flexibility**: Dynamic configuration of log levels without requiring application restart.
- **Production readiness**: Output formats optimized for log aggregators (ELK, Grafana Loki, Splunk).

---

## Logging System Architecture

### Layer Diagram

```
┌─────────────────────────────────────────────────────────┐
│ DOMAIN LAYER                                            │
│ - No technical logging (pure)                           │
│ - Domain events if necessary                            │
└─────────────────────────────────────────────────────────┘
                         ▲
                         │
┌─────────────────────────────────────────────────────────┐
│ APPLICATION LAYER                                       │
│ - Business event logging (INFO)                         │
│ - Object parameter logging (DEBUG)                      │
│ - SLF4J as abstraction                                  │
└─────────────────────────────────────────────────────────┘
                         ▲
                         │
┌─────────────────────────────────────────────────────────┐
│ INFRASTRUCTURE LAYER                                    │
│ - Complete technical logging                            │
│ - Logback configuration                                 │
│ - HTTP filters, MDC, formats                            │
│ - Appenders (Console, Async)                            │
└─────────────────────────────────────────────────────────┘
```

### Architectural Principles

1. **Domain Immutability**: The domain layer contains no dependencies on logging frameworks nor logs technical information.

2. **Application Abstraction**: The application layer uses SLF4J as abstraction, allowing implementation changes without modifying business code.

3. **Infrastructure Implementation**: All technical configuration, formats, appenders, and filters reside in the infrastructure layer.

---

## Main Components

### 1. CorrelationIdFilter

**Location**: `com.jb2dev.cv.infrastructure.logging.correlation.CorrelationIdFilter`

**Responsibility**: Intercept all HTTP requests and manage correlation identifier.

**Operation**:
1. Intercepts each incoming HTTP request.
2. Extracts `X-Correlation-Id` header if present.
3. If not present, generates a new UUID with `req-` prefix.
4. Stores ID in MDC (Mapped Diagnostic Context).
5. Propagates ID in HTTP response via header.
6. Cleans MDC after request completion (guaranteed with `finally`).

**Configuration**:
```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter
```

### 2. CorrelationIdHolder

**Location**: `com.jb2dev.cv.infrastructure.logging.correlation.CorrelationIdHolder`

**Responsibility**: Thread-safe wrapper over SLF4J MDC for correlation ID management.

**Methods**:
- `set(String correlationId)`: Stores ID in current thread context.
- `get()`: Retrieves ID from current context.
- `clear()`: Clears ID from context.

### 3. CorrelationIdGenerator

**Location**: `com.jb2dev.cv.infrastructure.logging.correlation.CorrelationIdGenerator`

**Responsibility**: Generate unique identifiers for correlation.

**Implementation**:
```java
public String generate() {
    return "req-" + UUID.randomUUID().toString();
}
```

### 4. Logback Configuration

**Location**: `src/main/resources/logback-spring.xml`

**Responsibility**: Central logging system configuration.

**Supported profiles**:
- `dev`, `default`: Human-readable logs for development.
- `prod`: JSON-formatted logs for production.

---

## Configuration

### logback-spring.xml File

#### General Structure

```xml
<configuration>
    <!-- Include Spring Boot defaults -->
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    
    <!-- Global properties -->
    <property name="APP_NAME" value="jb2dev-cv-api"/>
    <property name="LOG_PATTERN" value="..."/>
    
    <!-- Configuration by profile -->
    <springProfile name="dev,default,!prod">
        <!-- Development configuration -->
    </springProfile>
    
    <springProfile name="prod">
        <!-- Production configuration -->
    </springProfile>
</configuration>
```

#### Log Pattern (Development)

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{correlationId:-NO-CORRELATION-ID}] %logger{36} - %msg%n
```

**Components**:
- `%d{yyyy-MM-dd HH:mm:ss.SSS}`: Timestamp with milliseconds.
- `[%thread]`: Thread name.
- `%-5level`: Log level (ERROR, WARN, INFO, DEBUG, TRACE).
- `[%X{correlationId:-NO-CORRELATION-ID}]`: Correlation ID from MDC.
- `%logger{36}`: Logger name (maximum 36 characters).
- `%msg`: Log message.
- `%n`: New line.

#### Production Encoder

In production, `LogstashEncoder` from `logstash-logback-encoder` library is used:

```xml
<encoder class="net.logstash.logback.encoder.LogstashEncoder">
    <includeContext>true</includeContext>
    <includeMdc>true</includeMdc>
</encoder>
```

This encoder generates JSON-formatted logs with all structured fields.

### Application Configuration (application.yml)

```yaml
logging:
  level:
    root: INFO
    com.jb2dev.cv: DEBUG
    org.springframework.web: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{correlationId:-NO-CORRELATION-ID}] %logger{36} - %msg%n"
```

### Maven Dependencies

```xml
<!-- SLF4J API (provided by spring-boot-starter-logging) -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
</dependency>

<!-- Logback (provided by spring-boot-starter-logging) -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
</dependency>

<!-- Logstash Encoder for JSON logging -->
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>

<!-- Lombok for @Slf4j annotation -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

---

## Log Levels

### Level Hierarchy

| Level | Code | Description | Application Usage |
|-------|------|-------------|-------------------|
| **TRACE** | 5000 | Ultra-detailed information | JSON file I/O, parsing details |
| **DEBUG** | 10000 | Debug information | Object parameters, query results |
| **INFO** | 20000 | Important events | Use case start, main operations |
| **WARN** | 30000 | Warnings | Resources not found, failed validations |
| **ERROR** | 40000 | Errors | Critical failures, unexpected exceptions |

### Level Propagation

When a level is configured, all logs from that level and higher are shown:

```
TRACE → DEBUG → INFO → WARN → ERROR
  ↓       ↓       ↓      ↓       ↓
 All   -TRACE  -DEBUG -INFO  -WARN
```

### Package-Level Configuration

Configuration allows package-level granularity:

```yaml
logging:
  level:
    root: INFO                              # Default level
    com.jb2dev.cv: DEBUG                    # Entire application
    com.jb2dev.cv.application: INFO         # Application layer only
    com.jb2dev.cv.infrastructure: DEBUG     # Infrastructure layer only
    org.springframework: WARN               # Spring framework
    org.hibernate: WARN                     # Hibernate framework
```

---

## Request Correlation

### Correlation Flow

```
1. Incoming HTTP request
        ↓
2. CorrelationIdFilter intercepts
        ↓
3. X-Correlation-Id header exists?
        ↓
   Yes → Use that ID
   No → Generate new UUID
        ↓
4. Store in MDC
        ↓
5. Process request (ID available in all logs)
        ↓
6. Add ID to response header
        ↓
7. Clean MDC (finally)
        ↓
8. Outgoing HTTP response
```

### Correlation Example

**Client request**:
```http
GET /api/v1/education/101 HTTP/1.1
Host: localhost:8080
X-Correlation-Id: client-request-12345
```

**Server logs** (all share the same ID):
```
2026-02-06 12:39:10.916 [http-nio-8080-exec-1] DEBUG [client-request-12345] c.j.c.i.l.c.CorrelationIdFilter - Request started: GET /api/v1/education/101
2026-02-06 12:39:10.956 [http-nio-8080-exec-1] INFO  [client-request-12345] c.j.c.a.e.i.GetEducationInteractor - Executing GetEducation use case: id=101, language=ES_ES
2026-02-06 12:39:10.964 [http-nio-8080-exec-1] DEBUG [client-request-12345] c.j.c.a.e.i.GetEducationInteractor - Education retrieved: id=101, title=..., institution=...
2026-02-06 12:39:11.089 [http-nio-8080-exec-1] DEBUG [client-request-12345] c.j.c.i.l.c.CorrelationIdFilter - Request completed: GET /api/v1/education/101 - Status: 200
```

**Client response**:
```http
HTTP/1.1 200 OK
X-Correlation-Id: client-request-12345
Content-Type: application/json
...
```

### Correlation Benefits

1. **End-to-end traceability**: Complete tracking of a request from input to output.
2. **Simplified debugging**: Filter logs by correlation ID to analyze a specific request.
3. **Distributed systems**: Ready to propagate ID between microservices.
4. **Log analysis**: Efficient queries in aggregation systems (grep, Elasticsearch, Loki).

---

## Logging by Architecture Layer

### Domain Layer

**Principle**: No technical logging.

**Rationale**: 
- Domain must be pure and infrastructure-agnostic.
- Contains no dependencies on SLF4J or any logging framework.
- Business rules should not concern themselves with technical aspects.

**Domain exceptions**: Logged in upper layers (application/infrastructure) when caught.

### Application Layer

**Main levels**: INFO, DEBUG

**Components**: Interactors (use cases)

**Logging strategy**:

1. **INFO level**: Log use case start.
   ```java
   log.info("Executing GetEducation use case: id={}, language={}", id, language);
   ```

2. **DEBUG level**: Log retrieved object parameters.
   ```java
   log.debug("Education retrieved: id={}, title={}, institution={}, location={}, startDate={}, endDate={}", 
       result.id(), result.title(), result.institution(), result.location(), 
       result.startDate(), result.endDate());
   ```

3. **WARN level**: Resource not found before throwing exception.
   ```java
   log.warn("Education not found: id={}, language={}", id, language);
   ```

**Log-Execute-Log Pattern**:
```java
@Override
public EducationItem execute(int id, Language language) {
    log.info("Executing GetEducation use case: id={}, language={}", id, language);
    
    EducationItem result = repository.findById(id, language)
        .orElseThrow(() -> {
            log.warn("Education not found: id={}, language={}", id, language);
            return new ResourceNotFoundException("Education", String.valueOf(id));
        });
    
    log.debug("Education retrieved: id={}, title={}, institution={}, ...", 
        result.id(), result.title(), result.institution(), ...);
    
    return result;
}
```

### Infrastructure Layer

**Main levels**: DEBUG, TRACE

**Components**: Adapters, Controllers, Filters, Exception Handlers

**Logging strategy**:

1. **HTTP Filters** (DEBUG):
   ```java
   log.debug("Request started: {} {}", request.getMethod(), request.getRequestURI());
   log.debug("Request completed: {} {} - Status: {}", method, uri, response.getStatus());
   ```

2. **JSON Adapters** (DEBUG):
   ```java
   log.debug("Reading education items from JSON file: {}", filePath);
   log.debug("Loaded {} education items", items.size());
   ```

3. **JSON Reader** (TRACE):
   ```java
   log.trace("Attempting to read JSON resource: {} as {}", resourcePath, type.getSimpleName());
   log.trace("Successfully read JSON resource: {}", resourcePath);
   ```

4. **Exception Handlers** (WARN, ERROR):
   ```java
   log.warn("Resource not found: {} with id {}", exception.getResourceName(), exception.getResourceId());
   log.error("Unexpected exception occurred", exception);
   ```

---

## Runtime Logging Management

### Spring Boot Actuator

The application exposes Actuator endpoints for dynamic log level management.

**Endpoint**: `/actuator/loggers`

**Configuration** (application.yml):
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,loggers
  endpoint:
    loggers:
      enabled: true
```

### Query Current Levels

**Request**:
```http
GET /actuator/loggers/com.jb2dev.cv HTTP/1.1
```

**Response**:
```json
{
  "configuredLevel": "DEBUG",
  "effectiveLevel": "DEBUG"
}
```

### Change Level at Runtime

**Request**:
```http
POST /actuator/loggers/com.jb2dev.cv HTTP/1.1
Content-Type: application/json

{
  "configuredLevel": "INFO"
}
```

**Effects**:
- DEBUG logs stop appearing immediately.
- No application restart required.
- Change affects only the specified level and its children.

### Change Level for Specific Package

```http
POST /actuator/loggers/com.jb2dev.cv.application.education HTTP/1.1
Content-Type: application/json

{
  "configuredLevel": "TRACE"
}
```

This command activates TRACE only for education interactors.

---

## Output Formats

### Development Format (Human-Readable)

**Profile**: `dev`, `default`

**Appender**: ConsoleAppender

**Pattern**:
```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{correlationId:-NO-CORRELATION-ID}] %logger{36} - %msg%n
```

**Example**:
```
2026-02-06 12:39:28.663 [http-nio-8080-exec-3] INFO  [req-d17fe55b-8777-416b-a1ea-742b85bcae80] c.j.c.a.e.i.GetEducationInteractor - Executing GetEducation use case: id=8792, language=ES_ES
```

**Characteristics**:
- Human-readable timestamp.
- Correlation ID visible between brackets.
- Logger abbreviated to 36 characters.
- Ideal for local development and debugging.

### Production Format (JSON)

**Profile**: `prod`

**Appender**: AsyncAppender with LogstashEncoder

**Configuration**:
```xml
<appender name="CONSOLE_JSON" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
</appender>

<appender name="ASYNC_CONSOLE" class="ch.qos.logback.classic.AsyncAppender">
    <queueSize>512</queueSize>
    <discardingThreshold>0</discardingThreshold>
    <appender-ref ref="CONSOLE_JSON"/>
</appender>
```

**Example**:
```json
{
  "@timestamp": "2026-02-06T12:39:28.663Z",
  "@version": "1",
  "message": "Executing GetEducation use case: id=8792, language=ES_ES",
  "logger_name": "com.jb2dev.cv.application.education.impl.GetEducationInteractor",
  "thread_name": "http-nio-8080-exec-3",
  "level": "INFO",
  "level_value": 20000,
  "correlationId": "req-d17fe55b-8777-416b-a1ea-742b85bcae80"
}
```

**Characteristics**:
- Parseable JSON format.
- Timestamp in ISO-8601 (UTC).
- All fields structured.
- Correlation ID as JSON field.
- Optimized for ingestion into ELK, Loki, Splunk.

### Async Appender

**Purpose**: Prevent logging from blocking application threads.

**Configuration**:
```xml
<appender name="ASYNC_CONSOLE" class="ch.qos.logback.classic.AsyncAppender">
    <queueSize>512</queueSize>
    <discardingThreshold>0</discardingThreshold>
    <appender-ref ref="CONSOLE_JSON"/>
</appender>
```

**Parameters**:
- `queueSize`: Buffer of 512 log events.
- `discardingThreshold`: 0 = do not discard logs (even under high load).

**Benefits**:
- Better performance in production.
- Application threads don't wait for logging I/O.
- Log processing in separate thread.

---

## Implemented Best Practices

### 1. Use of Placeholders

**Correct**:
```java
log.info("Executing GetEducation use case: id={}, language={}", id, language);
```

**Incorrect** (avoided):
```java
log.info("Executing GetEducation use case: id=" + id + ", language=" + language);
```

**Reason**: Placeholders avoid unnecessary string concatenation when the log level is disabled.

### 2. Exception Logging

**Correct**:
```java
log.error("Failed to read JSON resource: {}", resourcePath, exception);
```

**Benefits**:
- Automatically includes complete stacktrace.
- Consistent format.

### 3. Complete Context

All logs include relevant contextual information:

```java
log.debug("Education retrieved: id={}, title={}, institution={}, location={}, startDate={}, endDate={}", 
    result.id(), result.title(), result.institution(), result.location(), 
    result.startDate(), result.endDate());
```

### 4. No Sensitive Data

The system is designed NOT to log:
- Passwords
- Authentication tokens
- Sensitive personal information (credit card numbers, etc.)

### 5. Appropriate Levels

Each level is used consistently:

| Level | When to Use |
|-------|-------------|
| TRACE | I/O details, internal parsing |
| DEBUG | Object parameters, query results |
| INFO | Start of important operations |
| WARN | Recoverable anomalous situations |
| ERROR | Critical failures, unexpected exceptions |

### 6. Logger per Class

Use of `@Slf4j` from Lombok to create a logger per class:

```java
@Slf4j
@Service
public class GetEducationInteractor implements GetEducationUseCase {
    // log is automatically available
}
```

This ensures:
- Appropriate logger per class.
- Facilitates filtering by package or class.
- Cleaner code.

---

## Integration with Observability Systems

### Elasticsearch + Logstash + Kibana (ELK)

**Integration pipeline**:

1. **Application** → Generates JSON logs (stdout)
2. **Filebeat/Fluentd** → Captures logs from stdout
3. **Logstash** → Parses and enriches logs
4. **Elasticsearch** → Indexes and stores logs
5. **Kibana** → Visualization and queries

**Example query in Kibana**:
```
correlationId:"req-d17fe55b-8777-416b-a1ea-742b85bcae80"
```

### Grafana Loki

**Integration pipeline**:

1. **Application** → Generates JSON logs (stdout)
2. **Promtail** → Captures logs from stdout
3. **Loki** → Stores indexed logs
4. **Grafana** → Visualization with LogQL

**Example query in LogQL**:
```logql
{app="jb2dev-cv-api"} |= "req-d17fe55b-8777-416b-a1ea-742b85bcae80"
```

**Advanced query** (average duration):
```logql
{app="jb2dev-cv-api"} 
  | json 
  | logger_name="c.j.c.a.e.i.GetEducationInteractor" 
  | level="INFO"
```

### Prometheus (Metrics)

Although this document focuses on logging, the system is prepared for Prometheus integration:

**Actuator exposes**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus
```

**Endpoint**: `/actuator/prometheus`

**Future implementation**: Micrometer for custom metrics based on logs.

---

## Usage Examples

### Use Case 1: Debugging Specific Request

**Scenario**: A user reports an error on the education request with ID 101.

**Step 1**: Identify the correlation ID of the error (from logs or response header).

**Step 2**: Filter logs by correlation ID.

```bash
# In log file
grep "req-abc-123" application.log

# In Kibana
correlationId:"req-abc-123"

# In Loki
{app="jb2dev-cv-api"} |= "req-abc-123"
```

**Result**: All logs related to that specific request.

### Use Case 2: Performance Analysis

**Scenario**: Detect slow operations in the education module.

**Step 1**: Activate DEBUG level for specific package.

```bash
curl -X POST http://localhost:8080/actuator/loggers/com.jb2dev.cv.application.education \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```

**Step 2**: Observe DEBUG logs with complete parameters.

**Step 3**: Analyze in Grafana/Kibana.

**Step 4**: Restore original level.

```bash
curl -X POST http://localhost:8080/actuator/loggers/com.jb2dev.cv.application.education \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "INFO"}'
```

### Use Case 3: Production Troubleshooting

**Scenario**: Intermittent error in production, JSON logs.

**Step 1**: Search for errors in the last 5 minutes.

```logql
{app="jb2dev-cv-api"} 
  | json 
  | level="ERROR" 
  | __timestamp__ > now() - 5m
```

**Step 2**: Identify error correlation ID.

**Step 3**: Get complete context with that ID.

```logql
{app="jb2dev-cv-api"} 
  | json 
  | correlationId="req-xyz-789"
```

**Step 4**: Analyze complete event sequence.

### Use Case 4: Access Audit

**Scenario**: Determine who accessed a specific resource.

**Query in Elasticsearch**:
```json
{
  "query": {
    "bool": {
      "must": [
        {"match": {"message": "Executing GetEducation use case"}},
        {"match": {"message": "id=101"}},
        {"range": {"@timestamp": {"gte": "2026-02-01", "lte": "2026-02-28"}}}
      ]
    }
  }
}
```

**Result**: List of all accesses to education resource ID 101 in February 2026.

---

## Conclusion

The logging system implemented in jb2dev-cv-api provides a solid foundation for observability, debugging, and behavior analysis. The architecture respects hexagonal design and DDD principles, ensuring separation of concerns and ease of maintenance.

### Key Points

- **End-to-end correlation** through automatically propagated correlation IDs.
- **Flexible configuration** with Spring profiles (dev/prod) and runtime management via Actuator.
- **Optimized formats** for development (human-readable) and production (parseable JSON).
- **Architecture respect** with appropriate logging in each layer.
- **Production ready** with AsyncAppender and integration with aggregation systems.

### Additional Resources

- Logback configuration: `src/main/resources/logback-spring.xml`
- Test configuration: `src/test/resources/logback-test.xml`
- Correlation components: `com.jb2dev.cv.infrastructure.logging.correlation.*`
- Actuator loggers: `http://localhost:8080/actuator/loggers`

---

**Document**: Logging System - jb2dev-cv-api  
**Version**: 1.0  
**Date**: 2026-02-06  
**Author**: JB2DEV
