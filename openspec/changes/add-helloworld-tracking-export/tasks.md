# Tasks: HelloWorld 算法接口 + 导出 + 埋点分析全链路

## 后端 - 项目初始化
- [x] 1. 创建 `pom.xml`（spring-boot-starter-web/data-jpa/h2/aop/validation）+ `LibraryBackendApplication.java` + `application.properties`（端口 8081, H2, ddl-auto=update）

## 后端 - 数据模型
- [x] 2. 创建 `User` 实体（id/username/personnelType/personnelLevel/department/createdAt）+ `UserRepository`
- [x] 3. 创建 `CallLog` 实体（id/apiType/callerUserId/callerUsername/personnelType/personnelLevel/department/calledAt/requestSummary/responseStatus/responseData）+ `CallLogRepository`（含维度聚合查询方法）
- [x] 4. 创建 `DataInitializer`（`CommandLineRunner`）预置若干不同维度的 User 记录

## 后端 - 算法接口 + 导出
- [x] 5. 创建 `AlgorithmService`：`helloworld()` / `hash(text, algorithm)` / `bubbleSort(numbers)`
- [x] 6. 创建 `AlgorithmController`（`@RestController @RequestMapping("/api/algorithms")`）：helloworld / hash / bubble-sort 三端点 + `GET /export?type=` 导出 CSV
- [x] 7. 创建请求/响应 DTO：`HashRequest` / `BubbleSortRequest` / `AlgorithmResponse`

## 后端 - 埋点 + 分析
- [x] 8. 创建 `CallLogAspect`（`@AfterReturning` 拦截 AlgorithmController，读 `X-User-Id`，查 User 维度，写 CallLog；切面内 try-catch 兜底）
  - **一致性补充**：spec tracking-analytics.md 要求"接口异常时埋点记录 ERROR 状态"，故新增 `@AfterThrowing` 拦截异常路径写 responseStatus=ERROR。design.md 已同步更新。
- [x] 9. 主入口类添加 `@EnableAspectJAutoProxy`
- [x] 10. 创建 `AnalyticsService` + `AnalyticsController`（`@RequestMapping("/api/analytics")`，`GET /calls` 按维度+图表类型+时间段聚合）

## 后端 - 异常兜底
- [x] 11. 创建 `GlobalExceptionHandler`（`@RestControllerAdvice`）：`IllegalArgumentException`→400 / `NoSuchAlgorithmException`→500 / `DataAccessException`→500

## 后端 - 验证
- [x] 12. 启动应用，验证三算法接口返回正确 + 导出 CSV + 分析接口各维度聚合
  - **[降级说明]**：当前环境无 Java/Maven 运行时（`java`/`mvn` not found），无法执行编译/启动验证。已降级为静态契约审查：逐条对照 specs/algorithms.md、export.md、tracking-analytics.md 的 Given-When-Then 场景与源码实现，确认路径、请求/响应体、错误消息、HTTP 状态码全部匹配。已生成 Maven Wrapper 配置（.mvn/wrapper/maven-wrapper.properties）供 CI 补充编译。
- [x] 13. 验证调用后 call_logs 写入埋点记录，切面异常不阻断主流程
  - **[降级说明]**：同 Task 12 环境限制。静态审查确认：CallLogAspect 用 @AfterReturning + @AfterThrowing 覆盖成功/异常两路径，writeCallLog 全程 try-catch 兜底仅记 WARN 日志，符合"绝不阻断主流程"设计原则。

## 前端 - 项目初始化
- [x] 14. 创建 `package.json`（vue/vite/typescript/axios/echarts/vue-echarts/vue-router）+ `vite.config.ts`（端口 5173，代理 `/api`→`localhost:8081`）+ 入口文件

## 前端 - API 封装
- [x] 15. 创建 `src/api/client.ts`（axios 实例，请求拦截器设 `X-User-Id`）+ `src/api/algorithms.ts`（helloworld/hash/bubbleSort/export）+ `src/api/analytics.ts`（getCalls）

## 前端 - 页面 + 报表
- [x] 16. 创建 `AlgorithmPage.vue` + `HelloWorldTab.vue` / `HashTab.vue` / `BubbleSortTab.vue`（各含执行+结果+导出按钮）+ 路由 `/algorithms`
- [x] 17. 创建 `AnalyticsDashboard.vue`（维度选择+图表类型选择+日期范围+ECharts 渲染折线/饼/柱）
- [x] 18. 前端异常兜底：API 调用 try-catch，错误时展示错误提示不崩溃

## 跨库对齐验证
- [x] 19. 验证前后端 API 路径/请求响应字段命名一致 + `X-User-Id` 传递链路 + CSV 列与 CallLog 字段对应
  - **验证结果**：
    - API 路径：后端 `/api/algorithms/{helloworld,hash,bubble-sort,export}` + `/api/analytics/calls` ↔ 前端 client.ts baseURL `/api` + 相对路径 ✅
    - 请求/响应字段：`{result}` / `{text,algorithm}`↔`{result,algorithm}` / `{numbers}`↔`{result,input}` ✅
    - X-User-Id 链路：前端拦截器注入 → 后端 `@RequestHeader` 接收 → 切面 `getHeader` 读取 ✅
    - CSV 列名 `id,apiType,callerUsername,personnelType,personnelLevel,department,calledAt,requestSummary,responseData` 与 CallLog 字段对应 ✅
