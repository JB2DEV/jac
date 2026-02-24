# Workflow: Security Scan

**Location:** `.github/workflows/security.yml`

---

## Purpose

Runs security analysis on a weekly schedule to detect newly disclosed vulnerabilities
in existing dependencies and to verify no secrets have been exposed in the codebase.

This is a **scheduled maintenance workflow**, separate from CI. While CI validates new code,
this workflow continuously monitors the existing codebase for emerging security issues.

---

## Trigger

```yaml
on:
  schedule:
    - cron: '0 3 * * 1'
  workflow_dispatch:
```

| Trigger | When | Purpose |
|---------|------|---------|
| `schedule` | Every Monday at 3 AM UTC | Automatic weekly scan |
| `workflow_dispatch` | Manual trigger | On-demand scan |

---

## What It Does

Calls the `_reusable-security` workflow, which performs:

1. **OWASP Dependency Check**: Scans all Maven dependencies for known CVEs
2. **TruffleHog Secret Scan**: Scans git history for exposed credentials

Results are visible in:
- GitHub Actions workflow run
- GitHub Security tab → Dependabot alerts
- Email notifications (if workflow fails)

---

## Why Weekly Scanning?

New security vulnerabilities are disclosed constantly. A dependency that was safe last
month might have a critical CVE published today. Weekly scans ensure:

- **Proactive detection**: Find vulnerabilities before attackers exploit them
- **Compliance**: Many security frameworks require periodic vulnerability scanning
- **Zero-day awareness**: Detect vulnerabilities in dependencies you're not actively changing

### Real-World Example

Your project uses `spring-boot-starter-web:3.4.1`. On February 25, 2026, a critical
vulnerability is disclosed affecting Spring Boot 3.4.x. Without scheduled scanning:
- Your code hasn't changed
- CI won't run
- You remain unaware of the vulnerability

With weekly scanning:
- Monday's scan detects the new CVE
- Workflow fails and sends notification
- You update to Spring Boot 3.4.2 (patched version)

---

## Scheduled Time: Why Monday 3 AM UTC?

```yaml
cron: '0 3 * * 1'
```

**Monday**: Start of the work week allows time to address issues before production deploys

**3 AM UTC**: Off-peak hours to avoid:
- Conflicts with CI/CD runs during work hours
- GitHub Actions queue congestion
- NVD database rate limiting

**UTC timezone**: GitHub Actions always uses UTC, regardless of repository location

---

## Manual Triggering

The `workflow_dispatch` trigger allows running the scan on-demand:

```
GitHub Actions → Security Scan → Run workflow → Run workflow
```

Use manual triggers when:
- A major CVE is announced affecting your tech stack
- Before a production deployment
- After adding new dependencies
- Investigating a security incident

---

## Notifications

GitHub sends notifications when scheduled workflows fail:

- Email to repository watchers
- GitHub notification bell
- Can be configured in GitHub Settings → Notifications

To receive security alerts:
```
Repository → Settings → Security & analysis → Dependabot alerts → Enable
```

---

## Design Decisions

### Why separate from CI workflow?

The CI workflow validates changes. The security workflow monitors existing code. Separating
them ensures:
- CI remains fast (security scans can be slow)
- Security runs even when no code changes
- Different notification routing (CI failures → PR, security failures → maintainers)

### Why not daily?

NVD updates multiple times per day, but most updates are minor. Weekly strikes a balance:
- Frequent enough to catch critical issues quickly
- Infrequent enough to avoid alert fatigue
- Reduces GitHub Actions minutes consumption

For critical infrastructure, increase to daily:

```yaml
schedule:
  - cron: '0 3 * * *'  # Every day at 3 AM UTC
```

### Why not use Dependabot instead?

Dependabot and OWASP Dependency Check serve different purposes:

| Tool | What It Does | When It Runs |
|------|--------------|--------------|
| Dependabot | Creates PRs to update dependencies | On new releases |
| OWASP | Scans dependencies for CVEs | On demand / schedule |

Both should be enabled. Dependabot keeps dependencies current, OWASP catches
vulnerabilities in dependencies you haven't updated yet.

---

## Troubleshooting

### First run takes 10+ minutes

The first OWASP scan downloads the NVD database (~500MB). Subsequent runs use cache
and complete in 1-2 minutes.

### Scan fails with "CVE database download timeout"

The NVD API has rate limits. If many workflows run simultaneously:
1. Wait 30 minutes and retry manually
2. Consider adjusting schedule to off-peak hours
3. Contact GitHub Support if persistent

### Too many false positives

Add suppressions to `owasp-suppressions.xml` with clear justifications. Review
suppressions quarterly to ensure they're still valid.

---

## Related Files

| File                                                  | Description                              |
|-------------------------------------------------------|------------------------------------------|
| `.github/workflows/_reusable-security.yml`            | Reusable security workflow               |
| `owasp-suppressions.xml`                              | False positive suppressions              |
| `pom.xml`                                             | OWASP plugin configuration               |

---

## Related Documentation

| Document                                              | Description                              |
|-------------------------------------------------------|------------------------------------------|
| `docs/devops/workflows/_reusable-security.md`         | Reusable security workflow documentation |
| `docs/devops/workflows/ci.md`                         | CI workflow that also runs security      |
