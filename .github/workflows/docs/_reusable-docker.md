# Reusable Workflow: `_reusable-docker`

**Location:** `.github/workflows/_reusable-docker.yml`

---

## Purpose

Builds the Docker image from the project's Dockerfile and optionally pushes it to
GitHub Container Registry (GHCR). This workflow supports two modes:

1. **Validation mode** (`push: false`): Verifies the Dockerfile is valid and the image builds successfully. Used in CI on every commit.
2. **Release mode** (`push: true`): Builds and publishes the image to GHCR with semantic version tags. Used in the release workflow.

---

## Trigger

```yaml
on:
  workflow_call:
    inputs:
      push:
        type: boolean
        default: false
      image-tag:
        type: string
        default: ''
```

Internal building block. Only runs when called by another workflow.

**Inputs:**
- `push`: If `true`, publishes the image to GHCR. If `false`, only validates the build.
- `image-tag`: Optional additional tag (e.g., `v1.2.0`). Used during releases.

---

## What It Does

1. Checks out the repository
2. Generates Docker image metadata (tags and labels)
3. Logs in to GHCR (only if `push: true`)
4. Sets up Docker Buildx (for advanced build features and caching)
5. Builds the image from the Dockerfile
6. Optionally pushes the image to GHCR
7. Uses GitHub Actions cache to speed up subsequent builds

---

## Image Registry: GHCR

This workflow publishes to **GitHub Container Registry** (ghcr.io), not Docker Hub.

**Why GHCR?**

| Feature | GHCR | Docker Hub |
|---------|------|------------|
| Integration | Native with GitHub | External service |
| Authentication | `GITHUB_TOKEN` (automatic) | Manual token setup |
| Pricing | Free for public repos | Free tier has rate limits |
| Visibility | Tied to GitHub repo | Separate namespace |

The image is published at: `ghcr.io/jb2dev/jac`

---

## Tagging Strategy

The `docker/metadata-action` generates tags automatically based on the git event:

| Git Event | Generated Tags | Example |
|-----------|----------------|---------|
| Push to `develop` | `develop` | `ghcr.io/jb2dev/jac:develop` |
| Push to `main` | `main` | `ghcr.io/jb2dev/jac:main` |
| Tag `v1.2.0` | `1.2.0`, `1.2`, `latest` | `ghcr.io/jb2dev/jac:1.2.0` |
| Any commit | `sha-abc1234` | `ghcr.io/jb2dev/jac:sha-abc1234` |

The `latest` tag is only applied to semver tags (releases), not to branch pushes.

If `image-tag` input is provided, it is added as an additional raw tag.

---

## Permissions

```yaml
permissions:
  contents: read
  packages: write
```

`packages: write` is required to push images to GHCR. This permission allows the
workflow to authenticate and publish to the container registry associated with
the repository.

---

## Docker Buildx and Caching

```yaml
- name: Set up Docker Buildx
  uses: docker/setup-buildx-action@v3
```

**Buildx** is Docker's next-generation build system. It enables:
- Multi-platform builds (amd64, arm64)
- Advanced caching strategies
- Parallel layer building

**Cache strategy:**

```yaml
cache-from: type=gha
cache-to: type=gha,mode=max
```

`type=gha` uses GitHub Actions cache to store Docker layers between workflow runs.
On the first build, all layers are built from scratch (~3-4 minutes). On subsequent
builds, only changed layers are rebuilt (~30-60 seconds).

`mode=max` caches all intermediate layers, not just the final image. This maximizes
cache hit rate at the cost of slightly more cache storage.

---

## Design Decisions

### Why validate in CI but not push?

Building the Docker image in CI (`push: false`) validates that:
- The Dockerfile syntax is correct
- All COPY instructions reference files that exist
- The multi-stage build completes successfully
- The resulting image is not broken

Pushing the image on every commit to `develop` would pollute the registry with
hundreds of intermediate images. Images are only pushed during releases (Step 7).

### Why GHCR and not Docker Hub?

GitHub Container Registry is the natural choice for GitHub-hosted projects:
- Zero external dependencies
- No manual token management
- Native integration with GitHub repository permissions
- Free and unlimited for public repositories

Docker Hub would require creating a Docker Hub account, generating an access token,
storing it as a GitHub secret, and managing token expiration. GHCR requires none
of this.

### Why multi-stage caching?

The project's Dockerfile uses a multi-stage build:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
# ... compile and package
FROM eclipse-temurin:21-jre-alpine
# ... runtime
```

Without caching, every build re-downloads all Maven dependencies (~200MB, 2-3 minutes).
With `cache-from: type=gha`, dependencies are cached and subsequent builds complete
in under a minute.

### Why does docker-validate run in parallel with tests?

The dependency graph in `ci.yml` is:

```
build
  ├──► unit-tests ──► integration-tests ──► quality
  └──► docker-validate
```

Docker validation is independent of test results. A broken test does not mean the
image is unbuildable. Running in parallel saves ~3 minutes of total pipeline time.

---

## Making the Image Public

By default, images pushed to GHCR are **private**. To make the image publicly
accessible:

```
1. Go to https://github.com/JB2DEV?tab=packages
2. Click on the package "jac"
3. Package settings → "Change visibility"
4. Select "Public"
5. Confirm by typing the repository name
```

Once public, anyone can pull the image:

```bash
docker pull ghcr.io/jb2dev/jac:latest
```

---

## Local Testing

To test the Docker build locally before pushing:

```bash
# Build the image
docker build -t jac-api:local .

# Run the image
docker run -p 8080:8080 jac-api:local

# Verify it works
curl http://localhost:8080/actuator/health
```

This is the same build that runs in CI, so if it works locally, it will work in
the workflow.

---

## Caller Examples

### CI: Validate only

```yaml
jobs:
  docker-validate:
    uses: ./.github/workflows/_reusable-docker.yml
    with:
      push: false
```

### Release: Build and push

```yaml
jobs:
  docker-release:
    uses: ./.github/workflows/_reusable-docker.yml
    with:
      push: true
      image-tag: ${{ github.ref_name }}
```

---

## Related Files

| File                                                  | Description                              |
|-------------------------------------------------------|------------------------------------------|
| `.github/workflows/ci.yml`                            | Caller that validates Docker builds      |
| `Dockerfile`                                          | Multi-stage Docker image definition      |
| `docker-compose.yml`                                  | Local development setup                  |

---

## Related Documentation

| Document                                              | Description                              |
|-------------------------------------------------------|------------------------------------------|
| `docs/devops/workflows/ci.md`                         | Caller workflow documentation            |
| `docs/docker.md`                                      | Complete Docker usage guide              |
