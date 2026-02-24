# Reusable Workflow: `_reusable-security`

**Location:** `.github/workflows/_reusable-security.yml`

---

## Purpose

Performs security scanning for exposed secrets and credentials in the codebase and git history.

Dependency vulnerability scanning is handled by SonarCloud (in the `quality` workflow), which
provides comprehensive vulnerability detection without requiring external API keys.

This workflow runs in two contexts:
- On every PR (validates new code doesn't introduce security issues)
- Weekly via schedule (continuous monitoring)

---

## Trigger

```yaml
on:
  workflow_call:
```

Internal building block. Only runs when called by another workflow.

---

## What It Does

### Secret Scanning with TruffleHog

1. Checks out the repository with full git history
2. Runs TruffleHog to scan all commits for exposed secrets
3. Only reports verified secrets (not generic patterns)
4. Fails the workflow if verified secrets are found

---

## Permissions

```yaml
permissions:
  contents: read
  security-events: write
```

- `security-events: write`: Required to upload security findings to GitHub Security tab
- `contents: read`: Minimum required for checkout

---

## TruffleHog Secret Scanning

### What It Detects

TruffleHog scans the entire git history for:
- API keys (AWS, GCP, Azure, OpenAI, Stripe, SendGrid, etc.)
- Database credentials (MySQL, PostgreSQL, MongoDB, Redis)
- Private keys (SSH, PGP, TLS, RSA)
- OAuth tokens and refresh tokens
- JWT tokens
- Generic secrets (high-entropy strings)

### Verified Secrets Only

```yaml
extra_args: --only-verified
```

TruffleHog has two modes:
- **Unverified**: Reports anything that looks like a secret (high false positive rate)
- **Verified**: Actually connects to the service to verify the credential works

We use `--only-verified` to avoid false positives. If TruffleHog finds a verified secret,
it means an attacker could use it right now.

### What To Do If Secrets Are Found

If TruffleHog detects verified secrets:

1. **Immediately revoke the exposed credential** in the service (AWS, GitHub, etc.)
2. **Remove the secret from git history** using `git filter-repo` or BFG Repo-Cleaner
3. **Rotate the secret** and store it properly (GitHub Secrets, vault, etc.)
4. **Review who had access** to the repository during the exposure window

Simply deleting the secret in a new commit is NOT enough. It remains in git history.

---

## Dependency Vulnerability Scanning

**Handled by SonarCloud**, not by this workflow.

SonarCloud (in the `quality` workflow) detects:
- Known vulnerabilities (CVEs) in all Maven dependencies
- Transitive dependency vulnerabilities
- Security hotspots in code requiring review
- Outdated dependencies with security issues

This approach provides:
- **More reliable**: No external API rate limits
- **Integrated**: Single dashboard for quality + security
- **No configuration**: No API keys or secrets required
- **Automatic updates**: SonarCloud's vulnerability database updates continuously

---

## Design Decisions

### Why not OWASP Dependency Check?

OWASP Dependency Check was removed due to reliability issues in CI environments:

| Issue | Impact |
|-------|--------|
| NVD API rate limiting | Frequent 403/404 errors in GitHub Actions |
| Shared IP addresses | GitHub Actions runners hit NVD rate limits |
| API key requirement | Additional secret management complexity |
| Slow first run | 5-10 minutes to download NVD database |

SonarCloud provides equivalent vulnerability detection without these issues.

### Why run security in parallel with docker-validate?

Both are independent validations that don't require test results:
- TruffleHog checks git history (independent of code compilation)
- Docker checks Dockerfile syntax

Running in parallel saves time. If any fails, the PR is blocked.

### Why scheduled scans?

New secrets can be accidentally committed at any time. Weekly scans detect:
- Secrets added to branches not yet merged
- Secrets in old commits that weren't caught by pre-commit hooks
- Credential rotation failures (old secrets still present in history)

The schedule runs every Monday at 3 AM UTC:

```yaml
schedule:
  - cron: '0 3 * * 1'
```

---

## Prerequisites

**None.** TruffleHog requires no configuration, secrets, or API keys.

---

## Local Testing

To run secret scanning locally before committing:

```bash
# Using Docker (recommended)
docker run --rm -v $(pwd):/repo trufflesecurity/trufflehog:latest \
  filesystem /repo --only-verified

# Or install TruffleHog locally
brew install trufflehog  # macOS
# or
go install github.com/trufflesecurity/trufflehog/v3@latest

trufflehog filesystem . --only-verified
```

---

## Caller Examples

### CI: On every PR

```yaml
jobs:
  security:
    needs: build
    uses: ./.github/workflows/_reusable-security.yml
```

### Scheduled: Weekly scan

```yaml
on:
  schedule:
    - cron: '0 3 * * 1'

jobs:
  security:
    uses: ./.github/workflows/_reusable-security.yml
```

---

## Expected Output

### On successful run (no secrets found)

- TruffleHog completes in ~30-60 seconds
- Workflow passes with green check
- No artifacts generated

### On failure (secrets detected)

- TruffleHog prints detected secrets in workflow logs
- Workflow fails with red X
- PR is blocked until secrets are removed from history

Example output:
```
🐷🔑🐷  TruffleHog. Unearth your secrets. 🐷🔑🐷

Found verified result 🐷🔑
Detector Type: AWS
Decoder Type: PLAIN
Raw result: AKIAIOSFODNN7EXAMPLE
Commit: a3c5d8f
File: config/production.yml
Email: developer@example.com
Repository: https://github.com/JB2DEV/jac
Timestamp: 2024-01-15 14:32:10
```

---

## Security Coverage Summary

| Security Concern | Tool | Workflow |
|------------------|------|----------|
| Exposed secrets | TruffleHog | `security` |
| Dependency CVEs | SonarCloud | `quality` |
| Code security hotspots | SonarCloud | `quality` |
| Code smells | SonarCloud | `quality` |

All security findings are visible in:
- GitHub Security tab → Code scanning alerts
- SonarCloud dashboard → Security section
- Pull request checks

---

## Related Files

| File                                                  | Description                              |
|-------------------------------------------------------|------------------------------------------|
| `.github/workflows/ci.yml`                            | Caller that runs security on PRs         |
| `.github/workflows/security.yml`                      | Scheduled weekly scan                    |
| `.github/workflows/_reusable-quality.yml`             | SonarCloud vulnerability scanning        |

---

## Related Documentation

| Document                                              | Description                              |
|-------------------------------------------------------|------------------------------------------|
| `docs/devops/workflows/ci.md`                         | Caller workflow documentation            |
| `docs/devops/workflows/security.md`                   | Scheduled security scan documentation    |
| `docs/devops/workflows/_reusable-quality.md`          | SonarCloud quality and security analysis |
