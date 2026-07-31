# Proposal: HelloWorld 算法接口 + 导出 + 埋点分析全链路

## 变更名称
`add-helloworld-tracking-export`

## 意图
library-backend 提供三个算法 REST 接口（HelloWorld / 哈希 / 冒泡排序）+ 导出（CSV）+ 埋点（记录调用次数和调用人）；library-frontend 新增三 Tab 页面展示执行结果，提供导出按钮，并展示多维度（人员类型/层级/部门）可视化报表（折线/饼/柱）。

## 范围

### 后端（library-backend）
1. 初始化 Spring Boot 2.7.x + Java 17 + Spring Data JPA + H2 项目骨架，包名 `com.library.backend`
2. 三个算法接口（`@RestController`）：`GET /api/algorithms/helloworld`、`POST /api/algorithms/hash`、`POST /api/algorithms/bubble-sort`
3. 导出接口：`GET /api/algorithms/export?type=` → CSV 文件流
4. 埋点体系：`CallLog` 实体 + AOP 切面自动写入；`User` 实体含 personnelType/personnelLevel/department
5. 分析接口：`GET /api/analytics/calls?dimension=&chartType=&startTime=&endTime=`

### 前端（library-frontend）
1. 初始化 Vue 3 + Vite + TypeScript 项目，集成 ECharts
2. `/algorithms` 页面三 Tab：HelloWorld / 哈希算法 / 冒泡排序
3. 每个 Tab 提供导出按钮
4. 分析报表：维度切换（人员类型/层级/部门）+ 图表类型切换（折线/饼/柱）+ 时间范围筛选

## 非目标
- 不实现用户认证/授权（调用人通过 `X-User-Id` header 传入）
- 不实现生产级数据库（H2 内存库）
- 不实现 Excel/PDF 导出（仅 CSV）
- 不涉及 ranxitest / cloud / dtazzi-cline 仓库改动

## 默认值（确认项）
| 项目 | 默认值 |
|------|--------|
| OpenSpec CLI 不可用 | 手动组织 `openspec/changes/<name>/` 产物结构，跳过 `openspec status` 校验 |
| 调用人身份来源 | 请求头 `X-User-Id` 传入，埋点时关联 User 维度；开发演示场景接受伪造风险 |
| 构建环境 | JDK 17 + Node.js 18 LTS；H2 内存库 `ddl-auto=update` 自动建表 |
| 后端端口 | 8081 |
| 前端端口 | 5173（Vite dev server，代理 `/api` → `localhost:8081`） |

## 异常兜底方案
| 异常场景 | 兜底策略 |
|----------|----------|
| 算法接口参数校验失败（空文本/不支持算法/非数组） | 全局异常处理器 `@RestControllerAdvice` 捕获，返回 HTTP 400 + `{"error": "..."}` |
| 算法执行内部异常（如哈希计算 NoSuchAlgorithmException） | 全局异常处理器捕获，返回 HTTP 500 + `{"error": "internal error"}`，记 ERROR 日志 |
| 埋点切面自身异常（User 查询失败/DB 写入失败） | 切面内 try-catch 包裹，失败仅记 WARN 日志，**不阻断主流程**，接口正常返回 |
| 导出类型无效或为空 | 返回 HTTP 400 + 错误提示 |
| 导出无数据 | 返回 HTTP 200 + 仅含 CSV 表头行 |
| 分析查询无数据 | 返回 HTTP 200 + `data: []` |
| 分析维度/图表类型无效 | 返回 HTTP 400 + 支持枚举列表 |
| 数据库不可用 | Spring Boot 标准错误响应，全局异常处理器捕获 `DataAccessException` |

## 回滚
- 全部为新增文件和新增实体，回滚删除即可
- H2 内存库重启即清空，无持久化迁移风险
