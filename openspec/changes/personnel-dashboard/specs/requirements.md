# Personnel Dashboard — Requirements (Backend API)

See [frontend spec](../library-frontend-main/openspec/changes/personnel-dashboard/specs/requirements.md) for full user-facing requirements.

## API-Specific Requirements

### R1: Paginated Employee List

- Default page size: 20
- Max page size: 100
- Search matches on `name` and `department` fields (LIKE query)
- Response includes total count for pagination

### R2: Employee Creation Validation

- `employeeId` auto-generated with prefix "EMP" + 6-digit sequential number
- `name`, `department`, `position`, `hireDate` are required
- `status` defaults to `ACTIVE`

### R3: Budget Management

- Budget records are linked to employee via `employeeId`
- Each budget record has year, optional quarter, optional month
- Budget summary endpoint aggregates: monthly → quarterly → annual rollup
- `usedAmount` updates cascade: updating a monthly budget updates the parent quarter and year
- Budget changes over ¥10,000 require approval from an APPROVAL whitelist member

### R4: Batch Import

- Accepts `multipart/form-data` file upload
- Supported formats: CSV, .xlsx, .xls
- File size limit: 10MB
- Each row validated against IMPORT whitelist before import
- Transactional: all-or-nothing per file (or report partial failures)
- Returns import result summary

### R5: Dual Whitelist

- **IMPORT type**: Used for batch import validation
- **APPROVAL type**: Used for budget change approval
- Whitelist entries are unique by `(type, employee_id)` combination
- Check endpoint: `GET /api/whitelist/check?employeeId=X&type=IMPORT` returns boolean