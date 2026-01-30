// ============================================================================
// LOGOS 智能规则中台 - Neo4j 完整导出脚本
// 生成日期: 2026-01-30
// 说明: 包含所有节点、关系和约束的完整数据库结构
// ============================================================================

// ============================================================================
// 第一部分: 清理现有数据（可选，生产环境慎用）
// ============================================================================
// MATCH (n) DETACH DELETE n;

// ============================================================================
// 第二部分: 创建唯一性约束
// ============================================================================
CREATE CONSTRAINT entity_code IF NOT EXISTS FOR (e:Entity) REQUIRE e.code IS UNIQUE;
CREATE CONSTRAINT metadata_id IF NOT EXISTS FOR (m:Metadata) REQUIRE m.id IS UNIQUE;
CREATE CONSTRAINT metadata_path IF NOT EXISTS FOR (m:Metadata) REQUIRE m.path IS UNIQUE;
CREATE CONSTRAINT constraint_id IF NOT EXISTS FOR (b:BusinessConstraint) REQUIRE b.id IS UNIQUE;
CREATE CONSTRAINT action_code IF NOT EXISTS FOR (a:Action) REQUIRE a.code IS UNIQUE;
CREATE CONSTRAINT rule_instance_id IF NOT EXISTS FOR (r:RuleInstance) REQUIRE r.id IS UNIQUE;

// ============================================================================
// 第三部分: 核心实体节点（三维本体架构）
// ============================================================================
// Entity: 规则上下文 - 输入参数与执行环境
MERGE (c:Entity {
    code: 'RuleContext', 
    name: '规则上下文', 
    desc: '输入参数与执行环境'
});

// Entity: 产品实例 - 规则检查的目标对象
MERGE (p:Entity {
    code: 'ProdInst', 
    name: '产品实例', 
    desc: '规则检查的目标对象'
});

// Entity: 业务约束 - 业务逻辑与判断条件
MERGE (b:Entity {
    code: 'BusinessConstraint', 
    name: '业务约束', 
    desc: '业务逻辑与判断条件'
});

// ============================================================================
// 第四部分: 实体间关系拓扑
// ============================================================================
MATCH (c:Entity {code: 'RuleContext'}), (p:Entity {code: 'ProdInst'}), (b:Entity {code: 'BusinessConstraint'})
MERGE (c)-[:appliesTo]->(p)
MERGE (p)-[:isConstrainedBy]->(b)
MERGE (b)-[:matches]->(p);

// ============================================================================
// 第五部分: 元数据节点（物理字段 ↔ 业务语义映射）
// ============================================================================

// --- RuleContext 元数据 ---
// soId: 服务提供ID（如 2831=拆机）
MERGE (m1:Metadata {
    id: 'soId', 
    name: '服务提供ID', 
    path: 'orderRequest.serviceOfferId', 
    type: 'String'
});

// operType: 操作类型（如 1100=查询, 1200=预提交）
MERGE (m2:Metadata {
    id: 'operType', 
    name: '操作类型', 
    path: 'orderRequest.operType', 
    type: 'String'
});

// 关联 RuleContext
MATCH (ctx:Entity {code: 'RuleContext'}), (m1:Metadata {id: 'soId'}), (m2:Metadata {id: 'operType'})
MERGE (m1)-[:BELONGS_TO]->(ctx)
MERGE (m2)-[:BELONGS_TO]->(ctx);

// --- ProdInst 元数据 ---
// prodId: 产品规格ID（如 80000122=灵犀专线）
MERGE (m3:Metadata {
    id: 'prodId', 
    name: '产品规格ID', 
    path: 'PROD_ID', 
    type: 'String'
});

// businessTypeCode: 业务类型编码（COL1 字段，如 3=融合光网）
MERGE (m4:Metadata {
    id: 'businessTypeCode', 
    name: '业务类型编码', 
    path: 'COL1', 
    type: 'String', 
    source: 'AccessProdInst'
});

// actionType: 动作类型
MERGE (m5:Metadata {
    id: 'actionType', 
    name: '动作类型', 
    path: 'ACTION_TYPE', 
    type: 'String'
});

// 关联 ProdInst
MATCH (p:Entity {code: 'ProdInst'}), (m3:Metadata {id: 'prodId'}), (m4:Metadata {id: 'businessTypeCode'}), (m5:Metadata {id: 'actionType'})
MERGE (m3)-[:BELONGS_TO]->(p)
MERGE (m4)-[:BELONGS_TO]->(p)
MERGE (m5)-[:BELONGS_TO]->(p);

// ============================================================================
// 第六部分: 业务约束节点
// ============================================================================
// BC_LX_001: 灵犀融合光网约束
MERGE (bc:BusinessConstraint {
    id: 'BC_LX_001',
    name: '灵犀融合光网约束',
    targetProductId: '80000122',
    targetBusinessType: '3',
    allowedActions: '2831',
    exemptOperTypes: '["1100", "1200"]',
    errorMessage: '灵犀专线业务类型为融合光网时，只允许做拆机操作'
});

// ============================================================================
// 第七部分: 全局原子动作
// ============================================================================
// ShouldSkipCheck: 判断是否跳过检查
MERGE (a1:Action {
    code: 'ShouldSkipCheck', 
    handler: 'LogosUtils.shouldSkip', 
    description: '判断是否跳过检查'
});

// ValidateConstraint: 验证约束逻辑
MERGE (a2:Action {
    code: 'ValidateConstraint', 
    handler: 'LogosEngine.validate', 
    description: '验证约束逻辑'
});

// BlockExecution: 阻断执行
MERGE (a3:Action {
    code: 'BlockExecution', 
    handler: 'Errors.error', 
    description: '阻断执行'
});

// ============================================================================
// 第八部分: 规则实例节点
// ============================================================================
// RULE_LX_001: 灵犀融合光网准入规则
MERGE (rule:RuleInstance {
    id: 'RULE_LX_001',
    name: '灵犀融合光网准入规则',
    spel: '#shouldSkip(#operType, "1100", "1200") ? true : (#businessTypeCode == "3" ? #soId == "2831" : true)',
    priority: 1,
    category: 'PROD_RULE',
    version: 'v1.0.0',
    status: 'PUBLISHED',
    errorMessage: '灵犀专线业务类型为融合光网时，只允许做拆机操作',
    targetProductId: '80000122',
    createdAt: datetime(),
    updatedAt: datetime()
});

// ============================================================================
// 第九部分: 规则实例关系
// ============================================================================
// 规则 CONSTRAINS 产品实例
MATCH (rule:RuleInstance {id: 'RULE_LX_001'}), (p:Entity {code: 'ProdInst'})
MERGE (rule)-[:CONSTRAINS]->(p);

// 规则 IMPLEMENTS 业务约束
MATCH (rule:RuleInstance {id: 'RULE_LX_001'}), (bc:BusinessConstraint {id: 'BC_LX_001'})
MERGE (rule)-[:IMPLEMENTS]->(bc);

// ============================================================================
// 第十部分: 数据字典（常量值语义映射）
// ============================================================================
// 以下为注释形式的数据字典，便于理解代码中的常量含义

// --- 产品ID ---
// 80000122: 灵犀专线

// --- 业务类型编码 (COL1) ---
// 1: 普通业务
// 2: FTTR业务
// 3: 融合光网

// --- 服务提供ID (soId) ---
// 2831: 拆机操作

// --- 操作类型 (operType) ---
// 1100: 查询
// 1200: 预提交
// 其他: 正式提交

// ============================================================================
// 导出完成
// ============================================================================
