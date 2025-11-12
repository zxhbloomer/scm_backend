-- =====================================================
-- AI Chat调用Workflow运行时表结构
-- 创建时间: 2025-11-11
-- 用途: 分离AI Chat调用Workflow的数据，独立于Workflow测试数据
-- =====================================================

-- 表1: AI Chat调用Workflow的运行时实例表
DROP TABLE IF EXISTS `ai_conversation_workflow_runtime`;
CREATE TABLE `ai_conversation_workflow_runtime` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `runtime_uuid` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '运行时UUID(业务主键)',
  `conversation_id` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '对话ID，格式：tenantId::uuid，关联ai_conversation表',
  `workflow_id` bigint NOT NULL COMMENT '工作流ID',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '执行用户ID',
  `input_data` json DEFAULT NULL COMMENT '输入数据(JSON格式)',
  `output_data` json DEFAULT NULL COMMENT '输出数据(JSON格式)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '执行状态(1-运行中,2-成功,3-失败)',
  `status_remark` text COLLATE utf8mb4_general_ci COMMENT '状态说明',
  `c_time` datetime DEFAULT NULL COMMENT '创建时间',
  `u_time` datetime DEFAULT NULL COMMENT '修改时间',
  `c_id` bigint DEFAULT NULL COMMENT '创建人ID',
  `u_id` bigint DEFAULT NULL COMMENT '修改人ID',
  `dbversion` int DEFAULT '0' COMMENT '数据版本(乐观锁)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_conversation_workflow_runtime_uuid` (`runtime_uuid`),
  KEY `idx_ai_conversation_workflow_runtime_conv` (`conversation_id`),
  KEY `idx_ai_conversation_workflow_runtime_workflow` (`workflow_id`,`status`),
  KEY `idx_ai_conversation_workflow_runtime_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='AI Chat调用Workflow运行时表-AI Chat workflow runtime instances';

-- 表2: AI Chat调用Workflow的节点执行表
DROP TABLE IF EXISTS `ai_conversation_workflow_runtime_node`;
CREATE TABLE `ai_conversation_workflow_runtime_node` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `runtime_node_uuid` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '运行时节点UUID(业务主键)',
  `conversation_workflow_runtime_id` bigint NOT NULL COMMENT 'AI Chat工作流运行时ID（关联ai_conversation_workflow_runtime表）',
  `node_id` bigint NOT NULL COMMENT '节点ID',
  `input_data` json DEFAULT NULL COMMENT '节点输入数据(JSON格式)',
  `output_data` json DEFAULT NULL COMMENT '节点输出数据(JSON格式)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '执行状态(1-等待中,2-运行中,3-成功,4-失败)',
  `status_remark` text COLLATE utf8mb4_general_ci COMMENT '状态说明',
  `c_time` datetime DEFAULT NULL COMMENT '创建时间',
  `u_time` datetime DEFAULT NULL COMMENT '修改时间',
  `c_id` bigint DEFAULT NULL COMMENT '创建人ID',
  `u_id` bigint DEFAULT NULL COMMENT '修改人ID',
  `dbversion` int DEFAULT '0' COMMENT '数据版本(乐观锁)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_conversation_workflow_runtime_node_uuid` (`runtime_node_uuid`),
  KEY `idx_ai_conversation_workflow_runtime_node_node` (`node_id`),
  KEY `idx_ai_conversation_workflow_runtime_node_runtime` (`conversation_workflow_runtime_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='AI Chat调用Workflow节点执行表-AI Chat workflow runtime node execution';

-- =====================================================
-- 索引说明
-- =====================================================
-- ai_conversation_workflow_runtime:
--   - uk_ai_conversation_workflow_runtime_uuid: 唯一索引，保证runtime_uuid唯一性
--   - idx_ai_conversation_workflow_runtime_conv: 按对话ID查询运行历史
--   - idx_ai_conversation_workflow_runtime_workflow: 按工作流ID和状态查询
--   - idx_ai_conversation_workflow_runtime_user: 按用户ID查询

-- ai_conversation_workflow_runtime_node:
--   - uk_ai_conversation_workflow_runtime_node_uuid: 唯一索引，保证节点UUID唯一性
--   - idx_ai_conversation_workflow_runtime_node_node: 按节点ID查询
--   - idx_ai_conversation_workflow_runtime_node_runtime: 按运行时ID和状态查询节点执行记录

-- =====================================================
-- 数据分离架构说明
-- =====================================================
-- 【Workflow Chat测试】
--   - 对话记录: ai_workflow_conversation_content
--   - 运行实例: ai_workflow_runtime
--   - 节点执行: ai_workflow_runtime_node

-- 【AI Chat调用Workflow】
--   - 对话记录: ai_conversation_content
--   - 运行实例: ai_conversation_workflow_runtime (本表)
--   - 节点执行: ai_conversation_workflow_runtime_node (本表)

-- =====================================================
-- 执行验证
-- =====================================================
-- 验证表创建成功
SELECT
    TABLE_NAME,
    TABLE_COMMENT,
    TABLE_ROWS
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME IN (
      'ai_conversation_workflow_runtime',
      'ai_conversation_workflow_runtime_node'
  );

-- 验证索引创建成功
SELECT
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    NON_UNIQUE
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME IN (
      'ai_conversation_workflow_runtime',
      'ai_conversation_workflow_runtime_node'
  )
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;
