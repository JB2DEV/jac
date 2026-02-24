# Reusable Workflow: `_reusable-integration-tests`

**Location:** `.github/workflows/_reusable-integration-tests.yml`

---

## Purpose

Runs all integration tests in the `infrastructure` package and publishes the results
to the GitHub Checks tab. This job starts a full Spring application context and is
expected to complete in 60–90 seconds.

---

## Trigger

```yaml
on:
  workflow_call:
```

Internal building block. Only runs when called by another workflow.

---

## What It Does

1. Checks out the repository.
2. Runs the `setup-java-maven` composite action.
3. Executes `mvn test -P integration-tests -B`.
4. Publishes JUnit XML results via `dorny/test-reporter`.

---

## Test Scope

The `integration-tests` Maven profile configures Surefire to include:

```
**/infrastructure/**/*Test.java
```

These tests cover:

- JSON adapters (`JsonEducationQueryAdapter`, etc.) — verifies that JSON files are
  read and deserialized correctly.
- REST controllers with `@SpringBootTest(webEnvironment = RANDOM_PORT)` — full HTTP
  request/response cycle.
- Filter behaviour (e.g., `CorrelationIdFilter`) — verifies MDC and response headers.

All tests in this scope load a complete Spring application context. This is the main
reason they are separated from unit tests: context startup takes time and should not
penalise fast domain tests.

---

## Permissions

```yaml
permissions:
  checks: write
  contents: read
```

Same reasoning as `_reusable-unit-tests`. Permissions are not inherited from the
caller in reusable workflows and must be declared explicitly.

---

## Design Decisions

### Why does this job depend on `unit-tests` and not just `build`?

The dependency chain `build → unit-tests → integration-tests` encodes a prioritisation
rule: domain and application logic must be correct before verifying infrastructure wiring.

If an interactor throws an unexpected exception, the integration test that exercises
the full HTTP stack will also fail — but the error message will point to the HTTP layer,
not to the business logic. The unit test failure catches the root cause first and
produces a more actionable error.

Depending on `unit-tests` also saves runner minutes: a Spring context startup
(~5–10 seconds) is avoided when the root cause is a failing unit test.

### Why not run all tests in one job?

If all tests ran in a single job, any failure would produce a single red job with
potentially hundreds of log lines to parse. Separating unit and integration tests
into distinct jobs produces two independent signals:

- Red `Unit Tests`: domain or application logic problem.
- Red `Integration Tests`: infrastructure wiring, configuration, or adapter problem.

The separation makes the failure immediately localised without reading any logs.

---

## Caller Example

```yaml
jobs:
  integration-tests:
    needs: unit-tests
    uses: ./.github/workflows/_reusable-integration-tests.yml
```

---

## Related Files

| File                                                  | Description                          |
|-------------------------------------------------------|--------------------------------------|
| `.github/workflows/ci.yml`                            | Primary caller of this workflow      |
| `.github/actions/setup-java-maven/action.yml`         | Composite action used in this job    |
| `pom.xml`                                             | Defines the `integration-tests` profile |

---

## Related Documentation

| Document                                              | Description                          |
|-------------------------------------------------------|--------------------------------------|
| `docs/devops/workflows/ci.md`                         | Caller workflow documentation        |
| `docs/devops/workflows/_reusable-unit-tests.md`       | Upstream dependency                  |
