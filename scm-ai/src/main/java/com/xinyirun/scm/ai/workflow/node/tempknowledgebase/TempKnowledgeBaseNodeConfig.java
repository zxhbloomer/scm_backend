package com.xinyirun.scm.ai.workflow.node.tempknowledgebase;

import lombok.Data;

/**
 * 临时知识库节点配置类
 *
 * 设计理念：
 * - 极简配置，只需要选择模型
 * - 后台硬编码提示词："创建临时知识库并同步完成向量索引"
 * - 自动使用上游节点的输出作为输入
 * - LLM根据输入内容自动判断text还是fileUrls
 *
 * @author zzxxhh
 * @since 2025-12-04
 */
@Data
public class TempKnowledgeBaseNodeConfig {

    /**
     * 模型名称（可选）
     *
     * 说明：
     * - 默认值: "gj-deepseek"
     * - 用于LLM的Function Calling功能
     */
    private String model_name;
}
