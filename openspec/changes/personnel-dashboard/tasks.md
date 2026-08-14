# Personnel Dashboard — Tasks (Backend)

## Data Layer

- [x] **B1**: Create Flyway migration V1 for `employee` table
- [x] **B2**: Create Flyway migration V2 for `budget` table
- [x] **B3**: Create Flyway migration V3 for `whitelist` table (with type field)
- [x] **B4**: Implement `Employee` entity with JPA annotations
- [x] **B5**: Implement `Budget` entity with JPA annotations
- [x] **B6**: Implement `Whitelist` entity (IMPORT/APPROVAL enum) with JPA annotations
- [x] **B7**: Implement `EmployeeRepository` (custom queries for search)
- [x] **B8**: Implement `BudgetRepository` (queries by employee, year, period)
- [x] **B9**: Implement `WhitelistRepository` (queries by type, employeeId)

## Service Layer

- [x] **B10**: Implement `EmployeeService` (CRUD, employeeId generation, soft delete)
- [x] **B11**: Implement `BudgetService` (CRUD, rollup aggregation, cascade update)
- [x] **B12**: Implement `BudgetApprovalService` (check approval whitelist, threshold logic)
- [x] **B13**: Implement `WhitelistService` (dual type: add, remove, batch, check)
- [x] **B14**: Implement `ImportService` (CSV/Excel parsing, import whitelist validation, result)

## Controller Layer

- [x] **B15**: Implement `EmployeeController` (list, get, create, update, delete)
- [x] **B16**: Implement `BudgetController` (list, create, update, summary)
- [x] **B17**: Implement `ImportController` (upload, template download)
- [x] **B18**: Implement `WhitelistController` (list, add, batch add, delete, check)
- [x] **B19**: Implement `GlobalExceptionHandler` and consistent error response

## DTO & Config

- [x] **B20**: Create request/response DTOs
- [x] **B21**: Configure file upload limits (10MB) and CORS
- [x] **B22**: Configure budget approval threshold (default ¥10,000)

## Testing

- [x] **B23**: Unit tests for EmployeeService
- [x] **B24**: Unit tests for BudgetService (rollup, cascade)
- [x] **B25**: Unit tests for BudgetApprovalService
- [x] **B26**: Unit tests for ImportService (with mock file data)
- [x] **B27**: Unit tests for WhitelistService
- [x] **B28**: Integration tests for all controllers
- [x] **B29**: Test batch import with valid/invalid/mixed whitelist data
- [x] **B30**: Test budget approval workflow