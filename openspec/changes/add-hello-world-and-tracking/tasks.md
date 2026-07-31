# Tasks: add-hello-world-and-tracking

> 有序、可勾选的实现计划。propose 阶段不勾选任何任务。每项任务可追溯到 spec/design。

## A. library-backend 工程骨架（后端）
- [ ] A1. 新建 `library-backend/pom.xml`：Spring Boot 2.6.6 parent，Java 17，依赖 web/data-jpa/h2/validation/test。参考 `ranxitest` 仓 `my-spring-boot-app/pom.xml`。
- [ ] A2. 新建 `src/main/resources/application.properties`：`server.port=8080`、H2 内存库、`ddl-auto=update`、CORS 允许前端 origin。参考 `ranxitest` 仓同名文件。
- [ ] A3. 新建主类 `LibraryBackendApplication`（`com.example.library`）。

## B. 后端数据模型与 Repository
- [ ] B1. 新建 `User` 实体（含 `userType/userLevel/department` 维度字段，可空）。
- [ ] B2. 新建 `ApiCallLog` 实体（含索引字段：apiName/calledAt/userType/userLevel/department）。
- [ ] B3. 新建 `UserRepository`、`ApiCallLogRepository`（含按维度聚合的查询方法）。
- [ ] B4. 数据初始化：`data.sql` 或 `ApplicationRunner` 插入若干样例 User（不同类型/层级/部门），便于报表演示。

## C. 后端三接口
- [ ] C1. `HelloWorldController`：`GET /api/hello-world` → `{ result:"Hello, World!" }`。
- [ ] C2. `HashController`：`POST /api/hash`，支持 SHA-256/SHA-512/MD5，非法算法返回 400。
- [ ] C3. `BubbleSortController`：`POST /api/bubble-sort`，返回 input/sorted/steps，非法输入返回 400。
- [ ] C4. 三接口单元测试（MockMvc）：覆盖 specs 中所有 Given/When/Then。

## D. 后端埋点
- [ ] D1. 新建 `CallLogAspect`（Spring AOP `@Around`）拦截三接口方法。
- [ ] D2. 从 `HttpServletRequest` 解析 `X-User-Id`，查 `User` 取维度快照，未传/不存在则 `unknown`。
- [ ] D3. 写入 `ApiCallLog`（apiName/userId/维度快照/status/durationMs/calledAt），切面异常不影响业务。
- [ ] D4. 埋点单元测试：带用户调用与匿名调用均落库（验证列值）。

## E. 后端导出与报表
- [ ] E1. `ExportController`：`GET /api/export?tab=&format=`，csv 返回文件流，json 返回 JSON，非法枚举返回 400。
- [ ] E2. `MetricsController`：`GET /api/metrics/summary?dimension=&chartType=`，返回 `[{label,value}]`，非法枚举返回 400。
- [ ] E3. `MetricsService` 聚合实现：line 按日聚合；pie/bar 按维度取值聚合。
- [ ] E4. 单元测试：导出格式、报表聚合正确性、400 路径。

## F. library-frontend 工程（前端）
- [ ] F1. 新建 Vite + React 18 + TS 工程，依赖 ECharts。
- [ ] F2. 配置 `VITE_API_BASE` 指向后端；封装 `fetch` HTTP 模块。
- [ ] F3. 页面 `/hello-world` + 三 Tab 组件（HelloWorld/Hash/BubbleSort），各 Tab 触发接口并渲染结果。
- [ ] F4. 每个 Tab 顶部导出按钮，调用 `/api/export` 并下载文件。
- [ ] F5. 报表区组件：折线图（趋势）、饼图（维度占比）、柱状图（维度对比），可切换 dimension。
- [ ] F6. Vitest 组件测试：Tab 切换、接口结果渲染、导出按钮触发。

## G. 跨库联调与文档
- [ ] G1. 后端 CORS 配置允许前端 origin，联调三接口 + 导出 + 报表。
- [ ] G2. 在 `library-frontend/README.md` 补充启动说明与后端依赖。
- [ ] G3. 冒烟验证清单：执行三接口 → 导出三 Tab → 查看三图表，全部通过。
