# Reusable Workflow: `_reusable-unit-tests`

**Location:** `.github/workflows/_reusable-unit-tests.yml`

---

## Purpose

Runs all unit tests in the `domain` and `application` packages and publishes
the results to the GitHub Checks tab. This job has no dependency on Spring and
is expected to complete in under 30 seconds.

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
3. Executes `mvn test -P unit-tests -B`.
4. Publishes JUnit XML results via `dorny/test-reporter`.

---

## Test Scope

The `unit-tests` Maven profile configures Surefire to include:

```
**/domain/**/*Test.java
**/application/**/*Test.java
```

These tests cover:

- Domain entities and value objects (pure Java, no frameworks).
- Application interactors (mocked ports via Mockito, no Spring context).

No `@SpringBootTest`, no file system access, no HTTP calls.

---

## Permissions

```yaml
permissions:
  checks: write
  contents: read
```

`checks: write` is declared at the workflow level so that `dorny/test-reporter`
can post results to the pull request Checks tab.

Permissions in a reusable workflow are **not inherited from the caller**. They must
be declared in the reusable workflow itself. If omitted, `dorny/test-reporter`
silently fails without publishing any results.

---

## Design Decisions

### Why `if: always()` on the publish step?

When tests fail, the `run` step exits with a non-zero code and subsequent steps
are skipped by default. Without `if: always()`, the test reporter would never run
on a failing test suite — which is precisely the moment it is most useful.

`if: always()` overrides the default skip behaviour and runs the step regardless
of whether previous steps succeeded or failed.

### Why publish results in the reusable workflow and not in the caller?

Test results are the direct output of this workflow's job. Keeping the publish step
here means the caller has no knowledge of how results are reported. The caller only
knows whether the job passed or failed. This is separation of concerns applied to
CI configuration.

If the reporting tool changes (e.g., from `dorny/test-reporter` to a different
action), only this file needs to change. All callers remain unaffected.

---

## Caller Example

```yaml
jobs:
  unit-tests:
    needs: build
    uses: ./.github/workflows/_reusable-unit-tests.yml
```

---

## Related Files

| File                                                  | Description                          |
|-------------------------------------------------------|--------------------------------------|
| `.github/workflows/ci.yml`                            | Primary caller of this workflow      |
| `.github/actions/setup-java-maven/action.yml`         | Composite action used in this job    |
| `pom.xml`                                             | Defines the `unit-tests` profile     |

---

## Related Documentation

| Document                                              | Description                          |
|-------------------------------------------------------|--------------------------------------|
| `docs/devops/workflows/ci.md`                         | Caller workflow documentation        |
| `docs/devops/workflows/_reusable-build.md`            | Upstream dependency                  |
