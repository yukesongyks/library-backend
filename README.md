# library-backend

图书管理系统后端 - 功能演示与埋点统计

## 技术栈

- Java 21
- Spring Boot 3.2.5
- Spring Data JPA
- H2 内存数据库
- Spring AOP（埋点切面）

## 项目结构

```
src/main/java/com/antdigital/library/
├── LibraryBackendApplication.java          # 启动类
├── common/
│   ├── config/
│   │   └── WebMvcConfig.java               # CORS 配置
│   ├── exception/
│   │   ├── ErrorCodeEnum.java              # 错误码枚举
│   │   ├── ServiceException.java            # 业务异常
│   │   └── GlobalExceptionHandler.java     # 全局异常处理
│   └── response/
│       └── ApiResponse.java                # 统一响应
├── demo/                                    # 演示功能模块
│   ├── controller/
│   │   ├── DemoController.java              # HelloWorld/Hash/BubbleSort 接口
│   │   └── ExportController.java            # 导出接口
│   ├── model/vo/
│   │   ├── HelloWorldVO.java
│   │   ├── HashVO.java
│   │   └── BubbleSortVO.java
│   └── service/
│       ├── DemoService.java
│       ├── ExportService.java
│       └── impl/
│           ├── DemoServiceImpl.java
│           └── ExportServiceImpl.java
└── track/                                   # 埋点统计模块
    ├── aspect/
    │   └── TrackAspect.java                 # 埋点切面
    ├── controller/
    │   └── TrackController.java              # 统计查询接口
    ├── model/
    │   ├── entity/TrackRecordDO.java         # 埋点实体
    │   └── vo/TrackStatisticsVO.java         # 统计VO
    ├── repository/
    │   └── TrackRecordRepository.java        # 数据访问层
    └── service/
        ├── TrackService.java
        └── impl/TrackServiceImpl.java
```

## API 接口列表

| 接口 | 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|------|
| HelloWorld | GET | /api/demo/helloworld | - | 返回欢迎消息 |
| 哈希算法 | GET | /api/demo/hash | input | SHA-256 哈希 |
| 冒泡排序 | GET | /api/demo/bubble-sort | input | 逗号分隔数字排序 |
| 导出 | GET | /api/demo/export | type, input | 导出 CSV 文件 |
| 埋点统计 | GET | /api/track/statistics | dimension, chartType | 聚合统计 |

## 埋点请求头（模拟身份）

| 请求头 | 说明 | 示例 |
|--------|------|------|
| X-User-Id | 用户ID | U001 |
| X-User-Name | 用户名 | 张三 |
| X-User-Type | 人员类型 | 管理员 |
| X-User-Level | 人员层级 | L3 |
| X-User-Department | 人员部门 | 技术部 |

## 统计维度

- `user_type` - 人员类型
- `user_level` - 人员层级
- `user_department` - 人员部门
- `user_id` - 调用人

## 图表类型

- `pie` - 饼图（维度聚合）
- `bar` - 柱状图（维度聚合）
- `line` - 折线图（按日期+维度聚合）

## 构建运行

```bash
mvn clean compile -DskipTests
mvn spring-boot:run
```

## 单元测试

```bash
mvn test -Dtest=DemoServiceImplTest
```
