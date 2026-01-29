针对 **LOGOS (智能规则中台)** 项目，基于业务专家整理的 MVP 建模与后端架构设计，以下是完整的 **API 接口规范文档 (OpenAPI 3.0)**。

---

# LOGOS 智能规则中台 API 接口规范 (V1.0)

## 1. 接口概述

本 API 旨在提供从自然语言需求到 SpEL 表达式的完整闭环，涵盖知识提取、规则生成、仿真验证及资产管理。

* **Base URL**: `/api/logos/v1`
* **Content-Type**: `application/json`

---

## 2. 完整 OpenAPI 3.0 规范

```yaml
openapi: 3.0.0
info:
  title: LOGOS Rule Engine API
  description: 基于本体建模与大模型的语义规则编译器接口
  version: 1.0.0

servers:
  - url: http://api.logos-system.com/api/v1

paths:
  # 1. 知识提取接口
  /ontology/extract:
    post:
      tags: [Knowledge Engine]
      summary: 源码知识提取 (Java to KG)
      description: 上传 Java 插件源码，利用 LLM 解析并提取实体、属性及逻辑约束。
      requestBody:
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
      responses:
        '200':
          description: 提取成功
          content:
            application/json:
              example:
                code: 0
                data:
                  nodes:
                    - {id: "col1", name: "业务类型编码", type: "Metadata", path: "COL1"}
                  relations:
                    - {from: "ProdInst", to: "col1", type: "HAS_PROPERTY"}

  # 2. 规则生成接口
  /rule/generate:
    post:
      tags: [Rule Lifecycle]
      summary: 自然语言生成 SpEL (NL to SpEL)
      description: 基于本体图谱 RAG，将自然语言需求转化为可执行的 SpEL 脚本。
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RuleGenerateRequest'
      responses:
        '200':
          description: 生成成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/RuleGenerateResponse'

  # 3. 仿真验证接口
  /rule/validate:
    post:
      tags: [Rule Lifecycle]
      summary: 规则仿真与执行 (Sandbox)
      description: 构造 Mock 数据对生成的 SpEL 进行逻辑闭环验证。
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RuleValidateRequest'
      responses:
        '200':
          description: 验证完成
          content:
            application/json:
              example:
                code: 0
                data:
                  success: false
                  actualValue: "Blocked"
                  logs: ["Check operType: 1300", "Match Constraint: BC_LX_001", "Result: Failed"]

components:
  schemas:
    RuleGenerateRequest:
      type: object
      properties:
        productId:
          type: string
          example: "80000122"
        naturalLanguage:
          type: string
          example: "当业务类型是融合光网时，只准做拆机操作"
    
    RuleGenerateResponse:
      type: object
      properties:
        code: {type: integer}
        data:
          type: object
          properties:
            spel: {type: string, example: "#businessTypeCode == '3' ? #soId == '2831' : true"}
            explanation: {type: string, example: "识别到业务类型映射为COL1..."}
            evidenceNodes: {type: array, items: {type: string}}

    RuleValidateRequest:
      type: object
      properties:
        spel: {type: string}
        mockData:
          type: object
          example:
            businessTypeCode: "3"
            soId: "1200"
            operType: "1300"

```

---

## 3. 请求/响应示例 (以 NL2SpEL 为例)

### 场景：灵犀专线准入规则生成

**Request:** `POST /rule/generate`

```json
{
  "productId": "80000122",
  "naturalLanguage": "灵犀融合光网业务除了拆机都拦截"
}

```

**Response:**

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "spel": "!(#businessTypeCode == '3' && #soId != '2831')",
    "explanation": "系统识别到'灵犀'(80000122)下的'融合光网'(COL1='3')。根据'拦截'意图，生成了互斥逻辑：若类型为3且非2831则返回false。",
    "evidenceNodes": ["ProdInst", "businessTypeCode", "soId"],
    "confidence": 0.95
  }
}

```

---

## 4. 错误码体系定义

为了确保“可管理”与“可追溯”，定义以下错误码：

| 错误码 (Code) | 含义 | 说明 |
| --- | --- | --- |
| **0** | **SUCCESS** | 操作成功 |
| **4001** | **ONTOLOGY_NOT_FOUND** | 本体库中未找到对应的产品或属性（RAG 检索失败） |
| **4002** | **AMBIGUOUS_INTENT** | 自然语言描述存在二义性，无法确定逻辑方向 |
| **5001** | **SPEL_SYNTAX_ERROR** | 生成的 SpEL 脚本存在语法错误，未能通过校验器 |
| **5002** | **LLM_TIMEOUT** | 大模型响应超时（GPT/DeepSeek 适配层异常） |
| **6001** | **SANDBOX_EXEC_FAILED** | 仿真执行异常，可能由于 Mock 数据缺失必要字段 |

---

## 5. 接口安全性与管理

1. **鉴权机制**: 所有接口需在 Header 中携带 `Authorization: Bearer {token}`。
2. **可解释性 Header**: 在响应中默认返回 `X-Logos-Trace-Id`，用于在 ELK 中查询完整的 LLM 推理日志和图谱检索路径。

---

### 💡 交付建议：

该文档可直接作为前端 React 工程师进行 Mock 开发的依据。同时，OpenAPI 规范支持直接导入 Postman 或 Swagger UI 进行在线调试。**是否需要我为您生成一份基于这些接口的 Java `Controller` 与 `DTO` 基础代码？**