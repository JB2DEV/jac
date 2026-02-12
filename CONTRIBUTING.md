# Contributing to jb2dev-cv-api

Thank you for your interest in contributing to this project! This document provides guidelines and instructions for contributing.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Testing](#testing)
- [Commit Messages](#commit-messages)
- [Pull Request Process](#pull-request-process)

## Code of Conduct

This project adheres to a code of conduct that all contributors are expected to follow. Please be respectful and professional in all interactions.

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8+
- Git
- Your favorite IDE (IntelliJ IDEA recommended)

### Setup

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/jac.git
   cd jac
   ```
3. Add upstream remote:
   ```bash
   git remote add upstream https://github.com/JB2DEV/jac.git
   ```
4. Build the project:
   ```bash
   mvn clean install
   ```

## Development Workflow

1. **Create a feature branch** from `develop`:
   ```bash
   git checkout develop
   git pull upstream develop
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** following the coding standards

3. **Test your changes**:
   ```bash
   mvn clean test
   ```

4. **Commit your changes** with meaningful commit messages

5. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a Pull Request** to the `develop` branch

## Coding Standards

### Architecture

This project follows **Hexagonal Architecture** and **Domain-Driven Design**:

- **Domain Layer**: Pure business logic, no external dependencies
  - Aggregates, Entities, Value Objects
  - Domain Services
  - Domain Exceptions
  - Port Interfaces (repository, external services)

- **Application Layer**: Use cases and application services
  - Input/Output ports
  - Interactors implementing use cases
  - No direct infrastructure dependencies

- **Infrastructure Layer**: Technical implementations
  - REST Controllers (adapters)
  - Persistence implementations
  - External service clients
  - Configuration

### Java Conventions

- Use **Java 21** features where appropriate
- Follow standard Java naming conventions
- Use **Lombok** annotations to reduce boilerplate (`@Getter`, `@Builder`, etc.)
- Use **MapStruct** for entity-DTO mapping
- Keep methods small and focused (Single Responsibility Principle)
- Use meaningful variable and method names

### Package Structure

```
com.jb2dev.cv
├── domain              # Domain layer (pure business logic)
│   ├── [context]
│   │   ├── model       # Aggregates, Entities, Value Objects
│   │   └── port        # Port interfaces (repositories, services)
├── application         # Application layer (use cases)
│   ├── [context]
│   │   ├── query       # Query objects and criteria
│   │   ├── port.in     # Input ports (use case interfaces)
│   │   ├── port.out    # Output ports (repository interfaces)
│   │   └── impl        # Use case implementations (interactors)
└── infrastructure      # Infrastructure layer (technical details)
    ├── rest            # REST controllers
    │   ├── [context]
    │   │   ├── dto     # Data Transfer Objects
    │   │   └── mapper  # MapStruct mappers
    ├── persistence     # Data persistence
    └── config          # Configuration classes
```

### Documentation

- Add JavaDoc for public APIs
- Document complex business logic
- Keep README.md updated
- Update CHANGELOG.md for notable changes

## Testing

### Test Strategy

- **Domain Layer**: Unit tests with no mocks
  - Test aggregates, entities, value objects
  - Test domain services
  - Validate business rules

- **Application Layer**: Unit tests with mocked dependencies
  - Test use case logic
  - Verify port interactions
  - Use Mockito for mocking

- **Infrastructure Layer**: Integration tests
  - Test REST controllers with MockMvc
  - Test persistence implementations

### Running Tests

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn clean test jacoco:report

# View coverage report
# Open: target/site/jacoco/index.html
```

### Test Naming Convention

```java
@Test
void shouldReturnPersonalInfo_whenValidIdProvided() {
    // Given
    // When
    // Then
}
```

### Coverage Goals

- Domain layer: 90%+
- Application layer: 85%+
- Overall: 80%+

## Commit Messages

Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

### Examples

```
feat(skills): add technical skill search with filtering

Implemented search functionality for technical skills with support for:
- Name filtering
- Category filtering
- Proficiency level filtering

Closes #42
```

```
fix(education): correct date validation in education items

Fixed validation to allow ongoing education without end date.

Fixes #15
```

## Pull Request Process

1. **Update documentation** if needed (README.md, CHANGELOG.md, etc.)

2. **Ensure all tests pass**:
   ```bash
   mvn clean test
   ```

3. **Update CHANGELOG.md** under `[Unreleased]` section

4. **Create Pull Request**:
   - Use a clear, descriptive title
   - Reference related issues
   - Describe what changed and why
   - Add screenshots if UI changes

5. **Code Review**:
   - Address review comments
   - Keep the conversation professional
   - Update the PR as needed

6. **Merge**:
   - PRs are merged by maintainers after approval
   - The branch will be deleted after merge

### PR Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
Describe testing performed

## Checklist
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] Code follows project conventions
- [ ] All tests pass

## Related Issues
Closes #issue_number
```

## Questions?

If you have questions or need help, please:
- Open an issue for discussion
- Review existing documentation
- Check closed PRs for similar changes

Thank you for contributing! 🚀

