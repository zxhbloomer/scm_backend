package com.xinyirun.scm.ai.workflow.node.classifier;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.utils.JsonUtil;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.data.NodeIODataTextContent;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import com.xinyirun.scm.common.exception.system.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流分类器节点
 *
 * 此节点使用LLM进行文本分类，支持自定义分类目录和路由。
 *
 * 输出数据：
 * 1. 原始输入（保持原参数名，通常为"input"）- 传递给下游节点继续处理
 * 2. classification - 分类结果名称，可在下游节点提示词中使用 ${classification}
 *
 * 路由机制：
 * 根据分类结果自动路由到对应的 targetNodeUuid 节点
 */
@Slf4j
public class ClassifierNode extends AbstractWfNode {

    public ClassifierNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo nodeDef, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        ClassifierNodeConfig nodeConfig = checkAndGetConfig(ClassifierNodeConfig.class);

        // 验证分类列表不为空
        if (nodeConfig.getCategories().size() < 2) {
            log.warn("分类器设置的分类过少, nodeUuid: {}, title: {}", node.getUuid(), node.getTitle());
            throw new BusinessException("分类器至少需要2个分类");
        }

        // 处理分类描述中的变量替换
        for (ClassifierCategory classifierCategory : nodeConfig.getCategories()) {
            String categoryName = classifierCategory.getCategoryName();
            if (StringUtils.isNotBlank(categoryName)) {
                categoryName = WorkflowUtil.renderTemplate(categoryName, state.getInputs());
                classifierCategory.setCategoryName(categoryName);
            }
        }

        // 获取输入文本
        Optional<NodeIOData> defaultInputOpt = state.getDefaultInput();
        if (defaultInputOpt.isEmpty()) {
            throw new BusinessException("分类器缺少输入数据");
        }

        // 生成分类提示词（支持可选的分类指令）
        String instruction = WorkflowUtil.renderTemplate(nodeConfig.getInstruction(), state.getInputs());
        String prompt = ClassifierPrompt.createPrompt(defaultInputOpt.get().valueToString(), nodeConfig.getCategories(), instruction);

        // 调用LLM进行分类
        NodeIOData llmOutput = WorkflowUtil.invokeLLM(wfState, nodeConfig.getModelName(), prompt);
        // LLM有时返回markdown代码块包装（如 ```json{...}```），需要提取纯JSON
        String llmRawText = llmOutput.valueToString();
        String llmJsonText = extractJson(llmRawText);
        ClassifierLLMResp classifierResp = JsonUtil.fromJson(llmJsonText, ClassifierLLMResp.class);

        // 验证LLM响应
        if (classifierResp == null || StringUtils.isBlank(classifierResp.getCategoryUuid())) {
            throw new BusinessException("LLM分类响应无效");
        }

        String categoryUuid = classifierResp.getCategoryUuid();
        Optional<ClassifierCategory> categoryOpt = nodeConfig.getCategories()
                .stream()
                .filter(item -> item.getCategoryUuid().equals(categoryUuid))
                .findFirst();

        if (categoryOpt.isEmpty()) {
            log.error("找不到分类目录, categoryUuid: {}", categoryUuid);
            throw new BusinessException("找不到分类目录");
        }

        // 构造输出：分类结果名称作为 output（前端执行过程展示），同时输出 classification 变量供下游引用
        List<NodeIOData> outputs = new ArrayList<>();

        // 1. output = 分类结果名称，前端执行过程展示
        outputs.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "分类结果", classifierResp.getCategoryName()));

        // 2. classification 变量，下游节点可通过 ${classification} 引用
        NodeIODataTextContent classificationContent = new NodeIODataTextContent();
        classificationContent.setValue(classifierResp.getCategoryName());
        classificationContent.setTitle("分类结果");

        outputs.add(NodeIOData.builder()
                .name("classification")
                .content(classificationContent)
                .build());

        // 返回结果并指定下一个分支（使用 categoryUuid 作为 sourceHandle）
        // WorkflowEngine 会根据 sourceHandle 从 edge 表查找所有目标节点
        // 当同一个 categoryUuid 对应多个目标节点时，会创建虚拟并行分发节点实现并行执行
        String matchedSourceHandle = categoryOpt.get().getCategoryUuid();
        log.info("Classifier分类匹配成功, categoryUuid={}, categoryName={}, nodeUuid={}",
                matchedSourceHandle, categoryOpt.get().getCategoryName(), node.getUuid());

        return NodeProcessResult.builder()
                .nextSourceHandle(matchedSourceHandle)
                .content(outputs)
                .build();
    }

    /**
     * 从LLM响应中提取JSON字符串
     * LLM有时会用markdown代码块包装JSON，如 ```json{...}``` 或 ```{...}```
     */
    private String extractJson(String text) {
        if (text == null) return null;
        String trimmed = text.trim();
        // 去除 ```json ... ``` 或 ``` ... ``` 包装
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewline > 0 && lastFence > firstNewline) {
                return trimmed.substring(firstNewline + 1, lastFence).trim();
            }
        }
        return trimmed;
    }
}
