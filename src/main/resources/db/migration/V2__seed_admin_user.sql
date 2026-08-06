-- V2: 初始化管理员账号
-- 密码: admin123 (BCrypt 加密，rounds=10)
-- 此哈希由 Spring Security BCryptPasswordEncoder 生成，可通过 BCryptPasswordEncoder.matches("admin123", hash) 验证
INSERT INTO `user` (`username`, `password`, `role`) VALUES
    ('admin', '$2b$10$8y3/O1XNVT0/IoATEivEae5bJoYqkgXK.50HYZrfiOM1G7BfJpts.', 'ROLE_ADMIN'),
    ('reader', '$2b$10$8y3/O1XNVT0/IoATEivEae5bJoYqkgXK.50HYZrfiOM1G7BfJpts.', 'ROLE_READER');
