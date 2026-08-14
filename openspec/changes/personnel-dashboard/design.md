# Personnel Dashboard — Design (Backend)

## Technology Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Build**: Maven
- **Database**: MySQL 8.x
- **ORM**: Spring Data JPA
- **Migration**: Flyway
- **File parsing**: Apache POI (Excel) + OpenCSV (CSV)

## Package Structure

```
com.library.personnel
├── controller
│   ├── EmployeeController.java
│   ├── BudgetController.java
│   ├── ImportController.java
│   └── WhitelistController.java
├── service
│   ├── EmployeeService.java
│   ├── BudgetService.java
│   ├── BudgetApprovalService.java
│   ├── ImportService.java
│   └── WhitelistService.java
├── repository
│   ├── EmployeeRepository.java
│   ├── BudgetRepository.java
│   └── WhitelistRepository.java
├── entity
│   ├── Employee.java
│   ├── Budget.java
│   └── Whitelist.java
├── enums
│   ├── EmployeeStatus.java
│   ├── BudgetPeriod.java
│   └── WhitelistType.java
├── dto
│   ├── request
│   │   ├── EmployeeRequest.java
│   │   ├── BudgetRequest.java
│   │   ├── WhitelistRequest.java
│   │   └── ImportResult.java
│   ├── response
│   │   ├── EmployeeResponse.java
│   │   ├── BudgetResponse.java
│   │   ├── BudgetSummaryResponse.java
│   │   └── WhitelistResponse.java
├── exception
│   └── GlobalExceptionHandler.java
└── config
    └── WebConfig.java
```

## Data Model

### Employee Table

```sql
CREATE TABLE employee (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id VARCHAR(20) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  department VARCHAR(100) NOT NULL,
  position VARCHAR(100) NOT NULL,
  phone VARCHAR(20),
  email VARCHAR(100),
  hire_date DATE NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  deleted BOOLEAN DEFAULT FALSE,
  INDEX idx_employee_id (employee_id),
  INDEX idx_name (name),
  INDEX idx_department (department),
  INDEX idx_status (status)
);
```

### Budget Table

```sql
CREATE TABLE budget (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id VARCHAR(20) NOT NULL,
  year INT NOT NULL,
  quarter INT,
  month INT,
  budget_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  used_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  note VARCHAR(255),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
  UNIQUE KEY uk_employee_period (employee_id, year, quarter, month),
  INDEX idx_budget_employee (employee_id),
  INDEX idx_budget_year (year)
);
```

### Whitelist Table

```sql
CREATE TABLE whitelist (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  type VARCHAR(20) NOT NULL,
  employee_id VARCHAR(20) NOT NULL,
  name VARCHAR(100) NOT NULL,
  note VARCHAR(255),
  added_at DATETIME NOT NULL,
  UNIQUE KEY uk_type_employee (type, employee_id),
  INDEX idx_whitelist_type (type),
  INDEX idx_whitelist_employee (employee_id)
);
```

## Response Format

### Success
```json
{
  "code": 0,
  "data": { ... },
  "message": "success"
}
```

### Paginated List
```json
{
  "code": 0,
  "data": {
    "content": [ ... ],
    "totalElements": 100,
    "totalPages": 5,
    "number": 0,
    "size": 20
  },
  "message": "success"
}
```

### Budget Summary
```json
{
  "code": 0,
  "data": {
    "employeeId": "EMP000001",
    "year": 2025,
    "totalBudget": 120000.00,
    "totalUsed": 45000.00,
    "totalRemaining": 75000.00,
    "quarters": [
      { "quarter": 1, "budget": 30000.00, "used": 15000.00, "remaining": 15000.00 },
      { "quarter": 2, "budget": 30000.00, "used": 10000.00, "remaining": 20000.00 },
      { "quarter": 3, "budget": 30000.00, "used": 0.00, "remaining": 30000.00 },
      { "quarter": 4, "budget": 30000.00, "used": 20000.00, "remaining": 10000.00 }
    ]
  },
  "message": "success"
}
```

### Import Result
```json
{
  "code": 0,
  "data": {
    "successCount": 8,
    "failureCount": 2,
    "failures": [
      { "row": 3, "error": "Not in import whitelist" },
      { "row": 7, "error": "Missing required field: name" }
    ]
  },
  "message": "success"
}
```

### Error
```json
{
  "code": 40001,
  "data": null,
  "message": "Validation error description"
}
```

## Cross-repo API Contract

See [frontend design](../library-frontend-main/openspec/changes/personnel-dashboard/design.md) for API contract details.

Alignment points:
- API prefix: `/api`
- Date format: `yyyy-MM-dd`
- DateTime format: `yyyy-MM-dd'T'HH:mm:ss`
- Page param: 0-based
- Error code convention: 0 = success, 4xxxx = client error, 5xxxx = server error
- Budget threshold for approval: ¥10,000 (configurable)