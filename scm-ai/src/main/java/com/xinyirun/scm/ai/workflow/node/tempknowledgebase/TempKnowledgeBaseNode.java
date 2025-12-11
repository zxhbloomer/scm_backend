package com.xinyirun.scm.ai.workflow.node.tempknowledgebase;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.mcp.utils.temp.knowledge.service.TempKnowledgeBaseAiService;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import com.xinyirun.scm.core.bpm.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 临时知识库节点
 *
 * 功能：创建2小时自动过期的临时知识库，同步完成向量索引
 *
 * 设计理念：
 * - 极简配置，只需要配置简介(brief)
 * - 自动使用上游节点的输出作为输入
 * - 直接调用TempKnowledgeBaseAiService创建知识库（不依赖LLM的Function Calling）
 * - 输出kbUuid供下游知识检索节点使用
 *
 * 执行流程:
 * 1. 解析节点配置，获取brief
 * 2. 获取上游节点的输出（必需）
 * 3. 从输入内容中提取URL（如果有）
 * 4. 直接调用TempKnowledgeBaseAiService.createTempKnowledgeBase()
 * 5. 将kbUuid作为节点输出，供下游节点使用
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
     * URL提取正则表达式
     * 匹配http://或https://开头的URL
     */
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)",
            Pattern.CASE_INSENSITIVE);

    public TempKnowledgeBaseNode(AiWorkflowComponentEntity wfComponent,
                                 AiWorkflowNodeVo node,
                                 WfState wfState,
                                 WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);
    }

    /**
     * 节点执行入口
     *
     * @return NodeProcessResult 节点执行结果
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

            // 3. 获取简介（必填配置，如果为空则使用默认值）
            String brief = config.getBrief();
            if (StringUtils.isBlank(brief)) {
                brief = "临时知识库内容";
                log.warn("临时知识库节点未配置简介，使用默认值: {}", brief);
            }

            // 4. 从输入内容中提取URL
            List<String> fileUrls = extractUrls(upstreamInput);
            String textContent = upstreamInput;

            // 如果有URL，从文本中移除URL（避免重复）
            if (!fileUrls.isEmpty()) {
                for (String url : fileUrls) {
                    textContent = textContent.replace(url, "").trim();
                }
                log.info("提取到 {} 个文件URL", fileUrls.size());
            }

            // 5. 从workflow上下文获取用户ID（异步线程中SecurityUtil不可用）
            Long staffId = wfState.getUserId();

            // 6. 直接调用TempKnowledgeBaseAiService创建临时知识库
            TempKnowledgeBaseAiService tempKbService = SpringContextHolder.getBean(
                    TempKnowledgeBaseAiService.class);

            Map<String, Object> result = tempKbService.createTempKnowledgeBase(
                    textContent,
                    fileUrls.isEmpty() ? null : fileUrls,
                    brief,
                    staffId
            );

            // 7. 检查结果
            boolean success = (boolean) result.getOrDefault("success", false);
            if (!success) {
                String message = (String) result.getOrDefault("message", "创建临时知识库失败");
                throw new RuntimeException(message);
            }

            // 8. 获取kbUuid
            String kbUuid = (String) result.get("kbUuid");
            log.info("临时知识库创建成功: kbUuid={}", kbUuid);

            // 9. 将结果作为节点输出
            String outputJson = JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

            // 10. 流式发送输出（检查show_process_output配置）
            // 只有当配置为true（默认）时才发送chunk到聊天界面
            Boolean showProcessOutput = config.getShowProcessOutput();
            if (showProcessOutput == null) {
                showProcessOutput = true;  // 默认显示
            }

            if (showProcessOutput && wfState.getStreamHandler() != null) {
                wfState.getStreamHandler().sendNodeChunk(node.getUuid(), outputJson);
            } else {
                log.info("临时知识库节点配置show_process_output=false，跳过流式输出");
            }

            // 11. 创建节点输出（供下游节点引用kbUuid）
            NodeIOData output = NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", outputJson);

            log.info("临时知识库节点执行完成: {}, kbUuid: {}", node.getTitle(), kbUuid);

            return NodeProcessResult.builder().content(List.of(output)).build();

        } catch (Exception e) {
            log.error("临时知识库节点执行失败: {}", node.getTitle(), e);
            throw new RuntimeException("临时知识库创建失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从文本中提取URL列表
     *
     * @param text 输入文本
     * @return URL列表（可能为空）
     */
    private List<String> extractUrls(String text) {
        List<String> urls = new ArrayList<>();
        if (StringUtils.isBlank(text)) {
            return urls;
        }

        Matcher matcher = URL_PATTERN.matcher(text);
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }

        return urls;
    }
}
