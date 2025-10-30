-- ================================================
-- 工作流字段重命名：解决JSqlParser保留字冲突
-- input -> input_data
-- output -> output_data
-- ================================================
-- 创建日期: 2025-10-30
-- 执行环境: scm_tenant_20250519_001
-- 执行说明: 在测试环境执行，验证通过后再在生产环境执行
-- ================================================

-- 1. 清空测试数据
TRUNCATE TABLE ai_workflow_runtime_node;
TRUNCATE TABLE ai_workflow_runtime;

-- 2. 修改 ai_workflow_runtime 表
ALTER TABLE ai_workflow_runtime
  CHANGE COLUMN `input` `input_data` json COMMENT '输入数据(JSON格式)',
  CHANGE COLUMN `output` `output_data` json COMMENT '输出数据(JSON格式)';

-- 3. 修改 ai_workflow_runtime_node 表
ALTER TABLE ai_workflow_runtime_node
  CHANGE COLUMN `input` `input_data` json COMMENT '节点输入数据(JSON格式)',
  CHANGE COLUMN `output` `output_data` json COMMENT '节点输出数据(JSON格式)';

-- 4. 验证修改结果
SELECT
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    COLUMN_COMMENT
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'scm_tenant_20250519_001'
  AND TABLE_NAME IN ('ai_workflow_runtime', 'ai_workflow_runtime_node')
  AND COLUMN_NAME IN ('input_data', 'output_data')
ORDER BY TABLE_NAME, ORDINAL_POSITION;
