package com.xinyirun.scm.ai.workflow.node.mcptool;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * MCP工具节点配置类
 *
 * 节点配置数据结构(存储在ai_workflow_node.node_config字段):
 * {
 *   "tool_input": "查询库位信息,条件: {input.query_condition}",
 *   "model_name": "gj-deepseek"
 * }
 *
 * 简化设计:
 * - tool_input: 传递给MCP工具的输入参数,支持变量引用
 * - model_name: LLM模型名称,用于Function Calling智能选择工具
 * - MCP工具会被自动发现并作为Function Call提供给LLM
 * - LLM根据输入智能选择并调用合适的工具
 *
 * @author zzxxhh
 * @since 2025-11-19
 */
@Data
public class McpToolNodeConfig {

    /**
     * 输入参数
     * 支持变量引用: {sys.xxx} {input.xxx}
     * 示例: "查询库位信息,条件: {input.query_condition}"
     */
    @JSONField(name = "tool_input")
    private String toolInput;

    /**
     * LLM模型名称
     * 用于Function Calling智能选择和调用MCP工具
     * 默认: gj-deepseek
     */
    @JSONField(name = "model_name")
    private String modelName;

    /**
     * 是否显示执行过程输出到chat流
     * true(默认): 流式输出显示在聊天界面
     * false: 不显示流式输出，但结果仍传递给下游节点
     */
    @JSONField(name = "show_process_output")
    private Boolean showProcessOutput = true;
}
