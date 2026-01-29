# LOGOS 项目初始化指南

## 项目结构

| 目录/文件 | 说明 |
|----------|------|
| 前端 | 前端应用代码 |
| 后端 | 后端服务代码 |
| `compose.yml` | Docker 服务配置文件 |
| `script` | 项目初始化脚本 |
| `docs` | 设计文档和技术文档 |
| `.gitignore` | Git 提交忽略配置 |

## 后端技术栈

| 技术 | 说明 | 版本 | 官网 |
|------|------|------|------|
| JDK | Java 开发工具包 | 21 | [java.com](https://www.java.com/zh-CN) |
| SpringBoot | 容器 + MVC 框架 | 3.5.3 | [spring.io](https://spring.io/projects/spring-boot) |
| Spring AI | Spring AI 框架支持 | 1.0.0 | [spring.io](https://spring.io/projects/spring-ai) |
| Fastjson2 | JSON 解析工具库 | 2.0.57 | [Github](https://github.com/alibaba/fastjson2) |
| SpringDoc | OpenAPI 文档生成工具 | 2.8.9 | [springdoc.org](https://springdoc.org) |
| Knife4j | OpenAPI3 API 文档增强工具 | 3.0.3 | [xiaominfo.com](https://doc.xiaominfo.com) |
| Guava | Google 工具类库 | 33.4.8-jre | [Github](https://github.com/google/guava) |
| Lombok | 对象封装简化工具 | 1.18.38 | [Github](https://github.com/rzwitserloot/lombok) |
| MapStruct | 对象映射工具 | 1.6.3 | [mapstruct.org](https://mapstruct.org) |
| Kryo | 序列化工具库 | 5.5.0 | [Github](https://github.com/EsotericSoftware/kryo) |

## 中间件约束

| 中间件 | 说明 | 版本 |
|------|------|------|
| Neo4j | 图数据库 | - |
