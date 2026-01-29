下面给你一份**可直接指导前端实现的 NL2SpEL 工作台组件树**。
我会按 **页面级 → 区块级 → 组件级 → 状态/事件** 四层来画，**不是概念图，而是工程图**。

---

# 一、NL2SpEL Workbench 总体组件树（页面级）

```
NL2SpELWorkbenchPage
├── ProductContextHeader
├── MainWorkspace
│   ├── SemanticInputPanel        （左）
│   ├── SemanticStructurePanel    （中）
│   └── EvidenceGraphPanel        （右）
├── SandboxDrawer                 （底）
└── RuleGovernanceFooter
```

> **核心思想**：
>
> * 左：人怎么说
> * 中：系统怎么理解
> * 右：凭什么这么理解
> * 下：跑一跑
> * 底：能不能上线

---

# 二、ProductContextHeader（认知对齐层）

```
ProductContextHeader
├── ProductSelector
│   ├── ProductDropdown
│   └── ProductMetaTooltip
├── ContextBadgeGroup
│   ├── ContextBadge (AccessProdInst)
│   ├── EnvBadge (Test / Prod)
│   └── VersionBadge
└── OntologyStatusIndicator
```

### 状态 & 逻辑

* ❗ **无 Product 不允许输入 NL**
* OntologyStatusIndicator：

  * ✅ 本体完整
  * ⚠ 有低置信映射
  * ❌ 缺字段（禁止生成）

---

# 三、左侧：SemanticInputPanel（自然语言 ≠ 自由输入）

```
SemanticInputPanel
├── NLComposer
│   ├── NLTextArea
│   ├── SemanticHintOverlay
│   └── NLParseButton
├── StructuredInputAssist
│   ├── ConditionSlotEditor
│   │   ├── AttributeSelector
│   │   ├── OperatorSelector
│   │   └── ValueSelector
│   ├── ActionSlotEditor
│   └── ExceptionSlotEditor
└── InputQualityIndicator
```

### 关键点（非常重要）

* **NLTextArea 是“草稿区”**
* 真正参与生成的是：

  * ConditionSlot
  * ActionSlot
  * ExceptionSlot

👉 **用户以为自己在打字，系统实际在构 AST**

---

# 四、中间：SemanticStructurePanel（系统理解层，核心）

```
SemanticStructurePanel
├── SemanticSummaryCard
│   ├── IfBlock
│   │   └── ConditionChips[]
│   ├── ThenBlock
│   │   └── ActionChips[]
│   └── ElseBlock
│       └── DefaultBehavior
├── ExplanationPanel
│   ├── ExplanationText
│   └── ConfidenceMeter
├── SpELPreviewPanel
│   ├── SpELCodeViewer (ReadOnly)
│   ├── CopyButton
│   └── SyntaxStatusBadge
└── SemanticDiffPanel (optional)
```

### UI 语义映射示例

```
IF
  [业务类型] == [融合光网]
THEN
  [允许操作] = [拆机]
ELSE
  [不限制]
```

### 状态控制

* ❌ 条件不完整 → 禁止 SpEL 生成
* ⚠ 置信度 < 阈值 → 黄色提示，不可发布

---

# 五、右侧：EvidenceGraphPanel（防幻觉核心）

```
EvidenceGraphPanel
├── EvidenceGraphCanvas
│   ├── ProductNode
│   ├── MetadataNode
│   ├── ValueNode
│   └── OfferNode
├── EvidenceDetailDrawer
│   ├── SourceInfo
│   ├── MappingConfidence
│   └── UsedInRulesList
└── EvidenceLegend
```

### 每个节点必须回答 3 个问题

1. **它从哪来？**

   * Java 代码 / 人工确认 / 历史规则
2. **可信度多少？**
3. **之前用在哪？**

👉 这是你方案**“锁死物理映射”的前端落点**

---

# 六、底部：SandboxDrawer（验证层）

```
SandboxDrawer
├── CaseGenerator
│   ├── AutoCaseList
│   └── ManualCaseEditor
├── ExecutionResultPanel
│   ├── CaseResultTable
│   └── PassFailSummary
└── ExecutionLogViewer
```

### 自动生成的 Case 类型

* 命中条件
* 非命中
* 边界值
* 异常值（缺字段）

---

# 七、RuleGovernanceFooter（治理与发布）

```
RuleGovernanceFooter
├── RuleMetaEditor
│   ├── RuleNameInput
│   └── RuleDescription
├── RiskIndicator
│   ├── ImpactScope
│   └── RiskLevelBadge
├── ActionButtons
│   ├── SaveDraftButton
│   ├── TestButton
│   └── PublishButton
└── VersionInfo
```

### 发布前硬约束

* ❌ 未 Sandbox → 不能发布
* ❌ 低置信字段 → 不能发布
* ❌ 无责任人 → 不能发布

---

# 八、核心数据流（前端最重要的一条）

```
NLText
  ↓
Semantic Slots（结构化）
  ↓
Semantic AST
  ↓
SpEL Generator
  ↓
Sandbox Execution
  ↓
Governance Decision
```

> **SpEL 在这里只是“中间产物”，不是主角**

---

# 九、你可以直接放进方案的一句话总结

> **NL2SpEL 工作台不是一个“生成器”，
> 而是一个把自然语言逐步“压缩”为可验证规则的语义工厂。**

---