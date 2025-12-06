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

    /**
     * 简介（必填）
     *
     * 说明：
     * - 用于填充 ai_knowledge_base_item 的 title 和 brief 字段
     * - 帮助用户区分不同的临时知识库节点
     * - 前端限制: 最大100个字符
     * - 向后兼容: 老节点此字段为空时，后端fallback到"文本内容"
     *
     * @since 2025-12-05
     */
    private String brief;
}
