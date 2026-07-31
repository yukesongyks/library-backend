# Spec: 算法接口

## Requirement: HelloWorld 接口

### Scenario: 调用 HelloWorld 接口返回结果
- **Given** 后端服务已启动，`/api/algorithms/helloworld` 端点可用
- **When** 前端发送 `GET /api/algorithms/helloworld` 请求，header 携带 `X-User-Id: 1`
- **Then** 响应 HTTP 200，响应体为 `{"result": "Hello, World!"}`
- **And** call_logs 表新增一条记录，apiType=`helloworld`，callerUserId=`1`，responseStatus=`SUCCESS`

### Scenario: 未携带 X-User-Id header
- **Given** 后端服务已启动
- **When** 前端发送 `GET /api/algorithms/helloworld` 请求，未携带 `X-User-Id` header
- **Then** 响应 HTTP 200，响应体为 `{"result": "Hello, World!"}`
- **And** call_logs 新增记录，callerUserId 为 null，人员维度字段为 null（不影响接口正常响应）

## Requirement: 哈希算法接口

### Scenario: 使用 SHA-256 计算哈希
- **Given** 后端服务已启动
- **When** 前端发送 `POST /api/algorithms/hash`，请求体 `{"text": "abc", "algorithm": "SHA-256"}`
- **Then** 响应 HTTP 200，响应体 `{"result": "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", "algorithm": "SHA-256"}`

### Scenario: 使用 MD5 计算哈希
- **Given** 后端服务已启动
- **When** 前端发送 `POST /api/algorithms/hash`，请求体 `{"text": "abc", "algorithm": "MD5"}`
- **Then** 响应 HTTP 200，响应体 `{"result": "900150983cd24fb0d6963f7d28e17f72", "algorithm": "MD5"}`

### Scenario: 不支持的哈希算法
- **Given** 后端服务已启动
- **When** 前端发送 `POST /api/algorithms/hash`，请求体 `{"text": "abc", "algorithm": "UNKNOWN"}`
- **Then** 响应 HTTP 400，响应体 `{"error": "Unsupported algorithm: UNKNOWN"}`

### Scenario: 空文本输入
- **Given** 后端服务已启动
- **When** 前端发送 `POST /api/algorithms/hash`，请求体 `{"text": "", "algorithm": "SHA-256"}`
- **Then** 响应 HTTP 400，响应体 `{"error": "text must not be empty"}`

## Requirement: 冒泡排序接口

### Scenario: 对无序数组排序
- **Given** 后端服务已启动
- **When** 前端发送 `POST /api/algorithms/bubble-sort`，请求体 `{"numbers": [5, 3, 8, 1, 9]}`
- **Then** 响应 HTTP 200，响应体 `{"result": [1, 3, 5, 8, 9], "input": [5, 3, 8, 1, 9]}`

### Scenario: 空数组输入
- **Given** 后端服务已启动
- **When** 前端发送 `POST /api/algorithms/bubble-sort`，请求体 `{"numbers": []}`
- **Then** 响应 HTTP 200，响应体 `{"result": [], "input": []}`

### Scenario: 已排序数组输入
- **Given** 后端服务已启动
- **When** 前端发送 `POST /api/algorithms/bubble-sort`，请求体 `{"numbers": [1, 2, 3]}`
- **Then** 响应 HTTP 200，响应体 `{"result": [1, 2, 3], "input": [1, 2, 3]}`
