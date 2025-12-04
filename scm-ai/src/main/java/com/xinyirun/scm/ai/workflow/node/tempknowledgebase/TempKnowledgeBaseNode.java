package com.xinyirun.scm.ai.workflow.node.tempknowledgebase;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 临时知识库节点
 *
 * 功能：创建2小时自动过期的临时知识库，同步完成向量索引
 *
 * 设计理念：
 * - 极简配置，只需要选择模型
 * - 后台硬编码提示词："创建临时知识库并同步完成向量索引"
 * - 自动使用上游节点的输出作为输入
 * - LLM根据输入内容自动判断text还是fileUrls，然后调用MCP工具
 * - 输出kbUuid供下游知识检索节点使用
 *
 * 执行流程:
 * 1. 解析节点配置，获取model_name
 * 2. 获取上游节点的输出（必需）
 * 3. 构建完整prompt（硬编码 + 上游输出）
 * 4. 通过LLM的Function Calling调用TempKnowledgeBaseMcpTools
 * 5. LLM根据输入内容自动判断text还是fileUrls
 * 6. 流式返回kbUuid，供下游节点使用
 *
 * 使用场景：
 * - 合同审批workflow：基于用户输入创建临时知识库
 * - 文档分析workflow：基于上传的文件创建临时知识库
 * - 智能问答workflow：基于对话内容创建临时知识库
 *
 * @author zzxxhh
 * @since 2025-12-04
 */
@Slf4j
public class TempKnowledgeBaseNode extends AbstractWfNode {

    /**
     * 硬编码的LLM提示词
     *
     * 目的：指导LLM调用TempKnowledgeBaseMcpTools.createTempKnowledgeBase()
     *
     * LLM会自动：
     * 1. 发现TempKnowledgeBaseMcpTools工具
     * 2. 将工具定义作为Function Call提供
     * 3. 根据此提示词调用createTempKnowledgeBase方法
     * 4. 根据输入内容自动判断text还是fileUrls参数
     */
    private static final String HARDCODED_PROMPT =
            "创建临时知识库并同步完成向量索引";

    public TempKnowledgeBaseNode(AiWorkflowComponentEntity wfComponent,
                                 AiWorkflowNodeVo node,
                                 WfState wfState,
                                 WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);
    }

    /**
     * 节点执行入口
     *
     * @return NodeProcessResult 节点执行结果（流式输出通过StreamHandler实时发送）
     */
    @Override
    protected NodeProcessResult onProcess() {
        log.info("开始执行临时知识库节点: {}", node.getTitle());

        try {
            // 1. 解析配置
            TempKnowledgeBaseNodeConfig config =
                    checkAndGetConfig(TempKnowledgeBaseNodeConfig.class);

            // 2. 获取上游节点的输出（必需）
            String upstreamInput = getFirstInputText();

            // 参数验证
            if (StringUtils.isBlank(upstreamInput)) {
                throw new RuntimeException(
                        "临时知识库节点缺少输入参数（上游节点无输出）");
            }

            log.info("临时知识库节点输入内容: {}",
                    upstreamInput.length() > 100 ?
                    upstreamInput.substring(0, 100) + "..." : upstreamInput);

            // 3. 获取模型名称（可选配置，默认使用gj-deepseek）
            String modelName = config.getModel_name();
            if (StringUtils.isBlank(modelName)) {
                modelName = "gj-deepseek";
            }

            // 4. 构建完整的prompt（硬编码 + 上游输出）
            String fullPrompt = HARDCODED_PROMPT + "\n\n输入内容:\n" + upstreamInput;

            log.info("临时知识库节点完整prompt: {}", fullPrompt);

            // 5. 使用LLM的Function Calling能力调用MCP工具
            // WorkflowUtil.streamingInvokeLLM会自动:
            // - 发现所有@McpTool注解的工具（包括TempKnowledgeBaseMcpTools）
            // - 将工具定义作为Function Call提供给LLM
            // - LLM根据prompt智能选择TempKnowledgeBaseMcpTools.createTempKnowledgeBase
            // - LLM根据输入内容自动判断是text还是fileUrls
            // - 自动传递tenantCode和staffId（框架注入）
            // - 流式返回工具执行结果（包含kbUuid）
            WorkflowUtil.streamingInvokeLLM(wfState, state, node,
                    modelName, fullPrompt);

            log.info("临时知识库节点执行完成: {}, 模型: {}", node.getTitle(), modelName);

            // 流式输出时，实际内容通过StreamHandler实时发送
            // 返回的kbUuid会包含在流式输出的JSON中，下游节点可以引用
            return new NodeProcessResult();

        } catch (Exception e) {
            log.error("临时知识库节点执行失败: {}", node.getTitle(), e);
            throw new RuntimeException("临时知识库创建失败: " + e.getMessage(), e);
        }
    }
}
