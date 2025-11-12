-- ========================================
-- AI对话工作流状态管理 - 数据库变更SQL
-- ========================================
-- 功能: 为ai_conversation表增加工作流状态管理字段
-- 用途: 支持多轮对话中的工作流上下文保持
-- 创建时间: 2025-11-10
-- ========================================

-- 为ai_conversation表增加工作流状态字段
ALTER TABLE ai_conversation
ADD COLUMN current_workflow_uuid VARCHAR(50) DEFAULT NULL COMMENT '当前活跃工作流UUID',
ADD COLUMN current_runtime_uuid VARCHAR(50) DEFAULT NULL COMMENT '当前工作流运行时UUID(用于恢复执行)',
ADD COLUMN workflow_state VARCHAR(20) DEFAULT 'IDLE' COMMENT '工作流状态: IDLE-空闲, WORKFLOW_RUNNING-执行中, WORKFLOW_WAITING_INPUT-等待输入';

-- 为workflow_state字段创建索引(优化查询等待输入状态的对话)
ALTER TABLE ai_conversation
ADD INDEX idx_ai_conversation_workflow_state (workflow_state);

-- 为current_workflow_uuid字段创建索引(优化根据工作流UUID查询对话)
ALTER TABLE ai_conversation
ADD INDEX idx_ai_conversation_workflow_uuid (current_workflow_uuid);

-- 为current_runtime_uuid字段创建索引(优化根据运行时UUID查询对话)
ALTER TABLE ai_conversation
ADD INDEX idx_ai_conversation_runtime_uuid (current_runtime_uuid);

-- ========================================
-- 字段说明
-- ========================================
-- current_workflow_uuid:
--   记录当前对话正在使用的工作流UUID
--   当工作流执行完成或取消时,置为NULL
--   用于判断用户输入是否应该继续执行当前工作流
--
-- current_runtime_uuid:
--   记录工作流运行时UUID(WorkflowEngine的执行ID)
--   当工作流等待用户输入时(onNodeWaitFeedback),记录此UUID
--   用于调用WorkflowStarter.resumeFlow()恢复工作流执行
--   工作流完成后置为NULL
--
-- workflow_state:
--   IDLE: 空闲状态,没有活跃工作流
--   WORKFLOW_RUNNING: 工作流正在执行
--   WORKFLOW_WAITING_INPUT: 工作流暂停,等待用户提供输入
--   (KISS优化: 移除ROUTING和WORKFLOW_COMPLETED瞬态状态)
--
-- ========================================
-- 状态转换流程
-- ========================================
-- 场景1: 首次工作流调用
--   IDLE
--   → (用户输入"帮我查询订单")
--   → ROUTING (调用WorkflowRoutingService.route)
--   → WORKFLOW_RUNNING (执行WorkflowStarter.streaming)
--   → WORKFLOW_WAITING_INPUT (收到onNodeWaitFeedback事件)
--   → (返回"请提供订单号")
--
-- 场景2: 多轮对话 - 提供输入
--   WORKFLOW_WAITING_INPUT
--   → (用户输入"ORD-20251110-001")
--   → WORKFLOW_RUNNING (调用WorkflowStarter.resumeFlow)
--   → WORKFLOW_COMPLETED (工作流执行完成)
--   → IDLE (清空workflow相关字段)
--
-- 场景3: 无匹配工作流
--   IDLE
--   → (用户输入普通问题)
--   → ROUTING (调用路由服务)
--   → (无匹配)
--   → IDLE (走普通AI对话)
--
-- ========================================
-- 验证SQL
-- ========================================
-- 查看表结构变更
DESC ai_conversation;

-- 验证索引创建
SHOW INDEX FROM ai_conversation WHERE Key_name LIKE 'idx_ai_conversation_workflow%';

-- 查询等待用户输入的对话(用于管理和监控)
SELECT
    id,
    title,
    current_workflow_uuid,
    current_runtime_uuid,
    workflow_state,
    u_time
FROM ai_conversation
WHERE workflow_state = 'WORKFLOW_WAITING_INPUT'
ORDER BY u_time DESC;
