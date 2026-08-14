# Personnel Dashboard — Tasks (Backend)

## Data Layer

- [ ] **B1**: Create Flyway migration V1 for `employee` table
- [ ] **B2**: Create Flyway migration V2 for `budget` table
- [ ] **B3**: Create Flyway migration V3 for `whitelist` table (with type field)
- [ ] **B4**: Implement `Employee` entity with JPA annotations
- [ ] **B5**: Implement `Budget` entity with JPA annotations
- [ ] **B6**: Implement `Whitelist` entity (IMPORT/APPROVAL enum) with JPA annotations
- [ ] **B7**: Implement `EmployeeRepository` (custom queries for search)
- [ ] **B8**: Implement `BudgetRepository` (queries by employee, year, period)
- [ ] **B9**: Implement `WhitelistRepository` (queries by type, employeeId)

## Service Layer

- [ ] **B10**: Implement `EmployeeService` (CRUD, employeeId generation, soft delete)
- [ ] **B11**: Implement `BudgetService` (CRUD, rollup aggregation, cascade update)
- [ ] **B12**: Implement `BudgetApprovalService` (check approval whitelist, threshold logic)
- [ ] **B13**: Implement `WhitelistService` (dual type: add, remove, batch, check)
- [ ] **B14**: Implement `ImportService` (CSV/Excel parsing, import whitelist validation, result)

## Controller Layer

- [ ] **B15**: Implement `EmployeeController` (list, get, create, update, delete)
- [ ] **B16**: Implement `BudgetController` (list, create, update, summary)
- [ ] **B17**: Implement `ImportController` (upload, template download)
- [ ] **B18**: Implement `WhitelistController` (list, add, batch add, delete, check)
- [ ] **B19**: Implement `GlobalExceptionHandler` and consistent error response

## DTO & Config

- [ ] **B20**: Create request/response DTOs
- [ ] **B21**: Configure file upload limits (10MB) and CORS
- [ ] **B22**: Configure budget approval threshold (default ¥10,000)

## Testing

- [ ] **B23**: Unit tests for EmployeeService
- [ ] **B24**: Unit tests for BudgetService (rollup, cascade)
- [ ] **B25**: Unit tests for BudgetApprovalService
- [ ] **B26**: Unit tests for ImportService (with mock file data)
- [ ] **B27**: Unit tests for WhitelistService
- [ ] **B28**: Integration tests for all controllers
- [ ] **B29**: Test batch import with valid/invalid/mixed whitelist data
- [ ] **B30**: Test budget approval workflow