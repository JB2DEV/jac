# Workflow: Auto Tag Release

**Location:** `.github/workflows/auto-tag.yml`

---

## Purpose

Automatically creates and pushes a semantic version tag when a release PR is merged to `main`.

This workflow eliminates manual tag creation and ensures every release PR results in a properly tagged release.

---

## Trigger

```yaml
on:
  pull_request:
    types: [closed]
    branches:
      - main
```

**Automatic execution** when ANY pull request to `main` is closed.

**Conditional execution:** Only runs if:
1. PR was **merged** (not just closed)
2. PR title starts with `"Release v"`

---

## What It Does

### 1. Validates PR State

```yaml
if: github.event.pull_request.merged == true && 
    startsWith(github.event.pull_request.title, 'Release v')
```

**Skips workflow if:**
- PR was closed without merging
- PR title doesn't match release pattern
- PR was merged but not a release PR

### 2. Extracts Version from PR Title

```bash
# Example: "Release v1.2.0" → extracts "1.2.0"
TITLE="${{ github.event.pull_request.title }}"
VERSION=$(echo "$TITLE" | sed -n 's/^Release v\([0-9]\+\.[0-9]\+\.[0-9]\+\)$/\1/p')
```

**Expected format:** `Release vX.Y.Z` (exact match, case-sensitive)

**Examples:**
- ✅ `Release v1.2.0` → version: `1.2.0`, tag: `v1.2.0`
- ✅ `Release v2.0.0` → version: `2.0.0`, tag: `v2.0.0`
- ✅ `Release v1.3.0-beta` → version: `1.3.0-beta`, tag: `v1.3.0-beta`
- ❌ `release v1.2.0` → fails (lowercase 'r')
- ❌ `Release 1.2.0` → fails (missing 'v')
- ❌ `Release v1.2` → fails (not semantic)

### 3. Creates Annotated Tag

```bash
git tag -a "v1.2.0" -m "Release v1.2.0"
git push origin "v1.2.0"
```

**Tag type:** Annotated (recommended for releases)  
**Tag location:** Current `main` branch HEAD (the merge commit)  
**Author:** `github-actions[bot]`

### 4. Comments on PR

Posts success comment with tracking links:

```markdown
✅ Release tag `v1.2.0` created successfully.

The release workflow is now running: 
https://github.com/JB2DEV/jac/actions/workflows/release.yml

Once complete, the release will be available at: 
https://github.com/JB2DEV/jac/releases/tag/v1.2.0
```

This provides immediate feedback and tracking links.

---

## Permissions

```yaml
permissions:
  contents: write  # Create and push tags
```

**Note:** Does NOT require `pull-requests: write` because it only comments, doesn't modify PRs.

---

## Design Decisions

### Why Trigger on PR Merge Instead of Manual Tag?

**Problems with manual tagging:**
- Easy to forget after merging PR
- Risk of tagging wrong commit
- Creates time gap between merge and release
- Inconsistent tag naming

**Benefits of automatic tagging:**
- Impossible to forget (always happens)
- Always tags the correct commit (merge commit)
- Zero time gap (tag → release trigger is immediate)
- Consistent tag format enforced

### Why Extract Version from PR Title?

The PR title is the single source of truth during release:
- Created by `prepare-release.yml` with exact format
- Visible in PR list and emails
- Easy to verify before merge
- No need to read file contents or parse commits

### Why Annotated Tag?

Annotated tags (vs lightweight tags):
- Include tagger name and date
- Include tag message
- Are "full objects" in Git (have their own SHA)
- Recommended by Git for releases
- Required by some CI/CD tools

### Why Comment on PR?

Provides immediate feedback to the person who merged:
- Confirms tag was created
- Links directly to release workflow run
- Links directly to final release page
- Creates audit trail in PR conversation

---

## Usage

**You don't manually trigger this workflow.** It runs automatically.

### Normal Flow

```
1. You merge PR "Release v1.2.0" to main
   ↓
2. Auto-tag workflow triggers (~5 seconds)
   ↓
3. Tag v1.2.0 created on main
   ↓
4. Tag pushed to GitHub
   ↓
5. Comment posted on PR with links
   ↓
6. Release workflow triggered by tag
```

**Total time:** ~10 seconds from merge to tag creation

---

## What Triggers Release Workflow

```
PR merge → auto-tag.yml → creates tag → release.yml
```

The tag push is what triggers `release.yml`:

```yaml
# In release.yml
on:
  push:
    tags:
      - 'v*.*.*'
```

Without the tag, the release workflow won't run.

---

## Error Handling

### Workflow Runs but No Tag Created

**Check workflow logs for:**

```
Error: Could not extract version from PR title: [actual title]
```

**Cause:** PR title doesn't match pattern `Release vX.Y.Z`

**Solution:** 
- Close the PR without merging
- Re-run `prepare-release.yml` with correct version
- New PR will have correct title

### Tag Already Exists

**Error:**
```
fatal: tag 'v1.2.0' already exists
```

**Cause:** You're trying to release the same version twice

**This should never happen** because `prepare-release.yml` checks for existing tags.

**If it happens:**
1. Someone manually created the tag
2. Or you force-pushed and re-ran the workflow

**Solution:** Delete the tag and re-run:
```bash
git push --delete origin v1.2.0
# Re-merge the PR or manually re-run auto-tag
```

### Comment Not Posted

**Possible causes:**
1. GitHub API rate limit (rare)
2. Workflow permissions insufficient

**Impact:** Low. Tag is still created, only the comment is missing.

**Solution:** Check workflow logs for comment step errors.

---

## Troubleshooting

### Tag Created but Release Workflow Didn't Run

**Check:**
1. Tag format: Must be `v*.*.*` (has `v` prefix)
2. Tag location: Must be pushed to GitHub (not just local)
3. Release workflow: Check `release.yml` exists and is valid

**Debug:**
```bash
# Check if tag exists on GitHub
git ls-remote --tags origin | grep v1.2.0

# If missing, manually push
git push origin v1.2.0
```

### Wrong Commit Tagged

**Cause:** You merged the PR but then pushed more commits to main

**Prevention:** Don't push to main between PR merge and tag creation (workflow runs in ~10s)

**Solution:** Delete tag and recreate:
```bash
git push --delete origin v1.2.0
git tag -a v1.2.0 <correct-commit-sha> -m "Release v1.2.0"
git push origin v1.2.0
```

### Workflow Runs on Non-Release PRs

**This is expected.** The workflow triggers on all PR merges to main, but the conditional check makes it skip non-release PRs:

```yaml
if: github.event.pull_request.merged == true && 
    startsWith(github.event.pull_request.title, 'Release v')
```

Non-release PRs will show as "skipped" in Actions.

---

## Advanced: Multiple Release PRs

If you need to maintain multiple release branches (e.g., `v1.x` and `v2.x`):

**Current workflow limitation:** Only works for releases to `main`.

**Workaround:** Modify trigger to include other branches:

```yaml
on:
  pull_request:
    types: [closed]
    branches:
      - main
      - 'release/v*'
```

Then adjust tag creation to include branch name if needed.

---

## Security Considerations

### Could Someone Create Fake Releases?

**No.** Even if someone creates a PR with title "Release v9.9.9":

1. They can't merge to `main` (branch protection)
2. If they somehow merge, CI must pass
3. Tag is created by `github-actions[bot]` (audit trail)
4. All release artifacts are signed/verified

### Could Tag Creation Fail?

**Yes,** if:
- Repository permissions changed
- Git history was rewritten
- Network issues with GitHub

**Impact:** Release workflow won't trigger. Fix the issue and manually create the tag.

---

## Monitoring

### How to Verify It Worked

After merging release PR:

1. **Check PR comments** → Should have "Release tag created" comment
2. **Check GitHub tags** → Repository → Tags → Should show new tag
3. **Check Actions** → Release workflow should be running
4. **Check Releases** → New release should appear after ~5-7 minutes

### Debugging Failed Tag Creation

```bash
# Check if tag exists locally but not on GitHub
git tag | grep v1.2.0

# Check workflow logs
# GitHub → Actions → Auto Tag Release → [workflow run]

# Manually inspect PR details
gh pr view [PR-NUMBER] --json title,mergedAt,merged
```

---

## Related Workflows

| Workflow | Relationship |
|----------|--------------|
| `prepare-release.yml` | Creates the PR that triggers this workflow |
| `release.yml` | Triggered by the tag created by this workflow |
| `ci.yml` | Runs on the release PR before merge |

---

## Testing

### How to Test Without Releasing

**Option 1: Use a test branch**

```yaml
# Temporarily modify auto-tag.yml
branches:
  - main
  - test-release  # Add test branch
```

Then merge a PR titled "Release v0.0.1-test" to `test-release`.

**Option 2: Dry-run mode** (requires workflow modification)

Add a `dry-run` input that creates the tag locally but doesn't push it.

---

## See Also

- `prepare-release.yml` - Creates the release PR
- `release.yml` - Full release automation
- `automated-release-flow.md` - Complete flow documentation
- `RELEASE_GUIDE.md` - Quick release reference
