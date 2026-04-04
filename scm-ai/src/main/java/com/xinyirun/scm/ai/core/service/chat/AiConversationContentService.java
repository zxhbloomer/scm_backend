package com.xinyirun.scm.ai.core.service.chat;

import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import com.xinyirun.scm.ai.bean.vo.config.AiModelConfigVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationRuntimeNodeVo;
import com.xinyirun.scm.ai.common.constant.AiMessageTypeConstant;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationContentMapper;
import com.xinyirun.scm.ai.core.mapper.chat.ExtAiConversationContentMapper;
import com.xinyirun.scm.ai.core.service.config.AiModelConfigService;
import com.xinyirun.scm.ai.core.service.workflow.AiConversationRuntimeNodeService;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowNodeService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.clickhouse.vo.ai.SLogAiChatVo;
import com.xinyirun.scm.common.utils.UuidUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.mq.rabbitmq.producer.business.log.ai.LogAiChatProducer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AI对话内容服务
 *
 * 提供AI对话内容管理功能，包括消息的创建、查询等操作
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiConversationContentService {

    @Resource
    private AiConversationContentMapper aiConversationContentMapper;

    @Resource
    private ExtAiConversationContentMapper extAiConversationContentMapper;

    @Autowired
    private LogAiChatProducer logAiChatProducer;

    @Autowired
    private AiModelConfigService aiModelConfigService;

    @Autowired
    private AiConversationRuntimeNodeService conversationRuntimeNodeService;

    @Autowired
    private AiWorkflowNodeService workflowNodeService;


    /**
     * 保存对话内容（简化版，用于Workflow场景）
     *
     * @param conversationId 对话ID
     * @param role 角色（1=用户, 2=AI）
     * @param content 内容
     * @param operatorId 操作员ID
     * @param runtimeUuid 运行时UUID（可选，关联ai_conversation_runtime）
     * @param aiOpenDialogPara AI打开弹窗的参数数据（可选，OpenPage节点输出的JSON）
     * @param workflowSteps 工作流思考步骤JSON（可选，刷新页面后恢复显示）
     * @return 保存的对话内容VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContentVo saveContent(String conversationId, Integer role, String content, Long operatorId, String runtimeUuid, String aiOpenDialogPara, String workflowSteps) {
        try {
            AiConversationContentEntity entity = new AiConversationContentEntity();
            entity.setMessageId(UuidUtil.createShort());
            entity.setConversationId(conversationId);
            // 根据role设置type: 1=USER, 2=ASSISTANT (使用Spring AI标准小写常量)
            entity.setType(role == 1 ? AiMessageTypeConstant.MESSAGE_TYPE_USER : AiMessageTypeConstant.MESSAGE_TYPE_ASSISTANT);
            // 移除前导和尾随空白字符，避免Markdown渲染为代码块
            entity.setContent(StringUtils.isNotBlank(content) ? content.trim() : content);
            // 设置运行时UUID（可选）
            entity.setRuntimeUuid(runtimeUuid);
            // 设置AI打开弹窗参数（可选，OpenPage节点输出的JSON）
            entity.setAi_open_dialog_para(aiOpenDialogPara);
            // 设置工作流思考步骤JSON（可选，刷新页面后恢复显示）
            entity.setWorkflow_steps(workflowSteps);

            // 获取默认LLM模型配置并设置模型字段
            try {
                AiModelConfigVo defaultModel = aiModelConfigService.getDefaultModelConfigWithKey("LLM");
                entity.setModelSourceId(String.valueOf(defaultModel.getId()));
                entity.setProviderName(defaultModel.getProvider());
                entity.setBaseName(defaultModel.getModelName());
            } catch (Exception e) {
                // 降级处理: 获取失败时使用默认值,不影响主流程
                log.warn("Workflow场景获取默认LLM模型配置失败,使用降级值: {}", e.getMessage());
                entity.setModelSourceId(null);
                entity.setProviderName("workflow");
                entity.setBaseName("workflow");
            }

            // 设置创建人和修改人ID（使用传入的operatorId参数）
            entity.setCId(operatorId);
            entity.setUId(operatorId);

            // 设置创建时间和修改时间
            LocalDateTime now = LocalDateTime.now();
            entity.setCreateTime(now);
            entity.setUpdateTime(now);

            // 保存到MySQL
            int result = aiConversationContentMapper.insert(entity);

            if (result > 0) {
                log.info("保存对话内容成功 (Workflow场景), conversationId: {}, role: {}, contentLength: {}",
                        conversationId, role, content != null ? content.length() : 0);

                // 异步发送MQ消息到ClickHouse日志系统
                try {
                    SLogAiChatVo logVo = buildLogVo(entity, "workflow", "workflow");
                    logAiChatProducer.mqSendMq(logVo);
                    log.debug("发送AI聊天日志MQ消息成功 (Workflow场景)，conversation_id: {}, role: {}",
                            conversationId, role);
                } catch (Exception e) {
                    // 日志发送失败不影响主业务，仅记录错误
                    log.error("发送AI聊天日志MQ消息失败 (Workflow场景)，conversation_id: {}, role: {}",
                            conversationId, role, e);
                }

                AiConversationContentVo vo = new AiConversationContentVo();
                BeanUtils.copyProperties(entity, vo);
                // 手动设置message_id（因为Entity字段名是messageId，VO字段名是message_id，BeanUtils不会自动拷贝）
                vo.setMessage_id(entity.getMessageId());
                return vo;
            }

            return null;
        } catch (Exception e) {
            log.error("保存对话内容失败 (Workflow场景), conversationId: {}, role: {}",
                    conversationId, role, e);
            throw new RuntimeException("保存对话内容失败", e);
        }
    }

    /**
     * 保存对话内容（包含模型信息）
     *
     * @param conversationId 对话ID
     * @param type 内容类型
     * @param content 内容
     * @param modelSourceId 模型源ID
     * @param providerName AI提供商名称
     * @param baseName 基础模型名称
     * @param operatorId 操作员ID
     * @return 保存的对话内容VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContentVo saveConversationContent(String conversationId, String type, String content,
                                                          String modelSourceId, String providerName, String baseName, Long operatorId) {
        try {
            AiConversationContentEntity entity = new AiConversationContentEntity();
            entity.setMessageId(UuidUtil.createShort());
            entity.setConversationId(conversationId);
            entity.setType(type);
            // 移除前导和尾随空白字符，避免Markdown渲染为代码块
            entity.setContent(StringUtils.isNotBlank(content) ? content.trim() : content);
            entity.setModelSourceId(modelSourceId);
            entity.setProviderName(providerName);
            entity.setBaseName(baseName);

            // 设置创建人和修改人ID（使用传入的operatorId参数）
            entity.setCId(operatorId);
            entity.setUId(operatorId);

            // 设置创建时间和修改时间
            LocalDateTime now = LocalDateTime.now();
            entity.setCreateTime(now);
            entity.setUpdateTime(now);

            // 1. 保存到MySQL
            int result = aiConversationContentMapper.insert(entity);

            if (result > 0) {
                log.info("保存对话内容成功, conversationId: {}, type: {}, provider: {}, model: {}",
                        conversationId, type, providerName, baseName);

                // 2. 异步发送MQ消息到ClickHouse日志系统
                try {
                    SLogAiChatVo logVo = buildLogVo(entity, providerName, baseName);
                    logAiChatProducer.mqSendMq(logVo);
                    log.debug("发送AI聊天日志MQ消息成功，conversation_id: {}, type: {}",
                            conversationId, type);
                } catch (Exception e) {
                    // 日志发送失败不影响主业务，仅记录错误
                    log.error("发送AI聊天日志MQ消息失败，conversation_id: {}, type: {}",
                            conversationId, type, e);
                }

                AiConversationContentVo vo = new AiConversationContentVo();
                BeanUtils.copyProperties(entity, vo);
                // 手动设置message_id（因为Entity字段名是messageId，VO字段名是message_id，BeanUtils不会自动拷贝）
                vo.setMessage_id(entity.getMessageId());
                return vo;
            }

            return null;
        } catch (Exception e) {
            log.error("保存对话内容失败, conversationId: {}, provider: {}, model: {}",
                    conversationId, providerName, baseName, e);
            throw new RuntimeException("保存对话内容失败", e);
        }
    }

    /**
     * 构建AI聊天日志VO对象
     *
     * <p>从MySQL实体对象转换为MQ消息VO对象
     * <p>补充应用层字段：tenant_code、c_name、request_id
     * <p>使用参数传入的模型信息：provider_name、base_name
     *
     * @param entity MySQL实体对象
     * @param providerName AI提供商名称
     * @param baseName 基础模型名称
     * @return SLogAiChatVo MQ消息VO对象
     */
    private SLogAiChatVo buildLogVo(AiConversationContentEntity entity, String providerName, String baseName) {
        SLogAiChatVo vo = new SLogAiChatVo();

        // 从entity拷贝基础字段
        vo.setConversation_id(entity.getConversationId());
        vo.setType(entity.getType());
        vo.setContent(entity.getContent());
        vo.setModel_source_id(entity.getModelSourceId());
        vo.setC_id(entity.getCId());
        // MyBatis Plus自动填充只在数据库层面，不会回填到entity对象，所以直接使用当前时间
        vo.setC_time(java.time.LocalDateTime.now());

        // 设置租户编码（从当前数据源上下文获取）
        vo.setTenant_code(DataSourceHelper.getCurrentDataSourceName());

        // 设置创建人名称（从entity获取，如果为null则留空）
        vo.setC_name(entity.getCId() != null ? String.valueOf(entity.getCId()) : null);

        // 设置请求标识（使用conversation_id作为请求标识）
        vo.setRequest_id(entity.getConversationId());

        // 设置模型信息（使用方法参数传入的值）
        if (StringUtils.isNotBlank(providerName)) {
            vo.setProvider_name(providerName);
        }
        if (StringUtils.isNotBlank(baseName)) {
            vo.setBase_name(baseName);
        }

        return vo;
    }

    /**
     * 根据消息ID删除单条消息记录
     *
     * @param messageId 消息ID (对应数据库message_id字段)
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByMessageId(String messageId) {
        try {
            int result = aiConversationContentMapper.deleteByMessageId(messageId);
            log.info("删除对话消息成功, message_id: {}, result: {}", messageId, result);
            return result > 0;
        } catch (Exception e) {
            log.error("删除对话消息失败, message_id: {}", messageId, e);
            throw new RuntimeException("删除对话消息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据对话ID删除对话历史记录
     *
     * @param conversationId 对话ID
     * @return 删除的记录数
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByConversationId(String conversationId) {
        try {
            int count = aiConversationContentMapper.deleteByConversationId(conversationId);
            log.info("删除对话历史记录成功, conversation_id: {}, 删除数量: {}", conversationId, count);
            return count;
        } catch (Exception e) {
            log.error("删除对话历史记录失败, conversation_id: {}", conversationId, e);
            throw new RuntimeException("删除对话历史记录失败", e);
        }
    }

    /**
     * 保存对话内容（包含模型信息和RAG引用记录）
     *
     * 注意: RAG引用功能已被移除,此方法仅保存基础对话内容
     *
     * @param conversationId 对话ID
     * @param type 内容类型（USER/ASSISTANT）
     * @param content 内容
     * @param modelSourceId 模型源ID
     * @param providerName AI提供商名称
     * @param baseName 基础模型名称
     * @param operatorId 操作员ID
     * @param embeddingScores 向量检索结果Map（已废弃,不再使用）
     * @param kbId 知识库ID（已废弃,不再使用）
     * @param entitiesFromQuestion 从问题中提取的实体JSON（已废弃,不再使用）
     * @param graphFromStore 从图数据库检索的图谱JSON（已废弃,不再使用）
     * @param entityCount 实体数量（已废弃,不再使用）
     * @param relationCount 关系数量（已废弃,不再使用）
     * @return 保存的对话内容VO
     * @deprecated RAG引用功能已移除,建议直接调用saveConversationContent()
     */
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContentVo saveConversationContentWithReferences(
            String conversationId, String type, String content,
            String modelSourceId, String providerName, String baseName, Long operatorId,
            Map<String, Double> embeddingScores,
            String kbId, String entitiesFromQuestion, String graphFromStore,
            Integer entityCount, Integer relationCount) {

        log.warn("【已废弃】saveConversationContentWithReferences方法调用,RAG引用功能已移除,仅保存基础对话内容");
        // 仅保存基础对话内容,忽略RAG相关参数
        return saveConversationContent(
                conversationId, type, content, modelSourceId, providerName, baseName, operatorId);
    }

    /**
     * 根据运行时ID构建工作流思考步骤JSON
     * 从ai_conversation_runtime_node和ai_workflow_node表重建前端steps格式
     *
     * @param runtimeId 运行时ID（ai_conversation_runtime.id）
     * @return JSON字符串，如果无节点数据返回null
     */
    public String buildWorkflowStepsJson(Long runtimeId) {
        if (runtimeId == null) {
            return null;
        }

        List<AiConversationRuntimeNodeVo> nodes = conversationRuntimeNodeService.listByWfRuntimeId(runtimeId);
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }

        JSONArray stepsArray = new JSONArray();

        // 检测是否存在虚拟节点（新格式：workerType="virtual_analysis" 或 "virtual_agent_call"）
        // 或旧格式（workerType="orchestrator" 或 "workflow"）
        boolean hasVirtualNodes = nodes.stream()
                .anyMatch(n -> {
                    JSONObject od = n.getOutputData();
                    if (od == null) return false;
                    String workerType = od.getString("workerType");
                    return "virtual_analysis".equals(workerType) || "virtual_agent_call".equals(workerType);
                });

        // 向后兼容：如果没有新格式虚拟节点，检查是否有旧格式 Orchestrator 节点
        boolean hasOldFormatNodes = !hasVirtualNodes && nodes.stream()
                .anyMatch(n -> Long.valueOf(0L).equals(n.getNodeId()));

        if (hasVirtualNodes) {
            // 新格式：直接遍历所有虚拟节点，按 c_time 顺序添加（已在 Mapper 中 ORDER BY c_time ASC）
            nodes.stream()
                .filter(n -> {
                    JSONObject od = n.getOutputData();
                    if (od == null) return false;
                    String workerType = od.getString("workerType");
                    return "virtual_analysis".equals(workerType) || "virtual_agent_call".equals(workerType);
                })
                .forEach(virtualNode -> {
                    JSONObject step = new JSONObject();
                    step.put("nodeUuid", virtualNode.getRuntimeNodeUuid());

                    String workerType = virtualNode.getOutputData().getString("workerType");
                    if ("virtual_analysis".equals(workerType)) {
                        step.put("nodeName", "Classifier");
                    } else {
                        step.put("nodeName", "AgentCall");
                    }

                    step.put("nodeTitle", virtualNode.getOutputData().getString("nodeTitle"));
                    // 状态：3=成功→done，2=运行中→running，其他→running
                    step.put("status", virtualNode.getStatus() != null && virtualNode.getStatus() == 3 ? "done" : "running");
                    // 耗时：u_time - c_time（毫秒）
                    long duration = 0;
                    if (virtualNode.getC_time() != null && virtualNode.getU_time() != null) {
                        duration = java.time.Duration.between(virtualNode.getC_time(), virtualNode.getU_time()).toMillis();
                    }
                    step.put("duration", duration);
                    stepsArray.add(step);
                });
        } else if (hasOldFormatNodes) {
            // 旧格式兼容：保留原有逻辑
            // 补充 __virtual_analysis__ 虚拟步骤
            JSONObject analysisStep = new JSONObject();
            analysisStep.put("nodeUuid", "__virtual_analysis__");
            analysisStep.put("nodeName", "Classifier");
            analysisStep.put("nodeTitle", "问题分析");
            analysisStep.put("status", "done");
            analysisStep.put("duration", 0);
            stepsArray.add(analysisStep);

            // 补充 __agent_call__ 虚拟步骤
            nodes.stream()
                    .filter(n -> Long.valueOf(0L).equals(n.getNodeId()))
                    .filter(n -> {
                        JSONObject od = n.getOutputData();
                        return od != null && "workflow".equals(od.getString("workerType"));
                    })
                    .forEach(workerNode -> {
                        String workflowTitle = workerNode.getOutputData().getString("title");
                        JSONObject agentCallStep = new JSONObject();
                        agentCallStep.put("nodeUuid", "__agent_call__");
                        agentCallStep.put("nodeName", "AgentCall");
                        agentCallStep.put("nodeTitle", workflowTitle != null ? workflowTitle : "工作流");
                        agentCallStep.put("status", "done");
                        agentCallStep.put("duration", 0);
                        stepsArray.add(agentCallStep);
                    });
        }

        // 遍历真实节点（跳过 nodeId=0 的所有节点，包括虚拟节点和旧格式 Orchestrator 节点）
        for (AiConversationRuntimeNodeVo node : nodes) {
            if (Long.valueOf(0L).equals(node.getNodeId())) {
                continue; // Orchestrator 节点已通过虚拟步骤处理，此处跳过
            }
            JSONObject step = new JSONObject();
            step.put("nodeUuid", node.getRuntimeNodeUuid());
            String componentName = workflowNodeService.getComponentNameByNodeId(node.getNodeId());
            step.put("nodeName", componentName);
            step.put("nodeTitle", node.getNodeTitle());
            // 执行状态：3=成功→done，其他→running
            step.put("status", node.getStatus() != null && node.getStatus() == 3 ? "done" : "running");
            // 耗时：u_time - c_time（毫秒）
            long duration = 0;
            if (node.getC_time() != null && node.getU_time() != null) {
                duration = java.time.Duration.between(node.getC_time(), node.getU_time()).toMillis();
            }
            step.put("duration", duration);
            // 从outputData重建summary，与实时流式执行时createNodeCompleteData的格式保持一致
            JSONObject summary = buildSummaryFromOutputData(componentName, node.getOutputData(), node.getInputData());
            if (summary != null && !summary.isEmpty()) {
                step.put("summary", summary);
            }
            stepsArray.add(step);
        }

        return stepsArray.toJSONString();
    }

    /**
     * 从outputData/inputData重建节点summary，与实时流式执行时createNodeCompleteData的格式保持一致
     * outputData格式: {"output":{"type":"1","title":"","value":"..."}, "result":{"type":"1","title":"","value":"..."}, ...}
     */
    private JSONObject buildSummaryFromOutputData(String componentName, JSONObject outputData, JSONObject inputData) {
        if (outputData == null) return null;
        JSONObject summary = new JSONObject();
        // 提取output.value作为outputText（大多数节点类型通用）
        JSONObject outputField = outputData.getJSONObject("output");
        String outputValue = outputField != null ? outputField.getString("value") : null;
        // Classifier节点：提取result字段
        if ("Classifier".equals(componentName)) {
            JSONObject resultField = outputData.getJSONObject("result");
            String resultValue = resultField != null ? resultField.getString("value") : null;
            if (resultValue == null) resultValue = outputValue;
            if (resultValue != null) {
                summary.put("result", resultValue);
                summary.put("outputText", resultValue);
            }
        } else if ("KnowledgeRetrieval".equals(componentName)) {
            JSONObject matchCountField = outputData.getJSONObject("matchCount");
            String matchCount = matchCountField != null ? matchCountField.getString("value") : null;
            if (matchCount != null) summary.put("matchCount", matchCount);
        } else if ("McpTool".equals(componentName)) {
            JSONObject toolNameField = outputData.getJSONObject("toolName");
            String toolName = toolNameField != null ? toolNameField.getString("value") : null;
            if (toolName != null) summary.put("toolName", toolName);
            if (outputValue != null) summary.put("outputText", outputValue);
        } else {
            // Answer/LLM/Template/SubWorkflow/OpenPage/HumanFeedback等：直接用output.value
            if (outputValue != null) summary.put("outputText", outputValue);
        }
        return summary.isEmpty() ? null : summary;
    }

    /**
     * 根据messageId更新工作流思考步骤JSON
     * 前端流结束后上报完整steps（含虚拟节点和summary），直接覆盖保存
     */
    public void updateWorkflowSteps(String messageId, String workflowSteps) {
        extAiConversationContentMapper.updateWorkflowStepsByMessageId(messageId, workflowSteps);
    }

    /**
     * 根据runtimeUuid查询AI消息
     */
    public AiConversationContentVo findByRuntimeUuid(String runtimeUuid) {
        return extAiConversationContentMapper.selectByRuntimeUuid(runtimeUuid);
    }

    /**
     * resume场景：将新内容追加到已有AI消息后面
     * 通过runtimeUuid找到第一次执行时保存的AI消息，追加resume执行的内容
     *
     * @param runtimeUuid 工作流运行时UUID
     * @param appendContent 要追加的内容
     * @return 更新后的完整内容，若未找到已有消息则返回null
     */
    public String appendContentByRuntimeUuid(String runtimeUuid, String appendContent) {
        if (StringUtils.isBlank(runtimeUuid) || StringUtils.isBlank(appendContent)) {
            return null;
        }
        AiConversationContentVo existing = extAiConversationContentMapper.selectByRuntimeUuid(runtimeUuid);
        if (existing == null || StringUtils.isBlank(existing.getMessage_id())) {
            return null;
        }
        String existingContent = StringUtils.defaultString(existing.getContent(), "");
        String newContent = existingContent.isEmpty() ? appendContent : existingContent + "\n\n" + appendContent;
        extAiConversationContentMapper.updateContentByMessageId(existing.getMessage_id(), newContent);
        return newContent;
    }

}
