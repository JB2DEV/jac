# Reusable Workflow: `_reusable-quality`

**Location:** `.github/workflows/_reusable-quality.yml`

---

## Purpose

Measures test coverage using JaCoCo and performs static code analysis using SonarCloud.
Publishes results in three places:

1. Downloadable JaCoCo HTML report as GitHub Actions artifact
2. Coverage summary comment on pull requests (via `madrapps/jacoco-report`)
3. Complete quality metrics dashboard on SonarCloud (coverage + bugs + vulnerabilities + code smells)

This workflow runs after all tests have passed. Quality metrics without passing tests
are meaningless, so this is the final gate in the CI pipeline.

---

## Trigger

```yaml
on:
  workflow_call:
    secrets:
      SONAR_TOKEN:
        required: true
```

Internal building block. Only runs when called by another workflow.

Requires the `SONAR_TOKEN` secret for uploading analysis results to SonarCloud.

---

## What It Does

1. Checks out the repository with full git history (`fetch-depth: 0` for SonarCloud)
2. Runs the `setup-java-maven` composite action
3. Executes `mvn clean verify jacoco:report` — runs all tests and generates coverage
4. Uploads the JaCoCo HTML report as a GitHub Actions artifact (14 day retention)
5. Posts a coverage summary comment on the PR via `madrapps/jacoco-report`
6. Runs SonarCloud analysis with `mvn sonar:sonar` — uploads coverage + quality metrics

---

## Permissions

```yaml
permissions:
  checks: write
  contents: read
  pull-requests: write
```

- `pull-requests: write`: required by `madrapps/jacoco-report` to post coverage comments
- `checks: write`: for consistent behavior with other test workflows
- `contents: read`: minimum required for checkout

---

## Coverage Thresholds

```yaml
min-coverage-overall: 80
min-coverage-changed-files: 80
```

JaCoCo enforces 80% minimum coverage for:
- Overall project coverage across domain and application packages
- Every individual file modified in the PR

These thresholds are aligned with the `jacoco:check` goal in `pom.xml`.

SonarCloud also tracks coverage but uses it for informational purposes and trending
rather than as a hard gate.

---

## SonarCloud Analysis

SonarCloud analyzes the codebase for:

- **Bugs**: potential runtime errors or incorrect behavior
- **Vulnerabilities**: security weaknesses
- **Code Smells**: maintainability issues (complexity, duplication, naming)
- **Coverage**: imported from JaCoCo XML report
- **Security Hotspots**: code requiring manual security review

The analysis includes:
- All source code in `src/main/java`
- Excludes: `CvApiApplication.java` (entry point, not unit-testable)

Results are visible in:
- Pull request checks (Quality Gate status)
- SonarCloud dashboard: `https://sonarcloud.io/dashboard?id=JB2DEV_jac`
- Badges in the README (Quality Gate, Coverage, Bugs, Vulnerabilities)

---

## Design Decisions

### Why run all tests again instead of reusing previous job results?

Each GitHub Actions job runs in an isolated runner with no shared filesystem.
Re-running tests with `mvn verify` is simpler and more maintainable than passing
artifacts between jobs. The Maven cache makes this fast enough (~90 seconds total).

### Why is quality after integration-tests and not parallel?

Quality metrics are a consequence of all tests passing. Running quality in parallel
with integration tests would produce reports even when tests fail, which is misleading.

### Why `verify` instead of `test`?

`mvn verify` runs the full lifecycle including the `jacoco:check` goal. This ensures
the coverage threshold is enforced before SonarCloud analyzes the code.

### Why fetch-depth: 0?

SonarCloud requires full git history to:
- Detect new code vs existing code for Quality Gate on new code
- Calculate code churn and technical debt accurately
- Show historical trends in the dashboard

Without full history, SonarCloud treats every commit as if all code is new.

### Why SonarCloud instead of separate tools?

SonarCloud provides an integrated solution for:
- Code coverage (from JaCoCo)
- Static analysis (bugs, code smells)
- Security analysis (vulnerabilities, hotspots)
- Quality trending over time
- Single dashboard for all metrics

This is more maintainable than integrating multiple separate services (Codecov for
coverage, Snyk for security, ESLint/CheckStyle for linting, etc.).

---

## Prerequisites

### GitHub Secrets

One secret must be configured in the repository:

```
Settings → Secrets and variables → Actions → New repository secret

SONAR_TOKEN: [from sonarcloud.io project settings]
```

### pom.xml Configuration

SonarCloud requires properties in `pom.xml`:

```xml
<properties>
    <sonar.projectKey>JB2DEV_jac</sonar.projectKey>
    <sonar.organization>jb2dev</sonar.organization>
    <sonar.host.url>https://sonarcloud.io</sonar.host.url>
    <sonar.coverage.jacoco.xmlReportPaths>
        ${project.build.directory}/site/jacoco/jacoco.xml
    </sonar.coverage.jacoco.xmlReportPaths>
    <sonar.exclusions>
        **/target/**,
        **/CvApiApplication.java
    </sonar.exclusions>
</properties>
```

### Maven Plugins

Both JaCoCo and SonarCloud plugins must be configured:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
</plugin>

<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>3.11.0.3922</version>
</plugin>
```

---

## Caller Example

```yaml
jobs:
  quality:
    needs: integration-tests
    uses: ./.github/workflows/_reusable-quality.yml
    secrets:
      SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

---

## Expected Output

On successful run:

1. **GitHub Actions artifact**: `jacoco-report` (downloadable ZIP with HTML report)
2. **PR comment**: Coverage table with per-file percentages
3. **SonarCloud**: Analysis complete, Quality Gate status visible in PR checks

If the Quality Gate fails in SonarCloud (new bugs, coverage drop, etc.), the
workflow succeeds but the PR check shows a warning. SonarCloud failures are
informational, not blocking, to avoid breaking the build on subjective quality rules.

---

## Related Files

| File                                                  | Description                              |
|-------------------------------------------------------|------------------------------------------|
| `.github/workflows/ci.yml`                            | Primary caller of this workflow          |
| `.github/actions/setup-java-maven/action.yml`         | Composite action used in this job        |
| `pom.xml`                                             | JaCoCo and SonarCloud configuration      |

---

## Related Documentation

| Document                                              | Description                              |
|-------------------------------------------------------|------------------------------------------|
| `docs/devops/workflows/ci.md`                         | Caller workflow documentation            |
| `docs/devops/workflows/_reusable-integration-tests.md`| Upstream dependency                      |
