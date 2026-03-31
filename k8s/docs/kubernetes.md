# Kubernetes Guide — jb2dev-cv-api

This guide explains how the `k8s/` folder is structured, what each file does, and how to deploy
the API in a local Kubernetes cluster using Minikube.

## Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Folder Structure](#folder-structure)
- [Helm Charts](#helm-charts)
  - [cv-api chart](#cv-api-chart)
  - [monitoring chart](#monitoring-chart)
- [Deployment Modes](#deployment-modes)
  - [Mode 1 — API only](#mode-1--api-only)
  - [Mode 2 — API + Monitoring](#mode-2--api--monitoring)
  - [Mode 3 — Full stack (API + Monitoring + Jaeger)](#mode-3--full-stack-api--monitoring--jaeger)
  - [Mode 4 — Local build](#mode-4--local-build)
- [What to Expect After Setup](#what-to-expect-after-setup)
  - [API endpoints](#api-endpoints)
  - [Grafana dashboards](#grafana-dashboards)
  - [Jaeger traces](#jaeger-traces)
- [Production Hardening Features](#production-hardening-features)
- [Teardown](#teardown)
- [Troubleshooting](#troubleshooting)

---

## Overview

The `k8s/` folder contains everything needed to run **jb2dev-cv-api** in a local Kubernetes
cluster with [Minikube](https://minikube.sigs.k8s.io/). It is organized around two Helm charts
and an automated script that handles the full lifecycle from cluster creation to deployment.

```
k8s/
├── charts/
│   ├── cv-api/         ← Helm chart for the API
│   └── monitoring/     ← Umbrella chart: Prometheus + Grafana + Loki (+ optional Jaeger)
└── scripts/
    ├── setup.sh        ← One-command setup for all modes
    └── teardown.sh     ← Cleanup script
```

The script `setup.sh` is the single entry point. It:

1. Checks all prerequisites are installed.
2. Starts a Minikube cluster (profile `jac-local`) if not already running.
3. Pulls or builds the Docker image and loads it into Minikube.
4. Installs the `cv-api` Helm chart in the `jac` namespace.
5. Optionally installs the `monitoring` chart in the `monitoring` namespace.
6. Optionally enables Jaeger for distributed tracing.

---

## Prerequisites

| Tool      | Minimum version | Install guide                            |
|-----------|-----------------|------------------------------------------|
| Docker    | 24+             | https://docs.docker.com/get-docker/      |
| Minikube  | 1.32+           | https://minikube.sigs.k8s.io/docs/start/ |
| kubectl   | 1.28+           | https://kubernetes.io/docs/tasks/tools/  |
| Helm      | 3.13+           | https://helm.sh/docs/intro/install/      |

> **Note (Windows)**: All scripts are Bash. Run them inside WSL 2, Git Bash, or any Bash-compatible shell.

---

## Folder Structure

### `k8s/charts/cv-api/`

Helm chart that deploys the REST API. It renders all necessary Kubernetes resources from templates.

```
cv-api/
├── Chart.yaml              ← Chart metadata (name, version, description)
├── values.yaml             ← Default configuration values
└── templates/
    ├── _helpers.tpl        ← Shared label and name helpers
    ├── namespace.yaml      ← Namespace: jac
    ├── configmap.yaml      ← Application environment variables
    ├── deployment.yaml     ← Deployment (RollingUpdate, 2 replicas by default)
    ├── service.yaml        ← ClusterIP service on port 8080
    ├── ingress.yaml        ← Ingress rule → jac.local
    ├── hpa.yaml            ← HorizontalPodAutoscaler (CPU + memory)
    ├── pdb.yaml            ← PodDisruptionBudget (minAvailable: 1)
    ├── serviceaccount.yaml ← Dedicated ServiceAccount, no cluster permissions
    ├── networkpolicy.yaml  ← Restricts ingress/egress traffic
    └── servicemonitor.yaml ← Prometheus scraping (disabled by default)
```

### `k8s/charts/monitoring/`

Umbrella Helm chart that wraps external charts as dependencies and deploys the full
observability stack in a single Helm release.

```
monitoring/
├── Chart.yaml     ← Declares kube-prometheus-stack and loki-stack as dependencies
├── values.yaml    ← Configures Prometheus, Grafana, Loki, Promtail, and Jaeger
├── _helpers.tpl   ← Shared helpers
└── jaeger.yaml    ← Jaeger all-in-one template (enabled via values)
```

**Dependencies** (downloaded automatically by `setup.sh`):

| Chart                    | Version  | Source                                             |
|--------------------------|----------|----------------------------------------------------|
| `kube-prometheus-stack`  | 56.21.4  | `prometheus-community.github.io/helm-charts`       |
| `loki-stack`             | 2.10.2   | `grafana.github.io/helm-charts`                    |

---

## Helm Charts

### cv-api chart

This chart deploys the API with a production-grade configuration by default.

**Key defaults from `values.yaml`:**

| Parameter                        | Default value                  | Description                                  |
|----------------------------------|--------------------------------|----------------------------------------------|
| `image.repository`               | `ghcr.io/jb2dev/jac`           | Image pulled from GitHub Container Registry  |
| `image.tag`                      | `latest`                       | Image tag                                    |
| `replicaCount`                   | `2`                            | Initial number of replicas                   |
| `resources.requests.cpu`         | `250m`                         | Minimum CPU guaranteed per pod               |
| `resources.limits.cpu`           | `500m`                         | Maximum CPU allowed per pod                  |
| `resources.requests.memory`      | `256Mi`                        | Minimum memory guaranteed per pod            |
| `resources.limits.memory`        | `512Mi`                        | Maximum memory allowed per pod               |
| `autoscaling.minReplicas`        | `2`                            | HPA minimum pods                             |
| `autoscaling.maxReplicas`        | `5`                            | HPA maximum pods                             |
| `autoscaling.targetCPU`          | `70%`                          | Scale up threshold                           |
| `ingress.host`                   | `jac.local`                    | Hostname exposed via NGINX Ingress           |
| `serviceMonitor.enabled`         | `false`                        | Enabled automatically when monitoring is on  |
| `env.SPRING_PROFILES_ACTIVE`     | `kubernetes`                   | Active Spring profile inside the cluster     |

**Health probes** are wired to Spring Boot Actuator endpoints:

| Probe           | Endpoint                           | Behaviour                                          |
|-----------------|------------------------------------|----------------------------------------------------|
| `startupProbe`  | `/actuator/health/liveness`        | Checks up to 60 s (12 × 5 s) while the JVM starts |
| `livenessProbe` | `/actuator/health/liveness`        | Restarts the pod if the JVM is stuck               |
| `readinessProbe`| `/actuator/health/readiness`       | Removes the pod from the load balancer if not ready|

**Security settings applied by default:**

- Pod runs as UID/GID `1000` (non-root).
- Root filesystem is read-only (`readOnlyRootFilesystem: true`).
- All Linux capabilities are dropped.
- `ServiceAccount` has no cluster permissions and does not auto-mount the token.
- `NetworkPolicy` only allows ingress from the `ingress-nginx` namespace and (when enabled) from `monitoring`.

### monitoring chart

Umbrella chart that installs the full observability stack in the `monitoring` namespace.

**What it deploys:**

| Component        | Enabled by default | Description                                                        |
|------------------|--------------------|---------------------------------------------------------------------|
| Prometheus       | ✅                  | Scrapes metrics from the API via ServiceMonitor                    |
| Grafana          | ✅                  | Dashboards for metrics and logs (admin / admin)                    |
| Loki             | ✅                  | Log aggregation backend                                            |
| Promtail         | ✅                  | Log collector — watches pods in the `jac` namespace automatically  |
| AlertManager     | ❌                  | Disabled (not needed for local development)                        |
| Jaeger           | ❌                  | Enabled only with `--full` flag                                    |

**Grafana is pre-configured** with Loki as a datasource, so logs appear immediately in
Grafana → Explore without any manual setup.

**Prometheus** is configured with `serviceMonitorSelectorNilUsesHelmValues: false`, which means
it will pick up any `ServiceMonitor` resource in the cluster regardless of labels.

---

## Deployment Modes

All modes are triggered via `setup.sh`. The script is idempotent — it can be run multiple times
safely.

```bash
# Make the script executable (first time only)
chmod +x k8s/scripts/setup.sh
```

---

### Mode 1 — API only

Deploys only the REST API. The fastest way to get the API running.

```bash
./k8s/scripts/setup.sh
```

**What happens:**

1. Minikube starts with profile `jac-local` (4 CPUs, 4 GB RAM, addons: `ingress`, `metrics-server`).
2. The image `ghcr.io/jb2dev/jac:latest` is pulled and loaded into Minikube. If the pull fails, it falls back to a local Docker build.
3. Helm installs the `cv-api` chart in the `jac` namespace.
4. The script prints the Minikube IP so you can add `jac.local` to `/etc/hosts`.

**Expected output:**

```
jb2dev-cv-api — Kubernetes Local Setup

▶  Checking prerequisites
   ✓  docker → /usr/bin/docker
   ✓  minikube → /usr/local/bin/minikube
   ✓  kubectl → /usr/local/bin/kubectl
   ✓  helm → /usr/local/bin/helm
   ✓  Local charts verified

▶  Starting Minikube (profile: jac-local)
   ✓  Cluster started
   ✓  kubectl context active: jac-local

▶  Preparing Docker image
   ✓  Image loaded from GHCR: ghcr.io/jb2dev/jac:latest

▶  Installing local chart: .../k8s/charts/cv-api
   ✓  Release 'cv-api' installed in namespace 'jac'

══════════════════════════════════════════════
  ✓  Setup completed successfully
══════════════════════════════════════════════

NAME    NAMESPACE  STATUS  CHART
cv-api  jac        deployed cv-api-0.1.0

  API:
    http://jac.local                 (requires /etc/hosts)
    http://jac.local/actuator/health
    kubectl port-forward -n jac svc/cv-api-cv-api-service 8080:8080
```

**Verify the API is running:**

```bash
# Check pods (should show 2 Running)
kubectl get pods -n jac

# Expected:
# NAME                          READY   STATUS    RESTARTS   AGE
# cv-api-xxxx-yyyy              1/1     Running   0          60s
# cv-api-xxxx-zzzz              1/1     Running   0          60s

# Health check via port-forward
kubectl port-forward -n jac svc/cv-api-cv-api-service 8080:8080
curl http://localhost:8080/actuator/health

# Expected:
# {"status":"UP","components":{"readinessState":{"status":"UP"},...}}

# If /etc/hosts is configured:
curl http://jac.local/actuator/health
curl http://jac.local/api/v1/profile/personal
```

---

### Mode 2 — API + Monitoring

Deploys the API and the full observability stack: **Prometheus + Grafana + Loki + Promtail**.

```bash
./k8s/scripts/setup.sh --monitoring
```

**What happens in addition to Mode 1:**

1. Helm repositories for `prometheus-community` and `grafana` are added if missing.
2. Chart dependencies (`kube-prometheus-stack`, `loki-stack`) are downloaded into `k8s/charts/monitoring/charts/` if not present (only on first run).
3. The `monitoring` chart is installed in the `monitoring` namespace (~5–8 minutes the first time).
4. `serviceMonitor.enabled` is set to `true` on the `cv-api` release — Prometheus starts scraping `/actuator/prometheus` every 15 seconds.

**Expected output (additional section):**

```
▶  Installing local chart: .../k8s/charts/monitoring
   ✓  Dependencies already available in .../charts
   ✓  Release 'monitoring' installed in namespace 'monitoring'

▶  Enabling ServiceMonitor in 'cv-api'
   ✓  ServiceMonitor enabled → Prometheus will scrape /actuator/prometheus

  Grafana:
    kubectl port-forward -n monitoring svc/kube-prometheus-stack-grafana 3000:80
    http://localhost:3000  →  admin / admin
```

**Verify monitoring is working:**

```bash
# Check all monitoring pods are Running
kubectl get pods -n monitoring

# Expected (approximate):
# NAME                                           READY   STATUS    AGE
# kube-prometheus-stack-grafana-xxxx             1/1     Running   3m
# kube-prometheus-stack-prometheus-0             2/2     Running   3m
# monitoring-loki-0                              1/1     Running   3m
# monitoring-promtail-xxxx                       1/1     Running   3m

# Verify Prometheus is scraping the API
kubectl port-forward -n monitoring svc/kube-prometheus-stack-prometheus 9090:9090
# Open http://localhost:9090/targets → look for "cv-api" with state UP

# Access Grafana
kubectl port-forward -n monitoring svc/kube-prometheus-stack-grafana 3000:80
# Open http://localhost:3000 → admin / admin
```

**In Grafana you should see:**

- **Explore → Prometheus**: Query `jvm_memory_used_bytes` or `http_server_requests_seconds_count`
  to confirm metrics are arriving from the API.
- **Explore → Loki**: Query `{namespace="jac", app="cv-api"}` to see real-time application logs
  in JSON format, collected automatically by Promtail.

**Import Spring Boot dashboard (optional):**

In Grafana → Dashboards → Import → enter ID `19004` (Spring Boot Statistics).
This dashboard shows JVM memory, GC activity, HTTP request rates, and error rates.

---

### Mode 3 — Full stack (API + Monitoring + Jaeger)

Deploys everything from Mode 2 plus **Jaeger** for distributed tracing.

```bash
./k8s/scripts/setup.sh --full
```

**What Jaeger provides:**
- Collects OTLP traces sent by the Spring Boot application.
- Visualises end-to-end request spans (HTTP layer → use case → repository).
- Integrated in Grafana as an additional datasource.

**Jaeger is deployed as `all-in-one`** (single pod, in-memory storage), which is ideal for
local development and demo purposes.

**Additional expected output:**

```
  Jaeger:
    kubectl port-forward -n monitoring svc/jaeger-query 16686:16686
    http://localhost:16686
```

**Verify Jaeger is working:**

```bash
# Check Jaeger pod
kubectl get pods -n monitoring -l app.kubernetes.io/name=jaeger

# Expected:
# NAME           READY   STATUS    AGE
# jaeger-xxxx    1/1     Running   2m

# Open the Jaeger UI
kubectl port-forward -n monitoring svc/jaeger-query 16686:16686
# Open http://localhost:16686
# Select service "jb2dev-cv-api" from the dropdown and click "Find Traces"
```

**In the Jaeger UI you should see:**
- One trace per HTTP request received by the API.
- Each trace shows spans for the controller, use case, and repository layers.
- HTTP status code, duration, and any errors are visible per span.

**In Grafana with Jaeger datasource:**
- Go to Explore → select Jaeger as datasource.
- Search by trace ID or service name.
- Correlate a log line in Loki with its trace in Jaeger using the `traceId` field.

---

### Mode 4 — Local build

Forces a local Docker build instead of pulling from GHCR. Useful when working on changes
that have not been pushed yet.

```bash
./k8s/scripts/setup.sh --local-build

# Can be combined with other flags:
./k8s/scripts/setup.sh --local-build --monitoring
./k8s/scripts/setup.sh --local-build --full
```

**What happens:**

1. `docker build -t jac-api:local .` is run from the repository root.
2. The image is loaded into Minikube with `minikube image load`.
3. The chart is installed with `image.repository=jac-api` and `image.tag=local`.

**When to use this mode:**

- You have modified source code and want to test it in Kubernetes before pushing.
- GHCR is not accessible (e.g., no internet, private network).
- You want to iterate quickly on Kubernetes configuration changes.

---

## What to Expect After Setup

### API endpoints

Once the API is running, these endpoints are available (replace `jac.local` with
`localhost:8080` if using port-forward):

| Endpoint                              | Description                              |
|---------------------------------------|------------------------------------------|
| `GET /api/v1/profile/personal`        | Personal information                     |
| `GET /api/v1/profile/contact`         | Contact details                          |
| `GET /api/v1/education`               | List of education items                  |
| `GET /api/v1/education/{id}`          | Single education item                    |
| `GET /api/v1/experience`              | List of work experiences                 |
| `GET /api/v1/experience/{id}`         | Single work experience                   |
| `GET /api/v1/skills/technical`        | Technical skills (filterable)            |
| `GET /api/v1/skills/soft`             | Soft skills                              |
| `GET /api/v1/skills/languages`        | Language skills                          |
| `GET /api/v1/training`                | List of training/certifications          |
| `GET /actuator/health`                | Overall health status                    |
| `GET /actuator/health/liveness`       | Liveness probe endpoint                  |
| `GET /actuator/health/readiness`      | Readiness probe endpoint                 |
| `GET /actuator/prometheus`            | Prometheus metrics scrape endpoint       |
| `GET /`                               | Interactive API documentation (Swagger)  |

### Grafana dashboards

After running with `--monitoring` or `--full`:

```bash
kubectl port-forward -n monitoring svc/kube-prometheus-stack-grafana 3000:80
```

Open `http://localhost:3000` → **admin / admin**

| Section                        | What to look at                                                     |
|--------------------------------|---------------------------------------------------------------------|
| Explore → Prometheus           | `http_server_requests_seconds_count` — request rate per endpoint   |
| Explore → Prometheus           | `jvm_memory_used_bytes` — heap and non-heap memory usage           |
| Explore → Prometheus           | `process_cpu_usage` — CPU consumption of the JVM                   |
| Explore → Loki                 | `{namespace="jac", app="cv-api"}` — structured JSON application logs|
| Dashboards → Import ID `19004` | Full Spring Boot dashboard (JVM, HTTP, GC, threads)                |

### Jaeger traces

After running with `--full`:

```bash
kubectl port-forward -n monitoring svc/jaeger-query 16686:16686
```

Open `http://localhost:16686`:

1. Select **Service** = `jb2dev-cv-api`.
2. Click **Find Traces**.
3. Click on any trace to see the full span tree.

Each HTTP request will show:
- The controller span (entry point, full URL and HTTP method).
- The use case / interactor span (business logic duration).
- The repository span (data loading from JSON files).

---

## Production Hardening Features

All of the following are enabled by default in `values.yaml` and require no extra flags:

### Auto-scaling (HPA)

The `HorizontalPodAutoscaler` scales pods between 2 and 5 replicas based on resource usage.

```bash
# Watch the HPA in real time
kubectl get hpa -n jac -w

# Expected (at rest):
# NAME              REFERENCE             TARGETS          MINPODS  MAXPODS  REPLICAS
# cv-api-...-hpa    Deployment/cv-api     5%/70%, 30%/80%  2        5        2
```

Scale-up triggers at 70% CPU or 80% memory. Scale-down has a 5-minute stabilisation window
to avoid flapping.

### Rolling updates

Every `helm upgrade` (e.g., when a new image is pushed via CI) performs a zero-downtime
rolling update:

```
maxUnavailable: 1   ← At most 1 pod down at a time
maxSurge: 1         ← At most 1 extra pod during the update
```

### Pod disruption budget

Ensures at least 1 pod is always available during node drain or voluntary disruptions:

```bash
kubectl get pdb -n jac
# NAME          MIN AVAILABLE  MAX UNAVAILABLE  ALLOWED DISRUPTIONS
# cv-api-...-pdb    1            N/A             1
```

### Security context

Verify the pod is running as non-root:

```bash
kubectl exec -n jac -it \
  $(kubectl get pod -n jac -l app=cv-api -o jsonpath='{.items[0].metadata.name}') \
  -- id

# Expected: uid=1000 gid=1000
```

### Network policy

Only ingress traffic from the `ingress-nginx` namespace is allowed to reach the pods on port 8080.
When monitoring is enabled, the `monitoring` namespace is also permitted. All other traffic is dropped.

---

## Teardown

### Remove releases only (keep Minikube running)

```bash
./k8s/scripts/teardown.sh
```

This uninstalls the `monitoring` and `cv-api` Helm releases and deletes the `jac` and
`monitoring` namespaces. Minikube keeps running so you can redeploy immediately.

### Delete the entire cluster

```bash
./k8s/scripts/teardown.sh --all
```

This runs `minikube delete -p jac-local`, removing all cluster state. The next `setup.sh`
invocation will create a fresh cluster from scratch.

---

## Troubleshooting

### Pod stuck in `Pending`

```bash
kubectl describe pod <pod-name> -n jac
# Look for "Events" at the bottom — often insufficient resources
```

Increase Minikube resources by editing `MINIKUBE_CPUS` and `MINIKUBE_MEMORY` in `setup.sh`.

### Pod stuck in `CrashLoopBackOff`

```bash
kubectl logs -n jac -l app=cv-api --previous
# Check for application startup errors
```

Common causes: wrong `SPRING_PROFILES_ACTIVE`, missing configuration, or image not loaded into Minikube.

### Image not found (`ErrImagePull` or `ImagePullBackOff`)

If using `--local-build`, ensure the image was loaded correctly:

```bash
minikube image ls -p jac-local | grep jac
# Should show: jac-api:local
```

If missing, run `setup.sh --local-build` again.

### `jac.local` not resolving

Add the Minikube IP to your hosts file:

```bash
# Get the IP
minikube ip -p jac-local

# Add to hosts (Linux/macOS)
echo "$(minikube ip -p jac-local) jac.local" | sudo tee -a /etc/hosts

# Windows (run PowerShell as Administrator)
Add-Content C:\Windows\System32\drivers\etc\hosts "$(minikube ip -p jac-local) jac.local"
```

### Prometheus not scraping the API

Check the ServiceMonitor was created and the target is visible:

```bash
kubectl get servicemonitor -n monitoring
kubectl port-forward -n monitoring svc/kube-prometheus-stack-prometheus 9090:9090
# Open http://localhost:9090/targets → search for cv-api → state should be UP
```

If the target shows `DOWN`, check that the monitoring release was installed before the ServiceMonitor
was enabled and that the `jac` namespace NetworkPolicy allows ingress from `monitoring`.

### Grafana shows no data from Loki

Verify Promtail is running and forwarding logs:

```bash
kubectl logs -n monitoring -l app=promtail -f
# Should show successful pushes to http://monitoring-loki:3100/loki/api/v1/push
```

Query in Grafana → Explore → Loki:
```
{namespace="jac", app="cv-api"}
```

If there are no results, generate some traffic first:
```bash
curl http://jac.local/api/v1/profile/personal
curl http://jac.local/actuator/health
```

### Jaeger troubleshooting

```bash
# Check Jaeger pod
kubectl get pods -n monitoring -l app.kubernetes.io/name=jaeger

# Expected:
# NAME           READY   STATUS    AGE
# jaeger-xxxx    1/1     Running   2m

# Check Jaeger service
kubectl get svc -n monitoring -l app.kubernetes.io/name=jaeger

# Expected:
# NAME            TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
# jaeger-query    ClusterIP   10.96.0.1      <none>        16686/TCP   2m

# Check Jaeger logs
kubectl logs -n monitoring -l app.kubernetes.io/name=jaeger
```
