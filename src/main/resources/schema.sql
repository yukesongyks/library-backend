-- Schema for api_call_log table (JPA will auto-create, this is for reference)
CREATE TABLE IF NOT EXISTS api_call_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     VARCHAR(64)  NOT NULL,
    user_type   VARCHAR(32)  NOT NULL,
    level       VARCHAR(32),
    department  VARCHAR(64),
    api_name    VARCHAR(32)  NOT NULL,
    called_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_api_call_log_dimension ON api_call_log(user_type, level, department, api_name, called_at);
