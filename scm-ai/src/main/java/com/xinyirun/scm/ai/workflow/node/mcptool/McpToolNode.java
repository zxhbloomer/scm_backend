package com.xinyirun.scm.ai.workflow.node.mcptool;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * MCP工具节点
 * 功能：通过LLM的Function Calling能力自动选择和调用MCP工具
 *
 * 执行流程:
 * 1. 解析节点配置,获取tool_input和model_name
 * 2. 自动发现系统中所有带@McpTool注解的工具
 * 3. 将这些工具作为Function Call提供给LLM
 * 4. LLM根据输入智能选择并调用合适的工具
 * 5. 收集工具执行结果并流式返回
 *
 * @author zzxxhh
 * @since 2025-11-19
 */
@Slf4j
public class McpToolNode extends AbstractWfNode {

    public McpToolNode(AiWorkflowComponentEntity wfComponent,
                      AiWorkflowNodeVo node,
                      WfState wfState,
                      WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);
    }

    /**
     * mcp 被大模型调用的入口
     * @return
     */
    @Override
    protected NodeProcessResult onProcess() {
        log.info("开始执行MCP工具节点: {}", node.getTitle());

        try {
            // 1. 解析配置
            McpToolNodeConfig config = checkAndGetConfig(McpToolNodeConfig.class);

            // 2. 构建输入
            String inputText = getFirstInputText();
            String toolInput = config.getToolInput();

            // 如果配置了tool_input,使用它;否则使用上游节点输出
            String prompt = StringUtils.isNotBlank(toolInput)
                ? WorkflowUtil.renderTemplate(toolInput, state.getInputs())
                : inputText;

            if (StringUtils.isBlank(prompt)) {
                throw new RuntimeException("MCP工具节点缺少输入参数");
            }

            log.info("MCP工具节点输入: {}", prompt);

            // 3. 获取模型名称
            String modelName = config.getModelName();
            if (StringUtils.isBlank(modelName)) {
                modelName = "gj-deepseek";  // 默认模型
            }

            // 4. 使用LLM的Function Calling能力自动选择和调用工具
            // WorkflowUtil.streamingInvokeLLM会自动:
            // - 发现所有@McpTool注解的工具
            // - 将工具定义作为Function Call提供给LLM
            // - LLM根据输入智能选择工具并调用
            // - 流式返回工具执行结果
            WorkflowUtil.streamingInvokeLLM(wfState, state, node, modelName, prompt);

            log.info("MCP工具节点执行完成: {}, 模型: {}", node.getTitle(), modelName);

            // 流式输出时,实际内容通过StreamHandler实时发送
            return new NodeProcessResult();

        } catch (Exception e) {
            log.error("MCP工具节点执行失败: {}", node.getTitle(), e);
            throw new RuntimeException("MCP工具执行失败: " + e.getMessage(), e);
        }
    }
}
