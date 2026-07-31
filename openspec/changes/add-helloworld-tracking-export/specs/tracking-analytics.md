# Spec: 埋点与分析接口

## Requirement: 调用埋点自动记录

### Scenario: 算法接口调用自动写入埋点
- **Given** 后端服务已启动，User 表存在 id=1 的用户（personnelType=开发, personnelLevel=P6, department=技术部）
- **When** 前端发送 `GET /api/algorithms/helloworld` 请求，header 携带 `X-User-Id: 1`
- **Then** 接口正常返回结果
- **And** call_logs 表新增一条记录，apiType=`helloworld`，callerUserId=`1`，callerUsername 对应 User.username
- **And** 该记录的 personnelType=`开发`，personnelLevel=`P6`，department=`技术部`
- **And** responseStatus=`SUCCESS`，calledAt 为当前时间

### Scenario: 未关联用户的调用埋点
- **Given** 后端服务已启动
- **When** 前端发送 `GET /api/algorithms/helloworld` 请求，header 携带 `X-User-Id: 999`（不存在）
- **Then** 接口正常返回结果
- **And** call_logs 表新增记录，callerUserId=`999`，人员维度字段为 null

### Scenario: 接口异常时埋点记录错误状态
- **Given** 后端服务已启动
- **When** 前端发送 `POST /api/algorithms/hash` 请求，请求体 `{"text": "abc", "algorithm": "UNKNOWN"}`
- **Then** 响应 HTTP 400
- **And** call_logs 表新增记录，responseStatus=`ERROR`，requestSummary 记录请求参数

## Requirement: 按维度聚合分析

### Scenario: 按人员类型柱状图查询
- **Given** call_logs 表存在多条记录，涉及 personnelType 为 开发/测试/产品
- **When** 前端发送 `GET /api/analytics/calls?dimension=personnelType&chartType=bar&startTime=2026-01-01T00:00:00&endTime=2026-12-31T23:59:59`
- **Then** 响应 HTTP 200，响应体 `{"chartType": "bar", "dimension": "personnelType", "data": [{"label": "开发", "value": <count>}, {"label": "测试", "value": <count>}, {"label": "产品", "value": <count>}]}`
- **And** 仅返回 startTime~endTime 范围内的记录聚合

### Scenario: 按人员层级饼图查询
- **Given** call_logs 表存在多条记录，涉及 personnelLevel 为 P5/P6/P7
- **When** 前端发送 `GET /api/analytics/calls?dimension=personnelLevel&chartType=pie`
- **Then** 响应 HTTP 200，响应体 `{"chartType": "pie", "dimension": "personnelLevel", "data": [{"label": "P5", "value": <count>}, ...]}`
- **And** data 各 value 之和等于查询范围内总调用次数

### Scenario: 按部门折线图查询
- **Given** call_logs 表存在多条记录，跨多天，涉及 department 为 技术部/产品部
- **When** 前端发送 `GET /api/analytics/calls?dimension=department&chartType=line&startTime=...&endTime=...`
- **Then** 响应 HTTP 200，响应体 `{"chartType": "line", "dimension": "department", "data": [{"date": "2026-07-01", "values": [{"label": "技术部", "value": <count>}, {"label": "产品部", "value": <count>}]}, ...]}`
- **And** 按天分组，每天含各部门的调用次数

### Scenario: 无数据时返回空数组
- **Given** call_logs 表在查询时间范围内无记录
- **When** 前端发送 `GET /api/analytics/calls?dimension=department&chartType=bar`
- **Then** 响应 HTTP 200，响应体 `{"chartType": "bar", "dimension": "department", "data": []}`

### Scenario: 无效维度参数
- **Given** 后端服务已启动
- **When** 前端发送 `GET /api/analytics/calls?dimension=invalid&chartType=bar`
- **Then** 响应 HTTP 400，响应体 `{"error": "Invalid dimension: invalid. Supported: personnelType, personnelLevel, department"}`

### Scenario: 无效图表类型参数
- **Given** 后端服务已启动
- **When** 前端发送 `GET /api/analytics/calls?dimension=department&chartType=invalid`
- **Then** 响应 HTTP 400，响应体 `{"error": "Invalid chartType: invalid. Supported: line, pie, bar"}`
