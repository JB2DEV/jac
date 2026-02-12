# Docker Guide

This guide will help you build, run, and deploy the application using Docker.

## Table of Contents

- [Building the Image](#building-the-image)
- [Running with Docker](#running-with-docker)
- [Running with Docker Compose](#running-with-docker-compose)
- [Useful Commands](#useful-commands)
- [Environment Configuration](#environment-configuration)
- [Publishing to Docker Hub](#publishing-to-docker-hub)
- [Troubleshooting](#troubleshooting)

---

## Building the Image

### Basic Build

```powershell
# Simple build
docker build -t jac-api:1.0.0 .

# Build with multiple tags
docker build -t jac-api:1.0.0 -t jac-api:latest .

# Build without cache (complete rebuild)
docker build --no-cache -t jac-api:1.0.0 .
```

### Multi-Stage Build

The Dockerfile uses a multi-stage build to optimize the image size:

1. **Stage 1 (Build)**: Maven with JDK 21 to compile
2. **Stage 2 (Runtime)**: JRE 21 Alpine (lightweight image) to run

**Benefits:**
- ✅ Smaller final image (~200MB vs ~800MB)
- ✅ Only includes necessary runtime
- ✅ Better security (smaller attack surface)

### Verify the Image

```powershell
# List local images
docker images | Select-String "jac-api"

# View image details
docker inspect jac-api:1.0.0

# View image size
docker images jac-api:1.0.0 --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"
```

---

## Running with Docker

### Basic Execution

```powershell
# Run in development mode (foreground)
docker run -p 8080:8080 jac-api:1.0.0

# Run in production mode (background)
docker run -d -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod --name jac-api jac-api:1.0.0
```

### Running with Full Configuration

```powershell
docker run -d `
  --name jac-api `
  -p 8080:8080 `
  -e SPRING_PROFILES_ACTIVE=prod `
  -e JAVA_OPTS="-Xmx512m -Xms256m" `
  --restart unless-stopped `
  jac-api:1.0.0
```

**Parameters:**
- `-d`: Run in background (detached)
- `--name`: Container name
- `-p 8080:8080`: Port mapping (host:container)
- `-e`: Environment variables
- `--restart`: Restart policy

### Verify It's Working

```powershell
# View logs
docker logs jac-api

# View logs in real-time
docker logs -f jac-api

# Verify application health
curl http://localhost:8080/actuator/health

# Or with PowerShell
Invoke-WebRequest http://localhost:8080/actuator/health

# Access Swagger UI
# Open in browser: http://localhost:8080/api/v1/swagger-ui
```

### Stop and Clean Up

```powershell
# Stop the container
docker stop jac-api

# Start the stopped container
docker start jac-api

# Remove the container
docker rm jac-api

# Stop and remove (forced)
docker rm -f jac-api
```

---

## Running with Docker Compose

Docker Compose simplifies container management.

### Start Services

```powershell
# Build and run
docker-compose up -d

# Build only
docker-compose build

# Force rebuild
docker-compose up -d --build

# View logs
docker-compose logs -f
```

### Service Management

```powershell
# View service status
docker-compose ps

# Stop services
docker-compose stop

# Start services
docker-compose start

# Restart services
docker-compose restart

# Stop and remove
docker-compose down

# Stop, remove and clean volumes
docker-compose down -v
```

### Health Checks

The docker-compose.yml includes automatic health checks:

```powershell
# View health status
docker-compose ps

# View health check logs
docker inspect jac-api --format='{{json .State.Health}}' | ConvertFrom-Json
```

---

## Useful Commands

### Debugging

```powershell
# Access the container (shell)
docker exec -it jac-api sh

# View processes inside container
docker top jac-api

# View real-time statistics
docker stats jac-api

# Inspect the container
docker inspect jac-api

# View image history
docker history jac-api:1.0.0
```

### Resource Management

```powershell
# View disk usage
docker system df

# Clean unused resources
docker system prune

# Clean unused images
docker image prune

# Clean everything (CAUTION)
docker system prune -a --volumes
```

---

## Environment Configuration

### Spring Profiles

```powershell
# Development (default)
docker run -p 8080:8080 jac-api:1.0.0

# Production
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod jac-api:1.0.0

# Development with console logs
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev jac-api:1.0.0
```

### JVM Configuration

```powershell
# Configure memory
docker run -p 8080:8080 `
  -e JAVA_OPTS="-Xmx512m -Xms256m" `
  jac-api:1.0.0

# Enable remote debugging
docker run -p 8080:8080 -p 5005:5005 `
  -e JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" `
  jac-api:1.0.0
```

### Environment Variables

```powershell
# Using .env file
docker run --env-file .env -p 8080:8080 jac-api:1.0.0

# Multiple variables
docker run `
  -e SPRING_PROFILES_ACTIVE=prod `
  -e SERVER_PORT=8080 `
  -e LOGGING_LEVEL_ROOT=INFO `
  -p 8080:8080 `
  jac-api:1.0.0
```

---

## Monitoring and Logs

### View Structured Logs (JSON)

In production, logs are in JSON format:

```powershell
# View JSON logs
docker logs jac-api | ConvertFrom-Json | Select-Object timestamp, message, level

# Filter by level
docker logs jac-api | Select-String "ERROR"
```

### Export Logs

```powershell
# Export logs to a file
docker logs jac-api > logs.txt

# Export only errors
docker logs jac-api 2>&1 | Select-String "ERROR" > errors.txt
```

---

## Quick Reference

### Most Used Commands

```powershell
# Build
docker build -t jac-api:1.0.0 .

# Run in development
docker run -p 8080:8080 jac-api:1.0.0

# Run in production
docker run -d -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod --name jac-api jac-api:1.0.0

# View logs
docker logs -f jac-api

# Stop
docker stop jac-api

# Clean up
docker rm jac-api

# Docker Compose
docker-compose up -d
docker-compose logs -f
docker-compose down
```

---

## References

- [Dockerfile Best Practices](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)

---



