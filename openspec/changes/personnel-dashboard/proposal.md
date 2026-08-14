# Proposal: Personnel Dashboard (人员看板) — Backend

## Intent

Build the backend for an independent personnel dashboard system, providing employee CRUD, batch import with whitelist validation, multi-dimensional cost budget tracking (annual/quarterly/monthly), and dual whitelist management (import + budget approval).

## Scope

- Employee entity CRUD REST API
- Batch import endpoint (CSV/Excel) with import whitelist validation
- Budget management API (annual/quarterly/monthly dimensions with rollup)
- Dual whitelist management API (IMPORT + APPROVAL types)
- Budget approval check via approval whitelist
- Database migrations for `employee`, `budget`, `whitelist` tables

## Non-goals

- Advanced HR features (attendance, payroll, performance review)
- Full RBAC (deferred to future)
- Real-time sync with external HR systems

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/employees` | List employees (paginated) |
| GET | `/api/employees/{id}` | Get employee detail |
| POST | `/api/employees` | Create employee |
| PUT | `/api/employees/{id}` | Update employee |
| DELETE | `/api/employees/{id}` | Soft-delete employee |
| GET | `/api/employees/{id}/budgets` | List budgets for employee |
| POST | `/api/employees/{id}/budgets` | Create budget record |
| PUT | `/api/employees/{id}/budgets/{budgetId}` | Update budget (needs approval if over threshold) |
| GET | `/api/employees/{id}/budgets/summary?year=2025` | Budget summary rollup |
| POST | `/api/employees/import` | Batch import |
| GET | `/api/employees/import/template` | Download template |
| GET | `/api/whitelist` | List whitelist (filterable by type) |
| POST | `/api/whitelist` | Add to whitelist |
| POST | `/api/whitelist/batch` | Batch add |
| DELETE | `/api/whitelist/{id}` | Remove from whitelist |
| GET | `/api/whitelist/check` | Check if employee is on whitelist |

## Risk

- **Low**: Independent system, no existing functionality affected
- **Medium**: Large file upload for batch import; need size limits and streaming
- **Medium**: Budget rollup complexity (monthly→quarterly→annual aggregation)