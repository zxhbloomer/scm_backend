-- ================================================================
-- Neo4j知识图谱功能清理SQL脚本
--
-- 目的：删除所有与Neo4j知识图谱相关的数据库表和字段
-- 数据库：scm_tenant_20250519_001
-- 执行时间：请在确认备份后执行
--
-- 警告：此操作不可逆，请务必先备份数据库！
-- ================================================================

USE scm_tenant_20250519_001;

-- ----------------------------------------------------------------
-- 第一部分：删除图谱相关表（2张表）
-- ----------------------------------------------------------------

-- 1. 删除图谱段数据表
-- 说明：存储知识库文档的图谱实体和关系数据
DROP TABLE IF EXISTS `ai_knowledge_base_graph_segment`;

-- 2. 删除QA图谱引用表
-- 说明：存储问答记录中引用的图谱实体和关系
DROP TABLE IF EXISTS `ai_knowledge_base_qa_ref_graph`;

-- ----------------------------------------------------------------
-- 第二部分：删除知识库表中的图谱相关字段（2个字段）
-- ----------------------------------------------------------------

-- 检查字段是否存在，然后删除
-- 3. 删除实体数量字段
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'scm_tenant_20250519_001'
    AND TABLE_NAME = 'ai_knowledge_base'
    AND COLUMN_NAME = 'entity_count'
);

SET @sql_statement = IF(
    @column_exists > 0,
    'ALTER TABLE `ai_knowledge_base` DROP COLUMN `entity_count`',
    'SELECT "Column entity_count does not exist" AS result'
);

PREPARE stmt FROM @sql_statement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4. 删除关系数量字段
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'scm_tenant_20250519_001'
    AND TABLE_NAME = 'ai_knowledge_base'
    AND COLUMN_NAME = 'relation_count'
);

SET @sql_statement = IF(
    @column_exists > 0,
    'ALTER TABLE `ai_knowledge_base` DROP COLUMN `relation_count`',
    'SELECT "Column relation_count does not exist" AS result'
);

PREPARE stmt FROM @sql_statement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------------------------------------------
-- 第三部分：删除知识库文档项表中的图谱相关字段（2个字段）
-- ----------------------------------------------------------------

-- 5. 删除图谱化状态字段
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'scm_tenant_20250519_001'
    AND TABLE_NAME = 'ai_knowledge_base_item'
    AND COLUMN_NAME = 'graphical_status'
);

SET @sql_statement = IF(
    @column_exists > 0,
    'ALTER TABLE `ai_knowledge_base_item` DROP COLUMN `graphical_status`',
    'SELECT "Column graphical_status does not exist" AS result'
);

PREPARE stmt FROM @sql_statement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 6. 删除图谱化状态变更时间字段
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'scm_tenant_20250519_001'
    AND TABLE_NAME = 'ai_knowledge_base_item'
    AND COLUMN_NAME = 'graphical_status_change_time'
);

SET @sql_statement = IF(
    @column_exists > 0,
    'ALTER TABLE `ai_knowledge_base_item` DROP COLUMN `graphical_status_change_time`',
    'SELECT "Column graphical_status_change_time does not exist" AS result'
);

PREPARE stmt FROM @sql_statement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------------------------------------------
-- 清理完成
-- ----------------------------------------------------------------

SELECT '===================================================' AS '';
SELECT 'Neo4j知识图谱功能清理完成！' AS '清理结果';
SELECT '===================================================' AS '';
SELECT '已删除对象统计：' AS '';
SELECT '- 数据表：2张 (ai_knowledge_base_graph_segment, ai_knowledge_base_qa_ref_graph)' AS '';
SELECT '- 知识库字段：2个 (entity_count, relation_count)' AS '';
SELECT '- 文档项字段：2个 (graphical_status, graphical_status_change_time)' AS '';
SELECT '===================================================' AS '';

-- ================================================================
-- 执行说明：
--
-- 1. 备份数据库
--    mysqldump -u root -p scm_tenant_20250519_001 > backup_before_neo4j_cleanup.sql
--
-- 2. 执行此脚本
--    mysql -u root -p scm_tenant_20250519_001 < neo4j-cleanup.sql
--
-- 3. 验证清理结果
--    - 确认表已删除：SHOW TABLES LIKE 'ai_knowledge_base_graph%';
--    - 确认表已删除：SHOW TABLES LIKE 'ai_knowledge_base_qa_ref_graph';
--    - 确认字段已删除：DESC ai_knowledge_base;
--    - 确认字段已删除：DESC ai_knowledge_base_item;
--
-- 4. 重启应用
--    确保应用正常启动，知识库功能只保留向量检索
-- ================================================================
