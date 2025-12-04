-- =====================================================================
-- 临时知识库组件初始化数据
-- 功能：在 ai_workflow_component 表中新增临时知识库节点类型
--
-- 执行时机：部署临时知识库工作流节点功能时
-- 执行方式：手动执行或集成到数据库迁移脚本
--
-- @author zzxxhh
-- @since 2025-12-04
-- =====================================================================

INSERT INTO ai_workflow_component (
    component_uuid,
    name,
    title,
    icon,
    remark,
    display_order,
    is_enable,
    is_deleted
)
VALUES (
    REPLACE(UUID(), '-', ''),                 -- 自动生成UUID
    'TempKnowledgeBase',                      -- 组件英文名称（与后端WfNodeFactory匹配）
    '临时知识库',                             -- 组件中文标题（显示在前端面板）
    NULL,                                     -- 图标（前端Vue组件中定义）
    '创建2小时自动过期的临时知识库，支持文本和文件输入，同步完成向量索引',  -- 功能说明
    16,                                       -- 显示顺序（在McpTool之后）
    1,                                        -- 启用状态（1=启用，0=禁用）
    0                                         -- 删除标记（0=未删除，1=已删除）
);

-- =====================================================================
-- 验证SQL执行结果
-- =====================================================================
-- 执行后运行以下查询验证：
-- SELECT * FROM ai_workflow_component WHERE name = 'TempKnowledgeBase';
--
-- 预期结果：
-- - component_uuid: 32位UUID（无横杠）
-- - name: TempKnowledgeBase
-- - title: 临时知识库
-- - display_order: 16
-- - is_enable: 1
-- - is_deleted: 0
-- =====================================================================
