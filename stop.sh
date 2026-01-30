#!/bin/bash

#===============================================================================
# LOGOS 智能规则中台 - 停止脚本
#===============================================================================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }

echo ""
echo "==============================================================================="
echo -e "${YELLOW}停止 LOGOS 所有服务${NC}"
echo "==============================================================================="
echo ""

# 停止前端
log_info "停止前端服务..."
pkill -f "vite" 2>/dev/null || true

# 停止后端
log_info "停止后端服务..."
pkill -f "spring-boot:run" 2>/dev/null || true
pkill -f "logos-backend" 2>/dev/null || true

# 停止 Neo4j (可选)
read -p "是否同时停止 Neo4j? [y/N] " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    log_info "停止 Neo4j..."
    docker stop neo4j-logos 2>/dev/null || true
fi

log_info "所有服务已停止"
