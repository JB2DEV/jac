# Workflow: CI

**Location:** `.github/workflows/ci.yml`

---

## Purpose

This workflow validates that every change pushed to `develop`, or targeting `develop` or `main`
via pull request, compiles correctly and passes all tests before being merged.

It is the foundational safety net of the project. All other workflows build on top of the
guarantee that this one provides.

---

## Trigger

| Event          | Branch          | Behaviour                                    |
|----------------|-----------------|----------------------------------------------|
| `push`         | `develop`       | Runs on every direct commit to `develop`     |
| `pull_request` | `develop`       | Runs on every PR targeting `develop`         |
| `pull_request` | `main`          | Runs on every PR targeting `main`            |

`main` is not included in the `push` trigger because commits reach `main` only through a
pull request from `develop`. The PR trigger is sufficient.

---

## Job Map

```
build
  └──► unit-tests
             └──► integration-tests
```

Each job depends on the previous one passing. If `build` fails, neither test job runs.
If `unit-tests` fails, `integration-tests` does not run.

This order mirrors the speed and cost of each stage:

| Job                  | Spring Context | Typical Duration | Failure Cost |
|----------------------|----------------|------------------|--------------|
| `build`              | No             | ~30s             | Low          |
| `unit-tests`         | No             | ~20s             | Low          |
| `integration-tests`  | Yes            | ~60–90s          | High         |

Running the cheapest checks first means a broken compilation or a failing unit test is caught
within 30–60 seconds, without wasting runner minutes on starting a full Spring context.

---

## Test Separation Strategy: Package-Based

Tests in this project are separated by the package they live in, not by annotations or
filename conventions. No code changes are required to classify a test — its location
in the source tree is the classification.

```
src/test/java/com/jb2dev/cv/
├── domain/          →  unit tests        (no Spring, no Mockito for infra)
├── application/     →  unit tests        (Mockito for ports, no Spring)
└── infrastructure/  →  integration tests (@SpringBootTest, full context)
```

This is enforced in `pom.xml` via two Maven profiles that each configure the
Surefire plugin with explicit `<includes>` patterns:

```
mvn test -P unit-tests         →  runs domain/** + application/**
mvn test -P integration-tests  →  runs infrastructure/**
mvn test                       →  runs everything (local development)
```

### Why package-based instead of `@Tag` or naming conventions?

| Strategy            | Code changes required | Relies on            | Risk                                   |
|---------------------|-----------------------|----------------------|----------------------------------------|
| `@Tag`              | Yes — every class     | Developer discipline | Forgetting to tag a new test           |
| Naming (`*IT.java`) | Yes — rename files    | Filename convention  | Inconsistency in file naming           |
| Package-based       | None                  | Existing architecture| Misplacing a test in the wrong package |

The package-based strategy has zero friction: the project's architectural layers already
enforce where a test should live. A `@SpringBootTest` in the `domain` package would be an
architectural violation regardless of CI, so the CI strategy and the architecture align
naturally.

### The one constraint this introduces

A test must live in the package that matches its nature. A unit test placed in
`infrastructure/` will be treated as an integration test (slow, Spring context required).
An integration test placed in `application/` will run without a Spring context and fail
with a missing bean error.

This is not a CI problem — it is an architectural problem that the CI makes visible
immediately.

---

## Jobs

### `build`

Compiles the project using `mvn clean compile`.

The `-B` flag (batch mode) disables Maven's interactive output and ANSI colour codes,
producing clean, CI-friendly log lines.

This job does not run tests. Its sole responsibility is to verify that the source code
compiles. Separating compilation from testing ensures that a compilation error produces
a clear, distinct failure rather than a confusing test execution failure.

### `unit-tests`

Runs `mvn test -P unit-tests -B`, which activates the `unit-tests` Maven profile.

That profile configures Surefire to include only:

```
**/domain/**/*Test.java
**/application/**/*Test.java
```

These tests have no dependency on Spring, databases, or the file system. They are
fast and deterministic.

### `integration-tests`

Runs `mvn test -P integration-tests -B`, which activates the `integration-tests` Maven profile.

That profile configures Surefire to include only:

```
**/infrastructure/**/*Test.java
```

These tests start a full Spring application context (`@SpringBootTest`). They exercise
the infrastructure layer: JSON adapters, controllers, filters, and end-to-end request flows.

`needs: unit-tests` enforces that integration tests are the last line of validation.
A failing unit test indicates a domain or application logic problem that must be
fixed before verifying infrastructure wiring.

---

## Permissions

```yaml
permissions:
  checks: write
  contents: read
```

`checks: write` is required by `dorny/test-reporter` to publish test results to the
GitHub Checks tab on pull requests. Without it, the reporter step silently fails in
repositories with restricted default workflow permissions.

`contents: read` is the minimum required for `actions/checkout` to clone the repository.

Declaring permissions explicitly follows the principle of least privilege: the workflow
only requests what it actually needs.

---

## Test Results Publishing

Both test jobs use `dorny/test-reporter@v1` to publish JUnit XML results.

This action reads the Surefire XML reports from `target/surefire-reports/*.xml` and
renders them as a structured test summary directly in the GitHub Actions UI, visible in:

- The **Summary** tab of the workflow run.
- The **Checks** tab of a pull request.

The `if: always()` condition ensures the report is published even when tests fail.
Without it, a test failure would skip the publish step, which is the moment when the
report is most needed for diagnosis.

---

## Design Decisions

### Why three separate jobs instead of one?

Each job runs on its own isolated runner and appears as a distinct entry in the
GitHub Actions dashboard. This produces three practical benefits:

1. **Granular failure visibility**: A red `Integration Tests` job tells you exactly
   which layer broke without reading any logs.
2. **Enforced execution order**: `needs` creates a dependency graph that GitHub Actions
   visualises as a pipeline. This makes the CI strategy self-documenting.
3. **Foundation for future parallelism**: When the project grows, unit tests and a static
   analysis job can run in parallel while integration tests wait only for the build.

### Why does each job repeat `checkout` and `setup-java-maven`?

Each GitHub Actions job runs on a **fresh, isolated runner**. There is no shared
filesystem between jobs. The checkout and setup steps must be repeated in every job.

This is expected behaviour, not a limitation. The Maven cache in `setup-java-maven`
mitigates the cost: after the first run, dependencies are restored from cache in seconds.

### Why monolithic now, if reusable workflows are planned?

The Reusable Workflow refactor (Step 3) is a structural improvement, not a functional one.
Building the monolithic version first means:

- The pipeline can be tested end-to-end before introducing the indirection of `workflow_call`.
- If something fails, the failure is in the job logic, not in the wiring between workflows.
- The refactor in Step 3 becomes a mechanical extraction of already-working logic.

---

## pom.xml Configuration Required

Add the following `<profiles>` block to `pom.xml` before the closing `</project>` tag:

```xml
<profiles>

    <profile>
        <id>unit-tests</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <includes>
                            <include>**/domain/**/*Test.java</include>
                            <include>**/application/**/*Test.java</include>
                        </includes>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>

    <profile>
        <id>integration-tests</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <includes>
                            <include>**/infrastructure/**/*Test.java</include>
                        </includes>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>

</profiles>
```

---

## Expected Workflow Run

When the pipeline runs successfully:

```
CI
├── ✅ Build              ~30s
├── ✅ Unit Tests         ~20s   → Unit Test Results (N passed)
└── ✅ Integration Tests  ~80s   → Integration Test Results (N passed)
```

On a pull request, the Checks tab shows three independent status checks, all of which
must pass before the branch protection rule allows merging.

---

## What This Workflow Does NOT Do

- It does not measure code coverage (Step 4).
- It does not build or push a Docker image (Step 6).
- It does not perform security scanning (Step 8).
- It does not create a release (Step 7).

---

## Related Files

| File                                                 | Description                                         |
|------------------------------------------------------|-----------------------------------------------------|
| `.github/workflows/ci.yml`                           | This workflow                                       |
| `.github/actions/setup-java-maven/action.yml`        | Composite action used in every job                  |
| `pom.xml`                                            | Contains the Maven profiles for test separation     |

---

## Related Documentation

| Document                                             | Description                                         |
|------------------------------------------------------|-----------------------------------------------------|
| `docs/devops/actions/setup-java-maven.md`            | Composite action used by this workflow              |
| `docs/devops/overview.md`                            | DevOps strategy and full pipeline map               |
| `docs/devops/workflows/reusable-build.md`            | Step 3: refactor of this workflow into reusable parts |
