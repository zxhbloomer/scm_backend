-- =====================================================================
-- AI工作流组件初始化数据
-- 严格参考 aideepin 的 17 种组件定义
-- =====================================================================

-- 1. 开始组件 (Start)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'Start', '开始', NULL, '流程由此开始', 1, 1, 0);

-- 2. 结束组件 (End)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'End', '结束', NULL, '流程由此结束', 2, 1, 0);

-- 3. 生成回答 (Answer - LLM)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'Answer', '生成回答', NULL, '调用大语言模型回答问题', 3, 1, 0);

-- 4. 文档提取 (DocumentExtractor)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'DocumentExtractor', '文档提取', NULL, '从文档中提取信息', 4, 1, 0);

-- 5. 关键词提取 (KeywordExtractor)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'KeywordExtractor', '关键词提取', NULL, '从内容中提取关键词,Top N指定需要提取的关键词数量', 5, 1, 0);

-- 6. 常见问题提取 (FaqExtractor)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'FaqExtractor', '常见问题提取', NULL, '从内容中提取出常见问题及对应的答案,Top N为提取的数量', 6, 1, 0);

-- 7. 知识检索 (KnowledgeRetrieval)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'KnowledgeRetrieval', '知识检索', NULL, '从知识库中检索信息,需选中知识库', 7, 1, 0);

-- 8. 条件分支 (Switcher)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'Switcher', '条件分支', NULL, '根据设置的条件引导执行不同的流程', 8, 1, 0);

-- 9. 内容归类 (Classifier)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'Classifier', '内容归类', NULL, '使用大语言模型对输入信息进行分析并归类,根据类别调用对应的下游节点', 9, 1, 0);

-- 10. 模板转换 (Template)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'Template', '模板转换', NULL, '将多个变量合并成一个输出内容', 10, 1, 0);

-- 11. DALL-E 3 画图 (Dalle3)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'Dalle3', 'DALL-E 3 画图', NULL, '调用Dall-e-3生成图片', 11, 1, 0);

-- 12. 通义万相画图 (Tongyiwanx)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'Tongyiwanx', '通义万相-画图', NULL, '调用文生图模型生成图片', 12, 1, 0);

-- 13. Google搜索 (Google)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'Google', 'Google搜索', NULL, '从Google中检索信息', 13, 1, 0);

-- 14. 人机交互 (HumanFeedback)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'HumanFeedback', '人机交互', NULL, '中断执行中的流程并等待用户的输入,用户输入后继续执行后续流程', 14, 1, 0);

-- 15. 邮件发送 (MailSend)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'MailSend', '邮件发送', NULL, '发送邮件到指定邮箱', 15, 1, 0);

-- 16. Http请求 (HttpRequest)
INSERT INTO ai_workflow_component (component_uuid, name, title, icon, remark, display_order, is_enable, is_deleted)
VALUES (REPLACE(UUID(), '-', ''), 'HttpRequest', 'Http请求', NULL, '通过Http协议发送请求,可将其他组件的输出作为参数,也可设置常量作为参数', 16, 1, 0);

-- 查询验证插入的数据
SELECT component_uuid, name, title, display_order, is_enable
FROM ai_workflow_component
WHERE is_deleted = 0
ORDER BY display_order;
