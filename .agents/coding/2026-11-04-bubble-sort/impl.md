# 冒泡排序实现 - 编码报告

- 任务：实现一个冒泡排序
- 技能：dtazziboot-java-coding-standards
- 日期：2026-11-04

## 模块进度追踪

| 序号 | 模块 | READ | TEST | IMPL | CHECK | DOCS | 状态 |
|:----:|------|:----:|:----:|:----:|:-----:|:----:|------|
| 1 | common / BubbleSort | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |

## READ: common

**模块职责**：跨模块通用基础能力（统一响应、异常、常量、工具类）。

**关键类**：

- `Result<T>` / `PageResult<T>` - 统一响应与分页结构
- `ResultCode` / `BusinessException` / `GlobalExceptionHandler` - 错误码、业务异常、全局异常处理
- `BusinessConstants` - 业务常量
- `BubbleSort`（新增）- 排序工具类

**依赖关系**：纯 JDK 实现，无外部依赖；异常语义与 `GlobalExceptionHandler` 对 `IllegalArgumentException` 的处理保持一致。

**已加载规范**：

- [x] naming.md
- [x] comments.md
- [x] formatting.md
- [x] unit-testing.md

## TEST: common / BubbleSort

**测试文件**：`src/test/java/com/library/common/util/BubbleSortTest.java`（JUnit 5 + AssertJ，package 与被测类一致）

| 方法 | 测试场景 | 状态 |
|------|----------|:----:|
| should_returnAscendingArray_when_inputIsUnordered | 乱序正常路径 | ✅ |
| should_returnAscendingArray_when_inputIsDescending | 完全逆序 | ✅ |
| should_keepOriginalOrder_when_inputIsAlreadyAscending | 已有序（提前退出分支） | ✅ |
| should_returnAscendingArray_when_inputHasDuplicatesAndNegatives | 重复值 + 负数 | ✅ |
| should_returnEmptyArray_when_inputIsEmpty | 空数组边界 | ✅ |
| should_returnSameElement_when_inputHasSingleElement | 单元素边界 | ✅ |
| should_sortInPlace_when_inputArrayProvided | 原地排序与返回引用 | ✅ |
| should_throwIllegalArgumentException_when_inputIsNull | 异常路径 | ✅ |
| should_returnAscendingArray_when_inputMatchesBoundaryCases | 参数化边界值（0、Integer.MIN/MAX、全等值） | ✅ |

**测试覆盖摘要**：

- 被测类：`BubbleSort`
- 测试方法数：9（含参数化用例，合计执行 11 个用例）
- 覆盖场景：正常路径 ✓、边界值 ✓、异常处理 ✓、分支覆盖 ✓

## IMPL: common / BubbleSort

**已实现文件**：

- `src/main/java/com/library/common/util/BubbleSort.java`

**实现要点**：

- `final` 工具类 + 私有构造，禁止实例化
- `sort(int[] source)` 原地升序排序，返回入参同一引用
- 外层循环收缩未排序区间右边界；内层相邻比较交换
- “本轮无交换”提前退出，已有序输入为 O(n)
- `null` 入参抛出 `IllegalArgumentException`，由全局异常处理器转为参数错误响应
- 时间复杂度 O(n²)、空间复杂度 O(1)

**编译验证**：✅ `mvn test -Dtest=BubbleSortTest` 编译通过

## CHECK: common / BubbleSort

### L1 静态检查

| 检查项 | 规范要求 | 符合情况 |
|--------|----------|:--------:|
| 命名规范 | 类名大驼峰、方法名小驼峰、常量全大写 | ✅ `BubbleSort` / `sort` / `swapped`、`DEFAULT_*` 无新增魔法常量 |
| 注释规范 | 类/方法 javadoc，含作者与日期；内部单行注释 `// ` | ✅ |
| 格式规范 | 4 空格缩进、运算符两侧空格、单行 ≤120 字符、方法 ≤80 行 | ✅ |
| 异常规范 | 参数非法抛 `IllegalArgumentException`，与全局异常处理一致 | ✅ |
| 安全规范 | 无 SQL、无外部输入拼接，纯内存计算 | ✅ |
| 单元测试 | `{类名}Test`、同 package、AAA 结构、断言具体值 | ✅ 9 个测试方法 |
| 集合/控制流 | 无 Stream 滥用、循环边界清晰、无嵌套过深 | ✅ |

### L2 动态验证

| 验证项 | 状态 | 说明 |
|--------|:----:|------|
| 编译验证 | ✅ | 便携 JDK 17.0.21 + Maven 3.9.9，`mvn test` 完成主源码与测试源码编译 |
| 单测验证 | ✅ | `Tests run: 11, Failures: 0, Errors: 0, Skipped: 0`，`BUILD SUCCESS` |
| 独立交叉验证 | ✅ | 独立程序对比 `Arrays.sort` 的 6 组输入（含 `Integer.MIN_VALUE`/`MAX_VALUE`、空数组、单元素）全部一致 |

### 📋 待人工验证

```bash
mvn compile -DskipTests
mvn test -Dtest=BubbleSortTest
```

**发现问题**：无

## DOCS: common / BubbleSort

**文档操作**：

- 架构文档：未涉及新模块，无需更新（仓库当前无 `docs/ARCHITECTURE.md`）
- 模块文档：新建 `docs/modules/common/README.md`（模块职责、关键类、BubbleSort 接口与复杂度、依赖、测试说明）
- 编码报告：`.agents/coding/2026-11-04-bubble-sort/impl.md`

## 变更清单

| 文件 | 类型 | 说明 |
|------|:----:|------|
| `src/main/java/com/library/common/util/BubbleSort.java` | 新增 | 冒泡排序工具类 |
| `src/test/java/com/library/common/util/BubbleSortTest.java` | 新增 | 单元测试（JUnit 5 + AssertJ） |
| `docs/modules/common/README.md` | 新增 | common 模块文档 |
| `.agents/coding/2026-11-04-bubble-sort/impl.md` | 新增 | 本编码报告 |

## 环境说明

仓库运行环境未预装 JDK / Maven，且沙箱内 `apt-get` 因 setgroups 权限失败；本次验证使用下载到 `/tmp/tools` 的便携 JDK 17.0.21 与 Maven 3.9.9 完成，未修改仓库构建配置，`target/` 构建产物已清理。
