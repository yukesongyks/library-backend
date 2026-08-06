# 系统设计文档：Hello World 功能集 + 埋点报表

## 1. 需求概述

本次需求包含以下核心功能：

1. **三个基础接口**：helloworld、哈希算法、冒泡排序
2. **前端展示页**：新增一个页面，含三个 Tab 分别展示上述接口的执行结果
3. **导出功能**：每个 Tab 支持导出当前展示结果，后端提供对应导出接口
4. **调用埋点**：后端记录每次接口调用的次数与调用人信息
5. **可视化报表**：前端在同一页面展示调用情况报表，支持按人员类型、人员层级、人员部门等维度查看，提供折线图、饼图、柱状图三种展示形式

---

## 2. 跨仓依赖与现状摘要

| 仓库 | 当前状态 | 本次改动范围 |
|------|---------|-------------|
| library-backend | 空项目（仅 README） | 新建 Spring Boot 工程，包含 Controller / Service / Entity / Repository / Config 等模块 |
| library-frontend | 空项目（仅 README） | 新建前端工程（Vue3 + Vite + ECharts），新增功能页面及路由 |

**数据流方向**：前端 → 后端 REST API → 数据库（H2/MySQL）→ 前端报表渲染

**仓间对齐点**：
- API 契约（路径、请求/响应结构）需前后端严格一致
- 导出文件格式统一为 CSV
- 埋点字段定义需前后端对齐（人员信息从请求头获取）

---

## 3. 架构设计

### 3.1 整体架构

```
┌─────────────┐       HTTP/REST        ┌──────────────────┐       JPA        ┌─────────┐
│  Frontend   │ ◄────────────────────► │    Backend       │ ◄──────────────► │   DB    │
│  (Vue3)     │                        │  (Spring Boot)   │                  │(H2/MySQL)│
└─────────────┘                        └──────────────────┘                  └─────────┘
      │                                       │
      │  - /api/hello                         │
      │  - /api/hash                          │
      │  - /api/bubble-sort                   │
      │  - /api/export/{type}                 │
      │  - /api/stats/query                   │
      │                                       │
      ▼                                       ▼
  ECharts 渲染                           AOP 埋点拦截
  Tab 切换                               统一响应封装
  导出下载                               CORS 配置
```

### 3.2 后端模块划分

| 模块 | 职责 | 关键类 |
|------|------|--------|
| controller | REST 接口入口 | HelloController, HashController, BubbleSortController, ExportController, StatsController |
| service | 业务逻辑 | HelloService, HashService, BubbleSortService, ExportService, StatsService |
| entity | 数据实体 | ApiCallLog |
| repository | 数据访问 | ApiCallLogRepository |
| aspect | 埋点切面 | ApiCallLogAspect |
| config | 配置类 | CorsConfig, WebConfig |
| dto | 数据传输对象 | ApiResponse, StatsQueryRequest, StatsVO |

### 3.3 前端模块划分

| 模块 | 职责 |
|------|------|
| views/DemoPage.vue | 主页面，包含 Tab + 报表区域 |
| components/HelloTab.vue | helloworld 结果展示 + 导出 |
| components/HashTab.vue | 哈希算法结果展示 + 导出 |
| components/BubbleSortTab.vue | 冒泡排序结果展示 + 导出 |
| components/StatsChart.vue | 报表组件（折线/饼/柱状图切换） |
| api/index.js | 统一 API 调用封装 |
| router/index.js | 路由配置 |

---

## 4. 接口契约设计

### 4.1 HelloWorld 接口

- **路径**: `GET /api/hello`
- **请求参数**: 无
- **响应体**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "result": "Hello World",
    "timestamp": "2026-08-06T10:00:00Z"
  }
}
```

### 4.2 哈希算法接口

- **路径**: `POST /api/hash`
- **请求体**:
```json
{
  "input": "待哈希的字符串",
  "algorithm": "SHA-256"
}
```
- **响应体**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "input": "待哈希的字符串",
    "algorithm": "SHA-256",
    "hashValue": "a1b2c3d4...",
    "timestamp": "2026-08-06T10:00:00Z"
  }
}
```

### 4.3 冒泡排序接口

- **路径**: `POST /api/bubble-sort`
- **请求体**:
```json
{
  "array": [5, 3, 8, 1, 2]
}
```
- **响应体**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "original": [5, 3, 8, 1, 2],
    "sorted": [1, 2, 3, 5, 8],
    "timestamp": "2026-08-06T10:00:00Z"
  }
}
```

### 4.4 导出接口

- **路径**: `GET /api/export/{type}`
- **路径参数**: `type` ∈ {hello, hash, bubble-sort}
- **响应**: Content-Type: text/csv, Content-Disposition: attachment; filename="{type}_result.csv"
- **说明**: 导出最近一次该类型的执行结果，CSV 格式

### 4.5 报表查询接口

- **路径**: `POST /api/stats/query`
- **请求体**:
```json
{
  "dimension": "USER_TYPE | USER_LEVEL | DEPARTMENT",
  "chartType": "LINE | PIE | BAR",
  "startDate": "2026-08-01",
  "endDate": "2026-08-06"
}
```
- **响应体**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "dimension": "DEPARTMENT",
    "chartType": "BAR",
    "items": [
      { "label": "技术部", "count": 120 },
      { "label": "产品部", "count": 85 },
      { "label": "运营部", "count": 60 }
    ]
  }
}
```

---

## 5. 数据模型设计

### 5.1 api_call_log 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | 主键 |
| api_path | VARCHAR(255) | 接口路径，如 /api/hello |
| api_type | VARCHAR(50) | 接口类型标识：hello / hash / bubble-sort / export / stats |
| caller_id | VARCHAR(100) | 调用人 ID（从请求头 X-User-Id 获取） |
| caller_name | VARCHAR(200) | 调用人姓名（从请求头 X-User-Name 获取） |
| user_type | VARCHAR(50) | 人员类型（从请求头 X-User-Type 获取） |
| user_level | VARCHAR(50) | 人员层级（从请求头 X-User-Level 获取） |
| department | VARCHAR(200) | 所属部门（从请求头 X-Department 获取） |
| request_params | TEXT | 请求参数快照（JSON） |
| response_status | INT | 响应状态码 |
| called_at | DATETIME | 调用时间 |

**索引**：
- idx_api_type_called_at (api_type, called_at)
- idx_caller_id (caller_id)
- idx_department (department)

---

## 6. 埋点方案

采用 **Spring AOP 切面** 实现无侵入式埋点：

- 自定义注解 `@ApiTrack(apiType = "hello")`
- 切面 `ApiCallLogAspect` 在目标方法执行后异步写入 `api_call_log`
- 人员信息从 HTTP 请求头中提取（由网关或前端注入）
- 若请求头缺失人员信息，使用默认值 "anonymous"

---

## 7. 前端报表设计

### 7.1 维度选择

下拉框可选：人员类型 / 人员层级 / 人员部门

### 7.2 图表类型切换

Tab 或按钮组切换：折线图 / 饼图 / 柱状图

### 7.3 技术选型

- 图表库：ECharts 5.x
- 日期范围选择器：Element Plus DatePicker
- 状态管理：Pinia（轻量）

---

## 8. 非功能性要求

| 项目 | 要求 |
|------|------|
| 响应时间 | 基础接口 < 200ms，报表查询 < 500ms |
| 并发 | 支持 100 QPS |
| 安全 | 接口无需鉴权（演示场景），但预留 Header 扩展位 |
| 跨域 | 后端配置 CORS 允许前端开发端口 |
| 数据库 | 开发环境使用 H2 内存库，生产可切换 MySQL |

---

## 9. 风险与待确认项

| # | 风险/待确认 | 影响 | 建议 |
|---|-----------|------|------|
| 1 | 人员信息来源未明确 | 埋点数据准确性 | 当前方案从请求头获取，需确认是否有统一用户中心 |
| 2 | 导出文件是否需要包含历史记录 | 导出接口复杂度 | 当前方案仅导出最近一次结果，如需历史需增加分页参数 |
| 3 | 前端技术栈未指定 | 开发一致性 | 建议使用 Vue3 + Vite + Element Plus + ECharts |
| 4 | 数据库选型未明确 | 部署运维 | 开发阶段用 H2，上线前确认 MySQL 连接信息 |

---

## 10. 测试策略

| 测试类型 | 覆盖范围 |
|---------|---------|
| 单元测试 | Service 层逻辑、工具类 |
| 接口测试 | 全部 5 个接口的正常/异常场景 |
| 集成测试 | 埋点写入验证、导出文件内容验证 |
| 前端 E2E | Tab 切换、图表渲染、导出下载 |
