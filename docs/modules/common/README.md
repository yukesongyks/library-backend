# common 模块

## 模块职责

存放跨模块通用的基础能力：统一响应结构、业务异常与错误码、全局异常处理、业务常量以及通用工具类。

## 关键类

| 类 | 说明 |
|----|------|
| `Result<T>` | 统一响应结构 |
| `PageResult<T>` | 分页响应结构 |
| `ResultCode` | 统一错误码 |
| `BusinessException` | 业务异常，由 `GlobalExceptionHandler` 统一转换为响应 |
| `GlobalExceptionHandler` | 全局异常处理，覆盖业务异常、参数校验异常、非法参数异常与兜底异常 |
| `BusinessConstants` | 业务常量 |
| `com.library.common.util.BubbleSort` | 冒泡排序工具类，提供整数数组的原地升序排序 |

## BubbleSort

**职责**：对 `int[]` 进行原地升序冒泡排序。

**接口**：

```java
public static int[] sort(int[] source)
```

| 项 | 说明 |
|----|------|
| 入参 | `source`：待排序数组，不允许为 `null` |
| 返回值 | 排序后的数组，与入参为同一引用（原地修改） |
| 异常 | 入参为 `null` 时抛出 `IllegalArgumentException`，由全局异常处理器转换为参数错误响应 |
| 时间复杂度 | 最坏/平均 O(n²)，最好 O(n)（已有序时借助“本轮无交换”提前退出） |
| 空间复杂度 | O(1) |

## 依赖关系

- 依赖：无（纯 JDK 实现）
- 被依赖：业务模块可直接静态调用，无需注入

## 测试

`src/test/java/com/library/common/util/BubbleSortTest.java`（JUnit 5 + AssertJ），覆盖乱序、逆序、已有序、重复值与负数、空数组、单元素、原地排序、`null` 入参以及边界值场景。
