package com.xinyirun.scm.ai.workflow.node.classifier;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
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
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流分类器节点
 *
 * 此节点使用LLM进行文本分类，支持自定义分类目录和路由。
 */
@Slf4j
public class ClassifierNode extends AbstractWfNode {

    public ClassifierNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeEntity nodeDef, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        ClassifierNodeConfig nodeConfig = checkAndGetConfig(ClassifierNodeConfig.class);

        // 验证分类列表不为空
        if (nodeConfig.getCategories().size() < 2) {
            log.warn("分类器设置的分类过少, nodeUuid: {}, title: {}", node.getNodeUuid(), node.getName());
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

        // 生成分类提示词
        String prompt = ClassifierPrompt.createPrompt(defaultInputOpt.get().valueToString(), nodeConfig.getCategories());

        // 调用LLM进行分类
        NodeIOData llmOutput = WorkflowUtil.invokeLLM(wfState, nodeConfig.getModelName(), prompt);
        ClassifierLLMResp classifierResp = JsonUtil.fromJson(llmOutput.valueToString(), ClassifierLLMResp.class);

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

        // 构造输出
        NodeIODataTextContent content = new NodeIODataTextContent();
        content.setValue(classifierResp.getCategoryName());
        content.setTitle("default");

        List<NodeIOData> result = List.of(
            NodeIOData.builder()
                .name(DEFAULT_OUTPUT_PARAM_NAME)
                .content(content)
                .build()
        );

        // 返回结果并指定下一个节点
        String nextNodeUuid = categoryOpt.get().getTargetNodeUuid();
        return NodeProcessResult.builder()
                .nextNodeUuid(nextNodeUuid)
                .content(result)
                .build();
    }
}
