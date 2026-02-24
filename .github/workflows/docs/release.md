# Workflow: Release

**Location:** `.github/workflows/release.yml`

---

## Purpose

Executes the complete release automation when a semantic version tag is pushed. This workflow:

1. Validates the release candidate with full test suite
2. Builds production artifacts (JAR)
3. Publishes Docker image to GitHub Container Registry
4. Creates GitHub Release with auto-generated changelog
5. Creates sync PR from main to develop

This is the final and most comprehensive workflow in the release pipeline.

---

## Trigger

```yaml
on:
  push:
    tags:
      - 'v*.*.*'
```

**Automatic execution** when a semantic version tag is pushed (e.g., `v1.2.0`, `v2.0.0-beta`).

**Triggered by:** `auto-tag.yml` after release PR is merged.

**Manual execution:** Can also be triggered by manually pushing a tag.

---

## Jobs

### Job 1: Validate

**Purpose:** Ensure release candidate is stable

```yaml
runs-on: ubuntu-latest
steps:
  - Setup Java & Maven
  - Run: mvn clean verify -B
```

**What it validates:**
- Code compiles
- Unit tests pass (domain + application)
- Integration tests pass (infrastructure)
- JaCoCo coverage threshold met (≥80%)

**Time:** ~120 seconds

**If this fails:** Entire release stops. No artifacts published.

---

### Job 2: Build JAR

**Purpose:** Create production-ready executable JAR

```yaml
needs: validate
runs-on: ubuntu-latest
steps:
  - Setup Java & Maven
  - Build: mvn clean package -DskipTests -B
  - Upload artifact: application-jar
```

**Output:** `target/cv-api-X.Y.Z.jar` (~40MB)

**Retention:** 1 day (only needed for release creation)

**Why skip tests?** Already validated in Job 1. Re-running adds 2-3 minutes with no benefit.

**Time:** ~60 seconds

---

### Job 3: Docker Release

**Purpose:** Build and publish Docker image to GHCR

```yaml
needs: validate
uses: ./.github/workflows/_reusable-docker.yml
with:
  push: true
  image-tag: ${{ github.ref_name }}
```

**Registry:** `ghcr.io/jb2dev/jac`

**Tags created** (example for v1.2.3):

| Tag | Description | Example |
|-----|-------------|---------|
| `vX.Y.Z` | Exact version (immutable) | `v1.2.3` |
| `X.Y` | Latest patch for minor | `1.2` |
| `latest` | Latest stable release | `latest` |
| `sha-{hash}` | Specific commit | `sha-abc1234` |

**Authentication:** Uses `GITHUB_TOKEN` (automatic)

**First time:** ~240 seconds (full build)  
**Subsequent:** ~90 seconds (cached layers)

**Runs in parallel with:** Build JAR job (both need validation)

---

### Job 4: Create Release

**Purpose:** Publish GitHub Release with artifacts

```yaml
needs: [validate, build-jar, docker-release]
runs-on: ubuntu-latest
steps:
  - Checkout with full history
  - Download JAR artifact
  - Create GitHub Release
```

**Release contents:**
- **Title:** Tag name (e.g., "v1.2.0")
- **Body:** Auto-generated changelog from merged PRs
- **Assets:** JAR file (`cv-api-1.2.0.jar`)
- **Status:** Published (not draft)
- **Pre-release:** Auto-detected from tag (e.g., `v1.2.0-beta`)

**Changelog generation:**
- Compares current tag with previous tag
- Extracts all merged PRs in between
- Groups by labels (bug, enhancement, etc.)
- Lists all contributors

**Time:** ~30 seconds

---

### Job 5: Sync Develop

**Purpose:** Keep develop in sync with main

```yaml
needs: create-release
runs-on: ubuntu-latest
steps:
  - Checkout main
  - Create PR: main → develop
```

**Creates PR with:**
- **Title:** `chore: sync main into develop after release vX.Y.Z`
- **Body:** Post-release sync explanation
- **Label:** `sync`

**Why PR instead of direct merge?**
- Allows human review before sync
- Detects merge conflicts if someone committed to develop during release
- Creates audit trail
- Safer than automatic merge

**Time:** ~10 seconds

**Manual step required:** You must review and merge this sync PR.

---

## Permissions

```yaml
permissions:
  contents: write          # Create GitHub Release, push merge
  packages: write          # Push Docker image to GHCR
  pull-requests: write     # Create sync PR
```

---

## Complete Flow Timeline

```
Tag pushed (v1.2.0)
├─ 0s    → Release workflow triggered
│
├─ 0-120s → Job 1: Validate (runs all tests)
│   └─ If fails → STOP (no release)
│
├─ 120-180s → Jobs 2+3 (parallel):
│   ├─ Build JAR (~60s)
│   └─ Docker Release (~240s, longest job)
│
├─ 240-270s → Job 4: Create GitHub Release
│   ├─ Download JAR
│   ├─ Generate changelog
│   └─ Publish release
│
└─ 270-280s → Job 5: Create sync PR
    └─ PR created (you merge it later)

Total: ~5-7 minutes
```

---

## Design Decisions

### Why Validate Before Building?

**Fail-fast principle:** No point building artifacts if tests fail.

**Saves:**
- GitHub Actions minutes
- Docker registry storage
- Wasted releases that have to be deleted

### Why Parallel Build Jobs?

JAR build (~60s) and Docker build (~240s) are independent after validation. Running in parallel saves ~60 seconds per release.

### Why Not Merge Develop Automatically?

**Risk scenarios:**
1. Someone committed to develop during release
2. Hotfix was applied to main but not develop
3. Merge conflict that needs manual resolution

**Solution:** Create PR for human review. Adds 1 manual step but prevents broken merges.

### Why Auto-Generate Changelog?

**Manual CHANGELOG.md benefits:**
- Curated content
- Editorial control
- Structured format (Keep a Changelog)

**GitHub Release Notes benefits:**
- Always accurate (from actual PRs)
- Zero maintenance
- Contributor attribution
- Links to commits/PRs

**We keep both:** CHANGELOG.md for developers, Release Notes for users.

---

## Artifacts Produced

After successful release, you'll have:

### 1. GitHub Release
```
URL: https://github.com/JB2DEV/jac/releases/tag/v1.2.0

Contents:
├── cv-api-1.2.0.jar (downloadable)
├── Source code (zip)
├── Source code (tar.gz)
└── Auto-generated release notes
```

### 2. Docker Images
```
ghcr.io/jb2dev/jac:v1.2.0   ← Exact version
ghcr.io/jb2dev/jac:1.2       ← Latest patch
ghcr.io/jb2dev/jac:latest    ← Latest stable
ghcr.io/jb2dev/jac:sha-abc   ← Specific commit
```

### 3. Git Tag
```
git tag -l
v1.2.0

git show v1.2.0
tag v1.2.0
Tagger: github-actions[bot]
Date: 2026-02-24

Release v1.2.0
```

### 4. Sync PR
```
Pull Requests → "chore: sync main into develop after release v1.2.0"
Status: Open (awaiting your merge)
```

---

## Error Handling

### Job 1 (Validate) Fails

**Most common failure point**

**Possible causes:**
- Tests failing on main (should never happen)
- JaCoCo coverage below 80%
- Integration test environment issues

**Impact:** Release completely aborted. No artifacts published.

**Recovery:**
1. Investigate failure in workflow logs
2. Fix issue in develop
3. Create new release PR with version X.Y.Z+1
4. Merge and retry

### Job 2 (Build JAR) Fails

**Rare** (tests passed in Job 1)

**Possible causes:**
- Maven packaging issue
- Artifact upload failure

**Impact:** Release continues (Docker might succeed) but JAR won't be in GitHub Release

**Recovery:**
1. Fix issue
2. Manually build and upload JAR to existing release
3. Or delete release and retry with new tag

### Job 3 (Docker Release) Fails

**Possible causes:**
- GHCR authentication failure
- Docker Hub rate limit (if using Docker Hub)
- Dockerfile syntax error

**Impact:** Release continues but Docker image not published

**Recovery:**
1. Fix Docker issue
2. Manually build and push image:
```bash
docker build -t ghcr.io/jb2dev/jac:v1.2.0 .
docker push ghcr.io/jb2dev/jac:v1.2.0
```

### Job 4 (Create Release) Fails

**Possible causes:**
- JAR artifact not found (Job 2 failed)
- Insufficient permissions

**Impact:** No GitHub Release created (but Docker might be published)

**Recovery:**
1. Check workflow logs for specific error
2. Manually create release in GitHub UI
3. Upload JAR manually

### Job 5 (Sync PR) Fails

**Possible causes:**
- Merge conflict between main and develop
- Insufficient permissions

**Impact:** Low. Release is complete, only sync is missing.

**Recovery:**
```bash
git checkout develop
git merge main
# Resolve conflicts if any
git push origin develop
```

---

## First Release Setup

### Make Docker Package Public

After your **first release only**, the Docker package needs to be made public:

```
1. GitHub → Your Profile → Packages
2. Click: jac
3. Package settings
4. Change visibility → Public
5. Confirm by typing repository name
```

**Why?** GHCR packages are private by default. Making it public allows anyone to pull.

**After this:** All future releases automatically use the public package.

---

## Verification Checklist

After release workflow completes:

```
□ GitHub Release published at /releases/tag/vX.Y.Z
□ JAR downloadable from release
□ Release notes auto-generated
□ Docker image pullable: docker pull ghcr.io/jb2dev/jac:vX.Y.Z
□ Docker image tagged as :latest
□ Sync PR created: main → develop
□ All workflow jobs green ✅
```

---

## Monitoring

### Real-time Monitoring

```
GitHub → Actions → Release vX.Y.Z

Watch jobs progress:
├── ✅ Validate           [~2 min]
├── ✅ Build JAR          [~1 min]
├── ✅ Docker Release     [~4 min] ← Longest
├── ✅ Create Release     [~30s]
└── ✅ Sync Develop       [~10s]
```

### Post-Release Verification

**Test JAR:**
```bash
# Download from GitHub Release
wget https://github.com/JB2DEV/jac/releases/download/v1.2.0/cv-api-1.2.0.jar

# Run it
java -jar cv-api-1.2.0.jar

# Test endpoint
curl http://localhost:8080/actuator/health
```

**Test Docker:**
```bash
# Pull image
docker pull ghcr.io/jb2dev/jac:v1.2.0

# Run container
docker run -d -p 8080:8080 ghcr.io/jb2dev/jac:v1.2.0

# Verify version
curl http://localhost:8080/api/v1/openapi | jq '.info.version'
# Should output: "1.2.0"
```

---

## Troubleshooting

### Release Published but Version Wrong

**Cause:** Version in files doesn't match tag

**This should never happen** if using `prepare-release.yml` properly.

**Solution:** 
1. Delete the release (GitHub UI)
2. Fix version in files
3. Create new tag with correct version

### Docker Image Shows Old Version

**Cause:** Caching issue

**Solutions:**
```bash
# Option 1: Pull with --no-cache
docker pull --no-cache ghcr.io/jb2dev/jac:latest

# Option 2: Pull by exact version (bypasses cache)
docker pull ghcr.io/jb2dev/jac:v1.2.0

# Option 3: Verify image metadata
docker inspect ghcr.io/jb2dev/jac:latest | jq '.[0].Config.Labels'
```

### Release Notes Empty

**Cause:** No PRs merged between this tag and previous tag

**Example:** If you tagged v1.2.0 and v1.2.1 on the same commit.

**Solution:** This is normal for hotfix releases with manual commits.

### Sync PR Has Conflicts

**Cause:** Someone committed to develop during release

**Solution:**
```bash
# Option 1: Resolve in GitHub UI
# Pull Requests → sync PR → Resolve conflicts

# Option 2: Resolve locally
git checkout develop
git pull
git merge main
# Fix conflicts
git commit
git push origin develop
# Close the sync PR (already synced)
```

---

## Advanced: Pre-releases

For beta, RC, or alpha versions:

**Tag format:** `v1.3.0-beta`, `v2.0.0-rc.1`

**Behavior:**
- Workflow runs normally
- GitHub Release marked as "Pre-release" (automatically detected)
- Docker tagged with pre-release version
- `:latest` tag **not updated** (only stable releases update :latest)

---

## Advanced: Hotfixes

For urgent fixes to production:

```bash
# 1. Branch from main
git checkout -b hotfix/1.2.1 main

# 2. Fix bug and update CHANGELOG
git commit -m "fix: critical security issue"

# 3. Push and run prepare-release
git push origin hotfix/1.2.1

# 4. GitHub Actions → Prepare Release → Input: 1.2.1
#    Creates PR: hotfix/1.2.1 → main

# 5. Merge PR → automatic tag → automatic release
```

Hotfixes follow the same release flow.

---

## Security Considerations

### Docker Image Signing

**Current:** Images are not signed

**Recommendation:** Add Docker Content Trust or Sigstore cosign:

```yaml
- name: Sign image
  run: cosign sign ghcr.io/jb2dev/jac:${{ github.ref_name }}
```

### JAR Verification

**Current:** JAR is not signed

**Recommendation:** Add Maven GPG plugin for JAR signing.

### Release Notes Review

**Current:** Auto-generated without review

**Risk:** Sensitive information could be exposed in PR titles/descriptions

**Mitigation:** Use conventional commits, avoid secrets in PR titles

---

## Related Workflows

| Workflow | Relationship |
|----------|--------------|
| `prepare-release.yml` | Creates release PR (step 1) |
| `auto-tag.yml` | Creates tag that triggers this workflow (step 2) |
| `_reusable-docker.yml` | Called by this workflow for Docker build |

---

## See Also

- `automated-release-flow.md` - Complete release flow documentation
- `RELEASE_GUIDE.md` - Quick release reference
- `_reusable-docker.md` - Docker workflow documentation
- `prepare-release.yml` - Release preparation
- `auto-tag.yml` - Tag automation
