#!/usr/bin/env bash
# =============================================================================
# teardown.sh — Removes jb2dev-cv-api resources from Minikube
#
# Usage:
#   ./k8s/scripts/teardown.sh         # Uninstalls Helm releases (Minikube stays)
#   ./k8s/scripts/teardown.sh --all   # Deletes the entire Minikube cluster
# =============================================================================

set -eo pipefail

# ── Colors ────────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
BOLD='\033[1m'; NC='\033[0m'

# ── Trap: always keep the window open; show error details on failure ──────────
_ERR_LINE=0; _ERR_CMD=""
_on_err()  { _ERR_LINE=$1; _ERR_CMD=$2; }
_on_exit() {
  local code=$?
  if [ "$code" -ne 0 ]; then
    echo -e "\n${RED}${BOLD}╔══════════════════════════════════════════════╗${NC}"
    echo -e "${RED}${BOLD}║   ✗  Teardown FAILED                         ║${NC}"
    echo -e "${RED}${BOLD}╚══════════════════════════════════════════════╝${NC}"
    echo -e "\n  ${RED}${BOLD}Exit code : $code${NC}"
    echo -e "  ${RED}${BOLD}Line      : $_ERR_LINE${NC}"
    echo -e "  ${RED}${BOLD}Command   : $_ERR_CMD${NC}\n"
  fi
  echo -e "${YELLOW}${BOLD}  Press Enter or type 'exit' to close this window...${NC}"
  while true; do
    read -r _in < /dev/tty
    [ "$_in" = "exit" ] || [ -z "$_in" ] && break
  done
}
trap '_on_err $LINENO "$BASH_COMMAND"' ERR
trap '_on_exit' EXIT

# ── Configuration ─────────────────────────────────────────────────────────────
MINIKUBE_PROFILE="jac-local"
RELEASE_API="cv-api"
RELEASE_MONITORING="monitoring"
NAMESPACE_API="jac"
NAMESPACE_MONITORING="monitoring"
DELETE_CLUSTER=false

for arg in "$@"; do
  [ "$arg" = "--all" ] && DELETE_CLUSTER=true
done

# ── Helpers ───────────────────────────────────────────────────────────────────
log()      { echo -e "\n${YELLOW}${BOLD}▶  $1${NC}"; }
log_ok()   { echo -e "   ${GREEN}✓  $1${NC}"; }
log_warn() { echo -e "   ${YELLOW}⚠  $1${NC}"; }

helm_uninstall() {
  local release=$1 namespace=$2
  if helm status "$release" -n "$namespace" >/dev/null 2>&1; then
    helm uninstall "$release" -n "$namespace" --wait 2>/dev/null \
      && log_ok "Release '$release' uninstalled" \
      || log_warn "Release '$release' could not be uninstalled (skipping)"
  else
    log_warn "Release '$release' not found — skipping"
  fi
}

wait_namespace_gone() {
  local ns=$1
  local retries=0
  while kubectl get namespace "$ns" >/dev/null 2>&1; do
    retries=$((retries + 1))
    [ $retries -ge 30 ] && { log_warn "Timeout waiting for namespace '$ns' to be deleted"; return; }
    log_warn "Waiting for namespace '$ns' to be fully removed... ($retries/30)"
    sleep 3
  done
  log_ok "Namespace '$ns' removed"
}

# ── Main ──────────────────────────────────────────────────────────────────────
clear
echo -e "${BOLD}"
echo -e "  ╔══════════════════════════════════════════════╗"
echo -e "  ║       jb2dev-cv-api — Teardown               ║"
echo -e "  ╚══════════════════════════════════════════════╝"
echo -e "${NC}"
echo -e "  Mode : $([ "$DELETE_CLUSTER" = true ] && echo "delete cluster (--all)" || echo "releases only (Minikube stays)")"
echo ""

if [ "$DELETE_CLUSTER" = true ]; then
  # ── Fix Docker CLI context before invoking Minikube ──────────────────────
  # Minikube prints a noisy warning when the Docker "default" context metadata
  # is missing.  Recreating it (idempotent) silences the warning cleanly.
  if ! docker context inspect default >/dev/null 2>&1; then
    docker context create default >/dev/null 2>&1 || true
  fi
  docker context use default >/dev/null 2>&1 || true

  log "Deleting Minikube cluster '$MINIKUBE_PROFILE'"
  minikube delete -p "$MINIKUBE_PROFILE"
  log_ok "Cluster '$MINIKUBE_PROFILE' deleted — all data removed"

  echo ""
  echo -e "${GREEN}${BOLD}╔══════════════════════════════════════════════╗${NC}"
  echo -e "${GREEN}${BOLD}║   ✓  Teardown completed successfully         ║${NC}"
  echo -e "${GREEN}${BOLD}╚══════════════════════════════════════════════╝${NC}"
  echo ""
  echo -e "  Cluster ${BOLD}$MINIKUBE_PROFILE${NC} and all its data have been removed."
  echo -e "  Recreate : ${BOLD}./k8s/scripts/setup.sh [--monitoring|--full]${NC}"
  echo ""

else
  log "Uninstalling Helm releases"
  helm_uninstall "$RELEASE_MONITORING" "$NAMESPACE_MONITORING"
  helm_uninstall "$RELEASE_API"        "$NAMESPACE_API"

  log "Deleting namespaces"
  kubectl delete namespace "$NAMESPACE_MONITORING" --ignore-not-found
  kubectl delete namespace "$NAMESPACE_API"        --ignore-not-found

  # Wait until both namespaces are fully gone before returning
  # so that a subsequent setup.sh starts with a clean slate
  wait_namespace_gone "$NAMESPACE_MONITORING"
  wait_namespace_gone "$NAMESPACE_API"

  echo ""
  echo -e "${GREEN}${BOLD}╔══════════════════════════════════════════════╗${NC}"
  echo -e "${GREEN}${BOLD}║   ✓  Teardown completed successfully         ║${NC}"
  echo -e "${GREEN}${BOLD}╚══════════════════════════════════════════════╝${NC}"
  echo ""
  echo -e "  Minikube is still running (profile: ${BOLD}$MINIKUBE_PROFILE${NC})"
  echo -e "  Redeploy : ${BOLD}./k8s/scripts/setup.sh [--monitoring|--full]${NC}"
  echo ""
fi


