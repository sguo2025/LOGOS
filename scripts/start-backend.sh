#!/bin/bash

# LOGOS 后端启动脚本

echo "========================================="
echo "  LOGOS 智能规则中台 - 后端服务启动"
echo "========================================="

cd /workspaces/LOGOS/backend

# 检查 Maven 是否安装
if ! command -v mvn &> /dev/null; then
    echo "Maven 未安装，正在安装..."
    sudo apt-get update && sudo apt-get install -y maven
fi

# 检查 Java 版本
java_version=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
echo "当前 Java 版本: $java_version"

# 检查 Neo4j 是否运行
if docker ps | grep -q logos-neo4j; then
    echo "✓ Neo4j 容器正在运行"
else
    echo "✗ Neo4j 未运行，请先启动 Neo4j"
    exit 1
fi

# 编译并启动
echo ""
echo "正在编译项目..."
mvn clean compile -DskipTests -q

if [ $? -eq 0 ]; then
    echo "✓ 编译成功"
    echo ""
    echo "正在启动 LOGOS 后端服务..."
    echo "API 文档地址: http://localhost:8080/api/logos/v1/swagger-ui.html"
    echo ""
    mvn spring-boot:run
else
    echo "✗ 编译失败"
    exit 1
fi
