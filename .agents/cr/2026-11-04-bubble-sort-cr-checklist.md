# Code Review Checklist

> **Change** `Coding Implementation — 冒泡排序（BubbleSort）` · **分支/Commit** `AI/task-DEV-0db410f7-d735-11f1-b57a-6bc445387e6b-446bfc8a-4a61-4c3c-8dc6-d0b509ca1561` / `041c358` · **日期** `2026-11-04`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：已先在被审仓库根目录对变更路径运行 `references/script/scan-all-rules.sh`，输出见下方「预扫结果」；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

---

## 预扫结果（scan-all-rules.sh）

命令：`bash <skill>/references/script/scan-all-rules.sh src/main/java/com/library/common/util src/test/java/com/library/common/util`

```
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: src/main/java/com/library/common/util src/test/java/com/library/common/util
Engine:  ripgrep


=== No findings. 52/222 rules scanned ===
```

补充可程序化检查（LLM 复核项）：

```
行宽 > 120 字符：BubbleSort.java 无；BubbleSortTest.java 无
Tab 缩进：无
行尾空白：无
```

补充动态验证（本次 CR 独立执行，非编码阶段结论复用）：

```
mvn -o test -Dtest=BubbleSortTest
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
独立对拍：2000 组随机 int[]（长度 0–29，取值 -100–100）与 Arrays.sort 结果一致，且返回值 == 入参引用
```

---

## Step 1 — 执行队列（产物 A）

> **Step4 列语义**：每个 **Sn / Gn** 表示「**本文件**在 Step4 审查中，对 `reliability-checklist.md` 第 **G*n*** 节、`security-checklist.md` 第 **S*n*** 节的扫描结论」。**Bug 模式（B/M/I）** 不在本表分列，在下方 **§4.1** 按清单 ID 核销。与变更无关填 `N/A`；已扫无命中填 `✅`；命中风险填 `⚠️` 或 `❌`。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|----|----|----|----|----|----|----|----|----|-----|--------|
| 1 | `src/main/java/com/library/common/util/BubbleSort.java` | REQ-1/2/3 排序实现 | ✅ | ✅ | N/A(无并发) | N/A(无写接口) | N/A(无事务) | N/A(无SQL) | N/A(无MQ) | N/A(无缓存) | N/A(无调度) | ✅ | N/A(无外部调用) | ✅ | ✅ | N/A(无资金) | N/A(无日志) | N/A(无金额/租户/时区) | N/A(无DB变更) | ✅ | N/A(纯工具无开关) | N/A(无SQL) | N/A(无HTML输出) | N/A(无外部请求) | N/A(无命令) | N/A(无XML) | N/A(无反序列化) | N/A(无文件) | N/A(非接口) | N/A(无密钥) | N/A(无CORS/跳转) | ✅ 已审 |
| 2 | `src/test/java/com/library/common/util/BubbleSortTest.java` | REQ-4/5 单元测试 | ✅ | ✅ | N/A(无并发) | N/A(无写接口) | N/A(无事务) | N/A(无SQL) | N/A(无MQ) | N/A(无缓存) | N/A(无调度) | ✅ | N/A(无外部调用) | ✅ | ✅ | N/A(无资金) | N/A(无日志) | N/A(无金额/租户/时区) | N/A(无DB变更) | ✅ | N/A(纯工具无开关) | N/A(无SQL) | N/A(无HTML输出) | N/A(无外部请求) | N/A(无命令) | N/A(无XML) | N/A(无反序列化) | N/A(无文件) | N/A(非接口) | N/A(无密钥) | N/A(无CORS/跳转) | ✅ 已审 |
| 3 | `docs/modules/common/README.md` | REQ-5 模块文档 | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) |
| 4 | `.agents/coding/2026-11-04-bubble-sort/impl.md` | 编码报告 | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) |

- 队列来源：`git show --stat HEAD`（4 个新增文件），已有 `.java` 文件 → 立论成立。
- 非 Java 文件（3、4）标 `跳过/N/A(非 Java)`，仅作文档一致性核对，不进入 Java 规则扫描。

---

## Step 2 — 功能（产物 B）

> 仅从 requirement / change 提 **REQ**，勿臆造。不符标 **P0**。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 一组无序 int 数组，When 调用排序，Then 得到升序结果 | 需求原文：「实现一个冒泡排序」；`impl.md` §READ「BubbleSort（新增）- 排序工具类」 | `BubbleSort.java` | ✅ | `BubbleSort.java:26-50` 双层循环相邻比较交换；测试 `BubbleSortTest.java:26-48`；对拍 2000 组与 `Arrays.sort` 一致 |
| REQ-2 | Given 调用方持有原数组，When 排序完成，Then 原地有序且返回同一引用 | `impl.md` §TEST 行「should_sortInPlace_when_inputArrayProvided｜原地排序与返回引用」 | `BubbleSort.java` | ✅ | `BubbleSort.java:49` `return source`；`BubbleSortTest.java:102-114` `isSameAs` + 原数组断言 |
| REQ-3 | Given 入参为 null，When 调用排序，Then 抛出 `IllegalArgumentException` 且消息明确 | `impl.md` §TEST 行「should_throwIllegalArgumentException_when_inputIsNull」；`docs/modules/common/README.md:33` | `BubbleSort.java` | ✅ | `BubbleSort.java:27-29`；`BubbleSortTest.java:116-123` 断言类型 + 消息 |
| REQ-4 | Given 空数组/单元素/重复值/最值边界，When 排序，Then 结果正确且不抛异常 | `impl.md` §TEST 表（空、单元素、重复值与负数、边界值） | `BubbleSortTest.java` | ✅ | `BubbleSortTest.java:76-100`、`125-143`；`mvn test` 11 用例全绿 |
| REQ-5 | Given 新增工具类，When 交付，Then 具备配套单测与模块文档 | `impl.md` §DOCS「新建 `docs/modules/common/README.md`」；§变更清单 | `BubbleSortTest.java`、`docs/modules/common/README.md` | ✅ | `docs/modules/common/README.md:19-35`（接口/异常/复杂度）；`BubbleSortTest.java` 9 个测试方法（11 次执行） |

---

## Step 3 — 可读性检查（产物 C）

对照 `references/readability-checklist.md` A1–A7 逐节核销：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | `BubbleSort.java` 文件名=类名；UTF-8；无 Tab、无行尾空白（脚本复核 0 命中） |
| A2 | 源文件结构/import 顺序 | ✅ | 两类均 package→import→单顶层类，段间空行；`BubbleSort` 无 import；测试类无 `import *`，静态 import 独立成组且组内字典序 |
| A3 | 代码样式 | ✅ | 4 空格缩进；K&R 大括号；运算符两侧空格；最长行 <120；成员间空行 |
| A4 | 命名规范 | ✅ | `BubbleSort`（UpperCamelCase）、`sort`/`swapped`/`source`/`expected`（lowerCamelCase）、测试类 `BubbleSortTest`；包名全小写 |
| A5 | 编码实践 | ✅ | 无 `@Override` 场景；无空 catch；静态方法以 `BubbleSort.sort(...)` 类名调用 |
| A6 | 特定元素样式 | ✅ | 数组声明 `int[] source`（括号属类型）；无 switch；修饰符顺序 `public static` 正确；无 long 字面量 |
| A7 | Javadoc 规范 | ✅ | 类与 public 方法均有 Javadoc（`BubbleSort.java:3-11`、`17-25`），块标记顺序 `@param`→`@return`→`@throws` 正确 |

---

## Step 4 — 可靠性检查（产物 D）

> 逐条核销：G/S 每个 ID 独占一行；Bug 模式按 B*/M*/I* 独占一行核销。报告等级：Blocker→P0、Major→P1、Info→P2。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 | N/A(无字面量 parse/of 调用) | | 
| B002 | N/A(无数组 equals 比较，测试用 `containsExactly`) | |
| B003 | N/A(无 Arrays.fill) | |
| B004 | N/A(无数组 toString) | |
| B005 | N/A(无 Arrays.asList) | |
| B006 | N/A(无 assertEquals) | |
| B007 | N/A(未捕获 Throwable/Error) | |
| B008 | N/A(无线程池) | |
| B009 | N/A(无移位运算) | |
| B010 | N/A(无 BigDecimal) | |
| B011 | N/A(无包装类型 == 比较) | |
| B012 | N/A(无 Calendar) | |
| B013 | N/A(无 Calendar) | |
| B014 | N/A(无集合查询) | |
| B015 | N/A(无 Collection.toArray) | |
| B016 | N/A(无 Comparable 实现) | |
| B017 | N/A(无 this==null) | |
| B018 | N/A(无三目类型提升) | |
| B019 | N/A(无 Money API) | |
| B020 | N/A(无编译期常量乘法) | |
| B021 | N/A(无 Jedis) | |
| B022 | N/A(无 SimpleDateFormat) | |
| B023 | N/A(异常均被 throw) | |
| B024 | N/A(无 Thread) | |
| B025 | N/A(无双括号初始化) | |
| B026 | N/A(无 `equals(null)`，null 判断为 `source == null`) | |
| B027 | N/A(未重写 equals) | |
| B028 | N/A(无 commons DateUtil) | |
| B029 | N/A(无 setter) | |
| B030 | N/A(无浮点比较) | |
| B031 | N/A(无 String.format) | |
| B032 | N/A(无注解实例操作) | |
| B033 | N/A(无 Unsafe) | |
| B034 | N/A(无 Hashtable) | |
| B035 | N/A(无同对象二元运算) | |
| B036 | N/A(无 IdentityHashMap) | |
| B037 | N/A(无可变参数) | |
| B038 | N/A(无递归) | |
| B039 | N/A(无 indexOf) | |
| B040 | N/A(无 isInstance) | |
| B041 | N/A(无 JDBC) | |
| B042 | N/A(JUnit 5 测试) | |
| B043 | N/A(无内部类测试) | |
| B044 | N/A(未混用 JUnit3/4) | |
| B045 | N/A(无 synchronized/加锁) | |
| B046 | ✅ | `BubbleSort.java:32-41` 循环条件变量 `end--`、`i++` 均在循环体内推进，无死循环 |
| B047 | N/A(无 Float.compare) | |
| B048 | N/A(无 Math.round) | |
| B049 | N/A(无日期格式) | |
| B050 | N/A(无日期格式) | |
| B051 | N/A(无系统属性读取) | |
| B052 | N/A(无日期格式) | |
| B053 | N/A(期望异常使用 `assertThatThrownBy`，无需 fail) | |
| B054 | N/A(无 EqualsTester) | |
| B055 | N/A(无 Mockito) | |
| B056 | N/A(无 Arrays.asList) | |
| B057 | N/A(增强 for 中无集合修改) | |
| B058 | N/A(无集合自引用方法) | |
| B059 | N/A(无 Collections.nCopies) | |
| B060 | N/A(无含 null 的三目拆箱) | |
| B061 | N/A(无 sun.misc) | |
| B062 | N/A(无 ClassLoader 强转) | |
| B063 | N/A(无 javax.xml.bind) | |
| B064 | N/A(无 Optional) | |
| B065 | N/A(无 Pojo 自赋值) | |
| B066 | N/A(无 Math.random) | |
| B067 | N/A(无 Random) | |
| B068 | N/A(无自赋值) | |
| B069 | N/A(无 compareTo 自身) | |
| B070 | N/A(无 equals 自身) | |
| B071 | N/A(无 size()>=0) | |
| B072 | N/A(无 Stream.toString) | |
| B073 | N/A(无 StringBuilder) | |
| B074 | N/A(无 substring) | |
| B075 | ✅ | `BubbleSort.java:32/34` 退出条件与增量方向一致（`end--` + `i<end`、`i++`） |
| B076 | N/A(无 @Transactional) | |
| B077 | N/A(未捕获 Throwable) | |
| B078 | N/A(无 assertThat(x).isEqualTo(x)) | |
| B079 | N/A(无 @Mock) | |
| B080 | ✅ | 全部 9 个测试方法均含 `assertThat`/`assertThatThrownBy` 断言 |
| B081 | N/A(无集合原地排序) | |
| M001 | N/A(无重复条件分支) | |
| M002 | N/A(无 instanceof) | |
| M003 | N/A(无包装类构造器) | |
| M004 | N/A(无 printStackTrace) | |
| M005 | N/A(无内部类) | |
| M006 | N/A(无恒真/恒假布尔表达式) | |
| M007 | N/A(无 catch 块) | |
| M008 | N/A(未重写 equals) | |
| M009 | N/A(无跨类型 equals) | |
| M010 | N/A(无位运算) | |
| M011 | N/A(无 switch) | |
| M012 | N/A(无 finally) | |
| M013 | N/A(无浮点强转) | |
| M014 | N/A(无枚举) | |
| M015 | N/A(无继承字段隐藏) | |
| M016 | N/A(无时间 API 调用) | |
| M017 | N/A(测试方法均有 @Test/@ParameterizedTest) | |
| M018 | N/A(无 Lock) | |
| M019 | N/A(无枚举 switch) | |
| M020 | N/A(无方法重写) | |
| M021 | N/A(未定义 equals) | |
| M022 | N/A(无 Optional) | |
| M023 | N/A(无对象打印输出) | |
| M024 | N/A(无 Optional) | |
| M025 | N/A(无 protected 成员) | |
| M026 | N/A(无 @Mock) | |
| M027 | N/A(无 ThreadLocal) | |
| I001 | ✅ | `BubbleSortTest.java:120-122` 同时断言异常类型与消息，非仅类型 |
| I002 | N/A(无 @Mock) | |
| I003 | N/A(无 @Mock) | |
| I004 | N/A(无 java.util.Date) | |
| I005 | N/A(非 JUnit3) | |
| I006 | N/A(无 setUp) | |
| I007 | N/A(无 tearDown) | |
| I008 | N/A(`@MethodSource` 对应 `provideBoundaryArrays` 存在，11 用例实际执行) | |
| I009 | ✅ | 统计：`BubbleSortTest` 9 个测试方法 / 11 次执行（参数化 3 组） |
| I010 | N/A(无容器启动 Runner) | |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A(无事务/并发读写) | 纯内存计算，无共享状态 |
| G1.2 | N/A(无加锁与二次校验) | |
| G1.3 | N/A(无乐观锁) | |
| G1.4 | N/A(无多资源加锁) | |
| G2.1 | N/A(无写接口/消息消费) | |
| G2.2 | N/A(无重试/定时任务/MQ) | |
| G2.3 | N/A(无幂等键) | |
| G3.1 | N/A(无分布式事务) | |
| G3.2 | N/A(无 @Transactional) | |
| G4.1 | N/A(无 SQL) | |
| G4.2 | N/A(无 SQL) | |
| G4.3 | N/A(无 SQL 查询) | |
| G5.1 | N/A(无 MQ 消费) | |
| G6.1 | N/A(无缓存) | |
| G6.2 | N/A(无缓存双写) | |
| G7.1 | N/A(无调度任务) | |
| G7.2 | N/A(无调度任务) | |
| G8.1 | N/A(无 catch，未吞异常) | |
| G8.2 | N/A(无外部依赖调用) | |
| G8.3 | N/A(无 I/O 资源) | |
| G8.4 | N/A(无线程池/定时任务) | |
| G8.5 | N/A(无 ThreadLocal) | |
| G8.6 | N/A(无线程池) | |
| G9.1 | N/A(无 RPC/HTTP/DB/Redis 调用) | |
| G9.2 | N/A(无外部调用) | |
| G9.3 | N/A(无重试) | |
| G10.1 | ✅ | `BubbleSort.java:22-24` Javadoc 明确 `source` 非 null、返回值同引用、null 抛 `IllegalArgumentException`，且 `docs/modules/common/README.md:29-35` 契约一致，无 null 语义混用 |
| G10.2 | ✅ | 新增类为增量交付，不涉及既有契约变更 |
| G11.1 | ✅ | `BubbleSortTest.java` 9 个测试方法（11 次执行），均含断言；`mvn test` 通过 |
| G11.2 | ✅ | 覆盖空数组、单元素、重复值、`Integer.MIN_VALUE/MAX_VALUE`、已排序提前退出分支 |
| G11.3 | ✅ | `BubbleSort.java:27-29` 对 null 入参显式防御并抛出明确异常 |
| G11.4 | N/A(仅比较运算，无算术/金额运算) | 交换使用临时变量，无溢出风险 |
| G12.1 | N/A(无资金场景) | |
| G12.2 | N/A(无资金场景) | |
| G13.1 | N/A(无日志输出) | |
| G14.1 | N/A(无金额运算) | |
| G14.2 | N/A(无多租户数据访问) | |
| G14.3 | N/A(无时间处理) | |
| G14.4 | N/A(无时间格式化) | |
| G15.1 | N/A(无表结构变更) | |
| G15.2 | N/A(无新旧接口共存) | |
| G15.3 | N/A(无灰度开关需求) | |
| G16.1 | N/A(纯工具类，无核心链路指标) | |
| G16.2 | ✅ | 唯一异常路径 `BubbleSort.java:28` 携带明确业务消息，无 catch 吞没；无日志为纯计算工具类的合理取舍 |
| G16.3 | N/A(无日志) | |
| G16.4 | ✅ | 全文件无 `catch`、无 `printStackTrace()`、无异常吞没 |
| G17.1 | N/A(纯工具类，无运行时开关需求) | |
| G17.2 | N/A(无外部依赖链路) | |
| G17.3 | N/A(无数据变更) | |
| G18.1 | N/A(清单未定义 G18 节) | |
| G18.2 | N/A(清单未定义 G18 节) | |
| G18.3 | N/A(清单未定义 G18 节) | |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A(无 SQL) | |
| S1.2 | N/A(无动态 SQL) | |
| S1.3 | N/A(无 like/in 查询) | |
| S2.1 | N/A(无 HTML/JS/URL 输出) | |
| S2.2 | N/A(无富文本) | |
| S2.3 | N/A(无模板引擎) | |
| S3.1 | N/A(无外部 URL 请求) | |
| S3.2 | N/A(无跳转) | |
| S3.3 | N/A(无外部请求) | |
| S4.1 | N/A(无系统命令) | |
| S4.2 | N/A(无文件操作) | |
| S5.1 | N/A(无 XML 解析) | |
| S5.2 | N/A(无 XPath) | |
| S6.1 | N/A(无反序列化) | |
| S6.2 | N/A(无反序列化) | |
| S6.3 | N/A(无敏感字段) | |
| S7.1 | N/A(无文件上传) | |
| S7.2 | N/A(无路径拼接) | |
| S7.3 | N/A(无文件存储) | |
| S8.1 | N/A(非 HTTP 接口，无资源操作) | |
| S8.2 | N/A(非接口) | |
| S8.3 | N/A(无数据 ID) | |
| S8.4 | N/A(无 Cookie) | |
| S9.1 | N/A(无密钥/凭证) | |
| S9.2 | N/A(无日志输出) | |
| S9.3 | N/A(无传输/存储) | |
| S9.4 | N/A(无随机数) | |
| S10.1 | N/A(无增删改接口) | |
| S10.2 | N/A(无 CORS 配置) | |
| S10.3 | N/A(无 URL 跳转) | |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A(未启用自定义规则) | 清单仅含 Controller `@Valid` 示例项，本次变更为工具类，项目未启用自定义规则 |
| U1.2 | N/A(未启用自定义规则) | 清单未定义该项 |
| U1.3 | N/A(未启用自定义规则) | 清单未定义该项 |
| U2.1 | N/A(未启用自定义规则) | U2 业务红线节为空 |
| U2.2 | N/A(未启用自定义规则) | U2 业务红线节为空 |
| U2.3 | N/A(未启用自定义规则) | U2 业务红线节为空 |

---

## 逐文件结论

| # | 文件 | Step2 | Step3 | Step4（G/S/B） | Step5 | 结论 |
|---|------|-------|-------|----------------|-------|------|
| 1 | `src/main/java/com/library/common/util/BubbleSort.java` | ✅ REQ-1/2/3 满足 | ✅ A1–A7 全通过 | ✅ 无命中（G11/G16 正向确认） | N/A | ✅ 已审，无问题 |
| 2 | `src/test/java/com/library/common/util/BubbleSortTest.java` | ✅ REQ-4/5 满足 | ✅ A1–A7 全通过 | ✅ 无命中（B080/I001/I009 正向确认） | N/A | ✅ 已审，无问题 |
| 3 | `docs/modules/common/README.md` | ✅ 与代码契约一致 | N/A(非 Java) | N/A(非 Java) | N/A | ✅ 已审，无问题 |
| 4 | `.agents/coding/2026-11-04-bubble-sort/impl.md` | ✅ 描述与代码/测试一致 | N/A(非 Java) | N/A(非 Java) | N/A | ✅ 已审，无问题 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（N/A 均写原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（N/A(未启用自定义规则)）
- [x] 所有 `❌/⚠️` 已写入 report —— 本次无 `❌/⚠️` 命中（0 项），report §7 附可选建议（不计入问题数）

**结果**：`⬜ 待审` = 0；命中 `❌/⚠️` = 0；`N/A` 均带原因。
