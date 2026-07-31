# Spec: hello-world-features

> 需求可测试化要求：所有场景以 Given/When/Then 描述，避免"快/好/稳定"等模糊词。

## Feature: 三接口执行

### Requirement: helloworld 接口
- `GET /api/hello-world` 返回固定字符串 `Hello, World!`。
- 响应 JSON：`{ "result": "Hello, World!" }`。

**Scenario: 正常调用 helloworld**
- Given 后端已启动。
- When 调用 `GET /api/hello-world`，请求头带 `X-User-Id: 1`。
- Then 响应 200，`result` 等于 `Hello, World!`。
- And `api_call_log` 新增一条记录，`api_name=helloworld`。

### Requirement: 哈希算法接口
- `POST /api/hash`，请求体 `{ "algorithm": "SHA-256", "input": "<text>" }`。
- `algorithm` 支持 `SHA-256`、`SHA-512`、`MD5`；不支持的算法返回 400。
- 响应 JSON：`{ "algorithm": "...", "input": "...", "hash": "<hex>" }`。

**Scenario: SHA-256 哈希**
- Given 输入 `abc`。
- When `POST /api/hash`，body `{ "algorithm": "SHA-256", "input": "abc" }`。
- Then 响应 200，`hash` 等于 `ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad`。

**Scenario: 不支持的算法**
- Given 算法 `FOO`。
- When `POST /api/hash`。
- Then 响应 400，错误信息指明支持的算法列表。

### Requirement: 冒泡排序接口
- `POST /api/bubble-sort`，请求体 `{ "numbers": [3,1,2] }`。
- 输入仅接受整数数组；空数组合法（返回空数组）；非整数或缺失返回 400。
- 响应 JSON：`{ "input": [3,1,2], "sorted": [1,2,3], "steps": <交换步数整数> }`。

**Scenario: 正常排序**
- Given 输入 `[3,1,2]`。
- When `POST /api/bubble-sort`。
- Then 响应 200，`sorted` 等于 `[1,2,3]`，`steps` 大于 0。

**Scenario: 空数组**
- Given 输入 `[]`。
- When `POST /api/bubble-sort`。
- Then 响应 200，`sorted` 等于 `[]`，`steps` 等于 0。

## Feature: 导出

### Requirement: 导出接口
- `GET /api/export?tab=<helloworld|hash|bubble-sort>&format=<csv|json>`。
- 导出对应 Tab 当前展示结果的样本数据（见 design.md「导出数据源」）。
- 响应：`format=csv` 返回 `text/csv` 文件下载；`format=json` 返回 `application/json`。
- 不支持的 `tab` 或 `format` 返回 400。

**Scenario: 导出 helloworld CSV**
- When `GET /api/export?tab=helloworld&format=csv`。
- Then 响应 200，`Content-Type` 为 `text/csv`，首行为表头。

## Feature: 调用埋点

### Requirement: 埋点落库
- 三接口每次成功调用均写一条 `api_call_log`：`api_name`、`user_id`、`username`、`user_type`、`user_level`、`department`、`called_at`、`status`、`duration_ms`。
- 调用人通过请求头 `X-User-Id` 传入；未传时 `user_id` 为 null 且维度为 `unknown`（埋点仍写入）。

**Scenario: 带用户调用被埋点**
- Given `User(id=1, userType=内部, userLevel=P6, department=技术部)` 已存在。
- When 带 `X-User-Id: 1` 调用 `GET /api/hello-world`。
- Then `api_call_log` 新增记录，`user_type=内部, user_level=P6, department=技术部`。

**Scenario: 匿名调用被埋点**
- When 不带 `X-User-Id` 调用 `GET /api/hello-world`。
- Then `api_call_log` 新增记录，`user_id=null, user_type=unknown`。

## Feature: 可视化报表

### Requirement: 报表数据接口
- `GET /api/metrics/summary?dimension=<userType|userLevel|department|apiName>&chartType=<line|pie|bar>`。
- 折线图（line）：按日聚合调用次数，返回 `[{ "label": "2026-07-31", "value": 12 }, ...]`。
- 饼图（pie）：按维度取值聚合总次数，返回 `[{ "label": "内部", "value": 8 }, ...]`。
- 柱状图（bar）：按维度取值聚合总次数，返回 `[{ "label": "技术部", "value": 5 }, ...]`。
- 不支持的 `dimension` 或 `chartType` 返回 400。

**Scenario: 饼图按部门**
- Given 已有若干埋点记录。
- When `GET /api/metrics/summary?dimension=department&chartType=pie`。
- Then 响应 200，返回数组每项含 `label` 与 `value`，`value` 之和等于总调用次数。

### Requirement: 前端三 Tab + 报表
- 页面含三个 Tab：HelloWorld / 哈希 / 冒泡排序，各 Tab 可触发对应接口并渲染结果。
- 每个 Tab 顶部有导出按钮，导出当前 Tab 结果。
- 页面底部报表区：含折线图、饼图、柱状图各一，可切换维度。

**Scenario: 切换 Tab 执行**
- When 用户点击「哈希」Tab，输入 `abc` 并点执行。
- Then 页面展示 SHA-256 哈希结果。
