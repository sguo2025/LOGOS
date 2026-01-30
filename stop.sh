#!/bin/bash

#===============================================================================
# LOGOS 智能规则中台 - 停止脚本
#===============================================================================

G='\033[0;32m'; Y='\033[1;33m'; NC='\033[0m'

echo -e "${Y}
===============================================================================
  停止 LOGOS 所有服务
===============================================================================${NC}
"

# 停止前端
echo -e "[INFO] 停止前端服务..."
pkill -9 -f "vite" 2>/dev/null || true
pkill -9 -f "node.*frontend" 2>/dev/null || true

# 停止后端
echo -e "[INFO] 停止后端服务..."
pkill -9 -f "spring-boot:run" 2>/dev/null || true
pkill -9 -f "logos-backend" 2>/dev/null || true
pkill -9 -f "LogosApplication" 2>/dev/null || true

# 停止 Neo4j (可选)
if [[ "$1" == "-a" || "$1" == "--all" ]]; then
    echo -e "[INFO] 停止 Neo4j..."
    docker stop neo4j-logos 2>/dev/null || true
fi

echo -e "${G}
[INFO] 所有服务已停止
${NC}
提示: 使用 ./stop.sh -a 同时停止 Neo4j
"
