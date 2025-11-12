-- =====================================================
-- 修复 AI 工作流流式输出节点重复发送问题
-- 日期: 2025-11-11
-- 问题: 用户输入"hello"后，前端显示JSON格式数据，然后才显示正常响应
-- 根因: End 节点配置为固定文本"任务执行完成"，未引用上游 Answer 节点的 LLM 响应
-- 解决: 修改 End 节点配置，使用模板变量 ${output} 引用上游节点输出
-- =====================================================

-- 修改工作流 "test" (workflow_id=28) 的结束节点配置
-- 将固定文本 "任务执行完成" 改为模板变量 "${output}"
UPDATE ai_workflow_node
SET node_config = JSON_OBJECT('result', '${output}'),
    u_time = NOW()
WHERE id = 93
  AND workflow_id = 28
  AND is_deleted = 0;

-- 验证修改结果
SELECT
  id,
  workflow_id,
  title,
  CAST(node_config AS CHAR) as node_config_text,
  u_time
FROM ai_workflow_node
WHERE id = 93
  AND workflow_id = 28
  AND is_deleted = 0;

-- =====================================================
-- 预期结果:
-- node_config_text (Base64解码后): {"result": "${output}"}
--
-- 技术说明:
-- 1. Answer 节点将 LLM 响应存储为 NodeIOData，name="output"
-- 2. End 节点接收上游节点的输出到 state.getInputs()
-- 3. WorkflowUtil.renderTemplate() 使用 ${变量名} 语法进行替换
-- 4. 最终 End 节点输出的是 Answer 节点的 LLM 响应，而不是固定文本
-- =====================================================
