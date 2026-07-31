# Code Review Report（复审）

> **Change** `成本分析报表 (library-backend 问题修复后复审)` · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-4d4515d3-ae13-4b19-` / `f7af6ad` · **日期** `2026-07-31` · **审查者** AI

> **技能** `dtazziboot-java-code-review` v1.1.0 · **复审范围** 问题修复阶段（round 1）全部变更文件

> **前次评审** commit `52d5e48`，blocker_count=2（P0-1 项目成本年度错配、P0-2 维度查询 year 忽略）

---

## 审查范围

| # | 文件路径 | 行数 | 归属 | 修复涉及 |
|---|---------|------|------|---------|
| 1 | `config/CorsConfig.java` | 22 | CORS 配置 | P1-1 修复 |
| 2 | `config/GlobalExceptionHandler.java` | 39 | 全局异常处理（新增） | P1-6 修复 |
| 3 | `controller/CostAnalysisController.java` | 103 | API 入口 | P1-8 修复 |
| 4 | `dto/CostQueryRequest.java` | 29 | 查询入参 DTO | P1-8 修复 |
| 5 | `dto/ProjectCostDTO.java` | 29 | 项目成本 DTO | P2-16 修复 |
| 6 | `entity/CostRecord.java` | 51 | 成本记录实体 | 实体调整 |
| 7 | `entity/BusinessLine.java` | — | 业务线实体 | 实体调整 |
| 8 | `entity/Department.java` | — | 部门实体 | 实体调整 |
| 9 | `entity/Employee.java` | — | 人员实体 | 实体调整 |
| 10 | `entity/Project.java` | — | 项目实体 | 实体调整 |
| 11 | `entity/ProjectBudget.java` | — | 项目预算实体 | 实体调整 |
| 12 | `repository/CostRecordRepository.java` | 66 | JPQL 查询 | P0-1/P0-2/P1-3/P2-13 修复 |
| 13 | `service/CostAnalysisService.java` | 275 | 业务逻辑 | P0-1/P0-2/P1-2/P1-4/P1-5/P2-14 修复 |
| 14 | `service/ExcelExportService.java` | 160 | Excel 导出 | P2-11 修复 |
| 15 | `application.yml` | 33 | Spring Boot 配置 | P1-7/P2-12 修复 |
| 16 | `application-dev.yml` | 7 | 开发环境配置（新增） | P1-7/P2-12 修复 |

---

## 前次问题修复验证

### P0-1 `getProjectCost` 项目实际消耗未按年度过滤 → ✅ 已修复

**修复内容**: `CostRecordRepository.findCostByProject` 增加了 `@Param("costYear") Integer costYear` 参数：

```java
@Query("SELECT c.project.id, c.project.name, SUM(c.amount) FROM CostRecord c " +
       "WHERE c.project IS NOT NULL AND (:costYear IS NULL OR c.costYear = :costYear) " +
       "GROUP BY c.project.id, c.project.name")
List<Object[]> findCostByProject(@Param("costYear") Integer costYear);
```

Service 层 `getProjectCost` 第 115 行将 `budgetYear` 透传：
```java
List<Object[]> actualRows = costRecordRepository.findCostByProject(budgetYear);
```

**验证结论**: 预算与实际消耗现在按同一 `budgetYear` 过滤，`budgetUsageRate` 和 `overspendAmount` 计算周期一致。✅ 通过

---

### P0-2 `getCostByDimension` 维度查询 year 参数被忽略 → ⚠️ 部分修复（4/5 维度已修复，role 维度仍忽略）

**修复内容**: 4 个维度查询方法增加了 `costYear` 参数：

| 维度 | Repository 方法 | year 是否生效 | 状态 |
|------|----------------|-------------|------|
| department | `findCostByDepartment(@Param("costYear") Integer costYear)` | ✅ | 已修复 |
| businessLine | `findCostByBusinessLine(@Param("costYear") Integer costYear)` | ✅ | 已修复 |
| project | `findCostByProject(@Param("costYear") Integer costYear)` | ✅ | 已修复 |
| employee | `findCostByEmployee(@Param("costYear") Integer costYear)` | ✅ | 已修复 |
| **role** | `findCostByRole()` — **仍无 costYear 参数** | ❌ **仍忽略** | **未修复** |
| quarter | `findByFilters(null,null,null,null,year,null,null)` | ✅ | 原已生效 |

**遗留问题** (`CostAnalysisService.java:160-163`):

```java
case "role": {
    List<Object[]> rows = costRecordRepository.findCostByRole();  // ← 未传 year
    return mapRoleStats(rows);
}
```

对应 Repository (`CostRecordRepository.java:40-42`):
```java
@Query("SELECT c.employee.role, SUM(c.amount) FROM CostRecord c " +
       "GROUP BY c.employee.role")  // ← 无 WHERE costYear 过滤
List<Object[]> findCostByRole();
```

**影响**: 前端 `DimensionFilter` 选择 `dimension=role&year=2026` 时，角色成本占比仍返回全部年度汇总，年度筛选对该维度无效。

**跨仓影响**: 前端 `costApi.ts` → `getCostByDimension("role", year)` 传递 `year` 参数，后端 role 分支仍静默忽略。

**修复建议**: 为 `findCostByRole` 增加可选 `costYear` 参数：
```java
@Query("SELECT c.employee.role, SUM(c.amount) FROM CostRecord c " +
       "WHERE (:costYear IS NULL OR c.costYear = :costYear) " +
       "GROUP BY c.employee.role")
List<Object[]> findCostByRole(@Param("costYear") Integer costYear);
```
Service 层 `case "role"` 改为 `costRecordRepository.findCostByRole(year)`。

注意：独立端点 `getCostByRole()`（S03, `GET /cost/role`）可保持无 year 参数（展示全量角色占比），但 `getCostByDimension("role", year)` 必须传 year。

---

### P1-1 CORS 通配符 → ✅ 已修复

`CorsConfig.java` 改为 `@Value` 注入域名白名单：
```java
@Value("${app.cors.allowed-origins:http://localhost:5173}")
private String[] allowedOrigins;
// ...
.allowedOrigins(allowedOrigins)
.allowCredentials(true);
```
`application.yml:31-33` 配置 `app.cors.allowed-origins: http://localhost:5173`。✅ 通过

---

### P1-2 缺少 @Transactional → ✅ 已修复

`CostAnalysisService.java:25` 添加类级注解：
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CostAnalysisService {
```
✅ 通过

---

### P1-3 LAZY 关联 N+1 查询 → ✅ 已修复

`CostRecordRepository.java:15-25` 的 `findByFilters` 增加 JOIN FETCH：
```java
@Query("SELECT c FROM CostRecord c " +
       "LEFT JOIN FETCH c.department " +
       "LEFT JOIN FETCH c.employee " +
       "WHERE ...")
```
✅ 通过

---

### P1-4 Object[] 类型强转风险 → ✅ 已修复

`CostAnalysisService.java:259-274` 新增安全提取辅助方法：
```java
private BigDecimal toBigDecimal(Object value) {
    if (value == null) return BigDecimal.ZERO;
    if (value instanceof BigDecimal) return (BigDecimal) value;
    return new BigDecimal(((Number) value).toString());
}
private int toInt(Object value) {
    return ((Number) value).intValue();
}
```
`getMonthlyTrend` 使用 `toBigDecimal(r[2])` 和 `toInt(r[1])` 替代直接强转。✅ 通过

> **注**: `mapRoleStats` 中 `((Employee.EmployeeRole) r[0])` 仍为直接强转，但 enum 映射（`EnumType.STRING`）跨 dialect 稳定，风险低于数值类型，降级为 P2。

---

### P1-5 laborCost = totalCost 语义不正确 → ✅ 已修复

`CostAnalysisService.java:66-70` 按 `costType` 过滤：
```java
BigDecimal laborTotal = records.stream()
    .filter(r -> "LABOR".equals(r.getCostType()))
    .map(CostRecord::getAmount)
    .reduce(BigDecimal.ZERO, BigDecimal::add);
dto.setLaborCost(laborTotal);
```
✅ 通过

---

### P1-6 缺少全局异常处理器 → ✅ 已修复

新增 `GlobalExceptionHandler.java`（39 行），包含 4 个 `@ExceptionHandler`：
- `IllegalArgumentException` → 400 Bad Request
- `MethodArgumentNotValidException` → 400 + 字段错误明细
- `ConstraintViolationException` → 400
- `Exception` → 500 + 通用消息

✅ 通过

---

### P1-7 H2 Console 在生产配置暴露 → ✅ 已修复

`application.yml:10-13` 默认关闭：
```yaml
h2:
  console:
    enabled: false
```
`application-dev.yml:1-4` 开发环境开启：
```yaml
spring:
  h2:
    console:
      enabled: true
```
✅ 通过

---

### P1-8 CostQueryRequest 缺少输入校验 → ✅ 已修复

`CostQueryRequest.java` 添加 JSR-303 注解：
```java
@Min(2000) @Max(2100) private Integer costYear;
@Min(1) @Max(12) private Integer costMonth;
@Min(1) @Max(4) private Integer quarter;
```
`CostAnalysisController.java:35` 添加 `@Valid`：
```java
public CostSummaryDTO getSummary(@Valid CostQueryRequest request) {
```
✅ 通过

---

### P2-11 throws Exception 过于宽泛 → ⚠️ 部分修复

`ExcelExportService` 已改为 `throws IOException` ✅。但 `CostAnalysisController.java:77,93` 的 `exportSummary`/`exportProjectCost` 仍声明 `throws Exception`：

```java
public ResponseEntity<byte[]> exportSummary(@Valid CostQueryRequest request) throws Exception {
public ResponseEntity<byte[]> exportProjectCost(@RequestParam(required = false) Integer budgetYear) throws Exception {
```

**修复建议**: Controller 改为 `throws IOException`（或直接不声明，由 GlobalExceptionHandler 兜底）。

---

### P2-12 ddl-auto: create-drop → ✅ 已修复

`application.yml:16` 默认 `ddl-auto: none`，`application-dev.yml:6-7` 开发环境 `create-drop`。✅ 通过

---

### P2-13 findTotalCostByYear 返回 null 风险 → ✅ 已修复

返回类型改为 `Optional<BigDecimal>`：
```java
Optional<BigDecimal> findTotalCostByYear(@Param("year") Integer year);
```
✅ 通过

---

### P2-14 getCostByRole 与 getCostByDimension("role") 代码重复 → ✅ 已修复

抽取为 `mapRoleStats(List<Object[]> rows)` 私有方法，两处调用复用。✅ 通过

---

### P2-16 ProjectCostDTO actualCost null 未防御 → ✅ 已修复

```java
BigDecimal actual = actualCost != null ? actualCost : BigDecimal.ZERO;
```
✅ 通过

---

## 严重性等级统计（复审）

| 等级 | 数量 | 说明 |
|------|------|------|
| **P0（阻塞）** | **1** | P0-2 部分遗留：role 维度 year 仍忽略 |
| **P1（推荐）** | **0** | 前次 P1 全部已修复 |
| **P2（参考）** | **3** | Controller throws Exception、mapRoleStats enum 强转、quarter+costMonth 行为 |

**blocker_count = 1**

---

## P0 — 阻塞问题（遗留）

### P0-R1 `getCostByDimension` case "role" 仍忽略 year 参数

**文件**: `service/CostAnalysisService.java:160-163`

**问题**: 前次 P0-2 列出 5 个维度忽略 year（department/businessLine/project/employee/role）。修复阶段为前 4 个维度的 Repository 方法增加了 `costYear` 参数并透传，但 **role 维度遗漏**：

- `findCostByRole()` Repository 方法未增加 `costYear` 参数（`CostRecordRepository.java:40-42`）
- Service `case "role"` 调用 `findCostByRole()` 时未传 `year`（`CostAnalysisService.java:161`）

**影响**: `GET /api/cost/dimension?dimension=role&year=2026` 返回全部年度的角色成本汇总，year 筛选对该维度无效。前端 `DimensionFilter` 的年度选择器在 role 维度下失效。

**跨仓影响**: 前端 `costApi.ts` → `getCostByDimension("role", year)` 传递 `year`，后端静默忽略。

**修复建议**:
```java
// Repository
@Query("SELECT c.employee.role, SUM(c.amount) FROM CostRecord c " +
       "WHERE (:costYear IS NULL OR c.costYear = :costYear) " +
       "GROUP BY c.employee.role")
List<Object[]> findCostByRole(@Param("costYear") Integer costYear);

// Service case "role"
case "role": {
    List<Object[]> rows = costRecordRepository.findCostByRole(year);
    return mapRoleStats(rows);
}
```

> 独立端点 `getCostByRole()`（S03）可继续调用 `findCostByRole(null)` 或保持无参重载，展示全量角色占比。

---

## P2 — 参考改进（遗留）

### P2-R1 Controller 导出方法仍 `throws Exception`

**文件**: `controller/CostAnalysisController.java:77,93`

`exportSummary` 和 `exportProjectCost` 仍声明 `throws Exception`，ExcelExportService 已改为 `throws IOException`，Controller 未同步。

**修复**: 改为 `throws IOException`。

### P2-R2 `mapRoleStats` 中 enum 直接强转

**文件**: `service/CostAnalysisService.java:233`

```java
((Employee.EmployeeRole) r[0]).name()
```

JPA enum 映射（`EnumType.STRING`）跨 dialect 稳定，风险低于数值类型强转，但与 `toBigDecimal`/`toInt` 的安全提取模式不一致。

**修复**: 可增加 `toEnum(Object value, Class<T> enumType)` 辅助方法统一处理，或保持现状（低风险）。

### P2-R3 quarter 与 costMonth 同时存在时行为不直观

**文件**: `service/CostAnalysisService.java:35-58`

行为与前次评审一致，注释已说明设计意图。当前 quarter 非空时在内存中按月份范围过滤，costMonth 仍传入 DB 查询。建议后续迭代中当 quarter 非空时忽略 costMonth。

---

## 跨仓对齐点检查

### 前后端 API 路径对齐 ✅

| 前端 (costApi.ts) | 后端 (CostAnalysisController) | 状态 |
|---|---|---|
| `GET /cost/summary` | `@GetMapping("/summary")` + `@Valid CostQueryRequest` | ✅ 对齐（新增 @Valid） |
| `GET /cost/trend?year=` | `@GetMapping("/trend")` + `@RequestParam(required=false) Integer year` | ✅ 对齐 |
| `GET /cost/role` | `@GetMapping("/role")` | ✅ 对齐 |
| `GET /cost/project?budgetYear=` | `@GetMapping("/project")` + `@RequestParam(required=false) Integer budgetYear` | ✅ 对齐 |
| `GET /cost/dimension?dimension=&year=` | `@GetMapping("/dimension")` + `@RequestParam String dimension` + `@RequestParam(required=false) Integer year` | ⚠️ 路径对齐，**role 维度 year 仍未生效**（见 P0-R1） |
| `GET /cost/export/summary` | `@GetMapping("/export/summary")` + `@Valid CostQueryRequest` | ✅ 对齐 |
| `GET /cost/export/project?budgetYear=` | `@GetMapping("/export/project")` + `@RequestParam(required=false) Integer budgetYear` | ✅ 对齐 |

### 前后端 DTO 字段对齐 ✅

无变更，前次评审已确认全部对齐。

### 跨仓功能缺陷传导

| 缺陷 | 后端位置 | 前端影响 | 状态 |
|------|---------|---------|------|
| P0-1 项目成本年度错配 | `CostAnalysisService.getProjectCost` | 预算占比/超支金额 | ✅ 已修复 |
| P0-2 维度查询 year 忽略 | `CostAnalysisService.getCostByDimension` | 年度筛选器 | ⚠️ role 维度仍失效 |

---

## 结论

### 合并建议：**阻止合并** ❌

前次评审的 2 个 P0 中，P0-1 已完全修复，P0-2 部分修复（4/5 维度已修复，**role 维度仍忽略 year 参数**）。存在 1 个遗留 P0 阻塞问题：

1. **P0-R1**: `getCostByDimension` case "role" 仍调用无参 `findCostByRole()`，year 参数被静默忽略，前端角色维度年度筛选器失效

### 修复进度

| 原问题 | 等级 | 修复状态 |
|--------|------|---------|
| P0-1 项目成本年度错配 | P0 | ✅ 已修复 |
| P0-2 维度查询 year 忽略 | P0 | ⚠️ 部分修复（role 遗留） |
| P1-1 CORS 通配符 | P1 | ✅ 已修复 |
| P1-2 缺 @Transactional | P1 | ✅ 已修复 |
| P1-3 N+1 查询 | P1 | ✅ 已修复 |
| P1-4 Object[] 强转 | P1 | ✅ 已修复 |
| P1-5 laborCost 语义 | P1 | ✅ 已修复 |
| P1-6 全局异常处理 | P1 | ✅ 已修复 |
| P1-7 H2 Console 暴露 | P1 | ✅ 已修复 |
| P1-8 输入校验 | P1 | ✅ 已修复 |
| P2-11 throws Exception | P2 | ⚠️ 部分修复（Controller 遗留） |
| P2-12 ddl-auto | P2 | ✅ 已修复 |
| P2-13 null 返回风险 | P2 | ✅ 已修复 |
| P2-14 代码重复 | P2 | ✅ 已修复 |
| P2-16 null 防御 | P2 | ✅ 已修复 |

### 修复优先级

1. **必须修复（合并前）**: P0-R1（role 维度 year 透传）
2. **可选改进（后续迭代）**: P2-R1, P2-R2, P2-R3

### 验证建议

修复 P0-R1 后，需前后端联调验证：
- `GET /api/cost/dimension?dimension=role&year=2026` 返回数据应仅含 2026 年角色成本汇总
- `GET /api/cost/dimension?dimension=department&year=2026` 返回数据应仅含 2026 年部门汇总（已修复，需回归验证）
