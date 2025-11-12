-- ================================================================
-- AI工作流智能路由 - 数据库DDL变更脚本
-- ================================================================
-- 功能: 为ai_workflow表新增5个字段,支持智能路由功能
-- 创建时间: 2025-11-10 09:55:57
-- 设计文档: docs/design/2025-11-10-AI工作流智能路由-方案.md
-- ================================================================

USE scm_tenant_20250519_001;

-- ================================================================
-- 1. 新增字段
-- ================================================================

-- 最后测试运行时间(辅助字段,控制发布按钮)
ALTER TABLE ai_workflow
ADD COLUMN last_test_time DATETIME DEFAULT NULL
COMMENT '最后测试运行时间,用于判断是否可发布';

-- 详细描述(供LLM理解)
ALTER TABLE ai_workflow
ADD COLUMN description TEXT DEFAULT NULL
COMMENT '工作流详细描述,说明适用场景、功能、输入输出等,供AI路由使用';

-- 关键词(供程序快速匹配,逗号分隔)
ALTER TABLE ai_workflow
ADD COLUMN keywords VARCHAR(500) DEFAULT ''
COMMENT '关键词,逗号分隔,用于快速匹配,如: 订单,采购,库存,入库,出库';

-- 分类标签(字典值:ai_workflow_category)
-- 0=业务处理, 1=知识问答, 2=通用对话
-- 仅用于前端UI筛选,不参与路由逻辑
ALTER TABLE ai_workflow
ADD COLUMN category VARCHAR(10) DEFAULT '0'
COMMENT '工作流分类(字典:ai_workflow_category): 0=业务处理, 1=知识问答, 2=通用对话';

-- 优先级(0-100)
ALTER TABLE ai_workflow
ADD COLUMN priority INT DEFAULT 50
COMMENT '优先级(0-100),越高越优先,用于多个工作流匹配时的排序';

-- ================================================================
-- 2. 索引优化
-- ================================================================

-- 租户+启用状态复合索引(用于查询可用工作流)
CREATE INDEX idx_workflow_tenant_enable
ON ai_workflow(tenant_code, is_enable);

-- 优先级索引(用于排序)
CREATE INDEX idx_workflow_priority
ON ai_workflow(priority);

-- 可选: keywords全文索引(如需高效关键词搜索)
-- 注意: MySQL 5.7+支持InnoDB全文索引
-- CREATE FULLTEXT INDEX idx_workflow_keywords ON ai_workflow(keywords);

-- ================================================================
-- 执行完成
-- ================================================================
-- 说明:
-- 1. 所有新字段都设置了DEFAULT值,不影响现有数据
-- 2. 索引创建可能需要较长时间,建议在业务低峰期执行
-- 3. 如果表数据量很大(>100万行),建议使用pt-online-schema-change工具
-- ================================================================
