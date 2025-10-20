-- ============================================
-- 删除 ai_config 表的 create_time 和 update_time 字段
-- 执行方式：在MySQL客户端中执行此SQL
-- ============================================

USE scm_tenant_20250519_001;

-- 删除字段
ALTER TABLE ai_config
DROP COLUMN create_time,
DROP COLUMN update_time;

-- 验证表结构
SHOW COLUMNS FROM ai_config;
