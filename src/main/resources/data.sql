INSERT INTO app_user (id, user_id, user_name, user_type, user_level, department) VALUES
  (1, 'U001', '张三', 'STUDENT', 'L1', '研发部'),
  (2, 'U002', '李四', 'TEACHER', 'L2', '产品部'),
  (3, 'U003', '王五', 'ADMIN',   'L3', '运营部'),
  (4, 'U004', '赵六', 'STUDENT', 'L2', '研发部'),
  (5, 'U005', '钱七', 'TEACHER', 'L1', '运营部')
ON CONFLICT (id) DO NOTHING;
