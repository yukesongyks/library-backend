# Tasks: HelloWorld 算法接口 + 导出 + 埋点分析全链路

## 后端 - 项目初始化
- [ ] 1. 创建 `pom.xml`（spring-boot-starter-web/data-jpa/h2/aop/validation）+ `LibraryBackendApplication.java` + `application.properties`（端口 8081, H2, ddl-auto=update）

## 后端 - 数据模型
- [ ] 2. 创建 `User` 实体（id/username/personnelType/personnelLevel/department/createdAt）+ `UserRepository`
- [ ] 3. 创建 `CallLog` 实体（id/apiType/callerUserId/callerUsername/personnelType/personnelLevel/department/calledAt/requestSummary/responseStatus/responseData）+ `CallLogRepository`（含维度聚合查询方法）
- [ ] 4. 创建 `DataInitializer`（`CommandLineRunner`）预置若干不同维度的 User 记录

## 后端 - 算法接口 + 导出
- [ ] 5. 创建 `AlgorithmService`：`helloworld()` / `hash(text, algorithm)` / `bubbleSort(numbers)`
- [ ] 6. 创建 `AlgorithmController`（`@RestController @RequestMapping("/api/algorithms")`）：helloworld / hash / bubble-sort 三端点 + `GET /export?type=` 导出 CSV
- [ ] 7. 创建请求/响应 DTO：`HashRequest` / `BubbleSortRequest` / `AlgorithmResponse`

## 后端 - 埋点 + 分析
- [ ] 8. 创建 `CallLogAspect`（`@AfterReturning` 拦截 AlgorithmController，读 `X-User-Id`，查 User 维度，写 CallLog；切面内 try-catch 兜底）
- [ ] 9. 主入口类添加 `@EnableAspectJAutoProxy`
- [ ] 10. 创建 `AnalyticsService` + `AnalyticsController`（`@RequestMapping("/api/analytics")`，`GET /calls` 按维度+图表类型+时间段聚合）

## 后端 - 异常兜底
- [ ] 11. 创建 `GlobalExceptionHandler`（`@RestControllerAdvice`）：`IllegalArgumentException`→400 / `NoSuchAlgorithmException`→500 / `DataAccessException`→500

## 后端 - 验证
- [ ] 12. 启动应用，验证三算法接口返回正确 + 导出 CSV + 分析接口各维度聚合
- [ ] 13. 验证调用后 call_logs 写入埋点记录，切面异常不阻断主流程

## 前端 - 项目初始化
- [ ] 14. 创建 `package.json`（vue/vite/typescript/axios/echarts/vue-echarts/vue-router）+ `vite.config.ts`（端口 5173，代理 `/api`→`localhost:8081`）+ 入口文件

## 前端 - API 封装
- [ ] 15. 创建 `src/api/client.ts`（axios 实例，请求拦截器设 `X-User-Id`）+ `src/api/algorithms.ts`（helloworld/hash/bubbleSort/export）+ `src/api/analytics.ts`（getCalls）

## 前端 - 页面 + 报表
- [ ] 16. 创建 `AlgorithmPage.vue` + `HelloWorldTab.vue` / `HashTab.vue` / `BubbleSortTab.vue`（各含执行+结果+导出按钮）+ 路由 `/algorithms`
- [ ] 17. 创建 `AnalyticsDashboard.vue`（维度选择+图表类型选择+日期范围+ECharts 渲染折线/饼/柱）
- [ ] 18. 前端异常兜底：API 调用 try-catch，错误时展示错误提示不崩溃

## 跨库对齐验证
- [ ] 19. 验证前后端 API 路径/请求响应字段命名一致 + `X-User-Id` 传递链路 + CSV 列与 CallLog 字段对应
