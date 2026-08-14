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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;