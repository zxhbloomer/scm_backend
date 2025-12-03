-- =====================================================
-- AI应用扩展表结构 - 严格基于aideepin设计
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_general_ci
-- 创建时间: 2025-01-21
-- =====================================================

-- =====================================================
-- 1. 工作流模块 (Workflow Module) - 6张表
-- =====================================================

-- 1.1 工作流定义主表
DROP TABLE IF EXISTS `ai_workflow`;
CREATE TABLE `ai_workflow` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `workflow_uuid` VARCHAR(100) NOT NULL COMMENT '工作流UUID',
  `title` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '工作流标题',
  `remark` TEXT COMMENT '工作流描述',
  `user_id` BIGINT NOT NULL DEFAULT 0 COMMENT '所属用户ID',
  `is_public` TINYINT NOT NULL DEFAULT 0 COMMENT '是否公开(0-私有,1-公开)',
  `is_enable` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用(0-禁用,1-启用)',
  `c_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `u_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `c_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `u_id` BIGINT DEFAULT NULL COMMENT '修改人ID',
  `dbversion` INT DEFAULT 0 COMMENT '数据版本，乐观锁使用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_workflow_uuid` (`workflow_uuid`),
  KEY `idx_ai_workflow_user` (`user_id`),
  KEY `idx_ai_workflow_public` (`is_public`),
  KEY `idx_ai_workflow_enable` (`is_enable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI工作流定义主表';

-- 1.2 工作流组件库表
DROP TABLE IF EXISTS `ai_workflow_component`;
CREATE TABLE `ai_workflow_component` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `component_uuid` VARCHAR(100) NOT NULL COMMENT '组件UUID',
  `name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '组件名称(英文标识)',
  `title` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '组件标题(显示名称)',
  `remark` TEXT COMMENT '组件描述',
  `display_order` INT NOT NULL DEFAULT 0 COMMENT '显示排序',
  `is_enable` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用(0-禁用,1-启用)',
  `c_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `u_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `c_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `u_id` BIGINT DEFAULT NULL COMMENT '修改人ID',
  `dbversion` INT DEFAULT 0 COMMENT '数据版本，乐观锁使用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_workflow_component_uuid` (`component_uuid`),
  UNIQUE KEY `uk_ai_workflow_component_name` (`name`),
  KEY `idx_ai_workflow_component_enable` (`is_enable`),
  KEY `idx_ai_workflow_component_order` (`display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI工作流组件库表';

-- 1.3 工作流节点定义表
DROP TABLE IF EXISTS `ai_workflow_node`;
CREATE TABLE `ai_workflow_node` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `node_uuid` VARCHAR(100) NOT NULL COMMENT '节点UUID',
  `workflow_id` BIGINT NOT NULL DEFAULT 0 COMMENT '所属工作流ID',
  `workflow_component_id` BIGINT NOT NULL DEFAULT 0 COMMENT '使用的组件ID',
  `user_id` BIGINT NOT NULL DEFAULT 0 COMMENT '所属用户ID',
  `title` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '节点标题',
  `remark` VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '节点描述',
  `input_config` JSON COMMENT '输入配置(JSON格式): {"params":[{"name":"user_define_param01","type":"string"}]}',
  `node_config` JSON COMMENT '节点配置(JSON格式): {"params":[{"prompt":"Summarize the following content:{user_define_param01}"}]}',
  `position_x` DOUBLE NOT NULL DEFAULT 0 COMMENT 'X坐标位置',
  `position_y` DOUBLE NOT NULL DEFAULT 0 COMMENT 'Y坐标位置',
  `c_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `u_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `c_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `u_id` BIGINT DEFAULT NULL COMMENT '修改人ID',
  `dbversion` INT DEFAULT 0 COMMENT '数据版本，乐观锁使用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_workflow_node_uuid` (`node_uuid`),
  KEY `idx_ai_workflow_node_workflow` (`workflow_id`),
  KEY `idx_ai_workflow_node_component` (`workflow_component_id`),
  KEY `idx_ai_workflow_node_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI工作流节点定义表';

-- 1.4 工作流边/连接表
DROP TABLE IF EXISTS `ai_workflow_edge`;
CREATE TABLE `ai_workflow_edge` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `edge_uuid` VARCHAR(100) NOT NULL COMMENT '边UUID',
  `workflow_id` BIGINT NOT NULL DEFAULT 0 COMMENT '所属工作流ID',
  `source_node_uuid` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '源节点UUID',
  `source_handle` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '源节点句柄',
  `target_node_uuid` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '目标节点UUID',
  `c_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `u_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `c_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `u_id` BIGINT DEFAULT NULL COMMENT '修改人ID',
  `dbversion` INT DEFAULT 0 COMMENT '数据版本，乐观锁使用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_workflow_edge_uuid` (`edge_uuid`),
  KEY `idx_ai_workflow_edge_workflow` (`workflow_id`),
  KEY `idx_ai_workflow_edge_source` (`source_node_uuid`),
  KEY `idx_ai_workflow_edge_target` (`target_node_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI工作流边/连接表';

-- 1.5 工作流运行时实例表
DROP TABLE IF EXISTS `ai_workflow_runtime`;
CREATE TABLE `ai_workflow_runtime` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `runtime_uuid` VARCHAR(100) NOT NULL COMMENT '运行时UUID',
  `user_id` BIGINT NOT NULL DEFAULT 0 COMMENT '执行用户ID',
  `workflow_id` BIGINT NOT NULL DEFAULT 0 COMMENT '工作流ID',
  `input` JSON COMMENT '输入参数(JSON): {"userInput01":"text01","userInput02":true,"userInput03":10}',
  `output` JSON COMMENT '输出结果(JSON)',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '执行状态(1-就绪,2-执行中,3-成功,4-失败)',
  `status_remark` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '状态备注',
  `c_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `u_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `c_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `u_id` BIGINT DEFAULT NULL COMMENT '修改人ID',
  `dbversion` INT DEFAULT 0 COMMENT '数据版本，乐观锁使用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_workflow_runtime_uuid` (`runtime_uuid`),
  KEY `idx_ai_workflow_runtime_user` (`user_id`),
  KEY `idx_ai_workflow_runtime_workflow` (`workflow_id`),
  KEY `idx_ai_workflow_runtime_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI工作流运行时实例表';

-- 1.6 工作流运行时节点状态表
DROP TABLE IF EXISTS `ai_workflow_runtime_node`;
CREATE TABLE `ai_workflow_runtime_node` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `runtime_node_uuid` VARCHAR(100) NOT NULL COMMENT '运行时节点UUID',
  `user_id` BIGINT NOT NULL DEFAULT 0 COMMENT '用户ID',
  `workflow_runtime_id` BIGINT NOT NULL DEFAULT 0 COMMENT '工作流运行时ID',
  `node_id` BIGINT NOT NULL DEFAULT 0 COMMENT '节点定义ID',
  `input` JSON COMMENT '节点输入(JSON)',
  `output` JSON COMMENT '节点输出(JSON)',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '执行状态(1-进行中,2-失败,3-成功)',
  `status_remark` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '状态备注',
  `c_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `u_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `c_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `u_id` BIGINT DEFAULT NULL COMMENT '修改人ID',
  `dbversion` INT DEFAULT 0 COMMENT '数据版本，乐观锁使用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_workflow_runtime_node_uuid` (`runtime_node_uuid`),
  KEY `idx_ai_workflow_runtime_node_runtime` (`workflow_runtime_id`),
  KEY `idx_ai_workflow_runtime_node_node` (`node_id`),
  KEY `idx_ai_workflow_runtime_node_user` (`user_id`),
  KEY `idx_ai_workflow_runtime_node_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI工作流运行时节点状态表';


-- =====================================================
-- 2. 绘图模块 (Drawing Module) - 3张表
-- =====================================================

-- 2.1 绘图任务主表
DROP TABLE IF EXISTS `ai_draw`;
CREATE TABLE `ai_draw` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `draw_uuid` VARCHAR(100) NOT NULL COMMENT '绘图任务UUID',
  `user_id` BIGINT NOT NULL DEFAULT 0 COMMENT '用户ID',
  `ai_model_id` BIGINT NOT NULL DEFAULT 0 COMMENT 'AI模型ID',
  `ai_model_name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '图像模型名称',
  `prompt` VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '生成图片的提示词',
  `negative_prompt` VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '负面提示词',
  `generate_size` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '生成图片的尺寸',
  `generate_quality` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '生成图片的质量',
  `generate_number` INT NOT NULL DEFAULT 1 COMMENT '生成图片的数量(1-10)',
  `generate_seed` INT NOT NULL DEFAULT -1 COMMENT '生成图片的随机种子',
  `dynamic_params` JSON COMMENT '动态参数(JSON)',
  `original_image` VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '原始图片UUID',
  `mask_image` VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '遮罩图片UUID',
  `resp_images_path` VARCHAR(4000) NOT NULL DEFAULT '' COMMENT '响应图片URL(逗号分隔)',
  `generated_images` VARCHAR(4000) NOT NULL DEFAULT '' COMMENT '生成的图片UUID(逗号分隔)',
  `interacting_method` TINYINT NOT NULL DEFAULT 1 COMMENT '交互方式(1-文生图,2-图片编辑,3-图生图,4-背景生成,5-扩大图片,6-风格转化)',
  `process_status` TINYINT NOT NULL DEFAULT 1 COMMENT '任务状态(1-进行中,2-失败,3-成功)',
  `process_status_remark` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '状态备注',
  `is_public` TINYINT NOT NULL DEFAULT 0 COMMENT '是否公开(0-私有,1-公开)',
  `with_watermark` TINYINT NOT NULL DEFAULT 0 COMMENT '是否带水印(0-否,1-是)',
  `star_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `c_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `u_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `c_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `u_id` BIGINT DEFAULT NULL COMMENT '修改人ID',
  `dbversion` INT DEFAULT 0 COMMENT '数据版本，乐观锁使用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_draw_uuid` (`draw_uuid`),
  KEY `idx_ai_draw_user` (`user_id`),
  KEY `idx_ai_draw_model` (`ai_model_id`),
  KEY `idx_ai_draw_status` (`process_status`),
  KEY `idx_ai_draw_public` (`is_public`),
  KEY `idx_ai_draw_star` (`star_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI绘图任务表';

-- 2.2 绘图点赞表
DROP TABLE IF EXISTS `ai_draw_star`;
CREATE TABLE `ai_draw_star` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL DEFAULT 0 COMMENT '用户ID',
  `draw_id` BIGINT NOT NULL DEFAULT 0 COMMENT '绘图任务ID',
  `c_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `u_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `c_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `u_id` BIGINT DEFAULT NULL COMMENT '修改人ID',
  `dbversion` INT DEFAULT 0 COMMENT '数据版本，乐观锁使用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_draw_star` (`user_id`, `draw_id`),
  KEY `idx_ai_draw_star_draw` (`draw_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI绘图点赞表';

-- 2.3 绘图评论表
DROP TABLE IF EXISTS `ai_draw_comment`;
CREATE TABLE `ai_draw_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `comment_uuid` VARCHAR(100) NOT NULL COMMENT '评论UUID',
  `user_id` BIGINT NOT NULL DEFAULT 0 COMMENT '用户ID',
  `draw_id` BIGINT NOT NULL DEFAULT 0 COMMENT '绘图任务ID',
  `remark` TEXT COMMENT '评论内容',
  `c_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `u_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `c_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `u_id` BIGINT DEFAULT NULL COMMENT '修改人ID',
  `dbversion` INT DEFAULT 0 COMMENT '数据版本，乐观锁使用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_draw_comment_uuid` (`comment_uuid`),
  KEY `idx_ai_draw_comment_draw` (`draw_id`),
  KEY `idx_ai_draw_comment_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI绘图评论表';


-- =====================================================
-- 3. MCP工具集成模块 (MCP Module) - 2张表
-- =====================================================

-- 3.1 MCP服务器模板表
DROP TABLE IF EXISTS `ai_mcp`;
CREATE TABLE `ai_mcp` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mcp_uuid` VARCHAR(100) NOT NULL COMMENT 'MCP UUID',
  `title` VARCHAR(200) NOT NULL DEFAULT '' COMMENT 'MCP标题',
  `transport_type` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '传输类型(sse/stdio)',
  `sse_url` VARCHAR(500) NOT NULL DEFAULT '' COMMENT 'SSE URL',
  `sse_timeout` INT NOT NULL DEFAULT 0 COMMENT 'SSE超时时间(秒)',
  `stdio_command` VARCHAR(500) NOT NULL DEFAULT '' COMMENT 'STDIO命令',
  `stdio_arg` VARCHAR(2000) NOT NULL DEFAULT '' COMMENT 'STDIO参数',
  `preset_params` JSON COMMENT '预设参数(JSON): [{"name":"BAIDU_MAP_API_KEY","title":"百度地图服务","value":"111111","require_encrypt":true,"encrypted":true}]',
  `customized_param_definitions` JSON COMMENT '用户自定义参数定义(JSON): [{"name":"GITHUB_PERSONAL_ACCESS_TOKEN","title":"github access token","require_encrypt":true}]',
  `install_type` VARCHAR(50) NOT NULL DEFAULT '' COMMENT 'MCP安装方式(docker/local/remote/wasm)',
  `website` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '官网地址',
  `remark` TEXT COMMENT '描述(支持markdown)',
  `is_enable` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用(0-禁用,1-启用)',
  `c_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `u_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `c_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `u_id` BIGINT DEFAULT NULL COMMENT '修改人ID',
  `dbversion` INT DEFAULT 0 COMMENT '数据版本，乐观锁使用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_mcp_uuid` (`mcp_uuid`),
  KEY `idx_ai_mcp_enable` (`is_enable`),
  KEY `idx_ai_mcp_transport` (`transport_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI MCP服务器模板表';

-- 4.2 用户MCP配置表
DROP TABLE IF EXISTS `ai_user_mcp`;
CREATE TABLE `ai_user_mcp` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_mcp_uuid` VARCHAR(100) NOT NULL COMMENT '用户MCP UUID',
  `user_id` BIGINT NOT NULL DEFAULT 0 COMMENT '用户ID',
  `mcp_id` BIGINT NOT NULL DEFAULT 0 COMMENT 'MCP模板ID',
  `mcp_customized_params` JSON COMMENT '用户自定义参数(JSON): [{"name":"BAIDU_MAP_API_KEY","value":"111111","encrypted":true}]',
  `is_enable` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用(0-禁用,1-启用)',
  `c_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `u_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `c_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `u_id` BIGINT DEFAULT NULL COMMENT '修改人ID',
  `dbversion` INT DEFAULT 0 COMMENT '数据版本，乐观锁使用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_user_mcp_uuid` (`user_mcp_uuid`),
  UNIQUE KEY `uk_ai_user_mcp_user_mcp` (`user_id`, `mcp_id`),
  KEY `idx_ai_user_mcp_mcp` (`mcp_id`),
  KEY `idx_ai_user_mcp_enable` (`is_enable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI用户MCP配置表';


-- =====================================================
-- 建表完成
-- =====================================================
