# Spec: 导出接口

## Requirement: 按 API 类型导出调用记录

### Scenario: 导出 helloworld 类型的调用记录
- **Given** 后端服务已启动，call_logs 表中存在若干 apiType=`helloworld` 的记录
- **When** 前端发送 `GET /api/algorithms/export?type=helloworld` 请求
- **Then** 响应 HTTP 200，`Content-Type: text/csv`，`Content-Disposition: attachment; filename="helloworld-export.csv"`
- **And** CSV 首行为表头：`id,apiType,callerUsername,personnelType,personnelLevel,department,calledAt,requestSummary,responseData`
- **And** 后续行仅包含 apiType=`helloworld` 的记录

### Scenario: 导出 hash 类型的调用记录
- **Given** 后端服务已启动，call_logs 表中存在若干 apiType=`hash` 的记录
- **When** 前端发送 `GET /api/algorithms/export?type=hash` 请求
- **Then** 响应 HTTP 200，CSV 内容仅包含 apiType=`hash` 的记录

### Scenario: 导出 bubble-sort 类型的调用记录
- **Given** 后端服务已启动，call_logs 表中存在若干 apiType=`bubble-sort` 的记录
- **When** 前端发送 `GET /api/algorithms/export?type=bubble-sort` 请求
- **Then** 响应 HTTP 200，CSV 内容仅包含 apiType=`bubble-sort` 的记录

### Scenario: 无效的导出类型
- **Given** 后端服务已启动
- **When** 前端发送 `GET /api/algorithms/export?type=unknown` 请求
- **Then** 响应 HTTP 400，响应体 `{"error": "Invalid export type: unknown. Supported: helloworld, hash, bubble-sort"}`

### Scenario: 导出类型为空
- **Given** 后端服务已启动
- **When** 前端发送 `GET /api/algorithms/export` 请求（未带 type 参数）
- **Then** 响应 HTTP 400，响应体 `{"error": "type parameter is required"}`

### Scenario: 导出空数据
- **Given** 后端服务已启动，call_logs 表中无指定 type 的记录
- **When** 前端发送 `GET /api/algorithms/export?type=helloworld` 请求
- **Then** 响应 HTTP 200，CSV 仅含表头行，无数据行
