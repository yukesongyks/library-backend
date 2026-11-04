# Code Review Report

> **Change** `bubble-sort` · **分支/Commit** `AI/task-DEV-0db410f7…b5db25` / `3269761` · **日期** `2026-11-04` · **审查者** AI
>
> 结论：**修复后合并**（无 P0，2 × P1 建议本轮修复）。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `6`（主 4 / 测试 2） |
| 变更行数 | `+393 / -24`（含 `pom.xml`、`.agents/system.changes/code.md`；非 Java 跳过） |

| 类/接口 | 路径 | 角色（可选） |
|---------|------|--------------|
| `BubbleSortController` | `src/main/java/com/library/backend/controller/BubbleSortController.java` | REST 入口 `POST /api/bubble-sort` |
| `BubbleSortService` | `src/main/java/com/library/backend/service/BubbleSortService.java` | 冒泡排序算法 |
| `ApiRequest` | `src/main/java/com/library/backend/dto/ApiRequest.java` | 请求 DTO（`array: List<Integer>`） |
| `ExportRequest` | `src/main/java/com/library/backend/dto/ExportRequest.java` | 请求 DTO（`jakarta` 包名修复） |
| `BubbleSortControllerTest` | `src/test/java/com/library/backend/controller/BubbleSortControllerTest.java` | 接口层单测（4 例） |
| `BubbleSortServiceTest` | `src/test/java/com/library/backend/service/BubbleSortServiceTest.java` | 算法单测（8 例） |

**验证手段与边界**：`scan-all-rules.sh`（52/222 规则，No findings）；行宽/Tab/行尾空白字符级扫描（0 命中）；`rg "@Valid|@Validated"`（0 命中）。**本环境无 `java`/`mvn`**（`command -v java mvn` 均失败），故本次审查**未执行编译与单测**；运行时结论引用 `.agents/system.changes/code.md` L186-192 记录的 `mvn test`（12 tests, 0 failures）与接口实测，此处不重复声称为本次验证。

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 2 | 3 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: `POST /api/bubble-sort` 入参与排序

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given `{"array":[5,3,8,1,2]}` When 调用 Then 返回 200 且 `sorted` 升序 | ✅ | design.md §4.3 路径与请求体 | `BubbleSortController.java:41-57`；`BubbleSortControllerTest.java:28-44` | 契约一致 |

### REQ-2: 响应结构 `{code,message,data:{original,sorted,timestamp}}`

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| When 排序成功 Then 三个 data 字段齐备 | ✅ | §4.3 响应体示例 | `BubbleSortController.java:51-56` | 字段名与顺序（`LinkedHashMap`）一致 |
| Then `timestamp` 形如 `2026-08-06T10:00:00Z` | ⚠️ | §4.3 示例为秒级 `Z` 结尾 | `BubbleSortController.java:54` | `Instant.now().toString()` 产出 `2026-11-04T01:47:57.940123Z`（含小数秒）；ISO-8601 合法，仅格式精度与示例不同 → **P2** |

### REQ-3: 冒泡排序正确性与入参不可变

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 无序/逆序/负数+重复/已升序/单元素/空数组 → 升序 | ✅ | 需求「实现一个冒泡排序」 | `BubbleSortService.java:25-50`；`BubbleSortServiceTest.java:21-97` | 标准双循环 + 无交换提前退出 |
| 入参 `int[]` 不被修改 | ✅ | Javadoc 承诺 | `BubbleSortService.java:30`（`Arrays.copyOf`）；`BubbleSortServiceTest.java:99-111` | 拷贝后排序 |

### REQ-4: 参数异常处理

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| `array` 缺失 → 400 | ⚠️ | code.md L166-170 记录「array 缺失 → 业务码 400」 | `BubbleSortController.java:44-46` | body `code=400` 但 **HTTP 200**，与下一行不一致 → **P1 G10.1** |
| 元素 `null` → 400 | ✅ | 同上 | `BubbleSortController.java:70-72` → `GlobalExceptionHandler.java:14-19` | HTTP 400 + body 400 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ✅ | A1–A7 全部通过：无 Tab/超宽行/import 通配；命名、K&R 大括号、Javadoc（`@param→@return→@throws`）均符合；类与方法注释完整（`BubbleSortService.java:7-24`、`BubbleSortController.java:18-40`） |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P1 / P2 | `G10.1`（P1）参数错误语义不一致；`G11.2`（P2）无数组长度上限 + O(n²)；其余 G 无命中 |
| 安全 | `security-checklist.md` S1–S10 | ✅ | — | 无 SQL/命令/文件/XML/SSRF；JSON 输出无 XSS；无硬编码密钥；`S8.1/S10.2` 属项目既有全局设计（见 §7 范围说明） |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ✅ | — | 预扫 `No findings (52/222)`；LLM 补扫 120 条无命中（详见 checklist §4.1） |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「未启用自定义规则」） |
|----|------|------|------|------------------------------------------|
| 自定义扩展 | `customized-checklist.md` U* | ⚠️ | P1 | 项目已加 `spring-boot-starter-validation` 并按 U1.1 核销：`U1.1` 命中 → 校验注解未启用（见 §7.1）；U2 未配置 → `N/A(未启用业务红线规则)` |

---

## 7. 结论

- **合并建议**：**修复后合并**（P0 = 0；P1 两项建议本轮修复，均为低风险小改动）
- **P0**：无
- **P1**：
  1. `U1.1` — `@Valid` 缺失导致 `ApiRequest` 校验注解全部失效，且注解的"必填"语义与系分 §4.3 请求体（仅 `array`）冲突。
  2. `G10.1` — 同类参数错误返回两套状态：`array` 缺失 = HTTP 200 + body 400；元素 `null` = HTTP 400。
- **P2**：
  1. `G11.2` — 请求数组无长度上限，配合 O(n²) 算法可被单请求长时间占用 CPU。
  2. `G11.1` — `BubbleSortControllerTest` 直接 `new` 控制器，未经 HTTP/Jackson 层，JSON 反序列化契约未覆盖。
  3. `REQ-2` — `timestamp` 含小数秒，与系分示例秒级格式不一致（前端若做字符串匹配需注意）。
- **一句话**：算法与契约主体正确、测试边界覆盖良好；主要风险集中在接口层参数语义与校验启用方式，属可快速收敛的收口项。

**范围说明（不计入问题计数）**：全仓无鉴权框架、`CorsConfig` 允许配置化 origin，属项目既有全局设计（Demo 定位），非本次变更新增；对外发布前另行统一治理。

---

## 7.1 问题片段（必填）

- **P1** `U1.1` `src/main/java/com/library/backend/dto/ApiRequest.java:25-31` — DTO 声明了必填/枚举约束，但控制器未启用校验，注解形同注释；同时与系分 §4.3「请求体仅 `array`」的契约冲突（若加 `@Valid`，仅传 `array` 的合法请求将直接 400）。
  片段范围：`src/main/java/com/library/backend/dto/ApiRequest.java:24-31`

```java
L24|    /** 用户ID */
L25|    @NotBlank(message = "userId不能为空")
L26|    private String userId;
L27|
L28|    /** 人员类型：EMPLOYEE | CONTRACTOR | INTERN */
L29|    @NotBlank(message = "userType不能为空")
L30|    @Pattern(regexp = "^(EMPLOYEE|CONTRACTOR|INTERN)$", message = "userType必须为EMPLOYEE、CONTRACTOR或INTERN")
L31|    private String userType;
```

  对应控制器未启用校验：`src/main/java/com/library/backend/controller/BubbleSortController.java:42`

```java
L41|    @PostMapping("/bubble-sort")
L42|    public ApiResponse<Map<String, Object>> bubbleSort(@RequestBody ApiRequest request) { // 缺 @Valid
L43|        List<Integer> values = request.getArray();
```

- **P1** `G10.1` `src/main/java/com/library/backend/controller/BubbleSortController.java:44-46` — `array` 缺失走 `return ApiResponse.error(400)`（HTTP 200），而元素 `null` 抛异常走 `GlobalExceptionHandler`（HTTP 400）；前端无法统一按 HTTP 状态判断失败。
  片段范围：`src/main/java/com/library/backend/controller/BubbleSortController.java:41-49`

```java
L41|    @PostMapping("/bubble-sort")
L42|    public ApiResponse<Map<String, Object>> bubbleSort(@RequestBody ApiRequest request) {
L43|        List<Integer> values = request.getArray();
L44|        if (values == null) {
L45|            return ApiResponse.error(BAD_REQUEST_CODE, "array不能为空"); // HTTP 200 + body 400
L46|        }
L47|        int[] array = toIntArray(values);
L48|
L49|        int[] sorted = bubbleSortService.sort(array);
```

  对照异常路径：`src/main/java/com/library/backend/config/GlobalExceptionHandler.java:14-19`

```java
L14|    @ExceptionHandler(IllegalArgumentException.class)
L15|    @ResponseStatus(HttpStatus.BAD_REQUEST)
L16|    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e) {
L17|        log.warn("Bad request: {}", e.getMessage());
L18|        return ApiResponse.error(400, e.getMessage()); // HTTP 400 + body 400
L19|    }
```

- **P2** `G11.2` `src/main/java/com/library/backend/controller/BubbleSortController.java:43-49` — 请求数组长度无上限，算法为 O(n²)，10⁵ 级元素即产生约 10¹⁰ 次比较，单请求可长时间占用 Web 线程。
  片段范围：`src/main/java/com/library/backend/service/BubbleSortService.java:31-47`

```java
L31|        int length = result.length;
L32|
L33|        for (int i = 0; i < length - 1; i++) {
L34|            boolean swapped = false;
L35|            for (int j = 0; j < length - i - 1; j++) {   // O(n^2)，无规模上限
L36|                if (result[j] > result[j + 1]) {
L37|                    int temp = result[j];
L38|                    result[j] = result[j + 1];
L39|                    result[j + 1] = temp;
L40|                    swapped = true;
L41|                }
L42|            }
L43|            // 本轮无交换说明已是升序，提前结束
L44|            if (!swapped) {
L45|                break;
L46|            }
L47|        }
```

- **P2** `G11.1` `src/test/java/com/library/backend/controller/BubbleSortControllerTest.java:24` — 直接实例化控制器，绕过 `@RequestBody` 反序列化与状态码，`array` 为 JSON 对象/字符串等非法形态的行为未被覆盖。
  片段范围：`src/test/java/com/library/backend/controller/BubbleSortControllerTest.java:22-44`

```java
L22|class BubbleSortControllerTest {
L23|
L24|    private final BubbleSortController controller = new BubbleSortController(new BubbleSortService());
L25|
L26|    // ==================== bubbleSort 测试 ====================
L27|
L28|    @Test
L29|    @DisplayName("正常数组应返回原始值与升序结果")
L30|    void should_returnOriginalAndSorted_when_arrayValid() {
```

- **P2** `REQ-2` `src/main/java/com/library/backend/controller/BubbleSortController.java:54` — `timestamp` 输出含小数秒，与系分示例 `2026-08-06T10:00:00Z` 的秒级精度不一致。
  片段范围：`src/main/java/com/library/backend/controller/BubbleSortController.java:51-56`

```java
L51|        Map<String, Object> result = new LinkedHashMap<>();
L52|        result.put("original", values);
L53|        result.put("sorted", Arrays.stream(sorted).boxed().toList());
L54|        result.put("timestamp", Instant.now().toString()); // 2026-11-04T01:47:57.940123Z
L55|
L56|        return ApiResponse.success(result);
```

---

## 8. 修复任务列表

### P0

- 无待修复项。

### P1

- [ ] **P1** `U1.1` — 统一校验启用方式：对 `bubbleSort` 参数加 `@Valid` 并同步从 `ApiRequest` 拆分/收敛必填约束（`userId`/`userType` 仅在埋点接口必填），保证 §4.3 仅传 `array` 的请求仍返回 200；若不启用校验，则移除 `ApiRequest` 上不生效的 `@NotBlank`/`@Pattern` 并注释说明。
- [ ] **P1** `G10.1` — `BubbleSortController.java:44-46` 改为抛 `IllegalArgumentException`（或返回 `ResponseEntity` 显式 400），使 `array` 缺失与元素 `null` 的 HTTP 状态、body `code` 完全一致。

### P2（可选）

- [ ] **P2** `G11.2` — `BubbleSortController.java:43-49` 增加数组长度上限校验（如 `array 长度 ≤ 10000`，超限返回 400），避免 O(n²) 放大为 CPU 耗尽。
- [ ] **P2** `G11.1` — `BubbleSortControllerTest.java:24` 增加基于 `MockMvc` 的用例，覆盖真实 JSON 反序列化与 HTTP 状态码。
- [ ] **P2** `REQ-2` — `BubbleSortController.java:54` 若需与系分示例严格对齐，改用 `DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC)` 格式化。
