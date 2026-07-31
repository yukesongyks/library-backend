# 成本分析报表 (Cost Analysis Report) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建企业成本统计报表系统，前端提供成本分析 Dashboard + 多维度统计页面，后端提供成本数据 REST API 及 Excel 导出能力，支持按部门/项目/业务线/人员/月份/季度/年度展示人力成本与项目成本。

**Architecture:** 前后端分离架构。后端 library-backend (Spring Boot 2.7.x / Java 17) 提供 JPA 实体、Repository、Service、REST Controller 及 Apache POI Excel 导出；前端 library-frontend (React 18 + Vite + TypeScript + ECharts + Ant Design + axios) 提供 Dashboard 页和成本分析页，通过 RESTful API 直连后端 `/api/cost/**` 端点。跨库接口契约为 JSON-over-HTTP，报表导出由后端生成 Excel 二进制流，前端触发下载。

**Tech Stack:**
- 后端：Spring Boot 2.7.18, Java 17, Spring Data JPA, H2 (dev) / MySQL 8 (prod), Lombok, Apache POI 5.2.3, Jackson
- 前端：React 18.2, Vite 5.x, TypeScript 5.x, Ant Design 5.x, ECharts 5.x, axios 1.6, dayjs 1.11

## Global Constraints

- 后端 Java 17，Spring Boot 2.7.18，Maven 构建，包名 `com.library.backend`
- 前端 Node 18+，Vite 5，TypeScript strict mode，pnpm 包管理
- API 路径前缀统一 `/api/cost`，所有响应 `Content-Type: application/json`
- 数据库表名前缀 `cost_`，JPA 实体驼峰映射下划线
- 所有金额字段类型 `BigDecimal`，精度 `scale=2`，货币 `RMB`
- 跨库接口向后兼容：仅新增字段/接口，不破坏现有契约
- 前端 axios baseURL 通过 Vite 环境变量 `VITE_API_BASE_URL` 配置

---

## File Structure

### 后端 library-backend (`src/main/java/com/library/backend/`)

| 文件 | 职责 |
|------|------|
| `entity/Department.java` | 部门实体 |
| `entity/Project.java` | 项目实体 |
| `entity/BusinessLine.java` | 业务线实体 |
| `entity/Employee.java` | 人员实体（含角色枚举 DEVELOPER/TESTER/PRODUCT/OPS） |
| `entity/CostRecord.java` | 成本记录实体（人力成本明细） |
| `entity/ProjectBudget.java` | 项目预算实体 |
| `repository/DepartmentRepository.java` | 部门 DAO |
| `repository/ProjectRepository.java` | 项目 DAO |
| `repository/BusinessLineRepository.java` | 业务线 DAO |
| `repository/EmployeeRepository.java` | 人员 DAO |
| `repository/CostRecordRepository.java` | 成本记录 DAO（含自定义聚合查询） |
| `repository/ProjectBudgetRepository.java` | 项目预算 DAO |
| `service/CostAnalysisService.java` | 成本聚合统计核心逻辑 |
| `service/ExcelExportService.java` | Apache POI Excel 导出 |
| `controller/CostAnalysisController.java` | REST API 端点 |
| `dto/CostSummaryDTO.java` | 成本汇总响应 |
| `dto/CostQueryRequest.java` | 查询请求参数 |
| `dto/ProjectCostDTO.java` | 项目成本响应（含预算占比/超支金额） |
| `config/CorsConfig.java` | 跨域配置（允许前端 origin） |
| `resources/application.yml` | 数据源与 JPA 配置 |
| `resources/data.sql` | 初始化种子数据 |

### 前端 library-frontend (`src/`)

| 文件 | 职责 |
|------|------|
| `main.tsx` | React 入口 |
| `App.tsx` | 路由与布局 |
| `api/client.ts` | axios 实例 + baseURL 配置 |
| `api/costApi.ts` | 成本相关 API 请求封装 |
| `types/cost.ts` | 成本相关 TS 类型定义 |
| `pages/Dashboard.tsx` | 成本分析 Dashboard 页（总览卡片 + 图表） |
| `pages/CostAnalysis.tsx` | 多维度成本分析页（筛选 + 表格 + 图表） |
| `pages/ProjectCost.tsx` | 项目成本明细页（预算/实际/占比/超支） |
| `components/StatCard.tsx` | 统计概览卡片 |
| `components/CostTrendChart.tsx` | 月度成本趋势折线图 |
| `components/RoleCostPie.tsx` | 人力成本角色占比饼图 |
| `components/ProjectBudgetBar.tsx` | 项目预算 vs 实际消耗柱状图 |
| `components/CostTable.tsx` | 成本明细表格（支持导出按钮） |
| `components/DimensionFilter.tsx` | 维度筛选器（部门/项目/业务线/人员/时间） |
| `hooks/useCostData.ts` | 成本数据获取 hook |
| `package.json` | 依赖与脚本 |
| `vite.config.ts` | Vite 配置（含 proxy） |
| `tsconfig.json` | TS 配置 |
| `.env.development` | 开发环境变量 |

---

## Task 1: 后端项目初始化与 Maven 配置

**Files:**
- Create: `library-backend/pom.xml`
- Create: `library-backend/src/main/resources/application.yml`

**Interfaces:**
- Produces: 可运行的 Spring Boot 项目骨架，后续 Task 均依赖此 Maven 坐标和包结构

- [ ] **Step 1: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
        <relativePath/>
    </parent>
    <groupId>com.library</groupId>
    <artifactId>library-backend</artifactId>
    <version>1.0.0</version>
    <name>library-backend</name>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.2.3</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建 application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:costdb;DB_CLOSE_DELAY=-1;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.H2Dialect
    defer-datasource-initialization: true
  sql:
    init:
      mode: always

logging:
  level:
    com.library.backend: DEBUG
```

- [ ] **Step 3: 验证项目可编译**

Run: `cd library-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
cd library-backend
git add pom.xml src/main/resources/application.yml
git commit -m "feat: init backend project scaffold with Spring Boot 2.7.18"
```

---

## Task 2: 后端数据模型实体层

**Files:**
- Create: `library-backend/src/main/java/com/library/backend/entity/Department.java`
- Create: `library-backend/src/main/java/com/library/backend/entity/BusinessLine.java`
- Create: `library-backend/src/main/java/com/library/backend/entity/Employee.java`
- Create: `library-backend/src/main/java/com/library/backend/entity/Project.java`
- Create: `library-backend/src/main/java/com/library/backend/entity/CostRecord.java`
- Create: `library-backend/src/main/java/com/library/backend/entity/ProjectBudget.java`

**Interfaces:**
- Produces: 6 个 JPA 实体，字段名供后续 Repository/Service 引用

- [ ] **Step 1: 创建 Department 实体**

```java
package com.library.backend.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "cost_department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String description;
}
```

- [ ] **Step 2: 创建 BusinessLine 实体**

```java
package com.library.backend.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "cost_business_line")
public class BusinessLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String description;
}
```

- [ ] **Step 3: 创建 Employee 实体（含角色枚举）**

```java
package com.library.backend.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "cost_employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmployeeRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_line_id")
    private BusinessLine businessLine;

    public enum EmployeeRole {
        DEVELOPER, TESTER, PRODUCT, OPS
    }
}
```

- [ ] **Step 4: 创建 Project 实体**

```java
package com.library.backend.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "cost_project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_line_id")
    private BusinessLine businessLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}
```

- [ ] **Step 5: 创建 CostRecord 实体（人力成本明细）**

```java
package com.library.backend.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "cost_record")
public class CostRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_line_id", nullable = false)
    private BusinessLine businessLine;

    @Column(nullable = false)
    private Integer costYear;

    @Column(nullable = false)
    private Integer costMonth;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String costType;
}
```

- [ ] **Step 6: 创建 ProjectBudget 实体**

```java
package com.library.backend.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "cost_project_budget")
public class ProjectBudget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private Integer budgetYear;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal budgetAmount;
}
```

- [ ] **Step 7: 验证编译**

Run: `cd library-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 8: Commit**

```bash
cd library-backend
git add src/main/java/com/library/backend/entity/
git commit -m "feat: add JPA entities for cost analysis domain model"
```

---

## Task 3: 后端 Repository 层与种子数据

**Files:**
- Create: `library-backend/src/main/java/com/library/backend/repository/DepartmentRepository.java`
- Create: `library-backend/src/main/java/com/library/backend/repository/BusinessLineRepository.java`
- Create: `library-backend/src/main/java/com/library/backend/repository/EmployeeRepository.java`
- Create: `library-backend/src/main/java/com/library/backend/repository/ProjectRepository.java`
- Create: `library-backend/src/main/java/com/library/backend/repository/CostRecordRepository.java`
- Create: `library-backend/src/main/java/com/library/backend/repository/ProjectBudgetRepository.java`
- Create: `library-backend/src/main/resources/data.sql`

**Interfaces:**
- Consumes: Task 2 的实体类
- Produces: 6 个 Spring Data JPA Repository，`CostRecordRepository` 提供自定义聚合查询方法

- [ ] **Step 1: 创建基础 Repository（5 个简单接口）**

```java
package com.library.backend.repository;

import com.library.backend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DepartmentRepository extends JpaRepository<Department, Long> {}

package com.library.backend.repository;
import com.library.backend.entity.BusinessLine;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BusinessLineRepository extends JpaRepository<BusinessLine, Long> {}

package com.library.backend.repository;
import com.library.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EmployeeRepository extends JpaRepository<Employee, Long> {}

package com.library.backend.repository;
import com.library.backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProjectRepository extends JpaRepository<Project, Long> {}

package com.library.backend.repository;
import com.library.backend.entity.ProjectBudget;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProjectBudgetRepository extends JpaRepository<ProjectBudget, Long> {}
```

- [ ] **Step 2: 创建 CostRecordRepository（含聚合查询）**

```java
package com.library.backend.repository;

import com.library.backend.entity.CostRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface CostRecordRepository extends JpaRepository<CostRecord, Long> {

    @Query("SELECT c FROM CostRecord c WHERE " +
           "(:departmentId IS NULL OR c.department.id = :departmentId) AND " +
           "(:businessLineId IS NULL OR c.businessLine.id = :businessLineId) AND " +
           "(:projectId IS NULL OR c.project.id = :projectId) AND " +
           "(:employeeId IS NULL OR c.employee.id = :employeeId) AND " +
           "(:costYear IS NULL OR c.costYear = :costYear) AND " +
           "(:costMonth IS NULL OR c.costMonth = :costMonth) AND " +
           "(:role IS NULL OR c.employee.role = :role)")
    List<CostRecord> findByFilters(
            @Param("departmentId") Long departmentId,
            @Param("businessLineId") Long businessLineId,
            @Param("projectId") Long projectId,
            @Param("employeeId") Long employeeId,
            @Param("costYear") Integer costYear,
            @Param("costMonth") Integer costMonth,
            @Param("role") Employee.EmployeeRole role);

    @Query("SELECT c.costYear, c.costMonth, SUM(c.amount) FROM CostRecord c " +
           "WHERE (:costYear IS NULL OR c.costYear = :costYear) " +
           "GROUP BY c.costYear, c.costMonth ORDER BY c.costYear, c.costMonth")
    List<Object[]> findMonthlyTrend(@Param("costYear") Integer costYear);

    @Query("SELECT c.employee.role, SUM(c.amount) FROM CostRecord c " +
           "GROUP BY c.employee.role")
    List<Object[]> findCostByRole();

    @Query("SELECT c.project.id, c.project.name, SUM(c.amount) FROM CostRecord c " +
           "WHERE c.project IS NOT NULL " +
           "GROUP BY c.project.id, c.project.name")
    List<Object[]> findCostByProject();

    @Query("SELECT c.department.id, c.department.name, SUM(c.amount) FROM CostRecord c " +
           "GROUP BY c.department.id, c.department.name")
    List<Object[]> findCostByDepartment();

    @Query("SELECT SUM(c.amount) FROM CostRecord c WHERE c.costYear = :year")
    BigDecimal findTotalCostByYear(@Param("year") Integer year);
}
```

- [ ] **Step 3: 创建种子数据 data.sql**

```sql
INSERT INTO cost_department (name, description) VALUES ('研发部', '产品研发');
INSERT INTO cost_department (name, description) VALUES ('测试部', '质量保证');
INSERT INTO cost_department (name, description) VALUES ('运维部', '基础设施运维');

INSERT INTO cost_business_line (name, description) VALUES ('业务线A', '核心业务');
INSERT INTO cost_business_line (name, description) VALUES ('业务线B', '创新业务');

INSERT INTO cost_employee (name, role, department_id, business_line_id) VALUES ('张三', 'DEVELOPER', 1, 1);
INSERT INTO cost_employee (name, role, department_id, business_line_id) VALUES ('李四', 'TESTER', 2, 1);
INSERT INTO cost_employee (name, role, department_id, business_line_id) VALUES ('王五', 'PRODUCT', 1, 2);
INSERT INTO cost_employee (name, role, department_id, business_line_id) VALUES ('赵六', 'OPS', 3, 1);

INSERT INTO cost_project (name, business_line_id, department_id) VALUES ('成本报表系统', 1, 1);
INSERT INTO cost_project (name, business_line_id, department_id) VALUES ('数据中台', 2, 1);

INSERT INTO cost_project_budget (project_id, budget_year, budget_amount) VALUES (1, 2026, 500000.00);
INSERT INTO cost_project_budget (project_id, budget_year, budget_amount) VALUES (2, 2026, 800000.00);

INSERT INTO cost_record (employee_id, project_id, department_id, business_line_id, cost_year, cost_month, amount, cost_type) VALUES
(1, 1, 1, 1, 2026, 1, 25000.00, 'LABOR'),
(1, 1, 1, 1, 2026, 2, 25000.00, 'LABOR'),
(2, 1, 2, 1, 2026, 1, 18000.00, 'LABOR'),
(3, 2, 1, 2, 2026, 1, 30000.00, 'LABOR'),
(4, 1, 3, 1, 2026, 1, 15000.00, 'LABOR'),
(1, 1, 1, 1, 2026, 3, 25000.00, 'LABOR'),
(2, 1, 2, 1, 2026, 2, 18000.00, 'LABOR'),
(3, 2, 1, 2, 2026, 2, 30000.00, 'LABOR'),
(4, 2, 3, 1, 2026, 2, 15000.00, 'LABOR');
```

- [ ] **Step 4: 验证编译与启动**

Run: `cd library-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
cd library-backend
git add src/main/java/com/library/backend/repository/ src/main/resources/data.sql
git commit -m "feat: add JPA repositories with aggregation queries and seed data"
```

---

## Task 4: 后端 DTO 与 Service 层（成本聚合逻辑）

**Files:**
- Create: `library-backend/src/main/java/com/library/backend/dto/CostQueryRequest.java`
- Create: `library-backend/src/main/java/com/library/backend/dto/CostSummaryDTO.java`
- Create: `library-backend/src/main/java/com/library/backend/dto/ProjectCostDTO.java`
- Create: `library-backend/src/main/java/com/library/backend/dto/DimensionStatDTO.java`
- Create: `library-backend/src/main/java/com/library/backend/service/CostAnalysisService.java`

**Interfaces:**
- Consumes: Task 3 的 Repository 方法
- Produces: `CostAnalysisService` 公共方法供 Controller 调用——`getCostSummary(CostQueryRequest)`, `getMonthlyTrend(Integer)`, `getCostByRole()`, `getProjectCost(Integer)`, `getCostByDimension(String, Integer)`

- [ ] **Step 1: 创建 CostQueryRequest DTO**

```java
package com.library.backend.dto;

import com.library.backend.entity.Employee;
import lombok.Data;

@Data
public class CostQueryRequest {
    private Long departmentId;
    private Long businessLineId;
    private Long projectId;
    private Long employeeId;
    private Integer costYear;
    private Integer costMonth;
    private Integer quarter;
    private Employee.EmployeeRole role;
}
```

- [ ] **Step 2: 创建 CostSummaryDTO**

```java
package com.library.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CostSummaryDTO {
    private BigDecimal totalCost;
    private BigDecimal laborCost;
    private Integer recordCount;
    private List<DimensionStatDTO> byDepartment;
    private List<DimensionStatDTO> byRole;
    private List<DimensionStatDTO> byMonth;
}
```

- [ ] **Step 3: 创建 DimensionStatDTO**

```java
package com.library.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DimensionStatDTO {
    private String dimensionName;
    private BigDecimal amount;
    private Double percentage;
}
```

- [ ] **Step 4: 创建 ProjectCostDTO（含预算占比/超支金额）**

```java
package com.library.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class ProjectCostDTO {
    private Long projectId;
    private String projectName;
    private BigDecimal budgetAmount;
    private BigDecimal actualCost;
    private BigDecimal budgetUsageRate;
    private BigDecimal overspendAmount;

    public void calculateDerived() {
        if (budgetAmount != null && budgetAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.budgetUsageRate = actualCost
                .multiply(new BigDecimal("100"))
                .divide(budgetAmount, 2, RoundingMode.HALF_UP);
            this.overspendAmount = actualCost.subtract(budgetAmount).max(BigDecimal.ZERO);
        } else {
            this.budgetUsageRate = BigDecimal.ZERO;
            this.overspendAmount = BigDecimal.ZERO;
        }
    }
}
```

- [ ] **Step 5: 创建 CostAnalysisService**

```java
package com.library.backend.service;

import com.library.backend.dto.*;
import com.library.backend.entity.CostRecord;
import com.library.backend.entity.Employee;
import com.library.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CostAnalysisService {

    private final CostRecordRepository costRecordRepository;
    private final ProjectBudgetRepository projectBudgetRepository;
    private final ProjectRepository projectRepository;

    public CostSummaryDTO getCostSummary(CostQueryRequest req) {
        List<CostRecord> records = costRecordRepository.findByFilters(
            req.getDepartmentId(), req.getBusinessLineId(), req.getProjectId(),
            req.getEmployeeId(), req.getCostYear(), req.getCostMonth(), req.getRole());

        CostSummaryDTO dto = new CostSummaryDTO();
        BigDecimal total = records.stream()
            .map(CostRecord::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalCost(total);
        dto.setLaborCost(total);
        dto.setRecordCount(records.size());
        dto.setByDepartment(groupByDimension(records, r -> r.getDepartment().getName()));
        dto.setByRole(groupByDimension(records, r -> r.getEmployee().getRole().name()));
        dto.setByMonth(groupByDimension(records,
            r -> r.getCostYear() + "-" + String.format("%02d", r.getCostMonth())));
        return dto;
    }

    public List<DimensionStatDTO> getMonthlyTrend(Integer costYear) {
        List<Object[]> rows = costRecordRepository.findMonthlyTrend(costYear);
        BigDecimal total = rows.stream()
            .map(r -> (BigDecimal) r[2])
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return rows.stream().map(r -> new DimensionStatDTO(
            r[0] + "-" + String.format("%02d", r[1]),
            (BigDecimal) r[2],
            total.compareTo(BigDecimal.ZERO) > 0
                ? ((BigDecimal) r[2]).multiply(new BigDecimal("100"))
                    .divide(total, 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0
        )).collect(Collectors.toList());
    }

    public List<DimensionStatDTO> getCostByRole() {
        List<Object[]> rows = costRecordRepository.findCostByRole();
        BigDecimal total = rows.stream()
            .map(r -> (BigDecimal) r[1])
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return rows.stream().map(r -> new DimensionStatDTO(
            ((Employee.EmployeeRole) r[0]).name(),
            (BigDecimal) r[1],
            total.compareTo(BigDecimal.ZERO) > 0
                ? ((BigDecimal) r[1]).multiply(new BigDecimal("100"))
                    .divide(total, 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0
        )).collect(Collectors.toList());
    }

    public List<ProjectCostDTO> getProjectCost(Integer budgetYear) {
        List<Object[]> rows = costRecordRepository.findCostByProject();
        return rows.stream().map(r -> {
            ProjectCostDTO dto = new ProjectCostDTO();
            dto.setProjectId((Long) r[0]);
            dto.setProjectName((String) r[1]);
            dto.setActualCost((BigDecimal) r[2]);
            projectBudgetRepository.findAll().stream()
                .filter(b -> b.getProject().getId().equals(r[0]) && b.getBudgetYear().equals(budgetYear))
                .findFirst()
                .ifPresent(b -> dto.setBudgetAmount(b.getBudgetAmount()));
            dto.calculateDerived();
            return dto;
        }).collect(Collectors.toList());
    }

    public List<DimensionStatDTO> getCostByDimension(String dimension, Integer costYear) {
        List<Object[]> rows;
        switch (dimension.toLowerCase()) {
            case "department": rows = costRecordRepository.findCostByDepartment(); break;
            case "role": rows = costRecordRepository.findCostByRole(); break;
            case "project": rows = costRecordRepository.findCostByProject(); break;
            default: rows = costRecordRepository.findCostByDepartment();
        }
        BigDecimal total = rows.stream()
            .map(r -> (BigDecimal) r[2])
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return rows.stream().map(r -> new DimensionStatDTO(
            (String) r[1],
            (BigDecimal) r[2],
            total.compareTo(BigDecimal.ZERO) > 0
                ? ((BigDecimal) r[2]).multiply(new BigDecimal("100"))
                    .divide(total, 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0
        )).collect(Collectors.toList());
    }

    private List<DimensionStatDTO> groupByDimension(List<CostRecord> records,
            java.util.function.Function<CostRecord, String> keyExtractor) {
        Map<String, BigDecimal> grouped = records.stream()
            .collect(Collectors.groupingBy(keyExtractor,
                Collectors.reducing(BigDecimal.ZERO, CostRecord::getAmount, BigDecimal::add)));
        BigDecimal total = grouped.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return grouped.entrySet().stream()
            .map(e -> new DimensionStatDTO(e.getKey(), e.getValue(),
                total.compareTo(BigDecimal.ZERO) > 0
                    ? e.getValue().multiply(new BigDecimal("100"))
                        .divide(total, 2, RoundingMode.HALF_UP).doubleValue()
                    : 0.0))
            .sorted(Comparator.comparing(DimensionStatDTO::getDimensionName))
            .collect(Collectors.toList());
    }
}
```

> 注意：`CostAnalysisService` 引用了 `java.math.RoundingMode`，编译器会要求 import。在 Step 5 的代码顶部补 `import java.math.RoundingMode;`。Service 方法签名固定，供 Controller 直接调用。

- [ ] **Step 6: 验证编译**

Run: `cd library-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 7: Commit**

```bash
cd library-backend
git add src/main/java/com/library/backend/dto/ src/main/java/com/library/backend/service/
git commit -m "feat: add cost analysis DTOs and service layer with aggregation logic"
```

---

## Task 5: 后端 Controller、CORS 与 Excel 导出

**Files:**
- Create: `library-backend/src/main/java/com/library/backend/config/CorsConfig.java`
- Create: `library-backend/src/main/java/com/library/backend/service/ExcelExportService.java`
- Create: `library-backend/src/main/java/com/library/backend/controller/CostAnalysisController.java`
- Create: `library-backend/src/main/java/com/library/backend/LibraryBackendApplication.java`

**Interfaces:**
- Consumes: Task 4 的 `CostAnalysisService` 方法
- Produces: REST API 端点 `/api/cost/summary`, `/api/cost/trend`, `/api/cost/role`, `/api/cost/project`, `/api/cost/dimension`, `/api/cost/export`

- [ ] **Step 1: 创建主启动类**

```java
package com.library.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryBackendApplication.class, args);
    }
}
```

- [ ] **Step 2: 创建 CorsConfig（允许前端 origin）**

```java
package com.library.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

- [ ] **Step 3: 创建 ExcelExportService**

```java
package com.library.backend.service;

import com.library.backend.dto.CostSummaryDTO;
import com.library.backend.dto.DimensionStatDTO;
import com.library.backend.dto.ProjectCostDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExcelExportService {

    public byte[] exportCostSummary(CostSummaryDTO summary) throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Sheet overview = workbook.createSheet("成本总览");
            createOverviewSheet(overview, summary, headerStyle);
            Sheet deptSheet = workbook.createSheet("部门维度");
            createDimensionSheet(deptSheet, summary.getByDepartment(), headerStyle);
            Sheet roleSheet = workbook.createSheet("角色维度");
            createDimensionSheet(roleSheet, summary.getByRole(), headerStyle);
            Sheet monthSheet = workbook.createSheet("月度维度");
            createDimensionSheet(monthSheet, summary.getByMonth(), headerStyle);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportProjectCost(List<ProjectCostDTO> projects) throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Sheet sheet = workbook.createSheet("项目成本");
            Row header = sheet.createRow(0);
            String[] headers = {"项目ID", "项目名称", "预算金额", "实际消耗", "预算占比(%)", "预计超支金额"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            for (int i = 0; i < projects.size(); i++) {
                ProjectCostDTO p = projects.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(p.getProjectId());
                row.createCell(1).setCellValue(p.getProjectName());
                row.createCell(2).setCellValue(p.getBudgetAmount() != null ? p.getBudgetAmount().doubleValue() : 0);
                row.createCell(3).setCellValue(p.getActualCost() != null ? p.getActualCost().doubleValue() : 0);
                row.createCell(4).setCellValue(p.getBudgetUsageRate() != null ? p.getBudgetUsageRate().doubleValue() : 0);
                row.createCell(5).setCellValue(p.getOverspendAmount() != null ? p.getOverspendAmount().doubleValue() : 0);
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void createOverviewSheet(Sheet sheet, CostSummaryDTO summary, CellStyle headerStyle) {
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("指标"); header.getCell(0).setCellStyle(headerStyle);
        header.createCell(1).setCellValue("数值"); header.getCell(1).setCellStyle(headerStyle);
        Row r1 = sheet.createRow(1);
        r1.createCell(0).setCellValue("总成本");
        r1.createCell(1).setCellValue(summary.getTotalCost() != null ? summary.getTotalCost().doubleValue() : 0);
        Row r2 = sheet.createRow(2);
        r2.createCell(0).setCellValue("人力成本");
        r2.createCell(1).setCellValue(summary.getLaborCost() != null ? summary.getLaborCost().doubleValue() : 0);
        Row r3 = sheet.createRow(3);
        r3.createCell(0).setCellValue("记录数");
        r3.createCell(1).setCellValue(summary.getRecordCount());
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createDimensionSheet(Sheet sheet, List<DimensionStatDTO> data, CellStyle headerStyle) {
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("维度"); header.getCell(0).setCellStyle(headerStyle);
        header.createCell(1).setCellValue("金额"); header.getCell(1).setCellStyle(headerStyle);
        header.createCell(2).setCellValue("占比(%)"); header.getCell(2).setCellStyle(headerStyle);
        if (data == null) return;
        for (int i = 0; i < data.size(); i++) {
            DimensionStatDTO d = data.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(d.getDimensionName());
            row.createCell(1).setCellValue(d.getAmount() != null ? d.getAmount().doubleValue() : 0);
            row.createCell(2).setCellValue(d.getPercentage() != null ? d.getPercentage() : 0);
        }
        for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);
    }
}
```

- [ ] **Step 4: 创建 CostAnalysisController**

```java
package com.library.backend.controller;

import com.library.backend.dto.*;
import com.library.backend.service.CostAnalysisService;
import com.library.backend.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/cost")
@RequiredArgsConstructor
public class CostAnalysisController {

    private final CostAnalysisService costAnalysisService;
    private final ExcelExportService excelExportService;

    @GetMapping("/summary")
    public CostSummaryDTO getSummary(CostQueryRequest request) {
        return costAnalysisService.getCostSummary(request);
    }

    @GetMapping("/trend")
    public List<DimensionStatDTO> getMonthlyTrend(@RequestParam(required = false) Integer year) {
        return costAnalysisService.getMonthlyTrend(year);
    }

    @GetMapping("/role")
    public List<DimensionStatDTO> getCostByRole() {
        return costAnalysisService.getCostByRole();
    }

    @GetMapping("/project")
    public List<ProjectCostDTO> getProjectCost(@RequestParam(required = false) Integer budgetYear) {
        return costAnalysisService.getProjectCost(budgetYear);
    }

    @GetMapping("/dimension")
    public List<DimensionStatDTO> getCostByDimension(
            @RequestParam String dimension,
            @RequestParam(required = false) Integer year) {
        return costAnalysisService.getCostByDimension(dimension, year);
    }

    @GetMapping("/export/summary")
    public ResponseEntity<byte[]> exportSummary(CostQueryRequest request) throws Exception {
        CostSummaryDTO summary = costAnalysisService.getCostSummary(request);
        byte[] bytes = excelExportService.exportCostSummary(summary);
        String filename = URLEncoder.encode("成本汇总报表.xlsx", StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @GetMapping("/export/project")
    public ResponseEntity<byte[]> exportProjectCost(@RequestParam(required = false) Integer budgetYear) throws Exception {
        List<ProjectCostDTO> projects = costAnalysisService.getProjectCost(budgetYear);
        byte[] bytes = excelExportService.exportProjectCost(projects);
        String filename = URLEncoder.encode("项目成本报表.xlsx", StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
```

- [ ] **Step 5: 启动后端验证 API**

Run: `cd library-backend && mvn spring-boot:run -q`
Expected: Started LibraryBackendApplication on port 8080

然后在新终端验证：
```bash
curl -s http://localhost:8080/api/cost/summary | head -c 200
```
Expected: JSON 响应含 `totalCost`、`laborCost`、`recordCount` 字段

- [ ] **Step 6: Commit**

```bash
cd library-backend
git add src/main/java/com/library/backend/config/ src/main/java/com/library/backend/service/ExcelExportService.java src/main/java/com/library/backend/controller/ src/main/java/com/library/backend/LibraryBackendApplication.java
git commit -m "feat: add REST controller, CORS config, Excel export, and main application"
```

---

## Task 6: 前端项目初始化与 API 层

**Files:**
- Create: `library-frontend/package.json`
- Create: `library-frontend/vite.config.ts`
- Create: `library-frontend/tsconfig.json`
- Create: `library-frontend/.env.development`
- Create: `library-frontend/index.html`
- Create: `library-frontend/src/main.tsx`
- Create: `library-frontend/src/api/client.ts`
- Create: `library-frontend/src/api/costApi.ts`
- Create: `library-frontend/src/types/cost.ts`

**Interfaces:**
- Consumes: Task 5 的后端 REST API 端点
- Produces: axios 客户端实例 + 类型化 API 请求封装，供 Task 7 页面组件消费

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "library-frontend",
  "private": true,
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc -b && vite build",
    "preview": "vite preview",
    "lint": "eslint src --ext .ts,.tsx"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.22.0",
    "antd": "^5.15.0",
    "echarts": "^5.5.0",
    "echarts-for-react": "^3.0.2",
    "axios": "^1.6.7",
    "dayjs": "^1.11.10"
  },
  "devDependencies": {
    "@types/react": "^18.2.56",
    "@types/react-dom": "^18.2.19",
    "@vitejs/plugin-react": "^4.2.1",
    "typescript": "^5.3.3",
    "vite": "^5.1.4"
  }
}
```

- [ ] **Step 2: 创建 vite.config.ts（含 proxy 代理后端）**

```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
```

- [ ] **Step 3: 创建 tsconfig.json**

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "baseUrl": ".",
    "paths": { "@/*": ["src/*"] }
  },
  "include": ["src"]
}
```

- [ ] **Step 4: 创建 .env.development 与 index.html**

`.env.development`:
```
VITE_API_BASE_URL=/api
```

`index.html`:
```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>成本分析报表</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
```

- [ ] **Step 5: 创建 main.tsx**

```typescript
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import 'antd/dist/reset.css'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
```

- [ ] **Step 6: 创建类型定义 src/types/cost.ts**

```typescript
export type EmployeeRole = 'DEVELOPER' | 'TESTER' | 'PRODUCT' | 'OPS'

export interface CostQueryRequest {
  departmentId?: number
  businessLineId?: number
  projectId?: number
  employeeId?: number
  costYear?: number
  costMonth?: number
  quarter?: number
  role?: EmployeeRole
}

export interface DimensionStat {
  dimensionName: string
  amount: number
  percentage: number
}

export interface CostSummary {
  totalCost: number
  laborCost: number
  recordCount: number
  byDepartment: DimensionStat[]
  byRole: DimensionStat[]
  byMonth: DimensionStat[]
}

export interface ProjectCost {
  projectId: number
  projectName: string
  budgetAmount: number | null
  actualCost: number
  budgetUsageRate: number
  overspendAmount: number
}
```

- [ ] **Step 7: 创建 axios 客户端 src/api/client.ts**

```typescript
import axios from 'axios'

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' },
})

client.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.message)
    return Promise.reject(error)
  },
)

export default client
```

- [ ] **Step 8: 创建 API 封装 src/api/costApi.ts**

```typescript
import client from './client'
import type { CostQueryRequest, CostSummary, DimensionStat, ProjectCost } from '../types/cost'

export async function getCostSummary(params: CostQueryRequest): Promise<CostSummary> {
  const { data } = await client.get<CostSummary>('/cost/summary', { params })
  return data
}

export async function getMonthlyTrend(year?: number): Promise<DimensionStat[]> {
  const { data } = await client.get<DimensionStat[]>('/cost/trend', { params: { year } })
  return data
}

export async function getCostByRole(): Promise<DimensionStat[]> {
  const { data } = await client.get<DimensionStat[]>('/cost/role')
  return data
}

export async function getProjectCost(budgetYear?: number): Promise<ProjectCost[]> {
  const { data } = await client.get<ProjectCost[]>('/cost/project', { params: { budgetYear } })
  return data
}

export async function getCostByDimension(dimension: string, year?: number): Promise<DimensionStat[]> {
  const { data } = await client.get<DimensionStat[]>('/cost/dimension', { params: { dimension, year } })
  return data
}

export function exportSummaryUrl(params: CostQueryRequest): string {
  const base = import.meta.env.VITE_API_BASE_URL || '/api'
  const query = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null) query.append(k, String(v))
  })
  return `${base}/cost/export/summary?${query.toString()}`
}

export function exportProjectCostUrl(budgetYear?: number): string {
  const base = import.meta.env.VITE_API_BASE_URL || '/api'
  return budgetYear
    ? `${base}/cost/export/project?budgetYear=${budgetYear}`
    : `${base}/cost/export/project`
}
```

- [ ] **Step 9: 安装依赖并验证构建**

Run: `cd library-frontend && pnpm install && pnpm build`
Expected: 编译成功无类型错误

- [ ] **Step 10: Commit**

```bash
cd library-frontend
git add -A
git commit -m "feat: init frontend project with Vite, React, Ant Design, ECharts, and API layer"
```

---

## Task 7: 前端页面与图表组件

**Files:**
- Create: `library-frontend/src/App.tsx`
- Create: `library-frontend/src/hooks/useCostData.ts`
- Create: `library-frontend/src/components/StatCard.tsx`
- Create: `library-frontend/src/components/CostTrendChart.tsx`
- Create: `library-frontend/src/components/RoleCostPie.tsx`
- Create: `library-frontend/src/components/ProjectBudgetBar.tsx`
- Create: `library-frontend/src/components/DimensionFilter.tsx`
- Create: `library-frontend/src/pages/Dashboard.tsx`
- Create: `library-frontend/src/pages/CostAnalysis.tsx`
- Create: `library-frontend/src/pages/ProjectCost.tsx`

**Interfaces:**
- Consumes: Task 6 的 `costApi.ts` 函数和 `cost.ts` 类型
- Produces: 完整可交互的前端页面（Dashboard / 成本分析 / 项目成本），含图表与导出按钮

- [ ] **Step 1: 创建 useCostData hook**

```typescript
// src/hooks/useCostData.ts
import { useState, useEffect, useCallback } from 'react'
import type { CostSummary, CostQueryRequest, DimensionStat, ProjectCost } from '../types/cost'
import * as costApi from '../api/costApi'

export function useCostSummary() {
  const [summary, setSummary] = useState<CostSummary | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetch = useCallback(async (params: CostQueryRequest) => {
    setLoading(true)
    setError(null)
    try {
      const data = await costApi.getCostSummary(params)
      setSummary(data)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : '获取数据失败')
    } finally {
      setLoading(false)
    }
  }, [])

  return { summary, loading, error, fetch }
}

export function useMonthlyTrend() {
  const [trend, setTrend] = useState<DimensionStat[]>([])
  const [loading, setLoading] = useState(false)
  const fetch = useCallback(async (year?: number) => {
    setLoading(true)
    try {
      setTrend(await costApi.getMonthlyTrend(year))
    } finally { setLoading(false) }
  }, [])
  return { trend, loading, fetch }
}

export function useProjectCost() {
  const [projects, setProjects] = useState<ProjectCost[]>([])
  const [loading, setLoading] = useState(false)
  const fetch = useCallback(async (year?: number) => {
    setLoading(true)
    try {
      setProjects(await costApi.getProjectCost(year))
    } finally { setLoading(false) }
  }, [])
  return { projects, loading, fetch }
}

export function useCostByRole() {
  const [roles, setRoles] = useState<DimensionStat[]>([])
  const fetch = useCallback(async () => {
    setRoles(await costApi.getCostByRole())
  }, [])
  useEffect(() => { fetch() }, [fetch])
  return { roles, fetch }
}
```

- [ ] **Step 2: 创建 StatCard 组件**

```tsx
// src/components/StatCard.tsx
import { Card, Statistic } from 'antd'

interface StatCardProps {
  title: string
  value: number
  precision?: number
  suffix?: string
  prefix?: string
  loading?: boolean
}

export default function StatCard({ title, value, precision = 2, suffix, prefix, loading }: StatCardProps) {
  return (
    <Card loading={loading}>
      <Statistic title={title} value={value} precision={precision} suffix={suffix} prefix={prefix} />
    </Card>
  )
}
```

- [ ] **Step 3: 创建 CostTrendChart 组件（月度趋势折线图）**

```tsx
// src/components/CostTrendChart.tsx
import ReactECharts from 'echarts-for-react'
import type { DimensionStat } from '../types/cost'

interface Props {
  data: DimensionStat[]
  loading?: boolean
}

export default function CostTrendChart({ data, loading }: Props) {
  const option = {
    title: { text: '月度成本趋势', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.map((d) => d.dimensionName) },
    yAxis: { type: 'value', name: '金额 (¥)' },
    series: [
      {
        name: '成本',
        type: 'line',
        smooth: true,
        data: data.map((d) => d.amount),
        areaStyle: { opacity: 0.3 },
      },
    ],
    grid: { left: '8%', right: '5%', bottom: '10%' },
  }
  return <ReactECharts option={option} style={{ height: 350 }} showLoading={loading} />
}
```

- [ ] **Step 4: 创建 RoleCostPie 组件（人力成本角色饼图）**

```tsx
// src/components/RoleCostPie.tsx
import ReactECharts from 'echarts-for-react'
import type { DimensionStat } from '../types/cost'

const ROLE_LABELS: Record<string, string> = {
  DEVELOPER: '开发',
  TESTER: '测试',
  PRODUCT: '产品',
  OPS: '运维',
}

interface Props {
  data: DimensionStat[]
  loading?: boolean
}

export default function RoleCostPie({ data, loading }: Props) {
  const option = {
    title: { text: '人力成本（按角色）', left: 'center' },
    tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        data: data.map((d) => ({
          name: ROLE_LABELS[d.dimensionName] ?? d.dimensionName,
          value: d.amount,
        })),
        label: { show: true, formatter: '{b}: {d}%' },
      },
    ],
  }
  return <ReactECharts option={option} style={{ height: 350 }} showLoading={loading} />
}
```

- [ ] **Step 5: 创建 ProjectBudgetBar 组件（预算 vs 实际消耗柱状图）**

```tsx
// src/components/ProjectBudgetBar.tsx
import ReactECharts from 'echarts-for-react'
import type { ProjectCost } from '../types/cost'

interface Props {
  data: ProjectCost[]
  loading?: boolean
}

export default function ProjectBudgetBar({ data, loading }: Props) {
  const option = {
    title: { text: '项目预算 vs 实际消耗', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0 },
    xAxis: { type: 'category', data: data.map((d) => d.projectName) },
    yAxis: { type: 'value', name: '金额 (¥)' },
    series: [
      { name: '预算', type: 'bar', data: data.map((d) => d.budgetAmount ?? 0) },
      { name: '实际消耗', type: 'bar', data: data.map((d) => d.actualCost) },
    ],
    grid: { left: '10%', right: '5%', bottom: '15%' },
  }
  return <ReactECharts option={option} style={{ height: 350 }} showLoading={loading} />
}
```

- [ ] **Step 6: 创建 DimensionFilter 组件（维度筛选器）**

```tsx
// src/components/DimensionFilter.tsx
import { Form, Select, InputNumber, Button, Space } from 'antd'
import { DownloadOutlined } from '@ant-design/icons'
import type { CostQueryRequest } from '../types/cost'

interface Props {
  onSearch: (params: CostQueryRequest) => void
  onExport: (params: CostQueryRequest) => void
}

const roleOptions = [
  { value: 'DEVELOPER', label: '开发' },
  { value: 'TESTER', label: '测试' },
  { value: 'PRODUCT', label: '产品' },
  { value: 'OPS', label: '运维' },
]

export default function DimensionFilter({ onSearch, onExport }: Props) {
  const [form] = Form.useForm<CostQueryRequest>()

  return (
    <Form form={form} layout="inline" onFinish={onSearch}
      initialValues={{ costYear: new Date().getFullYear() }}>
      <Form.Item name="departmentId" label="部门">
        <InputNumber placeholder="部门ID" min={1} />
      </Form.Item>
      <Form.Item name="businessLineId" label="业务线">
        <InputNumber placeholder="业务线ID" min={1} />
      </Form.Item>
      <Form.Item name="projectId" label="项目">
        <InputNumber placeholder="项目ID" min={1} />
      </Form.Item>
      <Form.Item name="costYear" label="年度">
        <InputNumber min={2020} max={2030} />
      </Form.Item>
      <Form.Item name="costMonth" label="月份">
        <InputNumber placeholder="1-12" min={1} max={12} />
      </Form.Item>
      <Form.Item name="role" label="角色">
        <Select allowClear placeholder="全部角色" options={roleOptions} style={{ width: 120 }} />
      </Form.Item>
      <Form.Item>
        <Space>
          <Button type="primary" htmlType="submit">查询</Button>
          <Button icon={<DownloadOutlined />} onClick={() => onExport(form.getFieldsValue())}>
            导出Excel
          </Button>
        </Space>
      </Form.Item>
    </Form>
  )
}
```

- [ ] **Step 7: 创建 Dashboard 页面**

```tsx
// src/pages/Dashboard.tsx
import { useEffect, useState } from 'react'
import { Row, Col, Card } from 'antd'
import StatCard from '../components/StatCard'
import CostTrendChart from '../components/CostTrendChart'
import RoleCostPie from '../components/RoleCostPie'
import ProjectBudgetBar from '../components/ProjectBudgetBar'
import { useCostSummary, useMonthlyTrend, useProjectCost, useCostByRole } from '../hooks/useCostData'

export default function Dashboard() {
  const { summary, loading, fetch: fetchSummary } = useCostSummary()
  const { trend, fetch: fetchTrend } = useMonthlyTrend()
  const { projects, fetch: fetchProjects } = useProjectCost()
  const { roles } = useCostByRole()
  const [year] = useState(new Date().getFullYear())

  useEffect(() => {
    fetchSummary({ costYear: year })
    fetchTrend(year)
    fetchProjects(year)
  }, [year, fetchSummary, fetchTrend, fetchProjects])

  return (
    <div style={{ padding: 24 }}>
      <h2>成本分析 Dashboard</h2>
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <StatCard title="总成本" value={summary?.totalCost ?? 0} prefix="¥" loading={loading} />
        </Col>
        <Col span={6}>
          <StatCard title="人力成本" value={summary?.laborCost ?? 0} prefix="¥" loading={loading} />
        </Col>
        <Col span={6}>
          <StatCard title="记录数" value={summary?.recordCount ?? 0} precision={0} loading={loading} />
        </Col>
        <Col span={6}>
          <StatCard title="统计年度" value={year} precision={0} loading={loading} />
        </Col>
      </Row>
      <Row gutter={16}>
        <Col span={12}>
          <Card><CostTrendChart data={trend} loading={loading} /></Card>
        </Col>
        <Col span={12}>
          <Card><RoleCostPie data={roles} loading={loading} /></Card>
        </Col>
      </Row>
      <Row gutter={16} style={{ marginTop: 16 }}>
        <Col span={24}>
          <Card><ProjectBudgetBar data={projects} loading={loading} /></Card>
        </Col>
      </Row>
    </div>
  )
}
```

- [ ] **Step 8: 创建 CostAnalysis 页面（多维度分析）**

```tsx
// src/pages/CostAnalysis.tsx
import { useState } from 'react'
import { Card, Table, Row, Col } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import DimensionFilter from '../components/DimensionFilter'
import CostTrendChart from '../components/CostTrendChart'
import RoleCostPie from '../components/RoleCostPie'
import { useCostSummary, useCostByRole } from '../hooks/useCostData'
import { exportSummaryUrl } from '../api/costApi'
import type { CostQueryRequest, DimensionStat } from '../types/cost'

export default function CostAnalysis() {
  const { summary, loading, fetch } = useCostSummary()
  const { roles } = useCostByRole()
  const [trendData, setTrendData] = useState<DimensionStat[]>([])

  const handleSearch = (params: CostQueryRequest) => {
    fetch(params)
    if (summary?.byMonth) setTrendData(summary.byMonth)
  }

  const handleExport = (params: CostQueryRequest) => {
    window.open(exportSummaryUrl(params), '_blank')
  }

  const columns: ColumnsType<DimensionStat> = [
    { title: '维度', dataIndex: 'dimensionName', key: 'dimensionName' },
    { title: '金额 (¥)', dataIndex: 'amount', key: 'amount', render: (v: number) => v.toFixed(2) },
    { title: '占比 (%)', dataIndex: 'percentage', key: 'percentage', render: (v: number) => v.toFixed(2) },
  ]

  return (
    <div style={{ padding: 24 }}>
      <h2>成本统计分析</h2>
      <Card style={{ marginBottom: 16 }}>
        <DimensionFilter onSearch={handleSearch} onExport={handleExport} />
      </Card>
      <Row gutter={16}>
        <Col span={12}>
          <Card title="部门维度"><Table columns={columns} dataSource={summary?.byDepartment ?? []} rowKey="dimensionName" loading={loading} size="small" /></Card>
        </Col>
        <Col span={12}>
          <Card title="月度维度"><Table columns={columns} dataSource={summary?.byMonth ?? []} rowKey="dimensionName" loading={loading} size="small" /></Card>
        </Col>
      </Row>
      <Row gutter={16} style={{ marginTop: 16 }}>
        <Col span={12}><Card><CostTrendChart data={trendData} loading={loading} /></Card></Col>
        <Col span={12}><Card><RoleCostPie data={roles} loading={loading} /></Card></Col>
      </Row>
    </div>
  )
}
```

- [ ] **Step 9: 创建 ProjectCost 页面（预算/实际/占比/超支 + 导出）**

```tsx
// src/pages/ProjectCost.tsx
import { useEffect, useState } from 'react'
import { Card, Table, Button, Tag } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { DownloadOutlined } from '@ant-design/icons'
import ProjectBudgetBar from '../components/ProjectBudgetBar'
import { useProjectCost } from '../hooks/useCostData'
import { exportProjectCostUrl } from '../api/costApi'
import type { ProjectCost } from '../types/cost'

export default function ProjectCostPage() {
  const { projects, loading, fetch } = useProjectCost()
  const [year] = useState(new Date().getFullYear())

  useEffect(() => { fetch(year) }, [year, fetch])

  const columns: ColumnsType<ProjectCost> = [
    { title: '项目ID', dataIndex: 'projectId', key: 'projectId' },
    { title: '项目名称', dataIndex: 'projectName', key: 'projectName' },
    { title: '预算金额 (¥)', dataIndex: 'budgetAmount', key: 'budgetAmount', render: (v: number | null) => (v ?? 0).toFixed(2) },
    { title: '实际消耗 (¥)', dataIndex: 'actualCost', key: 'actualCost', render: (v: number) => v.toFixed(2) },
    { title: '预算占比 (%)', dataIndex: 'budgetUsageRate', key: 'budgetUsageRate',
      render: (v: number) => <Tag color={v > 100 ? 'red' : v > 80 ? 'orange' : 'green'}>{v.toFixed(2)}%</Tag> },
    { title: '预计超支 (¥)', dataIndex: 'overspendAmount', key: 'overspendAmount',
      render: (v: number) => v > 0 ? <Tag color="red">{v.toFixed(2)}</Tag> : '—' },
  ]

  return (
    <div style={{ padding: 24 }}>
      <h2>项目成本明细
        <Button icon={<DownloadOutlined />} type="primary" style={{ marginLeft: 16 }}
          onClick={() => window.open(exportProjectCostUrl(year), '_blank')}>
          导出Excel
        </Button>
      </h2>
      <Card style={{ marginBottom: 16 }}><ProjectBudgetBar data={projects} loading={loading} /></Card>
      <Card>
        <Table columns={columns} dataSource={projects} rowKey="projectId" loading={loading} />
      </Card>
    </div>
  )
}
```

- [ ] **Step 10: 创建 App.tsx（路由与布局）**

```tsx
// src/App.tsx
import { BrowserRouter, Routes, Route, Link, Navigate } from 'react-router-dom'
import { Layout, Menu } from 'antd'
import Dashboard from './pages/Dashboard'
import CostAnalysis from './pages/CostAnalysis'
import ProjectCostPage from './pages/ProjectCost'

const { Header, Content } = Layout

export default function App() {
  return (
    <BrowserRouter>
      <Layout style={{ minHeight: '100vh' }}>
        <Header>
          <Menu theme="dark" mode="horizontal" defaultSelectedKeys={['dashboard']}>
            <Menu.Item key="dashboard"><Link to="/dashboard">Dashboard</Link></Menu.Item>
            <Menu.Item key="analysis"><Link to="/analysis">成本分析</Link></Menu.Item>
            <Menu.Item key="project"><Link to="/project">项目成本</Link></Menu.Item>
          </Menu>
        </Header>
        <Content>
          <Routes>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/analysis" element={<CostAnalysis />} />
            <Route path="/project" element={<ProjectCostPage />} />
          </Routes>
        </Content>
      </Layout>
    </BrowserRouter>
  )
}
```

- [ ] **Step 11: 安装缺失依赖 @ant-design/icons 并验证构建**

Run: `cd library-frontend && pnpm add @ant-design/icons && pnpm build`
Expected: 编译成功

- [ ] **Step 12: 启动前端 dev server 验证页面可加载**

Run: `cd library-frontend && pnpm dev`
Expected: Vite dev server 启动在 http://localhost:5173，浏览器访问可见 Dashboard 页面

- [ ] **Step 13: Commit**

```bash
cd library-frontend
git add -A
git commit -m "feat: add cost analysis dashboard, analysis page, project cost page with ECharts and export"
```
