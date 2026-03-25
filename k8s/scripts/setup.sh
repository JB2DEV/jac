#!/usr/bin/env bash
# =============================================================================
# setup.sh — Deploys jb2dev-cv-api in Minikube using local Helm charts
#
# Usage:
#   ./k8s/scripts/setup.sh                  # API only
#   ./k8s/scripts/setup.sh --monitoring     # API + Prometheus + Grafana + Loki
#   ./k8s/scripts/setup.sh --full           # API + monitoring + Jaeger
#   ./k8s/scripts/setup.sh --local-build    # Force local Docker build
#
# Local charts:
#   k8s/charts/cv-api/        → API Helm chart
#   k8s/charts/monitoring/    → Observability umbrella chart
# =============================================================================

set -eo pipefail

# ── Colors ────────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
BLUE='\033[0;34m'; BOLD='\033[1m'; NC='\033[0m'

# ── Paths ─────────────────────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
CHART_API="$REPO_ROOT/k8s/charts/cv-api"
CHART_MONITORING="$REPO_ROOT/k8s/charts/monitoring"

# ── Configuration ─────────────────────────────────────────────────────────────
MINIKUBE_PROFILE="jac-local"
MINIKUBE_CPUS="4"
MINIKUBE_MEMORY="4096"
NAMESPACE_API="jac"
NAMESPACE_MONITORING="monitoring"
RELEASE_API="cv-api"
RELEASE_MONITORING="monitoring"
IMAGE_REMOTE="ghcr.io/jb2dev/jac:latest"
IMAGE_LOCAL="jac-api:local"

# ── Flags ─────────────────────────────────────────────────────────────────────
WITH_MONITORING=false
WITH_FULL=false
LOCAL_BUILD=false

for arg in "$@"; do
  case $arg in
    --monitoring)  WITH_MONITORING=true ;;
    --full)        WITH_MONITORING=true; WITH_FULL=true ;;
    --local-build) LOCAL_BUILD=true ;;
  esac
done

# ── Helpers ───────────────────────────────────────────────────────────────────
log()      { echo -e "\n${BLUE}${BOLD}▶  $1${NC}"; }
log_ok()   { echo -e "   ${GREEN}✓  $1${NC}"; }
log_warn() { echo -e "   ${YELLOW}⚠  $1${NC}"; }
log_err()  { echo -e "\n${RED}${BOLD}✗  ERROR: $1${NC}\n"; exit 1; }

# ── Cleans a Helm release if it is in a non-deployable state ─────────────────
helm_clean_if_needed() {
  local release=$1 namespace=$2
  local status
  status=$(helm status "$release" -n "$namespace" -o json 2>/dev/null \
           | grep -o '"status":"[^"]*"' | head -1 | cut -d'"' -f4 \
           || echo "not-found")

  case "$status" in
    deployed)
      log_ok "Release '$release' already deployed — will upgrade" ;;
    "not-found"|"")
      log_ok "Release '$release' not found — will install fresh" ;;
    pending-install|pending-upgrade|pending-rollback|failed|unknown)
      log_warn "Release '$release' stuck in '$status' — cleaning..."
      helm uninstall "$release" -n "$namespace" --wait 2>/dev/null || true
      sleep 3
      log_ok "Release '$release' cleaned" ;;
  esac
}

# ── 1. Prerequisites ──────────────────────────────────────────────────────────
check_prereqs() {
  log "Checking prerequisites"
  for cmd in docker minikube kubectl helm; do
    command -v "$cmd" >/dev/null 2>&1 \
      || log_err "'$cmd' not found — install it before running this script."
    log_ok "$cmd  →  $(command -v "$cmd")"
  done
  [ -d "$CHART_API" ]        || log_err "Chart not found: $CHART_API"
  [ -d "$CHART_MONITORING" ] || log_err "Chart not found: $CHART_MONITORING"
  log_ok "Local charts found"
}

# ── 2. Minikube ───────────────────────────────────────────────────────────────
start_minikube() {
  log "Starting Minikube (profile: $MINIKUBE_PROFILE)"

  if minikube status -p "$MINIKUBE_PROFILE" --format='{{.Host}}' 2>/dev/null \
       | grep -q "Running"; then
    log_warn "Minikube already running — skipping start"
  else
    minikube start \
      --profile="$MINIKUBE_PROFILE" \
      --cpus="$MINIKUBE_CPUS" \
      --memory="${MINIKUBE_MEMORY}mb" \
      --driver=docker \
      --addons=ingress,metrics-server
    log_ok "Cluster started"
  fi

  kubectl config use-context "$MINIKUBE_PROFILE"
  log_ok "kubectl context → $MINIKUBE_PROFILE"
}

# ── 3. Docker image ───────────────────────────────────────────────────────────
prepare_image() {
  log "Preparing Docker image"

  if [ "$LOCAL_BUILD" = true ]; then
    log_warn "Local build requested — building from source..."
    docker build -t "$IMAGE_LOCAL" "$REPO_ROOT"
    minikube image load "$IMAGE_LOCAL" -p "$MINIKUBE_PROFILE"
    DEPLOY_IMAGE_REPO="${IMAGE_LOCAL%:*}"
    DEPLOY_IMAGE_TAG="${IMAGE_LOCAL##*:}"
    log_ok "Built and loaded: $IMAGE_LOCAL"

  elif docker pull "$IMAGE_REMOTE" 2>/dev/null; then
    minikube image load "$IMAGE_REMOTE" -p "$MINIKUBE_PROFILE"
    DEPLOY_IMAGE_REPO="${IMAGE_REMOTE%:*}"
    DEPLOY_IMAGE_TAG="${IMAGE_REMOTE##*:}"
    log_ok "Pulled and loaded from GHCR: $IMAGE_REMOTE"

  else
    log_warn "GHCR pull failed — falling back to local build..."
    docker build -t "$IMAGE_LOCAL" "$REPO_ROOT"
    minikube image load "$IMAGE_LOCAL" -p "$MINIKUBE_PROFILE"
    DEPLOY_IMAGE_REPO="${IMAGE_LOCAL%:*}"
    DEPLOY_IMAGE_TAG="${IMAGE_LOCAL##*:}"
    log_ok "Built and loaded: $IMAGE_LOCAL"
  fi

  export DEPLOY_IMAGE_REPO DEPLOY_IMAGE_TAG
}

# ── 4. API ────────────────────────────────────────────────────────────────────
deploy_api() {
  log "Deploying cv-api"

  helm_clean_if_needed "$RELEASE_API" "$NAMESPACE_API"

  helm upgrade --install "$RELEASE_API" "$CHART_API" \
    --namespace "$NAMESPACE_API" \
    --create-namespace \
    --set "image.repository=$DEPLOY_IMAGE_REPO" \
    --set "image.tag=$DEPLOY_IMAGE_TAG" \
    --set "serviceMonitor.enabled=false" \
    --wait \
    --timeout 5m

  log_ok "API deployed  →  namespace '$NAMESPACE_API'"
}

# ── 5. Monitoring dependencies ────────────────────────────────────────────────
resolve_monitoring_deps() {
  log "Resolving monitoring chart dependencies"

  helm repo add prometheus-community \
    https://prometheus-community.github.io/helm-charts 2>/dev/null || true
  helm repo add grafana \
    https://grafana.github.io/helm-charts 2>/dev/null || true
  log_ok "Helm repos registered"

  log_warn "Updating repo index..."
  helm repo update

  log_warn "Running helm dependency update..."
  helm dependency update "$CHART_MONITORING"
  log_ok "Chart dependencies ready"
}

# ── 6. Monitoring stack ───────────────────────────────────────────────────────
deploy_monitoring() {
  log "Deploying monitoring stack (Prometheus + Grafana + Loki)"

  resolve_monitoring_deps

  helm_clean_if_needed "$RELEASE_MONITORING" "$NAMESPACE_MONITORING"

  local jaeger_flag="false"
  [ "$WITH_FULL" = true ] && jaeger_flag="true"

  # Install without --wait: Loki's probe initialDelaySeconds is long,
  # we track readiness ourselves to give live feedback.
  helm upgrade --install "$RELEASE_MONITORING" "$CHART_MONITORING" \
    --namespace "$NAMESPACE_MONITORING" \
    --create-namespace \
    --set "jaeger.enabled=$jaeger_flag" \
    --timeout 12m

  log_ok "Monitoring chart applied  →  namespace '$NAMESPACE_MONITORING'"

  # ── Wait for every pod in monitoring to be Ready ──────────────────────────
  log "Waiting for monitoring pods to be Ready (may take ~2 min)..."
  local deadline=$(( $(date +%s) + 600 ))   # 10 min hard limit
  while true; do
    local unready
    unready=$(kubectl get pods -n "$NAMESPACE_MONITORING" \
      --no-headers 2>/dev/null \
      | awk '{split($2,a,"/"); if(a[1]!=a[2]) print $1}' \
      | grep -v "^$" | wc -l | tr -d ' ')

    if [ "$unready" -eq 0 ]; then
      break
    fi

    if [ "$(date +%s)" -ge "$deadline" ]; then
      log_warn "Timeout waiting for monitoring pods — continuing anyway"
      kubectl get pods -n "$NAMESPACE_MONITORING" 2>/dev/null || true
      break
    fi

    # Show which pods are still pending
    local pending_names
    pending_names=$(kubectl get pods -n "$NAMESPACE_MONITORING" \
      --no-headers 2>/dev/null \
      | awk '{split($2,a,"/"); if(a[1]!=a[2]) print $1}' \
      | tr '\n' ' ')
    log_warn "  Not ready yet: $pending_names— waiting 10 s..."
    sleep 10
  done
  log_ok "All monitoring pods are Ready"

  # ── Wait for Prometheus CRDs ──────────────────────────────────────────────
  log "Waiting for Prometheus CRDs..."
  local retries=0
  until kubectl get crd servicemonitors.monitoring.coreos.com >/dev/null 2>&1; do
    retries=$((retries + 1))
    [ $retries -ge 24 ] && log_err "Timed out waiting for Prometheus CRDs (4 min)"
    log_warn "CRD not ready — retrying in 10 s... ($retries/24)"
    sleep 10
  done
  log_ok "Prometheus CRDs ready"

  # ── Enable ServiceMonitor on the API release ──────────────────────────────
  log "Enabling ServiceMonitor on '$RELEASE_API'"

  helm upgrade --install "$RELEASE_API" "$CHART_API" \
    --namespace "$NAMESPACE_API" \
    --create-namespace \
    --reuse-values \
    --set "serviceMonitor.enabled=true" \
    --wait \
    --timeout 3m

  log_ok "ServiceMonitor enabled  →  Prometheus scrapes /actuator/prometheus every 15 s"
}

# ── 7. /etc/hosts hint ────────────────────────────────────────────────────────
configure_hosts() {
  log "Network access"
  local ip
  ip=$(minikube ip -p "$MINIKUBE_PROFILE" 2>/dev/null || echo "")

  if [ -z "$ip" ]; then
    log_warn "Could not resolve Minikube IP"
    return
  fi

  if grep -q "jac.local" /etc/hosts 2>/dev/null; then
    log_ok "jac.local already in /etc/hosts"
  else
    echo ""
    echo -e "  ${YELLOW}Add jac.local to your hosts file to use http://jac.local :${NC}"
    echo ""
    echo -e "  ${BOLD}Linux / macOS:${NC}"
    echo -e "    echo '$ip jac.local' | sudo tee -a /etc/hosts"
    echo ""
    echo -e "  ${BOLD}Windows  (PowerShell as Administrator):${NC}"
    echo -e "    Add-Content C:\\Windows\\System32\\drivers\\etc\\hosts '$ip jac.local'"
    echo ""
  fi
}

# ── 8. Summary ────────────────────────────────────────────────────────────────
print_summary() {
  local svc_api svc_grafana
  svc_api=$(kubectl get svc -n "$NAMESPACE_API" \
    -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "cv-api-service")
  svc_grafana=$(kubectl get svc -n "$NAMESPACE_MONITORING" \
    -l "app.kubernetes.io/name=grafana" \
    -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "monitoring-grafana")

  echo ""
  echo -e "${GREEN}${BOLD}╔══════════════════════════════════════════════╗${NC}"
  echo -e "${GREEN}${BOLD}║   ✓  Setup completed successfully            ║${NC}"
  echo -e "${GREEN}${BOLD}╚══════════════════════════════════════════════╝${NC}"
  echo ""

  echo -e "  ${BOLD}Releases:${NC}"
  helm list -n "$NAMESPACE_API" 2>/dev/null | grep -v "^$" || true
  if [ "$WITH_MONITORING" = true ]; then
    helm list -n "$NAMESPACE_MONITORING" 2>/dev/null | grep -v "^$" || true
  fi
  echo ""

  echo -e "  ${BOLD}━━━ API ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  echo -e "  Port-forward : kubectl port-forward -n $NAMESPACE_API svc/$svc_api 8080:8080"
  echo -e "  Health       : ${BLUE}http://localhost:8080/actuator/health${NC}"
  echo -e "  Swagger      : ${BLUE}http://localhost:8080/${NC}"
  echo -e "  Via Ingress  : ${BLUE}http://jac.local/${NC}  (requires /etc/hosts)"
  echo ""

  if [ "$WITH_MONITORING" = true ]; then
    echo -e "  ${BOLD}━━━ Grafana ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "  Port-forward : kubectl port-forward -n $NAMESPACE_MONITORING svc/$svc_grafana 3000:80"
    echo -e "  URL          : ${BLUE}http://localhost:3000${NC}  (admin / admin)"
    echo -e "  Prometheus   : Explore → Prometheus → jvm_memory_used_bytes"
    echo -e "  Logs         : Explore → Loki → {namespace=\"$NAMESPACE_API\",app=\"cv-api\"}"
    echo ""
  fi

  if [ "$WITH_FULL" = true ]; then
    echo -e "  ${BOLD}━━━ Jaeger ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "  Port-forward : kubectl port-forward -n $NAMESPACE_MONITORING svc/jaeger-query 16686:16686"
    echo -e "  URL          : ${BLUE}http://localhost:16686${NC}"
    echo ""
  fi

  echo -e "  ${BOLD}━━━ Verify pods ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  echo -e "  kubectl get pods -n $NAMESPACE_API"
  if [ "$WITH_MONITORING" = true ]; then
    echo -e "  kubectl get pods -n $NAMESPACE_MONITORING"
  fi
  echo ""
  echo -e "  Teardown: ${BOLD}./k8s/scripts/teardown.sh${NC}"
  echo ""
}

# ── Main ──────────────────────────────────────────────────────────────────────
clear
echo -e "${BOLD}"
echo -e "  ╔══════════════════════════════════════════════╗"
echo -e "  ║       jb2dev-cv-api — Kubernetes Setup       ║"
echo -e "  ╚══════════════════════════════════════════════╝"
echo -e "${NC}"
echo -e "  Profile    : $MINIKUBE_PROFILE"
echo -e "  Mode       : $([ "$WITH_FULL" = true ] && echo "full (API + Monitoring + Jaeger)" || ([ "$WITH_MONITORING" = true ] && echo "monitoring (API + Prometheus + Grafana + Loki)" || echo "api-only"))"
echo -e "  Image src  : $([ "$LOCAL_BUILD" = true ] && echo "local build" || echo "GHCR (ghcr.io/jb2dev/jac:latest)")"
echo ""

check_prereqs
start_minikube
prepare_image
deploy_api
[ "$WITH_MONITORING" = true ] && deploy_monitoring
configure_hosts
print_summary

# ── Keep the window open ──────────────────────────────────────────────────────
echo -e "${YELLOW}${BOLD}  Press Enter or type 'exit' to close this window...${NC}"
while true; do
  read -r input
  [ "$input" = "exit" ] || [ -z "$input" ] && break
done

