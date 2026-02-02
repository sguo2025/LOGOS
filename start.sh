#!/bin/bash

#===============================================================================
# LOGOS 智能规则中台 - 一键启动脚本
#===============================================================================

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
mkdir -p "$PROJECT_ROOT/logs"

# 颜色
G='\033[0;32m'; Y='\033[1;33m'; B='\033[0;34m'; NC='\033[0m'

echo -e "${G}
===============================================================================
  LOGOS 智能规则中台 - 一键启动
===============================================================================${NC}
"

#--- 1. Java 环境 ---
echo -e "${B}[1/4]${NC} 检查 Java 环境..."
[[ -f "/usr/local/sdkman/bin/sdkman-init.sh" ]] && source "/usr/local/sdkman/bin/sdkman-init.sh"
JAVA_VER=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [[ "$JAVA_VER" -lt 21 ]]; then
    echo "  安装 Java 21..."
    sdk install java 21.0.9-amzn -y 2>/dev/null || true
    echo "y" | sdk use java 21.0.9-amzn 2>/dev/null || true
    source "/usr/local/sdkman/bin/sdkman-init.sh"
fi
echo -e "  ${G}✓${NC} Java $(java -version 2>&1 | head -1 | cut -d'"' -f2)"

#--- 2. Neo4j ---
echo -e "${B}[2/4]${NC} 启动 Neo4j..."
if ! docker ps --format '{{.Names}}' | grep -q "neo4j-logos"; then
    docker rm -f neo4j-logos 2>/dev/null || true
    docker run -d --name neo4j-logos \
        -p 7474:7474 -p 7687:7687 \
        -e NEO4J_AUTH=neo4j/logos2024 \
        --restart unless-stopped \
        neo4j:5.15.0 >/dev/null 2>&1
    echo "  等待 Neo4j 就绪..."
    for i in {1..30}; do curl -s http://localhost:7474 >/dev/null 2>&1 && break || sleep 2; done
fi
echo -e "  ${G}✓${NC} Neo4j http://localhost:7474"

#--- 3. 后端 ---
echo -e "${B}[3/4]${NC} 启动后端服务..."
if ! curl -s http://localhost:8080/api/logos/v1/actuator/health 2>/dev/null | grep -q "UP"; then
    cd "$PROJECT_ROOT/backend"
    pkill -f "spring-boot:run" 2>/dev/null || true
    mvn spring-boot:run -Dspring-boot.run.profiles=dev > "$PROJECT_ROOT/logs/backend.log" 2>&1 &
    echo "  等待后端就绪..."
    for i in {1..60}; do curl -s http://localhost:8080/api/logos/v1/actuator/health 2>/dev/null | grep -q "UP" && break || sleep 2; done
fi
echo -e "  ${G}✓${NC} 后端 API http://localhost:8080/api/logos/v1"

#--- 4. 前端 ---
echo -e "${B}[4/4]${NC} 启动前端服务..."
cd "$PROJECT_ROOT/frontend"
if ! curl -s http://localhost:3000 >/dev/null 2>&1; then
    [[ ! -d "node_modules" ]] && npm install --silent
    pkill -f "vite" 2>/dev/null || true
    npm run dev > "$PROJECT_ROOT/logs/frontend.log" 2>&1 &
    for i in {1..15}; do curl -s http://localhost:3000 >/dev/null 2>&1 && break || sleep 1; done
fi
echo -e "  ${G}✓${NC} 前端 http://localhost:3000"

#--- 完成 ---
echo -e "${G}
===============================================================================
  所有服务已启动！
===============================================================================${NC}
  前端应用:     http://localhost:3000
  后端 API:     http://localhost:8080/api/logos/v1
  Swagger UI:   http://localhost:8080/api/logos/v1/swagger-ui.html
  Neo4j 浏览器: http://localhost:7474 (neo4j/logos2024)
===============================================================================
"
