# LOGOS Neo4j 脚本说明

## 文件清单

| 文件 | 说明 | 行数 |
|------|------|------|
| `init.cypher` | 初始化脚本（精简版） | 58 行 |
| `export.cypher` | 完整导出脚本（带注释） | 202 行 |

## 脚本结构

`export.cypher` 包含以下 10 个部分：

| 部分 | 内容 |
|------|------|
| **第一部分** | 清理数据（可选，生产环境慎用） |
| **第二部分** | 6 个唯一性约束 |
| **第三部分** | 3 个核心实体节点（RuleContext、ProdInst、BusinessConstraint） |
| **第四部分** | 实体间关系拓扑 |
| **第五部分** | 5 个元数据节点（物理字段↔语义映射） |
| **第六部分** | 业务约束节点（BC_LX_001） |
| **第七部分** | 3 个全局原子动作 |
| **第八部分** | 规则实例节点（RULE_LX_001） |
| **第九部分** | 规则实例关系 |
| **第十部分** | 数据字典注释 |

## 数据统计

当前数据库包含：

| 节点类型 | 数量 | 说明 |
|----------|------|------|
| Entity | 3 | RuleContext、ProdInst、BusinessConstraint |
| Metadata | 5 | soId、operType、prodId、businessTypeCode、actionType |
| BusinessConstraint | 1 | BC_LX_001（灵犀融合光网约束） |
| Action | 3 | ShouldSkipCheck、ValidateConstraint、BlockExecution |
| RuleInstance | 1 | RULE_LX_001（灵犀融合光网准入规则） |

## 使用方式

### 1. 首次初始化

```bash
# 使用初始化脚本
cat init.cypher | docker exec -i neo4j-logos cypher-shell -u neo4j -p logos2024
```

### 2. 完整导入（带注释版本）

```bash
# 使用完整导出脚本
cat export.cypher | docker exec -i neo4j-logos cypher-shell -u neo4j -p logos2024
```

### 3. 清空数据库后重新导入

```bash
# 先清空
docker exec -i neo4j-logos cypher-shell -u neo4j -p logos2024 <<< "MATCH (n) DETACH DELETE n;"

# 再导入
cat export.cypher | docker exec -i neo4j-logos cypher-shell -u neo4j -p logos2024
```

### 4. 通过 Neo4j 浏览器导入

1. 打开 http://localhost:7474
2. 登录（neo4j / logos2024）
3. 复制脚本内容到查询框执行

## 数据字典

### 产品ID
| 值 | 含义 |
|----|------|
| 80000122 | 灵犀专线 |

### 业务类型编码 (COL1)
| 值 | 含义 |
|----|------|
| 1 | 普通业务 |
| 2 | FTTR业务 |
| 3 | 融合光网 |

### 服务提供ID (soId)
| 值 | 含义 |
|----|------|
| 2831 | 拆机操作 |

### 操作类型 (operType)
| 值 | 含义 |
|----|------|
| 1100 | 查询 |
| 1200 | 预提交 |
| 其他 | 正式提交 |

## 本体架构图

```
┌───────────────┐
│  RuleContext  │  规则上下文
│  ├─ soId      │  服务提供ID
│  └─ operType  │  操作类型
└───────┬───────┘
        │ appliesTo
        ▼
┌───────────────┐
│   ProdInst    │  产品实例
│  ├─ prodId    │  产品规格ID (PROD_ID)
│  ├─ businessTypeCode │  业务类型编码 (COL1)
│  └─ actionType│  动作类型
└───────┬───────┘
        │ isConstrainedBy
        ▼
┌───────────────┐
│BusinessConstr │  业务约束
│  └─ BC_LX_001 │  灵犀融合光网约束
└───────────────┘
        │ IMPLEMENTS
        ▼
┌───────────────┐
│ RuleInstance  │  规则实例
│  └─ RULE_LX_001│  灵犀融合光网准入规则
└───────────────┘
```

## 更新日期

- 2026-01-30：初始版本，包含灵犀专线融合光网规则
