package com.xinyirun.scm.ai.workflow.node;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.utils.JsonUtil;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.WfNodeIODataUtil;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流结束节点
 * 参考 aideepin EndNode 完整实现
 *
 * 此节点是工作流执行的最后一个节点，负责：
 * - 将工作流执行结果渲染成最终输出格式
 * - 支持模板化结果输出
 * - 支持文件内容转换为Markdown格式
 */
@Slf4j
public class EndNode extends AbstractWfNode {

    public EndNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo nodeDef, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        List<NodeIOData> result = new ArrayList<>();

        // 使用 Fastjson2 的 JSONObject
        JSONObject nodeConfigObj = node.getNodeConfig();
        String output = "";

        if (null == nodeConfigObj || nodeConfigObj.isEmpty()) {
            log.warn("EndNode result config is empty, nodeUuid: {}, title: {}", node.getUuid(), node.getTitle());
        } else {
            // 从节点配置中获取结果模板
            String resultTemplate = nodeConfigObj.getString("result");
            if (null != resultTemplate) {
                // 将文件内容转换为Markdown格式 (参考 aideepin)
                WfNodeIODataUtil.changeFilesContentToMarkdown(state.getInputs());
                // 使用标准模板渲染方法 (参考 aideepin)
                output = WorkflowUtil.renderTemplate(resultTemplate, state.getInputs());
            }
        }

        result.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", output));
        return NodeProcessResult.builder().content(result).build();
    }
}
