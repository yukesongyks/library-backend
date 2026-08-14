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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;