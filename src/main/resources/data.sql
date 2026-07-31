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
