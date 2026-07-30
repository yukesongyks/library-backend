> **文档元信息**
>
> | 项目 | 内容 |
> |------|------|
> | 文档版本 | v1.0 |
> | 作者 | DTCoder（测分阶段自动产出） |
> | 创建日期 | 2026-07-30 |
> | 需求来源 | docs/specs/2026-07-30-personnel-dashboard-clarification.md |
> | 系分来源 | .agents/system.changes/design.md |
> | 评审状态 | 待评审 |

# 人员看板 测试分析文档（测分）

## 1. 测试范围与目标

### 1.1 测试目标

验证人员看板（library-backend）的 14 个功能点（F01-F14）在正常流程、边界条件、异常场景下的行为符合系分设计约定，重点覆盖：

- 员工 CRUD 全生命周期（新增→编辑→逻辑删除）+ 工号唯一性约束
- 成本预算 CRUD + 白名单鉴权（403 不泄露数据存在性）
- 批量导入逐行独立处理（单行失败不影响其他行）+ 整体级拒绝（格式/大小/超限）
- 前后端 REST 契约一致性（字段名、分页参数、导入响应结构）

### 1.2 测试范围

| 范围 | 包含 | 不包含 |
|------|------|--------|
| 人员管理模块 | F01-F06（列表/详情/新增/编辑/逻辑删除/批量导入） | — |
| 成本预算模块 | F07-F11（列表/新增/编辑/物理删除/批量导入） | — |
| 白名单管理模块 | F12-F14（查询/新增/删除） | — |
| 跨模块 | F02 详情含预算汇总（白名单联动）、预算导入引用工号校验 | — |
| 不测项 | — | 前端 UI 渲染（library-frontend 范围）、SSO/组织架构同步、敏捷看板拖拽、成本实际发生额核算 |

### 1.3 测试类型分层

| 层次 | 测试类型 | 覆盖范围 | 工具/框架 |
|------|----------|----------|-----------|
| L1 | 单元测试 | Service 层方法逻辑（S01-S15）、字段校验、金额范围校验、工号唯一校验 | JUnit5 + Mockito |
| L2 | 集成测试 | Controller→Service→Mapper→DB 全链路（H2/嵌入式MySQL）、白名单鉴权拦截器 | Spring Boot Test + @SpringBootTest |
| L3 | API 接口测试 | REST 端点 W01-W14 入参/出参/状态码/错误码契约 | MockMvc / REST Assured |
| L4 | 异常场景测试 | 导入逐行处理异常、并发唯一索引冲突、白名单 403 不泄露 | Spring Boot Test |

## 2. 测试环境与数据准备

### 2.1 测试环境

| 项目 | 说明 |
|------|------|
| 数据库 | H2 内存数据库（兼容 MySQL 模式）或 Testcontainers MySQL |
| 应用 | Spring Boot 内嵌容器，随机端口 |
| 租户上下文 | 测试用 tenant_id = "TEST_TENANT"，通过请求头注入 |
| 登录态 | Mock 拦截器注入 currentUserId，模拟白名单/非白名单用户 |
| 文件上传 | MockMultipartFile 构造 .xlsx/.csv 测试文件 |

### 2.2 测试数据基线

**员工基础数据：**

| id | tenant_id | employee_no | name | department | position | hire_date | phone | email | status | is_deleted |
|----|-----------|-------------|------|------------|----------|-----------|-------|-------|--------|------------|
| 1 | TEST_TENANT | E001 | 张三 | 研发部 | 工程师 | 2024-01-15 | 13800000001 | zhangsan@test.com | 0(在职) | 0(未删除) |
| 2 | TEST_TENANT | E002 | 李四 | 市场部 | 经理 | 2023-06-01 | 13800000002 | lisi@test.com | 0(在职) | 0(未删除) |
| 3 | TEST_TENANT | E003 | 王五 | 研发部 | 架构师 | 2022-03-10 | 13800000003 | wangwu@test.com | 1(离职) | 0(未删除) |
| 4 | TEST_TENANT | E004 | 赵六 | 财务部 | 会计 | 2024-09-01 | 13800000004 | zhaoliu@test.com | 0(在职) | 1(已删除) |

**成本预算基础数据：**

| id | tenant_id | employee_id | month | amount |
|----|-----------|-------------|-------|--------|
| 1 | TEST_TENANT | 1 | 2026-07 | 50000.00 |
| 2 | TEST_TENANT | 1 | 2026-06 | 48000.00 |
| 3 | TEST_TENANT | 2 | 2026-07 | 35000.00 |

**白名单基础数据：**

| id | tenant_id | user_id |
|----|-----------|--------|
| 1 | TEST_TENANT | WL_USER_001 |
| 2 | TEST_TENANT | WL_USER_002 |

> 非白名单测试用户：`NON_WL_USER`（不在白名单表中）

## 3. 功能点测试用例设计

### 3.1 人员管理模块（F01-F06）

#### TC-F01：员工分页列表查询（W01 GET /api/employees）

| 用例编号 | 场景 | 前置条件 | 入参 | 预期结果 | 优先级 |
|----------|------|----------|------|----------|--------|
| F01-TC01 | 无筛选条件分页查询 | 基线数据4条（含1条已删除） | pageNo=1, pageSize=20 | code=200, total=3（过滤is_deleted=1），list含E001/E002/E003 | P0 |
| F01-TC02 | 按姓名模糊查询 | 基线数据 | name=张, pageNo=1, pageSize=20 | code=200, total=1, list含E001 | P0 |
| F01-TC03 | 按工号精确查询 | 基线数据 | employeeNo=E002, pageNo=1, pageSize=20 | code=200, total=1, list含E002 | P0 |
| F01-TC04 | 按部门精确查询 | 基线数据 | department=研发部, pageNo=1, pageSize=20 | code=200, total=2, list含E001/E003 | P1 |
| F01-TC05 | 按在职状态筛选 | 基线数据 | status=0, pageNo=1, pageSize=20 | code=200, total=2, list含E001/E002（E003离职过滤） | P0 |
| F01-TC06 | 多条件组合筛选 | 基线数据 | name=王, status=1, pageNo=1, pageSize=20 | code=200, total=1, list含E003 | P1 |
| F01-TC07 | 已删除员工不在列表 | 基线数据含E004(is_deleted=1) | pageNo=1, pageSize=20 | E004 不出现在 list 中 | P0 |
| F01-TC08 | 分页参数缺失 | — | pageNo=1（缺pageSize） | code=非200, msg含参数校验失败, 错误码EMPLOYEE_001 | P0 |
| F01-TC09 | 页码超出范围 | 基线3条 | pageNo=100, pageSize=20 | code=200, total=3, list=空数组 | P1 |
| F01-TC10 | 排序验证 | 基线数据 | pageNo=1, pageSize=20 | list 按 gmt_modified 倒序排列 | P2 |

#### TC-F02：员工详情查询含预算汇总（W02 GET /api/employees/{id}）

| 用例编号 | 场景 | 前置条件 | 入参 | 预期结果 | 优先级 |
|----------|------|----------|------|----------|--------|
| F02-TC01 | 白名单用户查详情含预算汇总 | currentUser=WL_USER_001, 员工id=1（有2条预算） | GET /api/employees/1 | code=200, data.budgetSummary.totalAmount=98000.00（50000+48000） | P0 |
| F02-TC02 | 非白名单用户查详情预算汇总为null | currentUser=NON_WL_USER, 员工id=1 | GET /api/employees/1 | code=200, data.budgetSummary=null（非403，详情本身允许访问） | P0 |
| F02-TC03 | 查询不存在员工 | — | GET /api/employees/99999 | code=非200, msg=员工不存在, 错误码EMPLOYEE_002 | P0 |
| F02-TC04 | 查询已逻辑删除员工详情 | 基线含E004(is_deleted=1) | GET /api/employees/4 | code=200, 返回E004详情（详情接口不受is_deleted过滤），budgetSummary按白名单逻辑 | P1 |
| F02-TC05 | 员工无预算时预算汇总 | 员工id=3（无预算记录）, currentUser=WL_USER_001 | GET /api/employees/3 | code=200, data.budgetSummary.totalAmount=0.00 或 null（按实现确认） | P1 |

#### TC-F03：员工新增（W03 POST /api/employees）

| 用例编号 | 场景 | 前置条件 | 入参 | 预期结果 | 优先级 |
|----------|------|----------|------|----------|--------|
| F03-TC01 | 正常新增员工 | 工号E999不存在 | employeeNo=E999, name=测试, department=研发部 | code=200, data=新员工ID, DB新增记录status=0(默认在职) | P0 |
| F03-TC02 | 工号重复（库内已存在） | 基线含E001 | employeeNo=E001, name=重复, department=研发部 | code=非200, 错误码EMPLOYEE_003, msg=工号已存在 | P0 |
| F03-TC03 | 工号与已逻辑删除记录重复 | 基线含E004(is_deleted=1) | employeeNo=E004, name=测试, department=研发部 | code=非200, 错误码EMPLOYEE_003（含已删除记录校验） | P0 |
| F03-TC04 | 必填字段缺失-无工号 | — | name=测试, department=研发部（缺employeeNo） | code=非200, 错误码EMPLOYEE_001 | P0 |
| F03-TC05 | 必填字段缺失-无姓名 | — | employeeNo=E998, department=研发部（缺name） | code=非200, 错误码EMPLOYEE_001 | P0 |
| F03-TC06 | 必填字段缺失-无部门 | — | employeeNo=E998, name=测试（缺department） | code=非200, 错误码EMPLOYEE_001 | P0 |
| F03-TC07 | 可选字段全填 | — | employeeNo=E997, name=全字段, department=研发部, position=架构师, hireDate=2024-01-01, phone=13900000000, email=full@test.com, status=1 | code=200, DB记录所有字段值正确 | P1 |
| F03-TC08 | status默认值验证 | — | employeeNo=E996, name=默认, department=研发部（不传status） | code=200, DB记录status=0（默认在职） | P1 |

#### TC-F04：员工编辑（W04 PUT /api/employees/{id}）

| 用例编号 | 场景 | 前置条件 | 入参 | 预期结果 | 优先级 |
|----------|------|----------|------|----------|--------|
| F04-TC01 | 正常编辑非工号字段 | 员工id=1 | id=1, name=张三改, department=产品部 | code=200, DB记录name/department已更新, employeeNo不变 | P0 |
| F04-TC02 | 入参不含工号字段 | 员工id=2 | id=2, name=李四改（不含employeeNo） | code=200, 正常更新（忽略工号，不报错） | P0 |
| F04-TC03 | 入参工号与原值一致 | 员工id=1(E001) | id=1, name=张三, employeeNo=E001（与原值一致） | code=200, 正常更新（一致时忽略） | P0 |
| F04-TC04 | 入参工号与原值不一致 | 员工id=1(E001) | id=1, name=张三, employeeNo=E999（与原值不一致） | code=非200, 错误码EMPLOYEE_004, msg=工号不可修改 | P0 |
| F04-TC05 | 编辑不存在的员工 | — | id=99999, name=不存在 | code=非200, 错误码EMPLOYEE_002 | P0 |
| F04-TC06 | 编辑状态流转在职→离职 | 员工id=1(status=0) | id=1, status=1 | code=200, DB status=1（离职） | P1 |
| F04-TC07 | 编辑状态流转离职→在职 | 员工id=3(status=1) | id=3, status=0 | code=200, DB status=0（在职） | P1 |

#### TC-F05：员工逻辑删除（W05 DELETE /api/employees/{id}）

| 用例编号 | 场景 | 前置条件 | 入参 | 预期结果 | 优先级 |
|----------|------|----------|------|----------|--------|
| F05-TC01 | 正常逻辑删除在职员工 | 员工id=1(is_deleted=0) | DELETE /api/employees/1 | code=200, DB is_deleted=1（软删，不物理删除） | P0 |
| F05-TC02 | 删除后预算仍可读 | 员工id=1有预算记录, 删除后 | 删除id=1后查询预算 by employee_id=1 | 预算记录仍存在且可读（按employee_id关联不受is_deleted影响） | P0 |
| F05-TC03 | 删除后列表不可见 | 员工id=1已逻辑删除 | GET /api/employees（列表查询） | E001不出现在列表中（is_deleted过滤生效） | P0 |
| F05-TC04 | 删除不存在的员工 | — | DELETE /api/employees/99999 | code=非200, 错误码EMPLOYEE_002 | P0 |
| F05-TC05 | 重复删除已删除员工 | 员工id=4(is_deleted=1) | DELETE /api/employees/4 | code=非200, 错误码EMPLOYEE_002（或幂等返回200，按实现确认） | P1 |

#### TC-F06：员工批量导入（W06 POST /api/employees/import）

| 用例编号 | 场景 | 前置条件 | 入参 | 预期结果 | 优先级 |
|----------|------|----------|------|----------|--------|
| F06-TC01 | 正常导入全部成功 | 空库, xlsx含3行有效数据 | file=employees_valid.xlsx(3行: E101/E102/E103) | code=200, successCount=3, failureCount=0, failures=[] | P0 |
| F06-TC02 | 部分成功部分失败 | 基线含E001, xlsx含3行(E001重复+2行新) | file=employees_partial.xlsx | code=200, successCount=2, failureCount=1, failures含{row:1, employeeNo:E001, reason:工号已存在} | P0 |
| F06-TC03 | 全部行失败仍返回200 | 基线含E001/E002, xlsx含2行均为已存在工号 | file=employees_all_fail.xlsx | code=200（不抛5xx）, successCount=0, failureCount=2, failures含2条明细 | P0 |
| F06-TC04 | 必填字段缺失-工号为空 | xlsx含1行employeeNo为空 | file=employees_no_eno.xlsx | code=200, failureCount=1, failures含{reason:必填字段缺失} | P0 |
| F06-TC05 | 必填字段缺失-姓名为空 | xlsx含1行name为空 | file=employees_no_name.xlsx | code=200, failureCount=1, failures含{reason:必填字段缺失} | P0 |
| F06-TC06 | 日期格式非法 | xlsx含1行hireDate=2024/01/01 | file=employees_bad_date.xlsx | code=200, failureCount=1, failures含{reason:字段格式错误} | P0 |
| F06-TC07 | 状态枚举非法 | xlsx含1行status=2 | file=employees_bad_status.xlsx | code=200, failureCount=1, failures含{reason:字段格式错误} | P0 |
| F06-TC08 | 文件内工号重复 | xlsx含2行相同工号E200 | file=employees_dup_eno.xlsx | code=200, successCount=1, failureCount=1（第2行命中已入库）, failures含{reason:工号已存在} | P0 |
| F06-TC09 | 文件格式不支持 | — | file=test.txt（非xlsx/csv） | code=非200(400), 错误码EMPLOYEE_005, msg=文件为空或格式不支持 | P0 |
| F06-TC10 | 文件为空 | — | file=empty.xlsx（0行数据） | code=非200(400), 错误码EMPLOYEE_005, msg=文件为空或格式不支持 | P0 |
| F06-TC11 | 文件行数超限 | xlsx含1001行 | file=employees_1001.xlsx | code=非200(400), 错误码EMPLOYEE_006, msg=单次导入上限1000行 | P0 |
| F06-TC12 | 文件大小超限 | xlsx>5MB | file=employees_large.xlsx | code=非200(413), 错误码EMPLOYEE_007, msg=文件过大，上限5MB | P0 |
| F06-TC13 | CSV格式导入兼容 | 空库, csv含2行有效数据 | file=employees_valid.csv | code=200, successCount=2, failureCount=0 | P1 |
| F06-TC14 | 混合失败原因 | xlsx含4行（必填缺失+工号重复+日期非法+1行有效） | file=employees_mixed.xlsx | code=200, successCount=1, failureCount=3, failures含3种不同reason | P1 |
| F06-TC15 | 系统内部异常不回滚 | Mock Mapper INSERT 抛异常 on 第2行 | file=employees_3rows.xlsx | 第1行入库成功，第2行记入failures（导入服务异常），第3行继续处理（逐行独立） | P1 |

### 3.2 成本预算模块（F07-F11）

#### TC-F07：成本预算列表查询（W07 GET /api/cost-budgets）

| 用例编号 | 场景 | 前置条件 | 入参 | 预期结果 | 优先级 |
|----------|------|----------|------|----------|--------|
| F07-TC01 | 白名单用户查询全部预算 | currentUser=WL_USER_001, 基线3条预算 | pageNo=1, pageSize=20 | code=200, total=3, list含3条（含员工姓名冗余） | P0 |
| F07-TC02 | 非白名单用户查询预算 | currentUser=NON_WL_USER | pageNo=1, pageSize=20 | code=非200(403), 错误码BUDGET_002, msg=无成本预算访问权限 | P0 |
| F07-TC03 | 白名单用户按员工ID筛选 | currentUser=WL_USER_001 | employeeId=1, pageNo=1, pageSize=20 | code=200, total=2（E001的2条预算） | P0 |
| F07-TC04 | 白名单用户按月份筛选 | currentUser=WL_USER_001 | month=2026-07, pageNo=1, pageSize=20 | code=200, total=2（E001+E002的7月预算） | P1 |
| F07-TC05 | 白名单用户按工号筛选 | currentUser=WL_USER_001 | employeeNo=E001, pageNo=1, pageSize=20 | code=200, total=2（工号转查employeeId） | P1 |
| F07-TC06 | 白名单-403不泄露数据存在性 | currentUser=NON_WL_USER, 有预算数据 | 任意查询参数 | code=403, 不返回total/list，响应体不含任何预算数据 | P0 |
| F07-TC07 | 分页参数缺失 | currentUser=WL_USER_001 | pageNo=1（缺pageSize） | code=非200, 错误码BUDGET_001 | P0 |

#### TC-F08：成本预算新增（W08 POST /api/cost-budgets）

| 用例编号 | 场景 | 前置条件 | 入参 | 预期结果 | 优先级 |
|----------|------|----------|------|----------|--------|
| F08-TC01 | 白名单用户正常新增 | currentUser=WL_USER_001, 员工id=3无7月预算 | employeeId=3, month=2026-07, amount=40000.00 | code=200, data=新预算ID, DB新增记录 | P0 |
| F08-TC02 | 非白名单用户新增 | currentUser=NON_WL_USER | employeeId=3, month=2026-07, amount=40000.00 | code=非200(403), 错误码BUDGET_002 | P0 |
| F08-TC03 | 同人员同月重复（唯一索引兜底） | 基线含员工id=1的2026-07预算 | employeeId=1, month=2026-07, amount=60000.00 | code=非200, 错误码BUDGET_003, msg=该月份预算已存在 | P0 |
| F08-TC04 | 预算金额为负数 | currentUser=WL_USER_001 | employeeId=3, month=2026-07, amount=-1000.00 | code=非200, 错误码BUDGET_004, msg=预算金额超出范围 | P0 |
| F08-TC05 | 预算金额超上限 | currentUser=WL_USER_001 | employeeId=3, month=2026-07, amount=1000000000001.00（>10^12） | code=非200, 错误码BUDGET_004 | P0 |
| F08-TC06 | 预算金额边界值0 | currentUser=WL_USER_001 | employeeId=3, month=2026-07, amount=0.00 | code=200（金额=0在范围内） | P1 |
| F08-TC07 | 预算金额边界值10^12 | currentUser=WL_USER_001 | employeeId=3, month=2026-07, amount=1000000000000.00 | code=200（上限边界值合法） | P1 |
| F08-TC08 | 引用不存在的员工ID | currentUser=WL_USER_001 | employeeId=99999, month=2026-07, amount=1000.00 | code=非200, 错误码BUDGET_001（或专属错误码，按实现确认） | P1 |
| F08-TC09 | 月份格式非法 | currentUser=WL_USER_001 | employeeId=3, month=2026-7, amount=1000.00 | code=非200, 错误码BUDGET_001（格式校验） | P1 |
| F08-TC10 | 必填字段缺失 | currentUser=WL_USER_001 | employeeId=3, month=2026-07（缺amount） | code=非200, 错误码BUDGET_001 | P0 |

#### TC-F09：成本预算编辑（W09 PUT /api/cost-budgets/{id}）

| 用例编号 | 场景 | 前置条件 | 入参 | 预期结果 | 优先级 |
|----------|------|----------|------|----------|--------|
| F09-TC01 | 白名单用户正常编辑金额 | currentUser=WL_USER_001, 预算id=1 | id=1, amount=55000.00 | code=200, DB amount=55000.00 | P0 |
| F09-TC02 | 非白名单用户编辑 | currentUser=NON_WL_USER | id=1, amount=55000.00 | code=非200(403), 错误码BUDGET_002 | P0 |
| F09-TC03 | 编辑月份导致唯一冲突 | 预算id=1(员工1,2026-07), 预算id=2(员工1,2026-06) | id=1, month=2026-06 | code=非200, 错误码BUDGET_003（同人员同月冲突） | P1 |
| F09-TC04 | 编辑金额超范围 | currentUser=WL_USER_001 | id=1, amount=-500.00 | code=非200, 错误码BUDGET_004 | P1 |
| F09-TC05 | 编辑不存在的预算 | currentUser=WL_USER_001 | id=99999, amount=1000.00 | code=非200（预算不存在，按实现确认错误码） | P1 |

#### TC-F10：成本预算物理删除（W10 DELETE /api/cost-budgets/{id}）

| 用例编号 | 场景 | 前置条件 | 入参 | 预期结果 | 优先级 |
|----------|------|----------|------|----------|--------|
| F10-TC01 | 白名单用户正常物理删除 | currentUser=WL_USER_001, 预算id=1 | DELETE /api/cost-budgets/1 | code=200, DB记录已物理删除（非软删） | P0 |
| F10-TC02 | 非白名单用户删除 | currentUser=NON_WL_USER | DELETE /api/cost-budgets/1 | code=非200(403), 错误码BUDGET_002 | P0 |
| F10-TC03 | 删除后唯一约束释放可重新新增 | 预算id=1(员工1,2026-07)已删除 | 删除id=1后新增employeeId=1, month=2026-07 | 新增成功（物理删除后唯一索引释放） | P1 |
| F10-TC04 | 删除不存在的预算 | currentUser=WL_USER_001 | DELETE /api/cost-budgets/99999 | code=非200（预算不存在） | P1 |

#### TC-F11：成本预算批量导入（W11 POST /api/cost-budgets/import）

| 用例编号 | 场景 | 前置条件 | 入参 | 预期结果 | 优先级 |
|----------|------|----------|------|----------|--------|
| F11-TC01 | 白名单用户正常导入 | currentUser=WL_USER_001, 基线含E001/E002, xlsx含3行有效预算 | file=budgets_valid.xlsx(E001/2026-08/10000, E002/2026-08/20000, E003/2026-08/30000) | code=200, successCount=3, failureCount=0 | P0 |
| F11-TC02 | 非白名单用户导入 | currentUser=NON_WL_USER | file=budgets.xlsx | code=非200(403), 错误码BUDGET_002 | P0 |
| F11-TC03 | 引用不存在的工号 | xlsx含1行工号E999(不存在) | file=budgets_bad_eno.xlsx | code=200, failureCount=1, failures含{reason:工号不存在} | P0 |
| F11-TC04 | 同人员同月重复（库内） | 基线含E001的2026-07预算, xlsx含E001/2026-07 | file=budgets_dup.xlsx | code=200, failureCount=1, failures含{reason:该月份预算已存在} | P0 |
| F11-TC05 | 预算金额越界 | xlsx含1行amount=-100 | file=budgets_neg.xlsx | code=200, failureCount=1, failures含{reason:预算金额超出范围} | P0 |
| F11-TC06 | 月份格式非法 | xlsx含1行month=2026-7 | file=budgets_bad_month.xlsx | code=200, failureCount=1, failures含{reason:字段格式错误} | P0 |
| F11-TC07 | 全部行失败仍返回200 | xlsx含2行均为已存在预算 | file=budgets_all_fail.xlsx | code=200, successCount=0, failureCount=2 | P1 |
| F11-TC08 | 文件格式不支持 | — | file=budgets.txt | code=非200(400), 错误码BUDGET_005（文件为空或格式不支持） | P0 |
| F11-TC09 | 文件行数超限 | xlsx>1000行 | file=budgets_1001.xlsx | code=非200(400), 错误码BUDGET_006 | P0 |
| F11-TC10 | 文件大小超限 | xlsx>5MB | file=budgets_large.xlsx | code=非200(413), 错误码BUDGET_007 | P0 |
| F11-TC11 | 必填字段缺失 | xlsx含1行工号为空 | file=budgets_no_eno.xlsx | code=200, failureCount=1, failures含{reason:必填字段缺失} | P0 |

### 3.3 白名单管理模块（F12-F14）

#### TC-F12/F13/F14：白名单查询/新增/删除（W12/W13/W14）

| 用例编号 | 场景 | 前置条件 | 入参 | 预期结果 | 优先级 |
|----------|------|----------|------|----------|--------|
| WL-TC01 | 查询白名单列表 | 基线含2条白名单 | GET /api/cost-budgets/whitelist | code=200, list含WL_USER_001/WL_USER_002 | P0 |
| WL-TC02 | 新增白名单用户 | WL_USER_003不存在 | POST /api/cost-budgets/whitelist, body={userId:WL_USER_003} | code=200, DB新增白名单记录 | P0 |
| WL-TC03 | 新增后鉴权生效 | WL_USER_003已新增 | 以WL_USER_003查询预算列表 | code=200（白名单鉴权通过，之前403变为200） | P0 |
| WL-TC04 | 删除白名单用户 | 基线含WL_USER_002 | DELETE /api/cost-budgets/whitelist/WL_USER_002 | code=200, DB记录已删除 | P0 |
| WL-TC05 | 删除后鉴权失效 | WL_USER_002已删除 | 以WL_USER_002查询预算列表 | code=403（白名单鉴权拦截） | P0 |
| WL-TC06 | 新增重复白名单用户 | WL_USER_001已存在 | POST body={userId:WL_USER_001} | code=非200（重复新增，按实现确认错误码） | P1 |
| WL-TC07 | 删除不存在的白名单用户 | WL_USER_999不存在 | DELETE /api/cost-budgets/whitelist/WL_USER_999 | code=非200 或 幂等200（按实现确认） | P1 |
| WL-TC08 | 白名单鉴权-isWhitelisted方法单元测试 | — | 调用isWhitelisted("WL_USER_001") / isWhitelisted("NON_WL") | 返回true / false | P0 |

### 3.4 跨模块联动测试

| 用例编号 | 场景 | 测试链路 | 预期结果 | 优先级 |
|----------|------|----------|----------|--------|
| CROSS-TC01 | 员工逻辑删除后预算仍可读 | 删除E001 → 以白名单身份查E001预算列表 | 预算记录仍存在可读（employee_id关联不受is_deleted影响） | P0 |
| CROSS-TC02 | 员工详情预算汇总白名单联动 | 以非白名单查E001详情 → budgetSummary=null → 加入白名单 → 再查 | 第一次null，第二次有值 | P0 |
| CROSS-TC03 | 预算导入引用已删除员工工号 | E004已逻辑删除, 导入预算引用E004工号 | 预算导入成功（逻辑删除员工仍可通过工号引用，按设计"预算关联仍按employee_id可读"） | P1 |
| CROSS-TC04 | 租户隔离验证 | tenant_id=A的员工 vs tenant_id=B的员工 | A租户查询不到B租户的员工（所有查询默认带tenant_id过滤） | P0 |

## 4. 异常场景测试

### 4.1 导入异常场景（逐行独立处理原则）

| 场景编号 | 异常场景 | 触发方式 | 预期处理 | 对应规则 |
|----------|----------|----------|----------|----------|
| EX-01 | 单行必填字段缺失 | 导入文件含工号/姓名为空的行 | 跳过该行，记入failures:必填字段缺失 | R01 |
| EX-02 | 工号库内重复 | 导入文件含已存在工号 | 跳过该行，记入failures:工号已存在 | R02 |
| EX-03 | 日期格式非法 | 导入文件含hireDate格式非yyyy-MM-dd | 跳过该行，记入failures:字段格式错误 | R03 |
| EX-04 | 状态枚举非法 | 导入文件含status非0/1 | 跳过该行，记入failures:字段格式错误 | R04 |
| EX-05 | 文件行数超1000 | 导入文件1001行 | 整体拒绝400, 不进入逐行解析 | R05 |
| EX-06 | 文件大小超5MB | 导入文件>5MB | 整体拒绝413, 不进入逐行解析 | R06 |
| EX-07 | 文件格式不支持 | 导入.txt文件 | 整体拒绝400, 不进入逐行解析 | R07 |
| EX-08 | 全部行失败 | 所有行均校验失败 | 仍返回200+完整failures明细, 不抛5xx | — |
| EX-09 | 系统内部异常 | Mock DB异常 on 第N行 | 第N行记入failures:导入服务异常, 其他行继续处理, 已入库数据不回滚 | — |
| EX-10 | 文件内工号重复 | 文件内2行相同工号 | 第1行入库成功, 第2行记入failures:工号已存在 | — |

### 4.2 白名单鉴权异常场景

| 场景编号 | 异常场景 | 预期处理 |
|----------|----------|----------|
| WL-EX01 | 非白名单用户访问预算列表 | 403 + 无成本预算访问权限 |
| WL-EX02 | 非白名单用户新增预算 | 403 + 无成本预算访问权限 |
| WL-TC03 | 非白名单用户删除预算 | 403 + 无成本预算访问权限 |
| WL-EX04 | 非白名单用户导入预算 | 403 + 无成本预算访问权限 |
| WL-EX05 | 403响应不泄露数据存在性 | 响应体不含total/list/任何预算字段, 与"有数据"和"无数据"场景响应一致 |

### 4.3 并发场景

| 场景编号 | 并发场景 | 控制策略 | 预期结果 |
|----------|----------|----------|----------|
| CC-TC01 | 两个用户同时导入含相同工号的文件 | 唯一索引uk_biz_employee_no兜底 + 逐行try-catch | 一个成功一个失败（记入failures:工号已存在），不抛异常 |
| CC-TC02 | 并发新增同人员同月预算 | 唯一索引uk_biz_cost_budget_emp_month兜底 | 一个成功, 另一个返回BUDGET_003该月份预算已存在 |
| CC-TC03 | 并发编辑同一预算记录 | — | 最后写入胜出（无乐观锁需求, 按设计逐行独立处理） |

## 5. 测试覆盖矩阵

### 5.1 功能点-接口-用例覆盖

| 功能点 | 接口 | 正常用例数 | 边界用例数 | 异常用例数 | 总计 |
|--------|------|-----------|-----------|-----------|------|
| F01 员工列表 | W01 | 5 | 3 | 2 | 10 |
| F02 员工详情 | W02 | 2 | 1 | 2 | 5 |
| F03 员工新增 | W03 | 2 | 2 | 4 | 8 |
| F04 员工编辑 | W04 | 3 | 2 | 2 | 7 |
| F05 逻辑删除 | W05 | 2 | 1 | 2 | 5 |
| F06 批量导入 | W06 | 4 | 3 | 8 | 15 |
| F07 预算列表 | W07 | 4 | 1 | 2 | 7 |
| F08 预算新增 | W08 | 1 | 4 | 5 | 10 |
| F09 预算编辑 | W09 | 1 | 2 | 2 | 5 |
| F10 预算删除 | W10 | 2 | 0 | 2 | 4 |
| F11 预算导入 | W11 | 1 | 2 | 8 | 11 |
| F12-F14 白名单 | W12-W14 | 4 | 0 | 4 | 8 |
| 跨模块联动 | — | 2 | 0 | 2 | 4 |
| 异常场景 | — | 0 | 0 | 18 | 18 |
| 并发场景 | — | 0 | 0 | 3 | 3 |
| **合计** | — | **30** | **21** | **66** | **117** |

### 5.2 错误码覆盖

| 错误码 | 说明 | 覆盖用例 |
|--------|------|----------|
| EMPLOYEE_001 | 参数校验失败 | F01-TC08, F03-TC04~TC06, F04-TC05 |
| EMPLOYEE_002 | 员工不存在 | F02-TC03, F04-TC05, F05-TC04, F05-TC05 |
| EMPLOYEE_003 | 工号已存在 | F03-TC02, F03-TC03 |
| EMPLOYEE_004 | 工号不可修改 | F04-TC04 |
| EMPLOYEE_005 | 文件为空或格式不支持 | F06-TC09, F06-TC10 |
| EMPLOYEE_006 | 单次导入上限1000行 | F06-TC11 |
| EMPLOYEE_007 | 文件过大上限5MB | F06-TC12 |
| EMPLOYEE_008 | 导入服务异常 | F06-TC15 |
| BUDGET_001 | 参数校验失败 | F07-TC07, F08-TC08~TC10 |
| BUDGET_002 | 无成本预算访问权限 | F07-TC02, F07-TC06, F08-TC02, F09-TC02, F10-TC02, F11-TC02 |
| BUDGET_003 | 该月份预算已存在 | F08-TC03, F09-TC03, F11-TC04 |
| BUDGET_004 | 预算金额超出范围 | F08-TC04, F08-TC05, F09-TC04 |

## 6. Service 层单元测试覆盖（S01-S15）

| Service方法 | 测试重点 | Mock对象 | 用例数 |
|-------------|----------|----------|--------|
| S01 pageEmployees | 分页SQL拼接、is_deleted过滤、多条件动态查询 | EmployeeMapper | 4 |
| S02 getEmployeeById | 查询结果映射、白名单联动预算汇总 | WhitelistService, CostBudgetService | 3 |
| S03 createEmployee | 工号唯一校验（含已删除）、默认值填充 | EmployeeMapper | 4 |
| S04 updateEmployee | 工号不可改校验逻辑、字段更新 | EmployeeMapper | 3 |
| S05 deleteEmployee | 软删标记更新、不存在校验 | EmployeeMapper | 2 |
| S06 importEmployees | 文件校验→解析→逐行处理→failures收集、异常不回滚 | FileParser, EmployeeMapper | 6 |
| S07 pageBudgets | 白名单前置校验、分页查询 | WhitelistService, CostBudgetMapper | 3 |
| S08 createBudget | 白名单校验、金额范围校验、唯一索引兜底 | WhitelistService, CostBudgetMapper | 5 |
| S09 updateBudget | 白名单校验、唯一冲突检测、金额校验 | WhitelistService, CostBudgetMapper | 3 |
| S10 deleteBudget | 白名单校验、物理删除 | WhitelistService, CostBudgetMapper | 2 |
| S11 importBudgets | 文件校验→解析→工号转ID→逐行处理 | WhitelistService, FileParser, EmployeeMapper, CostBudgetMapper | 5 |
| S12 listWhitelist | 全量查询 | WhitelistMapper | 1 |
| S13 addWhitelist | 重复校验、新增 | WhitelistMapper | 2 |
| S14 removeWhitelist | 删除、不存在处理 | WhitelistMapper | 2 |
| S15 isWhitelisted | 鉴权核心逻辑 | WhitelistMapper | 2 |
| **合计** | | | **47** |

## 7. 测试数据文件清单

| 文件名 | 用途 | 对应用例 |
|--------|------|----------|
| employees_valid.xlsx | 3行有效员工数据 | F06-TC01 |
| employees_partial.xlsx | 1行重复+2行新 | F06-TC02 |
| employees_all_fail.xlsx | 2行均为已存在工号 | F06-TC03 |
| employees_no_eno.xlsx | 工号为空 | F06-TC04 |
| employees_no_name.xlsx | 姓名为空 | F06-TC05 |
| employees_bad_date.xlsx | 日期格式非法 | F06-TC06 |
| employees_bad_status.xlsx | 状态枚举非法 | F06-TC07 |
| employees_dup_eno.xlsx | 文件内工号重复 | F06-TC08 |
| employees_1001.xlsx | 1001行超限 | F06-TC11 |
| employees_large.xlsx | >5MB文件 | F06-TC12 |
| employees_mixed.xlsx | 4行混合失败原因 | F06-TC14 |
| employees_valid.csv | CSV格式有效数据 | F06-TC13 |
| budgets_valid.xlsx | 3行有效预算数据 | F11-TC01 |
| budgets_bad_eno.xlsx | 引用不存在工号 | F11-TC03 |
| budgets_dup.xlsx | 同人员同月重复 | F11-TC04 |
| budgets_neg.xlsx | 金额越界 | F11-TC05 |
| budgets_bad_month.xlsx | 月份格式非法 | F11-TC06 |
| budgets_all_fail.xlsx | 全部行失败 | F11-TC07 |
| empty.xlsx | 空文件 | F06-TC10 |
| test.txt | 非支持格式 | F06-TC09 |

## 8. 跨仓对齐点验证

| 对齐点 | 验证内容 | 验证方式 | 责任方 |
|--------|----------|----------|--------|
| 前后端REST契约一致 | 字段名（employeeNo/hireDate/status等驼峰命名）、分页参数（pageNo/pageSize）、导入响应结构（successCount/failureCount/failures） | API契约文档对比 + MockMvc响应断言 | library-backend（本测分） |
| 白名单鉴权 | 非白名单返回403且不泄露数据存在性 | WL-EX05场景验证403响应体无预算字段 | library-backend |
| 导入模板统一列定义 | 人员导入列：工号/姓名/部门/岗位/入职日期/手机/邮箱/状态；预算导入列：工号/月份/金额 | 测试数据文件列定义与契约一致 | library-backend |

## 9. 风险与待确认项

| 编号 | 风险/待确认 | 影响 | 建议 |
|------|------------|------|------|
| R01 | 技能 `/dtazziboot-generate-test-analysis-doc` 在技能注册表中不存在，本测分文档基于系分设计自主产出 | 测分文档格式可能与 dtazziboot 标准模板有偏差 | 建议补充注册该技能或提供标准模板对齐 |
| R02 | 实施计划文件 `docs/plans/2026-07-30-personnel-dashboard-implementation-plan.md` 在 worktree 中不存在 | 缺少实施计划输入，测分可能遗漏计划中明确的测试范围 | 建议上游补充实施计划文件 |
| R03 | 白名单 userId 取自外部标识字符串，测试中 Mock 注入 | 生产环境认证体系可能与 Mock 不一致 | 集成测试阶段需对接真实认证上下文 |
| R04 | 详情接口查询已逻辑删除员工的预算汇总行为（budgetSummary 返回 null 还是有值）设计中标注"按白名单逻辑" | F02-TC04/F02-TC05 预期值待实现确认 | 编码阶段明确已删除员工详情预算汇总行为 |
| R05 | 租户隔离（tenant_id）测试依赖 Mock 上下文注入 | 生产多租户场景需额外验证 | 建议补充多租户集成测试 |
| R06 | 文件解析采用 EasyExcel，CSV 自研解析 | CSV 解析边界（编码/分隔符/引号转义）需额外测试 | 补充 CSV 解析边界用例 |
