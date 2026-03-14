package com.xinyirun.scm.ai.workflow.node.mcptool;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * MCP工具节点配置类
 *
 * 节点配置数据结构(存储在ai_workflow_node.node_config字段):
 * {
 *   "tool_input": "查询库位信息,条件: {input.query_condition}",
 *   "model_name": "gj-deepseek",
 *   "tool_names": ["querySupplier", "queryInventory"],
 *   "direct_call": false,
 *   "direct_params": {"keyword": "${var_user_input}"}
 * }
 *
 * 简化设计:
 * - tool_input: LLM模式下的自然语言指令，支持变量引用
 * - model_name: LLM模型名称，用于Function Calling智能选择工具
 * - tool_names: 指定加载的MCP工具名称列表，null或空表示加载全部工具
 * - direct_call: true时跳过LLM，直接调用tool_names[0]指定的工具
 * - direct_params: direct_call=true时传给工具的参数Map，支持${var}变量引用
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
     * 为空时使用系统默认语言模型
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

    /**
     * 指定加载的MCP工具名称列表
     * null或空列表: 加载全部MCP工具（向后兼容）
     * 指定具体工具名: 只加载这些工具,减少token消耗
     */
    @JSONField(name = "tool_names")
    private List<String> toolNames;

    /**
     * 工作流共享输出，开启后其他节点可引用本节点输出
     */
    @JSONField(name = "shared_output")
    private Boolean sharedOutput = false;

    /**
     * 是否直接调用工具，跳过LLM
     * false(默认): 走LLM Function Calling流程
     * true: 直接调用tool_names[0]指定的工具，配合direct_params使用
     */
    @JSONField(name = "direct_call")
    private Boolean directCall = false;

    /**
     * 直接调用模式下传给工具的参数Map
     * 仅在direct_call=true时生效
     * 支持${varName}变量引用，引用上游节点输出
     * 示例: {"keyword": "${var_user_input}"}
     *       {"page_code": "P00000170"}
     */
    @JSONField(name = "direct_params")
    private java.util.Map<String, Object> directParams;
}
