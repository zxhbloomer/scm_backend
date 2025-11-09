-- 功能：为 ai_workflow_conversation_content 表新增 runtime_uuid 字段
-- 作用：实现运行时级别的对话记录隔离，支持精准删除单个 runtime 的对话历史
-- 日期：2025-01-08
-- 关联问题：删除工作流运行实例时误删其他实例的对话历史

ALTER TABLE ai_workflow_conversation_content
ADD COLUMN runtime_uuid VARCHAR(32) COMMENT '运行时UUID，关联 ai_workflow_runtime.runtime_uuid'
AFTER conversation_id;

-- 为新字段创建索引，提升删除操作性能
CREATE INDEX idx_ai_workflow_conversation_content_runtime_uuid
ON ai_workflow_conversation_content(runtime_uuid);

-- 注意：历史数据的 runtime_uuid 为 NULL
-- 建议：在业务代码中忽略 runtime_uuid=NULL 的历史数据，或执行数据清理
