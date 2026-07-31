# Design: add-hello-world-and-tracking

> 跨库架构 / 数据模型 / API 契约 / 迁移影响。实现方据此可直接落地，无需读取对话记录。

## 1. 架构总览

```
library-frontend (新建, SPA)         library-backend (新建, Spring Boot 2.6.6 / Java 17)
┌──────────────────────────┐         ┌──────────────────────────────────────────┐
│  Page: /hello-world      │  HTTP   │  Controllers (REST, /api/**)             │
│  ├─ Tab: HelloWorld      │ ──────> │  ├─ HelloWorldController                │
│  ├─ Tab: Hash            │         │  ├─ HashController                       │
│  └─ Tab: BubbleSort      │         │  ├─ BubbleSortController                 │
│  Export Button per Tab   │ ──────> │  ├─ ExportController                     │
│  Reports (line/pie/bar)  │ <────── │  └─ MetricsController                    │
└──────────────────────────┘         │  Services + Repositories (JPA)           │
                                     │  H2 in-memory (ddl-auto=update)         │
                                     │  CallLogAspect / Interceptor (埋点)       │
                                     └──────────────────────────────────────────┘
```

约定参考（不修改该仓，仅参考 Spring Boot 工程结构）：
- `ranxitest-0314-test/my-spring-boot-app`：包名 `com.example.myapp`，分层 controllers/services/models/repositories，Thymeleaf 模板。本次后端不用 Thymeleaf（纯 REST），但分层与依赖沿用。

## 2. 数据模型（library-backend）

### 2.1 User（扩展，向后兼容）
现有 `ranxitest` 仓 `User` 仅有 username/email/phone/bio/location/avatarUrl/createdAt/updatedAt。本次在 `library-backend` 新建 `User` 时直接包含维度字段：

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | Long | PK, IDENTITY | |
| username | String(50) | not null, unique | |
| email | String(100) | not null, unique | |
| userType | String(30) | nullable | 人员类型，如 `内部/外部`；新增列，可空 |
| userLevel | String(20) | nullable | 人员层级，如 `P6/P7`；新增列，可空 |
| department | String(100) | nullable | 人员部门；新增列，可空 |
| createdAt | LocalDateTime | not null | |
| updatedAt | LocalDateTime | | |

迁移：H2 + `ddl-auto=update` 自动建表；新列可空，老查询不受影响。

### 2.2 ApiCallLog（新增表）
| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | Long | PK, IDENTITY | |
| apiName | String(50) | not null | `helloworld/hash/bubble-sort` |
| userId | Long | nullable | 未传则 null |
| username | String(50) | nullable | 快照 |
| userType | String(30) | nullable | 快照，未传 `unknown` |
| userLevel | String(20) | nullable | 快照 |
| department | String(100) | nullable | 快照 |
| status | String(20) | not null | `SUCCESS/FAILED` |
| durationMs | Long | not null | 执行耗时 |
| calledAt | LocalDateTime | not null | 调用时间，用于折线图按日聚合 |

索引：`(apiName)`、`(calledAt)`、`(userType)`、`(userLevel)`、`(department)` 以支持聚合查询。

### 2.3 导出数据源
导出不依赖实时结果回放，而返回各 Tab 的"样本结果"以保证幂等：
- `helloworld` → `{ result: "Hello, World!" }`
- `hash` → 最近 N 条哈希记录（或固定样本 `{ algorithm, input, hash }`）
- `bubble-sort` → 最近 N 条排序记录（或固定样本 `{ input, sorted, steps }`）
> 决策：初版返回固定样本，避免导出依赖历史会话状态。

## 3. REST API 契约（跨库对齐点）

所有路径前缀 `/api`，JSON 编码 UTF-8。调用人通过请求头 `X-User-Id`（Long）传入。

| 方法 | 路径 | 请求 | 响应 200 | 响应 400 |
|---|---|---|---|---|
| GET | `/api/hello-world` | header `X-User-Id?` | `{ "result": "Hello, World!" }` | - |
| POST | `/api/hash` | `{ "algorithm":"SHA-256", "input":"abc" }` | `{ "algorithm", "input", "hash" }` | 不支持的算法 |
| POST | `/api/bubble-sort` | `{ "numbers":[3,1,2] }` | `{ "input", "sorted", "steps" }` | 非整数/缺失 |
| GET | `/api/export` | `?tab=<helloworld\|hash\|bubble-sort>&format=<csv\|json>` | 文件流/JSON | 非法 tab/format |
| GET | `/api/metrics/summary` | `?dimension=<userType\|userLevel\|department\|apiName>&chartType=<line\|pie\|bar>` | `[{ "label", "value" }]` | 非法枚举 |

### 3.1 维度与图表语义
- `dimension`：`userType` / `userLevel` / `department` / `apiName`。
- `chartType`：
  - `line`：按 `called_at` 的日期（yyyy-MM-dd）聚合调用次数 → 趋势。
  - `pie`：按 `dimension` 取值聚合总次数 → 占比。
  - `bar`：按 `dimension` 取值聚合总次数 → 对比（与 pie 同数据源，前端渲染形态不同）。

### 3.2 埋点实现方式
采用 Spring AOP `@Around` 切面，拦截三接口 Controller 方法，在方法执行前后记录耗时与状态，写入 `ApiCallLog`。调用人信息从 `HttpServletRequest` 的 `X-User-Id` 解析后查 `User` 快照；未传或用户不存在则维度为 `unknown`。切面失败不影响业务调用（捕获并记 `status=FAILED`）。

## 4. 前端工程约定（library-frontend）

- 技术栈：Vite + React 18 + TypeScript（与 `cloud-main`/`dtazzi-cline-main` 的 TS 生态对齐）。
- 图表库：ECharts（折线/饼图/柱状图统一支持，减少多库依赖）。
- HTTP：原生 `fetch`，baseURL 通过 Vite 环境变量 `VITE_API_BASE` 指向后端 `http://localhost:8080`。
- 路由：单页面 `/hello-world`，内含 Tab 组件 + 报表组件。
- CORS：后端配置 `*` 或允许前端 origin（开发期）。

## 5. 跨库接口契约兼容性
- 全部为新增端点，无既有契约被破坏。
- `User` 加列均为可空新增列，向后兼容。
- 导出与报表接口的请求/响应字段在 specs 中固定，前端按 schema 调用。

## 6. 安全与权限（降级范围）
- 不引入 Spring Security。调用人通过 `X-User-Id` 模拟，仅用于埋点演示。
- 导出与报表接口不做鉴权（演示用），生产化时需补鉴权（Out of scope）。

## 7. 验证策略
- 后端：JUnit 5 + Spring MockMvc，覆盖三接口正常/异常路径、导出格式、报表聚合、埋点写入。
- 前端：Vitest 组件测试覆盖 Tab 切换与接口结果渲染；ECharts 渲染以 DOM 挂载断言。
- 跨库联调：手动启动前后端，执行三接口 → 触发导出 → 查看报表，作为冒烟验证。
