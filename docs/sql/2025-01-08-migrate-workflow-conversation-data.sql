-- ============================================================
-- AI工作流对话记录领域隔离 - 数据迁移SQL
-- 日期: 2025-01-08
-- 目的: 将workflow对话记录从ai_conversation_content迁移到ai_workflow_conversation_content
-- 识别规则: conversation_id LIKE '%::%::%' (格式: tenantCode::workflowUuid::userId)
-- ============================================================

-- ============================================================
-- 步骤1: 统计待迁移数据量（重要：执行前查看）
-- ============================================================

SELECT
    '待迁移workflow记录总数' AS description,
    COUNT(*) AS record_count
FROM ai_conversation_content
WHERE conversation_id LIKE '%::%::%';

-- ============================================================
-- 步骤2: 迁移数据到新表
-- ============================================================
-- 注意：此操作会复制所有符合条件的记录到新表
-- 建议：在低流量时段执行

INSERT INTO ai_workflow_conversation_content (
    id,
    message_id,
    conversation_id,
    type,
    content,
    model_source_id,
    c_time,
    u_time,
    c_id,
    u_id,
    dbversion,
    provider_name,
    base_name
)
SELECT
    id,
    message_id,
    conversation_id,
    type,
    content,
    model_source_id,
    c_time,
    u_time,
    c_id,
    u_id,
    dbversion,
    provider_name,
    base_name
FROM ai_conversation_content
WHERE conversation_id LIKE '%::%::%';

-- ============================================================
-- 步骤3: 验证迁移数量
-- ============================================================
-- 确保迁移前后数量一致

SELECT
    '原表workflow记录数' AS source_table,
    COUNT(*) AS record_count
FROM ai_conversation_content
WHERE conversation_id LIKE '%::%::%'

UNION ALL

SELECT
    '新表记录总数' AS destination_table,
    COUNT(*) AS record_count
FROM ai_workflow_conversation_content;

-- ============================================================
-- 步骤4: 数据一致性抽样检查
-- ============================================================
-- 随机抽取3条记录对比内容

SELECT
    'ai_conversation_content' AS source_table,
    id,
    message_id,
    conversation_id,
    LEFT(content, 50) AS content_preview,
    c_time
FROM ai_conversation_content
WHERE conversation_id LIKE '%::%::%'
ORDER BY RAND()
LIMIT 3;

SELECT
    'ai_workflow_conversation_content' AS target_table,
    id,
    message_id,
    conversation_id,
    LEFT(content, 50) AS content_preview,
    c_time
FROM ai_workflow_conversation_content
ORDER BY RAND()
LIMIT 3;

-- ============================================================
-- 步骤5: 删除原表中的workflow数据（谨慎执行）
-- ============================================================
-- ⚠️ 警告：此操作不可逆！
-- 确保步骤3验证通过后再执行

-- DELETE FROM ai_conversation_content
-- WHERE conversation_id LIKE '%::%::%';

-- ============================================================
-- 步骤6: 删除后验证
-- ============================================================
-- 执行删除后，验证原表中已无workflow记录

-- SELECT
--     '原表剩余workflow记录数' AS description,
--     COUNT(*) AS record_count
-- FROM ai_conversation_content
-- WHERE conversation_id LIKE '%::%::%';
--
-- -- 预期结果: record_count = 0

-- ============================================================
-- 回滚方案（如需回滚）
-- ============================================================
-- 如果迁移出现问题，可以使用以下SQL回滚：

-- -- 1. 从新表恢复数据到原表
-- INSERT INTO ai_conversation_content (
--     id,
--     message_id,
--     conversation_id,
--     type,
--     content,
--     model_source_id,
--     c_time,
--     u_time,
--     c_id,
--     u_id,
--     dbversion,
--     provider_name,
--     base_name
-- )
-- SELECT
--     id,
--     message_id,
--     conversation_id,
--     type,
--     content,
--     model_source_id,
--     c_time,
--     u_time,
--     c_id,
--     u_id,
--     dbversion,
--     provider_name,
--     base_name
-- FROM ai_workflow_conversation_content;
--
-- -- 2. 清空新表
-- TRUNCATE TABLE ai_workflow_conversation_content;

-- ============================================================
-- 执行建议
-- ============================================================
-- 1. 在测试环境完整执行一遍
-- 2. 备份数据库（mysqldump）
-- 3. 选择低流量时段执行
-- 4. 逐步执行，每步验证
-- 5. 步骤5删除操作最后执行，需二次确认
