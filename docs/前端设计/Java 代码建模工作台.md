下面这份是**“Java 代码建模工作台（Source → Ontology / KG）”的前端组件树**，定位为你整个系统的**第一性入口**：

> 👉 *所有 NL2SpEL 的可信度，100% 取决于这里建模得够不够好。*

我仍然按 **页面级 → 区块级 → 组件级 → 关键状态/交互** 来画，**直接可指导 React 落地**。

---

# 一、Java 代码建模工作台 · 页面级组件树

```
JavaModelingWorkbenchPage
├── ProjectContextHeader
├── MainModelingWorkspace
│   ├── SourceCodePanel        （左）
│   ├── ExtractionAndMappingPanel（中）
│   └── OntologyPreviewPanel   （右）
├── KnowledgeCommitDrawer
└── ModelingGovernanceFooter
```

> **整体心智模型**
>
> * 左：原始事实（代码）
> * 中：系统理解 + 人工校准
> * 右：最终进入本体的“知识形态”

---

# 二、ProjectContextHeader（建模上下文锁定）

```
ProjectContextHeader
├── ProjectSelector
│   ├── ProjectDropdown
│   └── ModuleBreadcrumb
├── ProductBindingInfo
│   ├── ProductIdBadge
│   └── ProductName
└── ModelingScopeIndicator
```

### 关键规则

* ❌ 未绑定 ProductId → 禁止建模
* 一个建模会话 **只能绑定一个 Product**

👉 这是防止“跨产品污染 KG”的第一道前端闸门

---

# 三、左侧：SourceCodePanel（事实源，不参与推理）

```
SourceCodePanel
├── CodeSourceTabs
│   ├── UploadTab
│   ├── GitRepoTab
│   └── SnippetTab
├── CodeFileTree
├── CodeEditor
│   ├── SyntaxHighlighter
│   ├── LineNumber
│   └── SemanticHighlightLayer
└── CodeInsightToolbar
```

### SemanticHighlightLayer（非常关键）

用颜色直接标出：

* 🟦 ProductId
* 🟩 getAttr('colX')
* 🟨 OfferId / 操作码
* 🟥 硬编码魔法值

👉 **这是“从代码中看出业务”的第一步**

---

# 四、中间：ExtractionAndMappingPanel（建模核心）

这是整个工作台**最复杂、最值钱的部分**。

```
ExtractionAndMappingPanel
├── ExtractionControlBar
│   ├── StartExtractionButton
│   ├── ExtractionModeSelector
│   └── ConfidenceThresholdSetting
├── ExtractionTimeline
├── CandidateKnowledgeTable
│   ├── AttributeCandidateSection
│   │   └── AttributeCandidateRow[]
│   ├── ValueCandidateSection
│   │   └── ValueCandidateRow[]
│   └── LogicCandidateSection
│       └── LogicCandidateRow[]
└── ManualCorrectionPanel
```

---

## 4.1 ExtractionTimeline（系统在“怎么想”）

```
ExtractionTimeline
├── TimelineStep
│   ├── StepType (Parse / Infer / Map)
│   ├── StatusIcon
│   └── StepDetailPopover
```

示例：

```
✔ 识别 ProductId = 80000122
✔ 发现 getAttr('col1')
⚠ 推断 col1 = 业务类型 (0.78)
✔ OfferId 2831 = 拆机
```

👉 **这是建立“人敢点确认”的前提**

---

## 4.2 CandidateKnowledgeTable（候选知识池）

### A. 字段候选（Metadata）

```
AttributeCandidateRow
├── AttributeId (col1)
├── InferredMeaning (业务类型)
├── ConfidenceMeter
├── EvidenceLink (跳转代码行)
└── HumanDecision
    ├── ConfirmAsMetadata
    ├── RebindMeaning
    └── Reject
```

### B. 枚举值候选（Value）

```
ValueCandidateRow
├── RawValue (3)
├── InferredMeaning (融合光网)
├── BoundAttribute (col1)
├── ConfidenceMeter
└── DecisionSelector
```

### C. 逻辑候选（规则碎片）

```
LogicCandidateRow
├── LogicPattern
│   └── "#getAttr('col1') == '3'"
├── BusinessExplanation
├── UsedOfferId
└── Decision
```

👉 注意：
**这里不是“生成规则”，而是“拆出规则零件”**

---

## 4.3 ManualCorrectionPanel（人类兜底）

```
ManualCorrectionPanel
├── CorrectionTabs
│   ├── AttributeCorrectionTab
│   ├── ValueCorrectionTab
│   └── LogicCorrectionTab
├── CorrectionForm
└── CorrectionImpactPreview
```

### CorrectionImpactPreview

显示：

* 会影响哪些已有规则
* 是否已有同名 Metadata
* 是否冲突

👉 这是**企业级系统的必要敬畏**

---

# 五、右侧：OntologyPreviewPanel（进入 KG 前的“最终形态”）

```
OntologyPreviewPanel
├── OntologyGraphMiniMap
├── OntologyNodeList
│   ├── ProductNode
│   ├── MetadataNode[]
│   ├── ValueNode[]
│   └── LogicNode[]
└── OntologyDiffViewer
```

### OntologyDiffViewer（非常重要）

```
+ 新增 Metadata: col1（业务类型）
+ 新增 Value: 3 → 融合光网
~ 更新 Logic: 限制操作
```

👉 **用户在“提交前”就知道自己在改什么**

---

# 六、KnowledgeCommitDrawer（知识入库闸门）

```
KnowledgeCommitDrawer
├── CommitSummary
│   ├── NewConceptCount
│   ├── UpdatedConceptCount
│   └── RiskLevel
├── CommitOptions
│   ├── WriteToGlobalOntology
│   ├── ProductScopedOnly
│   └── TemporarySessionOnly
├── ReviewerSelector
└── CommitActionButtons
```

### 强规则

* 高风险变更 → 必须指定 Reviewer
* Global Ontology → 必须二次确认

---

# 七、ModelingGovernanceFooter（建模治理）

```
ModelingGovernanceFooter
├── ModelingStatus
├── AuditInfo
│   ├── Operator
│   └── Timestamp
├── VersionInfo
└── ExitGuard
```

ExitGuard：

* ⚠ 未提交建模结果 → 提示丢失风险

---

# 八、前端核心数据流（非常重要）

```
Java Code
  ↓
事实抽取（不可变）
  ↓
候选知识（可否认）
  ↓
人工确认（可信）
  ↓
本体结构化（可复用）
```

> ⚠️ **没有“人工确认”的知识，永远不进入 KG**

---

# 九、你可以直接写进方案的总结话术

> **Java 代码建模工作台不是“代码解析工具”，
> 而是一个“把历史代码中的隐性业务知识，
> 转化为可治理、可推理、可复用本体资产的生产线”。**

---