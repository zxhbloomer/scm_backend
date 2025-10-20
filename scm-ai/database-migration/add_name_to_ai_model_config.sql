-- 功能: 为ai_model_config表添加name字段
-- 用途: name用于前端显示,model_name用于API调用
-- 日期: 2025-10-20

-- 步骤1: 添加name字段
ALTER TABLE ai_model_config
ADD COLUMN name VARCHAR(200) COMMENT '模型显示名称' AFTER id;

-- 步骤2: 数据迁移 - 将现有model_name复制到name作为初始值
UPDATE ai_model_config
SET name = model_name
WHERE name IS NULL;

-- 步骤3: 验证数据
SELECT id, name, model_name, provider FROM ai_model_config;
