# Reusable Workflow: `_reusable-build`

**Location:** `.github/workflows/_reusable-build.yml`

---

## Purpose

Compiles the project and verifies the source code is syntactically valid. This is the
first gate in the CI pipeline: no test job runs until this one succeeds.

---

## Trigger

```yaml
on:
  workflow_call:
```

This workflow is not triggered by git events. It only runs when called by another
workflow using `uses:`. It is an internal building block, not an entry point.

The `_` prefix in the filename is a naming convention that signals this intent:
the file is a reusable component, not a standalone workflow.

---

## What It Does

1. Checks out the repository.
2. Runs the `setup-java-maven` composite action (Java 21 + Maven cache).
3. Executes `mvn clean compile -B`.

If compilation fails, the caller workflow stops immediately. No test job is scheduled.

---

## Design Decisions

### Why compile in a separate job?

A compilation failure and a test failure are different problems that deserve distinct
signals in the CI dashboard. A red `Build` job means the code does not compile.
A red `Unit Tests` job means the logic is broken. Separating them makes the failure
immediately actionable without reading logs.

### Why no `outputs` in this workflow?

The compiled classes are not passed to the test jobs. Each test job re-runs
`mvn compile` implicitly as part of `mvn test`. This is intentional:

- GitHub Actions has no shared filesystem between jobs. Passing compiled artifacts
  via `upload-artifact` / `download-artifact` would add complexity with minimal
  benefit for a project of this size.
- Maven's incremental compilation and the dependency cache make re-compilation
  fast enough that the overhead is negligible.

### Why `-B` flag?

Batch mode (`-B`) disables Maven's interactive output, ANSI colour codes, and
download progress indicators. This produces clean, grep-friendly log lines in
the GitHub Actions log viewer.

---

## Caller Example

```yaml
jobs:
  build:
    uses: ./.github/workflows/_reusable-build.yml
```

---

## Related Files

| File                                                  | Description                          |
|-------------------------------------------------------|--------------------------------------|
| `.github/workflows/ci.yml`                            | Primary caller of this workflow      |
| `.github/actions/setup-java-maven/action.yml`         | Composite action used in this job    |

---

## Related Documentation

| Document                                              | Description                          |
|-------------------------------------------------------|--------------------------------------|
| `docs/devops/workflows/ci.md`                         | Caller workflow documentation        |
| `docs/devops/actions/setup-java-maven.md`             | Composite action documentation       |
