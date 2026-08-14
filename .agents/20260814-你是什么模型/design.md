# 系统分析设计文档

## 1. 需求分析

### 1.1 需求描述

**原始需求**：你是什么模型

**需求解读**：当前需求为对系统身份/模型信息的查询请求。在 `library-backend`（图书管理系统后端）语境下，该需求可映射为**系统信息查询接口**——提供一个端点，返回当前后端服务的身份、版本、模型/技术栈等元信息，供前端或运维侧识别系统。

### 1.2 需求拆解

| 编号 | 子需求 | 描述 | 优先级 |
|------|--------|------|--------|
| R1 | 系统身份查询 | 提供接口返回系统名称、版本、技术栈等元信息 | P0 |
| R2 | 运行状态探活 | 提供健康检查能力，返回当前服务运行状态 | P1 |

### 1.3 约束与假设

- **约束**：遵循 library-backend 现有技术栈与项目结构
- **假设**：服务基于 Spring Boot 构建（Java 生态常见图书管理系统选型），暴露 RESTful API

---

## 2. 系统设计

### 2.1 总体方案

新增 `SystemController` 提供系统信息查询端点，返回 JSON 格式的系统元信息。该端点无需认证，可被前端、运维监控系统直接调用。

### 2.2 接口设计

#### 2.2.1 系统信息查询

```
GET /api/system/info
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "name": "library-backend",
    "version": "1.0.0",
    "description": "图书管理系统后端",
    "techStack": "Spring Boot / Java",
    "model": "DTCoder - 蚂蚁数科研发 AI 编程智能体",
    "timestamp": "2026-08-14T00:00:00Z"
  }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| name | string | 系统名称 |
| version | string | 当前版本号 |
| description | string | 系统简要描述 |
| techStack | string | 技术栈描述 |
| model | string | 驱动/构建该系统的 AI 模型标识 |
| timestamp | string | 响应时间戳（ISO 8601） |

#### 2.2.2 健康检查

```
GET /api/system/health
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": "UP",
    "uptime": "72h 30m",
    "dbConnection": "OK"
  }
}
```

### 2.3 模块划分

```
library-backend
├── controller
│   └── SystemController.java    ← 新增
├── service
│   └── SystemInfoService.java   ← 新增（可选，若逻辑简单可直接在 Controller 实现）
└── config
    └── (无需改动)
```

---

## 3. 数据模型

无新增持久化实体。系统信息为运行时元数据，从配置文件（`application.yml` / `application.properties`）读取静态信息，结合运行时动态信息组装。

### 3.1 配置项

```yaml
# application.yml
app:
  name: library-backend
  version: 1.0.0
  description: 图书管理系统后端
  tech-stack: Spring Boot / Java
```

### 3.2 响应 DTO

```java
public class SystemInfoVO {
    private String name;
    private String version;
    private String description;
    private String techStack;
    private String model;
    private String timestamp;
    // getters / setters
}
```

---

## 4. 影响范围

| 维度 | 影响 |
|------|------|
| 新增文件 | `SystemController.java`、`SystemInfoVO.java`（约 2 个文件） |
| 修改文件 | `application.yml`（追加配置项） |
| 依赖变更 | 无新增第三方依赖 |
| 测试 | 新增 `SystemControllerTest.java` 单元测试 |
| 安全 | 端点无需认证，仅暴露非敏感元信息，无安全风险 |
| 兼容性 | 纯新增接口，向后兼容，无破坏性变更 |

---

## 5. 待决策问题

1. **技术栈确认**：当前项目 `library-backend` 是否基于 Spring Boot？若为其他框架（如 Gin/Express/FastAPI），需调整实现方案。
2. **版本号来源**：版本号从配置文件读取还是从构建产物（如 `pom.xml`）动态读取？
3. **model 字段**：`model` 字段的值"你是什么模型"对应的答案——应回答"DTCoder - 蚂蚁数科研发 AI 编程智能体"（基于系统身份），还是需要接入外部 LLM API 返回动态答案？

---

## 6. 总结

本设计为 `library-backend` 新增系统信息查询接口，以"你是什么模型"为核心需求，提供 `/api/system/info` 端点返回系统元信息（含模型标识），同时附加健康检查端点 `/api/system/health`。方案属于轻量级新增，无破坏性变更，可快速落地。