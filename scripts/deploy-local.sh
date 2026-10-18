#!/usr/bin/env bash
#
# 本地构建 + 直传服务器部署（替代 GitHub Actions 的跨境上传，约 1~3 分钟）
#
# 用法（在仓库根目录执行）：
#   ./scripts/deploy-local.sh backend    # 只部署后端两个 jar
#   ./scripts/deploy-local.sh frontend   # 只部署前台 dist
#   ./scripts/deploy-local.sh admin      # 只部署后台 dist
#   ./scripts/deploy-local.sh all        # 全部（默认）
#
# 前置条件：
#   1. Maven：~/tools/apache-maven-3.9.9（没有就下载，脚本会自动下载）
#   2. JDK 17：/Users/yuuu/Library/Java/JavaVirtualMachines/ms-17.0.18
#   3. 本机可免密 SSH 到服务器（已配好 ~/.ssh 密钥）
#
set -euo pipefail

SERVER_HOST="${BLOG_SERVER_HOST:-124.221.214.115}"
SERVER_USER="${BLOG_SERVER_USER:-root}"
SSH_PORT="${BLOG_SERVER_SSH_PORT:-22}"
DEPLOY_ROOT="/app/blog/blog/app/app"
STAGE="/app/deploy/incoming"

MAVEN_HOME="${HOME}/tools/apache-maven-3.9.9"
JAVA_HOME_PATH="/Users/yuuu/Library/Java/JavaVirtualMachines/ms-17.0.18/Contents/Home"
MVN="${MAVEN_HOME}/bin/mvn"
SSH="ssh -p ${SSH_PORT} -o BatchMode=yes ${SERVER_USER}@${SERVER_HOST}"
RSYNC="rsync -e \"ssh -p ${SSH_PORT} -o BatchMode=yes\" -rlptz --inplace --partial --timeout 600"

SCOPE="${1:-all}"
echo "==> 部署目标：${SCOPE} -> ${SERVER_USER}@${SERVER_HOST}"

# ---------- 环境检查 ----------
if [ ! -x "${MVN}" ]; then
  echo "==> 未找到 Maven，尝试下载到 ${MAVEN_HOME}"
  mkdir -p "${HOME}/tools"
  curl -sL -o /tmp/maven-bin.tar.gz \
    https://repo.huaweicloud.com/apache/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz
  tar -xzf /tmp/maven-bin.tar.gz -C "${HOME}/tools"
fi
if [ ! -d "${JAVA_HOME_PATH}" ]; then
  echo "错误：未找到 JDK 17（${JAVA_HOME_PATH}）" >&2
  exit 1
fi
export JAVA_HOME="${JAVA_HOME_PATH}"
export PATH="${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"

# ---------- 构建 ----------
if [ "${SCOPE}" = "backend" ] || [ "${SCOPE}" = "all" ]; then
  echo "==> 构建后端（跳过测试）"
  (cd backend && "${MVN}" -q -DskipTests package)
  JAR_BLOG="backend/yuuu-blog/target/yuuu-blog.jar"
  JAR_ADMIN="backend/yuuu-admin/target/yuuu-admin.jar"
  for j in "${JAR_BLOG}" "${JAR_ADMIN}"; do
    [ -f "${j}" ] || { echo "错误：构建产物缺失 ${j}" >&2; exit 1; }
    unzip -t "${j}" >/dev/null 2>&1 || { echo "错误：jar 完整性校验失败 ${j}" >&2; exit 1; }
  done
  echo "==> 构建产物校验通过"
fi

if [ "${SCOPE}" = "frontend" ] || [ "${SCOPE}" = "all" ]; then
  echo "==> 构建前台 dist"
  (cd frontend && NODE_OPTIONS=--openssl-legacy-provider npm run build)
fi

if [ "${SCOPE}" = "admin" ] || [ "${SCOPE}" = "all" ]; then
  echo "==> 构建后台 dist"
  (cd admin && npm run build:prod)
fi

# ---------- 上传 ----------
echo "==> 上传"
eval "${SSH}" "mkdir -p ${STAGE}/jars"
if [ "${SCOPE}" = "backend" ] || [ "${SCOPE}" = "all" ]; then
  eval "${RSYNC}" "backend/yuuu-blog/target/yuuu-blog.jar" "${SERVER_USER}@${SERVER_HOST}:${STAGE}/jars/yuuu-blog.jar"
  eval "${RSYNC}" "backend/yuuu-admin/target/yuuu-admin.jar" "${SERVER_USER}@${SERVER_HOST}:${STAGE}/jars/yuuu-admin.jar"
fi
if [ "${SCOPE}" = "frontend" ] || [ "${SCOPE}" = "all" ]; then
  eval "${RSYNC}" "frontend/dist/" "${SERVER_USER}@${SERVER_HOST}:${STAGE}/frontend/"
fi
if [ "${SCOPE}" = "admin" ] || [ "${SCOPE}" = "all" ]; then
  eval "${RSYNC}" "admin/dist/" "${SERVER_USER}@${SERVER_HOST}:${STAGE}/admin/"
fi

# ---------- 服务器应用 ----------
echo "==> 服务器端应用"
eval "${SSH}" "bash -s ${DEPLOY_ROOT} ${STAGE} ${SCOPE}" <<'REMOTE'
set -e
DEPLOY_ROOT="$1"
STAGE="$2"
SCOPE="$3"
TS=$(date +%s)
BK=/app/deploy/backup/$TS
mkdir -p "$BK"

APPLIED=0
if [ "$SCOPE" = "backend" ] || [ "$SCOPE" = "all" ]; then
  systemctl stop yuuu-admin yuuu-blog || true
  for pair in "yuuu-blog:$DEPLOY_ROOT/front" "yuuu-admin:$DEPLOY_ROOT/backend"; do
    NAME=${pair%%:*}; DIR=${pair#*:}
    SRC="$STAGE/jars/$NAME.jar"
    JAR="$DIR/$NAME.jar"
    if [ -f "$SRC" ]; then
      unzip -t "$SRC" >/dev/null 2>&1 || { echo "$NAME: 完整性校验失败，跳过"; continue; }
      cp -a "$JAR" "$BK/" 2>/dev/null || true
      cp -f "$SRC" "$JAR"
      echo "$NAME: 已替换"
      APPLIED=1
    fi
  done
  systemctl start yuuu-blog yuuu-admin
fi

if [ "$SCOPE" = "frontend" ] || [ "$SCOPE" = "all" ]; then
  cp -a "$DEPLOY_ROOT/front/dist" "$BK/front-dist" 2>/dev/null || true
  rsync -a "$STAGE/frontend/" "$DEPLOY_ROOT/front/dist/"
  echo "frontend dist: 已部署"
fi
if [ "$SCOPE" = "admin" ] || [ "$SCOPE" = "all" ]; then
  cp -a "$DEPLOY_ROOT/backend/dist" "$BK/backend-dist" 2>/dev/null || true
  rsync -a "$STAGE/admin/" "$DEPLOY_ROOT/backend/dist/"
  echo "admin dist: 已部署"
fi

if [ "$APPLIED" = "1" ]; then
  for i in $(seq 1 30); do
    sleep 4
    if curl -sf -o /dev/null http://127.0.0.1:8989/ && curl -sf -o /dev/null "http://127.0.0.1:7777/category/getCategoryList"; then
      echo "healthcheck OK"
      break
    fi
    [ $i -eq 30 ] && { echo "healthcheck FAILED，回滚"; cp -f "$BK"/*.jar "$DEPLOY_ROOT/front/" 2>/dev/null || true; exit 1; }
  done
fi
echo "backup: $BK"
REMOTE

echo "==> 完成"
