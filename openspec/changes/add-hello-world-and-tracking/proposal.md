# Proposal: add-hello-world-and-tracking

> 对应需求：hello world-1.0T2 重跑。跨库全栈变更（library-backend 后端 + library-frontend 前端）。

## Why（意图）

当前 `library-backend` 与 `library-frontend` 均为空仓（仅 README）。本次需求要求从零搭建一套可演示、可追踪调用的全栈样例：

1. 后端提供三个独立接口：`helloworld`、哈希算法、冒泡排序。
2. 前端新增一个页面，三个 Tab 分别展示三个接口的执行结果。
3. 前端新增导出按钮，后端提供导出接口，支持导出各 Tab 当前展示结果。
4. 后端做调用埋点：记录调用次数与调用人，并按人员类型 / 层级 / 部门等维度落库。
5. 前端在当前页面可视化报表：折线图、饼图、柱状图，展示调用情况。

目标是为团队提供一套"接口可执行、调用可追踪、数据可可视化"的最小可运行闭环。

## Scope（范围）

### In scope
- `library-backend`：新建 Spring Boot 工程（参考 `ranxitest` 仓的 Spring Boot 2.6.6 / Java 17 / JPA / H2 约定）。
  - 三个业务接口：`GET /api/hello-world`、`POST /api/hash`、`POST /api/bubble-sort`。
  - 导出接口：`GET /api/export`，按 `tab` 与 `format` 导出对应展示结果。
  - 调用埋点：拦截三接口调用，写入 `api_call_log`，含调用人与人员维度快照。
  - 报表数据接口：`GET /api/metrics/summary`，按维度与图表类型聚合。
  - `User` 模型扩展：新增 `userType` / `userLevel` / `department` 字段（向后兼容，新增列）。
- `library-frontend`：新建前端工程。
  - 单页面三 Tab 展示三接口结果（含触发执行与结果渲染）。
  - 导出按钮调用后端导出接口并下载文件。
  - 可视化报表区：折线图（调用趋势）、饼图（维度占比）、柱状图（维度对比）。
- 跨库 REST 契约：后端暴露稳定 API 路径与 JSON Schema，前端按契约调用。

### Non-goals（明确不做）
- 不引入真实鉴权框架（Spring Security / OAuth），调用人通过请求头 `X-User-Id` 模拟传入，便于埋点演示。
- 不替换 H2 为外部数据库（沿用 H2 内存库，`ddl-auto=update` 自动建表/加列）。
- 不做生产级高可用 / 限流 / 异步队列。
- 不动 `ranxitest` / `dtazzi-cline` / `cloud` 仓库。
- 不实现除三接口外的其它业务算法。

## Affected Areas（影响面）
- `library-backend`：全新工程（pom.xml、application.properties、controller/service/model/repository 层）。
- `library-frontend`：全新工程（package.json、入口、页面组件、图表库依赖）。
- 跨库接口契约：新增 REST API（向后兼容，均为新增端点）。

## Risk & Rollout
- 风险：前端图表库选型与后端报表聚合 SQL 形态强相关，需在 design 阶段先定聚合结构。→ 已在 design.md 固化聚合返回结构与维度枚举。
- 风险：`User` 加列迁移。→ H2 + `ddl-auto=update` 自动加列；新列可空，老逻辑不受影响。
- 回滚：均为新增文件 / 新增端点 / 新增列，删除对应目录或回滚迁移即可恢复空仓状态。

## Handoff
- 变更名：`add-hello-world-and-tracking`。
- 产物文件：`proposal.md`、`design.md`、`tasks.md`、`specs/hello-world-features.md`、`openspec/config.yaml`。
- 下一步：用户审批后执行 `openspec-apply`（或进入「编码实现」阶段按 tasks.md 落地）。
