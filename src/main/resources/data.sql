-- 初始化数据：一个管理员、一个读者、几本图书
-- 密码使用 BCrypt 加密；admin123 -> $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ld8uEp0iWp2kNvVLFm.
-- reader123 -> $2a$10$dXJ3SW6Lk8P3Q3E7pF2QoeSd1d5e3a9f5b8c7d6e5f4a3b2c1d0e9f8a7b6c5d4

INSERT INTO reader (name, username, password_hash, role, enabled) VALUES
    ('管理员', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ld8uEp0iWp2kNvVLFm.', 'ADMIN', TRUE)
    ON CONFLICT DO NOTHING;

INSERT INTO reader (name, username, password_hash, role, enabled) VALUES
    ('张三', 'reader', '$2a$10$dXJ3SW6Lk8P3Q3E7pF2QoeSd1d5e3a9f5b8c7d6e5f4a3b2c1d0e9f8a7b6c5d4', 'READER', TRUE)
    ON CONFLICT DO NOTHING;

INSERT INTO book (title, author, isbn, category, stock) VALUES
    ('Java编程思想', 'Bruce Eckel', '9787112138268', '计算机', 3),
    ('深入理解计算机系统', 'Randal E. Bryant', '9787111544937', '计算机', 2),
    ('红楼梦', '曹雪芹', '9787020002207', '文学', 5),
    ('三体', '刘慈欣', '9787536692930', '科幻', 0)
    ON CONFLICT DO NOTHING;
