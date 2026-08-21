-- H2 2.x reserves MONTH as a keyword; allow it as the column identifier (matches brief schema verbatim otherwise).
SET NON_KEYWORDS MONTH;

DROP TABLE IF EXISTS labor_cost;
DROP TABLE IF EXISTS project_cost;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS business_line;

CREATE TABLE department (
  id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64)  NOT NULL,
  code VARCHAR(32)  NOT NULL UNIQUE
);

CREATE TABLE business_line (
  id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64)  NOT NULL,
  code VARCHAR(32)  NOT NULL UNIQUE
);

CREATE TABLE project (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  name            VARCHAR(128) NOT NULL,
  code            VARCHAR(32)  NOT NULL UNIQUE,
  business_line_id BIGINT      NOT NULL,
  department_id   BIGINT       NOT NULL,
  budget_amount   DECIMAL(12,2) NOT NULL DEFAULT 0
);

CREATE TABLE employee (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(64) NOT NULL,
  employee_no   VARCHAR(32) NOT NULL UNIQUE,
  department_id BIGINT      NOT NULL,
  role          VARCHAR(16) NOT NULL
);

CREATE TABLE labor_cost (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  employee_id BIGINT       NOT NULL,
  project_id  BIGINT       NOT NULL,
  month       VARCHAR(7)   NOT NULL,
  amount      DECIMAL(12,2) NOT NULL DEFAULT 0
);

CREATE TABLE project_cost (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id   BIGINT       NOT NULL,
  month        VARCHAR(7)   NOT NULL,
  actual_amount DECIMAL(12,2) NOT NULL DEFAULT 0
);

CREATE INDEX idx_labor_month ON labor_cost(month);
CREATE INDEX idx_project_cost_month ON project_cost(month);