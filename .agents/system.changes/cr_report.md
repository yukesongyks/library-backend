# Code Review Report

> **Change** `成本分析报表 (library-backend 编码实现)` · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-4d4515d3-ae13-4b19-` / `52d5e48` · **日期** `2026-07-31` · **审查者** AI

> **技能** `dtazziboot-java-code-review` v1.1.0 · **自动化预扫** `scan-all-rules.sh` (52/222 rules scanned, 10 findings)

---

## 审查范围

| # | 文件路径 | 行数 | 归属 |
|---|---------|------|------|
| 1 | `controller/CostAnalysisController.java` | 98 | W01–W07 API 入口 |
| 2 | `service/CostAnalysisService.java` | 254 | S01–S05 业务逻辑 |
| 3 | `service/ExcelExportService.java` | 153 | S06–S07 Excel 导出 |
| 4 | `dto/CostQueryRequest.java` | 16 | 查询入参 DTO |
| 5 | `dto/CostSummaryDTO.java` | 16 | 汇总出参 DTO |
| 6 | `dto/DimensionStatDTO.java` | 14 | 维度统计 DTO |
| 7 | `dto/ProjectCostDTO.java` | 28 | 项目成本 DTO（含派生计算） |
| 8 | `entity/CostRecord.java` | 42 | 成本记录实体 |
| 9 | `entity/Employee.java` | 32 | 人员实体（含 EmployeeRole 枚举） |
| 10 | `entity/Project.java` | 24 | 项目实体 |
| 11 | `entity/ProjectBudget.java` | 24 | 项目预算实体 |
| 12 | `entity/BusinessLine.java` | 19 | 业务线实体 |
| 13 | `entity/Department.java` | 19 | 部门实体 |
| 14 | `repository/CostRecordRepository.java` | 59 | 7 条自定义 JPQL 查询 |
| 15 | `repository/ProjectBudgetRepository.java` | 10 | 预算仓储 |
| 16 | `config/CorsConfig.java` | 17 | CORS 配置 |
| 17 | `LibraryBackendApplication.java` | 11 | 启动类 |
| 18 | `pom.xml` | 69 | Maven 依赖 |
| 19 | `application.yml` | 29 | Spring Boot 配置 |
| 20 | `data.sql` | 28 | 种子数据 |

共 22 个 `.java` 文件 + 3 个配置/资源文件，+1274 行变更。

---

## 自动化预扫结果（scan-all-rules.sh）

```
=== Summary: 10 findings (P0=0, P1=1, P2=9) | 52/222 rules scanned ===

[P1] S10.2 — CorsWildcard: config/CorsConfig.java:12
[P2] A2.2 — WildcardImport: controller/CostAnalysisController.java:13
[P2] A2.2 — WildcardImport: entity/BusinessLine.java:4
[P2] A2.2 — WildcardImport: entity/CostRecord.java:4
[P2] A2.2 — WildcardImport: entity/Department.java:4
[P2] A2.2 — WildcardImport: entity/Employee.java:4
[P2] A2.2 — WildcardImport: entity/Project.java:4
[P2] A2.2 — WildcardImport: entity/ProjectBudget.java:4
[P2] A2.2 — WildcardImport: service/ExcelExportService.java:6
[P2] A3.4 — LineWidthExceeded: controller/CostAnalysisController.java:88
```

---

## 严重性等级统计

| 等级 | 数量 | 说明 |
|------|------|------|
| **P0（阻塞）** | **2** | 功能性缺陷，必须阻止合并 |
| **P1（推荐）** | **8** | 安全/可靠性隐患，合并前应修复 |
| **P2（参考）** | **11** | 可靠性建议/代码风格，可选改进 |

**blocker_count = 2**

---

## P0 — 阻塞问题

### P0-1 `getProjectCost` 项目实际消耗未按年度过滤，预算与实际周期错配

**文件**: `service/CostAnalysisService.java:111-139`

**问题**: `getProjectCost(Integer budgetYear)` 方法中：
- 第 113-115 行：预算按 `budgetYear` 过滤 ✅
- 第 118 行：实际消耗调用 `costRecordRepository.findCostByProject()`，该查询 **无年度过滤** ❌

```java
// 第 118 行 — 查询项目实际消耗（无 year 过滤）
List<Object[]> actualRows = costRecordRepository.findCostByProject();
```

对应 Repository 查询（`CostRecordRepository.java:40-43`）：
```java
@Query("SELECT c.project.id, c.project.name, SUM(c.amount) FROM CostRecord c "
     + "WHERE c.project IS NOT NULL "
     + "GROUP BY c.project.id, c.project.name")
List<Object[]> findCostByProject();
```

**影响**: 当前端传入 `budgetYear=2026` 时，返回的 `budgetAmount` 为 2026 年预算，但 `actualCost` 为 **全部年度** 的累计消耗。`budgetUsageRate`（预算占比）和 `overspendAmount`（预计超支金额）计算结果完全错误。

**跨仓影响**: 前端 `costApi.ts` → `getProjectCost(budgetYear?)` 传递 `budgetYear` 参数，前端 `ProjectCost` 类型依赖 `budgetUsageRate` 和 `overspendAmount` 展示预算占比和超支金额，数据将误导用户。

**修复建议**: 为 `findCostByProject` 增加可选 `costYear` 参数：
```java
@Query("SELECT c.project.id, c.project.name, SUM(c.amount) FROM CostRecord c "
     + "WHERE c.project IS NOT NULL AND (:costYear IS NULL OR c.costYear = :costYear) "
     + "GROUP BY c.project.id, c.project.name")
List<Object[]> findCostByProject(@Param("costYear") Integer costYear);
```
Service 层将 `budgetYear` 透传给 `findCostByProject(budgetYear)`。

---

### P0-2 `getCostByDimension` 维度聚合查询中 `year` 参数对 5 个维度被静默忽略

**文件**: `service/CostAnalysisService.java:145-203`

**问题**: `getCostByDimension(String dimension, Integer year)` 方法中，`year` 参数仅在 `case "quarter"` 分支（第 178 行）被使用。其余 5 个维度的 Repository 查询 **均无年度过滤**：

| 维度 | 调用 | year 是否生效 |
|------|------|-------------|
| department | `findCostByDepartment()` | ❌ 忽略 |
| businessLine | `findCostByBusinessLine()` | ❌ 忽略 |
| project | `findCostByProject()` | ❌ 忽略 |
| employee | `findCostByEmployee()` | ❌ 忽略 |
| role | `findCostByRole()` | ❌ 忽略 |
| quarter | `findByFilters(null,null,null,null,year,null,null)` | ✅ 生效 |

**影响**: 前端 `DimensionFilter` 组件提供年度选择器，用户选择 2026 年后，按部门/业务线/项目/人员/角色维度的统计数据仍返回全部年度汇总，年度筛选无效果。

**跨仓影响**: 前端 `costApi.ts` → `getCostByDimension(dimension, year)` 传递 `year` 参数，但后端静默忽略，导致前端筛选器失效。

**修复建议**: 为 5 个维度查询方法增加可选 `costYear` 参数：
```java
@Query("SELECT c.department.id, c.department.name, SUM(c.amount) FROM CostRecord c "
     + "WHERE (:costYear IS NULL OR c.costYear = :costYear) "
     + "GROUP BY c.department.id, c.department.name")
List<Object[]> findCostByDepartment(@Param("costYear") Integer costYear);
```
Service 层在 `case "department"` 等分支中将 `year` 透传。

---

## P1 — 推荐修复

### P1-1 `CorsConfig` 使用 `allowedOrigins("*")` 通配符

**来源**: scan-all-rules.sh `S10.2 — CorsWildcard`
**文件**: `config/CorsConfig.java:12`

```java
registry.addMapping("/api/**")
    .allowedOrigins("*")          // ← P1
    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    .allowedHeaders("*")
    .allowCredentials(false);
```

**影响**: 虽然 `allowCredentials(false)` 使 `*` 在技术上合法，但生产环境通配 CORS 允许任意源跨域访问，存在 CSRF 和数据泄露风险。
**修复**: 配置明确的前端域名白名单（如 `http://localhost:5173` 开发、生产域名），通过 `@Value` 注入。

---

### P1-2 `CostAnalysisService` 缺少 `@Transactional(readOnly = true)`

**文件**: `service/CostAnalysisService.java:22-24`

```java
@Service
@RequiredArgsConstructor
public class CostAnalysisService {  // ← 无 @Transactional(readOnly = true)
```

**影响**: 所有查询方法无事务边界。当前依赖 Spring Boot 默认 `open-in-view=true` 保持 Hibernate Session 开放，但：
1. OSIV 反模式，每个请求持有 Session 时间过长
2. 若后续关闭 OSIV（生产最佳实践），`getCostSummary` 中 `r.getDepartment().getName()` 等 LAZY 访问将抛 `LazyInitializationException`
3. 无 `readOnly=true` 错失 JPA 层只读优化（不跟踪脏检查、不加悲观锁）

**修复**: 类级添加 `@Transactional(readOnly = true)`。

---

### P1-3 `getCostSummary` LAZY 关联访问引发 N+1 查询

**文件**: `service/CostAnalysisService.java:65-68`

```java
dto.setByDepartment(groupByDimension(records, r -> r.getDepartment().getName()));
dto.setByRole(groupByDimension(records, r -> r.getEmployee().getRole().name()));
dto.setByMonth(groupByDimension(records,
    r -> r.getCostYear() + "-" + String.format("%02d", r.getCostMonth())));
```

**影响**: `findByFilters` 查询为 `SELECT c FROM CostRecord c`（无 JOIN FETCH），返回的 CostRecord 列表中 `department`/`employee` 均为 LAZY 代理。`groupByDimension` 内对每条记录调用 `getDepartment().getName()` 和 `getEmployee().getRole().name()`，触发 N+1 查询（每条记录 2 次额外 SELECT）。100 条记录 = 200 次额外查询。

**修复**: 在 `findByFilters` JPQL 中使用 JOIN FETCH：
```java
@Query("SELECT c FROM CostRecord c "
     + "LEFT JOIN FETCH c.department "
     + "LEFT JOIN FETCH c.employee "
     + "WHERE ...")
```

---

### P1-4 `getMonthlyTrend` / `getCostByRole` Object[] 类型强转风险

**文件**: `service/CostAnalysisService.java:78-81, 96-99`

```java
// getMonthlyTrend — 第 78-81 行
.map(r -> (BigDecimal) r[2])                          // SUM 结果类型因 dialect 而异
r[0] + "-" + String.format("%02d", (Integer) r[1])     // costMonth 可能返回 Long/BigInteger
```

```java
// getCostByRole — 第 99 行
((Employee.EmployeeRole) r[0]).name()                  // 依赖 Hibernate enum 映射
```

**影响**: JPA 聚合查询 `SUM(c.amount)` 的返回类型因数据库 dialect 和 Hibernate 版本而异（BigDecimal / BigInteger / Long）。当前 H2 H2Dialect 返回 BigDecimal，但切换 MySQL/PostgreSQL 后可能 `ClassCastException`。`(Integer) r[1]` 同理（costMonth 可能返回 Long）。

**修复**: 使用 `Number` 接口统一提取，避免直接强转：
```java
int month = ((Number) r[1]).intValue();
BigDecimal amount = (BigDecimal) r[2]; // 或 new BigDecimal(((Number) r[2]).toString())
```

---

### P1-5 `getCostSummary` 中 `laborCost = totalCost` 语义不正确

**文件**: `service/CostAnalysisService.java:62-63`

```java
dto.setTotalCost(total);
dto.setLaborCost(total);  // ← 人力成本 = 总成本，未按 costType 过滤
```

**影响**: `CostRecord` 实体有 `costType` 字段（`CostRecord.java:41`），种子数据均为 `'LABOR'`。但 DTO 的 `laborCost` 语义上应仅为人力成本（`costType='LABOR'`），当前将所有成本类型（含潜在的非人力成本）等同于人力成本。若后续增加设备/运营等成本类型，`laborCost` 数据错误。

**修复**:
```java
BigDecimal laborTotal = records.stream()
    .filter(r -> "LABOR".equals(r.getCostType()))
    .map(CostRecord::getAmount)
    .reduce(BigDecimal.ZERO, BigDecimal::add);
dto.setLaborCost(laborTotal);
```

---

### P1-6 缺少全局异常处理器，参数校验异常返回 500

**文件**: 全局（无 `@ControllerAdvice` / `@ExceptionHandler`）

**影响**: `getCostSummary` 中 `throw new IllegalArgumentException("季度参数非法")`（第 39 行）和 `getCostByDimension` 中 `throw new IllegalArgumentException("维度参数非法: " + dimension)`（第 201 行）会直接返回 HTTP 500 Internal Server Error，而非 400 Bad Request。前端无法区分参数错误与服务端故障。

**修复**: 新增 `@RestControllerAdvice` 全局异常处理器：
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
```

---

### P1-7 H2 Console 在生产配置中暴露

**文件**: `application.yml:10-13`

```yaml
h2:
  console:
    enabled: true    # ← P1
    path: /h2-console
```

**影响**: H2 Console 提供数据库管理 UI，可执行任意 SQL。虽然当前使用 H2 内存库（仅开发/测试），但若 profile 管理不当（`application.yml` 作为默认配置），生产环境可能误开启。

**修复**: 将 H2 Console 配置移至 `application-dev.yml`，默认关闭：
```yaml
# application.yml（默认/生产）
h2:
  console:
    enabled: false
```

---

### P1-8 `CostQueryRequest` 缺少输入校验

**文件**: `dto/CostQueryRequest.java:7-16`

```java
@Data
public class CostQueryRequest {
    private Integer costYear;    // 无 @Min/@Max 校验
    private Integer costMonth;   // 无 @Min(1)/@Max(12) 校验
    private Integer quarter;     // Service 层校验 1-4，但 Controller 无 @Valid
    // ...
}
```

**影响**: `costMonth` 可传入 0 或 13，`costYear` 可传入负数，均不报错直接进入查询。Service 层仅校验 `quarter`（1-4），其他参数无校验。

**修复**: 添加 JSR-303 注解 + Controller `@Valid`：
```java
@Min(1) @Max(12) private Integer costMonth;
@Min(2000) @Max(2100) private Integer costYear;
@Min(1) @Max(4) private Integer quarter;
```

---

## P2 — 参考改进

### P2-1 ~ P2-9 通配符导入（Wildcard Import）

**来源**: scan-all-rules.sh `A2.2 — WildcardImport`（9 处）

涉及文件：`CostAnalysisController.java:13`、`ExcelExportService.java:6`、6 个实体类 `import javax.persistence.*;`

**修复**: 改为精确导入（阿里巴巴 Java 规范）：
```java
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
// ...
```

### P2-10 `CostAnalysisController.java:88` 行宽超限

**来源**: scan-all-rules.sh `A3.4 — LineWidthExceeded`

**修复**: 折行处理。

### P2-11 `ExcelExportService` 方法签名 `throws Exception` 过于宽泛

**文件**: `service/ExcelExportService.java:21, 49`

```java
public byte[] exportCostSummary(CostSummaryDTO summary) throws Exception {  // ← P2
public byte[] exportProjectCost(List<ProjectCostDTO> projects) throws Exception {  // ← P2
```

**修复**: 声明具体异常 `throws IOException`。

### P2-12 `application.yml` 使用 `ddl-auto: create-drop`

**文件**: `application.yml:16`

```yaml
jpa:
  hibernate:
    ddl-auto: create-drop  # ← 每次重启删表重建
```

**影响**: 适合 H2 内存库开发/测试，但不应作为默认配置。
**修复**: 移至 `application-dev.yml`，默认使用 `validate` 或 `none`。

### P2-13 `findTotalCostByYear` 返回 null 风险

**文件**: `repository/CostRecordRepository.java:57-58`

```java
@Query("SELECT SUM(c.amount) FROM CostRecord c WHERE c.costYear = :year")
BigDecimal findTotalCostByYear(@Param("year") Integer year);
```

**影响**: 当无匹配记录时，`SUM()` 返回 `null`。返回类型为 `BigDecimal`（非 `Optional`），调用方需手动判空。当前该方法未被调用，为潜在风险。
**修复**: 返回 `Optional<BigDecimal>` 或在 Service 层用 `Optional.ofNullable(...).orElse(BigDecimal.ZERO)`。

### P2-14 `getCostByRole` 与 `getCostByDimension("role")` 代码重复

**文件**: `service/CostAnalysisService.java:93-106` 与 `163-176`

两处对 `findCostByRole()` 的处理逻辑完全相同（总成本计算 + 百分比 + DTO 映射），违反 DRY。
**修复**: 抽取为私有方法 `mapRoleStats(List<Object[]> rows)`。

### P2-15 `data.sql` 使用显式 ID 插入

**文件**: `src/main/resources/data.sql`

种子数据使用 `INSERT INTO ... (id, ...)` 显式指定 ID。H2 IDENTITY 列允许显式插入，但插入后自增计数器可能未同步，后续 JPA 自动插入可能冲突。
**修复**: 移除显式 ID，或插入后执行 `ALTER TABLE ... ALTER COLUMN id RESTART WITH <max+1>`。

### P2-16 `ProjectCostDTO.calculateDerived()` 对 `actualCost` null 未防御

**文件**: `dto/ProjectCostDTO.java:17-22`

```java
public void calculateDerived() {
    if (budgetAmount != null && budgetAmount.compareTo(BigDecimal.ZERO) > 0) {
        this.budgetUsageRate = actualCost   // ← actualCost 可能为 null
            .multiply(new BigDecimal("100"))
            .divide(budgetAmount, 2, RoundingMode.HALF_UP);
```

**影响**: 当前调用方（`CostAnalysisService.getProjectCost`）始终设置 `actualCost` 为非 null（`getOrDefault(..., BigDecimal.ZERO)`），但 DTO 自身未防御 `actualCost == null`。
**修复**: 增加 `actualCost != null ? actualCost : BigDecimal.ZERO` 判断。

### P2-17 `getCostSummary` 中 quarter 与 costMonth 同时存在时行为不直观

**文件**: `service/CostAnalysisService.java:35-56`

当同时传入 `costMonth=5` 和 `quarter=1`（月份 1-3）时：
1. DB 查询过滤 `costMonth=5`（仅返回 5 月数据）
2. 内存再按 quarter 范围（1-3 月）过滤，5 月不在范围内 → 结果为空

注释（第 41-42 行）说明了此设计，但行为对用户不直观。
**修复**: 当 quarter 非空时忽略 costMonth，或在前端做互斥校验。

---

## 跨仓对齐点检查

### 前后端 API 路径对齐 ✅

| 前端 (costApi.ts) | 后端 (CostAnalysisController) | 状态 |
|---|---|---|
| `GET /cost/summary` (via client baseURL `/api`) | `@RequestMapping("/api/cost")` + `@GetMapping("/summary")` | ✅ 对齐 |
| `GET /cost/trend?year=` | `@GetMapping("/trend")` + `@RequestParam(required=false) Integer year` | ✅ 对齐 |
| `GET /cost/role` | `@GetMapping("/role")` | ✅ 对齐 |
| `GET /cost/project?budgetYear=` | `@GetMapping("/project")` + `@RequestParam(required=false) Integer budgetYear` | ✅ 对齐 |
| `GET /cost/dimension?dimension=&year=` | `@GetMapping("/dimension")` + `@RequestParam String dimension` + `@RequestParam(required=false) Integer year` | ✅ 路径对齐，但 **year 后端未生效**（见 P0-2） |
| `GET /cost/export/summary` | `@GetMapping("/export/summary")` | ✅ 对齐 |
| `GET /cost/export/project?budgetYear=` | `@GetMapping("/export/project")` + `@RequestParam(required=false) Integer budgetYear` | ✅ 对齐 |

### 前后端 DTO 字段对齐 ✅

| 前端 TypeScript 类型 | 后端 Java DTO | 字段 | 状态 |
|---|---|---|---|
| `CostQueryRequest` | `CostQueryRequest` | departmentId, businessLineId, projectId, employeeId, costYear, costMonth, quarter, role | ✅ 全部对齐 |
| `CostSummary` | `CostSummaryDTO` | totalCost, laborCost, recordCount, byDepartment, byRole, byMonth | ✅ 全部对齐 |
| `DimensionStat` | `DimensionStatDTO` | dimensionName, amount, percentage | ✅ 全部对齐 |
| `ProjectCost` | `ProjectCostDTO` | projectId, projectName, budgetAmount, actualCost, budgetUsageRate, overspendAmount | ✅ 全部对齐 |
| `EmployeeRole` | `Employee.EmployeeRole` | DEVELOPER, TESTER, PRODUCT, OPS | ✅ 全部对齐 |

### 跨仓功能缺陷传导

| 缺陷 | 后端位置 | 前端影响 |
|------|---------|---------|
| **P0-1** 项目实际成本未按年度过滤 | `CostAnalysisService.getProjectCost:118` | 前端 `ProjectCost` 页面预算占比/超支金额数据错误 |
| **P0-2** 维度查询 year 参数被忽略 | `CostAnalysisService.getCostByDimension:147-162` | 前端 `DimensionFilter` 年度选择器无效果 |

---

## 结论

### 合并建议：**阻止合并** ❌

存在 2 个 P0 阻塞问题，均为功能性缺陷，会导致前端展示错误数据：

1. **P0-1**: 项目成本查询中预算年度与实际消耗年度错配，`budgetUsageRate` 和 `overspendAmount` 计算结果错误
2. **P0-2**: 维度聚合查询中 `year` 参数对 5/6 个维度被静默忽略，前端年度筛选器失效

### 修复优先级

1. **必须修复（合并前）**: P0-1, P0-2
2. **推荐修复（合并前）**: P1-1 (CORS), P1-2 (事务), P1-3 (N+1), P1-6 (异常处理)
3. **可选改进（后续迭代）**: P1-4, P1-5, P1-7, P1-8, 全部 P2

### 验证建议

修复 P0 后，需前后端联调验证：
- `GET /api/cost/project?budgetYear=2026` 返回的 `actualCost` 应仅含 2026 年数据
- `GET /api/cost/dimension?dimension=department&year=2026` 返回数据应仅含 2026 年汇总
