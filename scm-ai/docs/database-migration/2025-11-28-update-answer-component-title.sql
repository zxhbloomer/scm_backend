-- =====================================================================
-- AI工作流Answer组件标题统一更新
-- 日期: 2025-11-28
-- 说明: 将 Answer 节点（大模型节点）的显示名称从"生成回答"改为"大模型"
--      与前端UI和代码注释保持一致
-- =====================================================================

-- 更新 Answer 组件的标题
UPDATE ai_workflow_component
SET title = '大模型'
WHERE name = 'Answer'
  AND is_deleted = 0;

-- 验证更新结果
SELECT component_uuid, name, title, remark, display_order, is_enable
FROM ai_workflow_component
WHERE name = 'Answer'
  AND is_deleted = 0;

-- =====================================================================
-- 验证信息:
-- 应该看到 name='Answer' 的记录，title 已从 '生成回答' 改为 '大模型'
-- =====================================================================
