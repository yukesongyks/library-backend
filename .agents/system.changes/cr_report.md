# 代码评审报告

**任务ID**: DEV-966dcd0a-7905-11f1-9649-3b4281182f10-4d166906-8c85-4bd4-8e0a-eeee82d1ba1b  
**评审阶段**: test / 代码评审  
**评审日期**: 2026-08-06  
**评审范围**: ApiRequest.java, ExportRequest.java  
**评审方法**: SDD 范式结构化审查（功能核对→可读性→可靠性→自定义扩展）

---

## 评审结论

| 等级 | 数量 | 判定 |
|------|------|------|
| P0 (Blocker) | 1 | **不通过** |
| P1 (Major) | 2 | - |
| P2 (Minor) | 4 | - |
| **总计** | **7** | - |

---

## 问题清单

### P0 - Blocker

#### [CR-001] ApiRequest 缺失 algorithm 字段，哈希接口功能缺陷
- **文件**: `src/main/java/com/library/backend/dto/ApiRequest.java`
- **位置**: 类字段定义区
- **描述**: 系分设计文档 §4.2 明确定义哈希接口请求体包含 `algorithm` 字段（如 "SHA-256"），但 `ApiRequest` 中仅有 `input` 字段，完全缺失 `algorithm`。前端无法指定哈希算法，导致哈希功能不可用或只能硬编码默认算法，违反需求"哈希算法接口"的完整性要求。
- **影响**: 哈希接口核心功能缺失，前后端契约断裂。
- **修复建议**: 在 `ApiRequest` 中新增 `private String algorithm;` 字段，并添加 `@NotBlank` 校验（当 apiType 为 hash 时条件必填，或在 Service 层做条件校验）。
- **关联设计**: `.agents/system.changes/design.md` §4.2

---

### P1 - Major

#### [CR-002] 人员信息字段来源与系分设计冲突
- **文件**: `src/main/java/com/library/backend/dto/ApiRequest.java`
- **位置**: L23-L35 (userId, userType, level, department)
- **描述**: 系分设计 §6 明确"人员信息从 HTTP 请求头中提取（由网关或前端注入）"，AOP 切面从 Header 读取。但 `ApiRequest` 将这些字段放入请求体 Body。存在两种风险：(1) AOP 从 Header 取值，Body 中字段被忽略，造成前端传参困惑；(2) AOP 从 Body 取值，与设计文档矛盾，且 Hello 接口为 GET 请求无 Body，埋点将无法获取人员信息。
- **影响**: 埋点数据准确性受损，GET 接口（/api/hello）无法携带人员信息。
- **修复建议**: 方案A：移除 Body 中人员字段，统一从 Header 获取，Hello 接口也通过 Header 传递人员信息。方案B：更新设计文档，明确混合策略（POST 从 Body、GET 从 Header），并在 AOP 中实现双源兼容。推荐方案A。
- **关联设计**: `.agents/system.changes/design.md` §6, §4.1

#### [CR-003] ExportRequest 与导出接口契约不一致
- **文件**: `src/main/java/com/library/backend/dto/ExportRequest.java`
- **位置**: 类定义及 tab 字段
- **描述**: 系分设计 §4.4 定义导出接口为 `GET /api/export/{type}`，使用路径参数区分导出类型。但 `ExportRequest` 是 POST 请求体 DTO，包含 `tab` 字段。若 Controller 实际使用 `@RequestBody ExportRequest`，则接口变为 POST 且参数在 Body 中，与设计文档 GET+路径参数方式冲突。
- **影响**: 前后端联调失败，前端按设计文档调用 GET 将 405 Method Not Allowed。
- **修复建议**: 确认实际实现方式。若采用 POST+Body，需同步更新设计文档 §4.4；若坚持 GET+路径参数，应删除 `ExportRequest` 或将其改为查询参数 DTO。
- **关联设计**: `.agents/system.changes/design.md` §4.4

---

### P2 - Minor

#### [CR-004] userType 缺少枚举值校验
- **文件**: `src/main/java/com/library/backend/dto/ApiRequest.java`
- **位置**: L28-L29
- **描述**: 注释标明 `EMPLOYEE | CONTRACTOR | INTERN`，但未使用 `@Pattern(regexp="^(EMPLOYEE|CONTRACTOR|INTERN)$")` 或自定义 `@UserType` 注解约束。非法值（如 "admin"）可通过校验进入业务层。
- **修复建议**: 添加正则校验注解或创建枚举类 + `@Valid` 校验。

#### [CR-005] 条件必填无法表达
- **文件**: `src/main/java/com/library/backend/dto/ApiRequest.java`
- **位置**: L37-L41 (input, array)
- **描述**: `input` 仅在哈希接口必填，`array` 仅在冒泡排序必填。统一 DTO 无法通过 Bean Validation 表达条件必填，依赖 Service 层手动校验，易遗漏。
- **修复建议**: 在对应 Service 方法入口添加参数校验，或拆分为 `HashRequest` / `BubbleSortRequest` 独立 DTO。

#### [CR-006] ExportRequest.filters 类型过于宽泛
- **文件**: `src/main/java/com/library/backend/dto/ExportRequest.java`
- **位置**: L28
- **描述**: `Map<String, Object>` 接受任意嵌套结构，反序列化安全风险较高，且下游处理需大量类型判断。
- **修复建议**: 定义 `ExportFilterCriteria` 类明确字段（startDate, endDate, apiName 等），或至少使用 `Map<String, String>`。

#### [CR-007] ExportRequest.tab 缺少枚举校验
- **文件**: `src/main/java/com/library/backend/dto/ExportRequest.java`
- **位置**: L24-L25
- **描述**: 注释标明 `hello | hash | bubble-sort`，但未添加 `@Pattern` 约束。非法 tab 值可导致导出逻辑异常或返回空数据。
- **修复建议**: 添加 `@Pattern(regexp="^(hello|hash|bubble-sort)$", message="tab类型不合法")`。

---

## 跨仓对齐点检查

| 对齐点 | 状态 | 说明 |
|--------|------|------|
| API 请求体结构 | ❌ 未对齐 | ApiRequest 缺 algorithm 字段；人员字段来源与 Header 方案冲突 |
| 导出接口契约 | ❌ 未对齐 | 设计为 GET+路径参数，代码为 POST+Body DTO |
| 埋点字段定义 | ⚠️ 部分对齐 | 字段名一致，但获取来源（Header vs Body）未统一 |
| 响应体结构 | ✅ 已对齐 | ApiResponse 统一封装，未发现偏差（本次未审查） |

---

## 评审总结

本次评审发现 **1 个 Blocker** 和 **2 个 Major** 问题，均涉及前后端契约不一致，必须在联调前修复。核心问题是代码实现与系分设计文档存在显著偏差：哈希接口缺字段、导出接口方法/参数位置不匹配、埋点人员信息来源矛盾。建议优先修复 P0 和 P1 问题后重新提交评审。
