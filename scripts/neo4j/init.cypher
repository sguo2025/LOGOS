// 1. 创建唯一性约束
CREATE CONSTRAINT entity_code IF NOT EXISTS FOR (e:Entity) REQUIRE e.code IS UNIQUE;
CREATE CONSTRAINT metadata_id IF NOT EXISTS FOR (m:Metadata) REQUIRE m.id IS UNIQUE;
CREATE CONSTRAINT constraint_id IF NOT EXISTS FOR (b:BusinessConstraint) REQUIRE b.id IS UNIQUE;
CREATE CONSTRAINT action_code IF NOT EXISTS FOR (a:Action) REQUIRE a.code IS UNIQUE;

// 2. 创建核心实体节点
MERGE (c:Entity {code: 'RuleContext', name: '规则上下文', desc: '输入参数与执行环境'})
MERGE (p:Entity {code: 'ProdInst', name: '产品实例', desc: '规则检查的目标对象'})
MERGE (b:Entity {code: 'BusinessConstraint', name: '业务约束', desc: '业务逻辑与判断条件'});

// 3. 建立实体间的基础关系拓扑
MATCH (c:Entity {code: 'RuleContext'}), (p:Entity {code: 'ProdInst'}), (b:Entity {code: 'BusinessConstraint'})
MERGE (c)-[:appliesTo]->(p)
MERGE (p)-[:isConstrainedBy]->(b)
MERGE (b)-[:matches]->(p);

// 4. 映射物理属性到元数据节点 (RuleContext)
MATCH (ctx:Entity {code: 'RuleContext'})
MERGE (m1:Metadata {id: 'soId', name: '服务提供ID', path: 'orderRequest.serviceOfferId', type: 'String'})-[:BELONGS_TO]->(ctx)
MERGE (m2:Metadata {id: 'operType', name: '操作类型', path: 'orderRequest.operType', type: 'String'})-[:BELONGS_TO]->(ctx);

// 5. 映射物理属性到元数据节点 (ProdInst)
MATCH (p:Entity {code: 'ProdInst'})
MERGE (m3:Metadata {id: 'prodId', name: '产品规格ID', path: 'PROD_ID', type: 'String'})-[:BELONGS_TO]->(p)
MERGE (m4:Metadata {id: 'businessTypeCode', name: '业务类型编码', path: 'COL1', type: 'String'})-[:BELONGS_TO]->(p)
MERGE (m5:Metadata {id: 'actionType', name: '动作类型', path: 'ACTION_TYPE', type: 'String'})-[:BELONGS_TO]->(p);

// 6. 创建业务约束 (灵犀融合光网准入规则)
MERGE (bc:BusinessConstraint {
    id: 'BC_LX_001',
    name: '灵犀融合光网约束',
    targetProductId: '80000122',
    targetBusinessType: '3',
    allowedActions: '2831',
    exemptOperTypes: '["1100", "1200"]',
    errorMessage: '灵犀专线业务类型为融合光网时，只允许做拆机操作'
});

// 7. 注册全局原子动作
MERGE (a1:Action {code: 'ShouldSkipCheck', handler: 'LogosUtils.shouldSkip', description: '判断是否跳过检查'})
MERGE (a2:Action {code: 'ValidateConstraint', handler: 'LogosEngine.validate', description: '验证约束逻辑'})
MERGE (a3:Action {code: 'BlockExecution', handler: 'Errors.error', description: '阻断执行'});
