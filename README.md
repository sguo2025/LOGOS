# LOGOS — Logic Ontology Generation & Operation System

**含义**
- LOGOS 源自古希腊语，意为“逻辑/话语”。本项目直击“自然语言（话语）生成逻辑（代码）”的核心命题。

**工程含义**
- 自然语言到规则代码：将业务话语结构化为语义槽位，编译为可执行规则（如 SpEL）。
- 本体驱动语义对齐：以知识图谱锁定物理字段与业务含义，消除硬编码与歧义。
- 四可原则：能生成、可验证、可管理、可解释，确保规则工程可控、可审计、可演进。

**系统组成**
- 前端工作台：
	- Java 代码建模工作台（Source → Ontology/KG）
	- NL2SpEL 工作台（自然语言 → 规则表达式）
- 后端内核：
	- Spring Boot 规则执行与治理、SpEL 执行器
	- LLM 适配层与 RAG 检索增强（提示词工程 + 映射校验）
	- Neo4j 业务本体与映射关系存储

**关键流程**
- NL（自然语言） → 语义槽位（Condition/Action/Exception） → 语义 AST → SpEL → 沙箱验证 → 发布治理。

**进一步阅读**
- 技术架构：docs/技术架构.md
- 模型设计：docs/模型设计.md
- 前端设计：docs/前端设计/Java 代码建模工作台.md、docs/前端设计/NL2SpEL 工作台的组件树.md
- 初始化指南：docs/init.md
