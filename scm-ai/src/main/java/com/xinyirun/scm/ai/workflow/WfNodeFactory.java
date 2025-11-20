package com.xinyirun.scm.ai.workflow;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import com.xinyirun.scm.ai.workflow.node.EndNode;
import com.xinyirun.scm.ai.workflow.node.answer.LLMAnswerNode;
import com.xinyirun.scm.ai.workflow.node.classifier.ClassifierNode;
import com.xinyirun.scm.ai.workflow.node.httprequest.HttpRequestNode;
import com.xinyirun.scm.ai.workflow.node.humanfeedback.HumanFeedbackNode;
import com.xinyirun.scm.ai.workflow.node.knowledgeretrieval.KnowledgeRetrievalNode;
import com.xinyirun.scm.ai.workflow.node.documentextractor.DocumentExtractorNode;
import com.xinyirun.scm.ai.workflow.node.faqextractor.FaqExtractorNode;
import com.xinyirun.scm.ai.workflow.node.keywordextractor.KeywordExtractorNode;
import com.xinyirun.scm.ai.workflow.node.mailsend.MailSendNode;
import com.xinyirun.scm.ai.workflow.node.start.StartNode;
import com.xinyirun.scm.ai.workflow.node.mcptool.McpToolNode;
import com.xinyirun.scm.ai.workflow.node.subworkflow.SubWorkflowNode;
import com.xinyirun.scm.ai.workflow.node.switcher.SwitcherNode;
import com.xinyirun.scm.ai.workflow.node.template.TemplateNode;

/**
 * 工作流节点工厂类
 *
 * 功能：根据组件类型创建对应的节点实例
 *
 * @author zxh
 * @since 2025-10-25
 */
public class WfNodeFactory {

    /**
     * 根据组件类型创建对应的节点实例
     *
     * @param wfComponent 组件定义
     * @param nodeDefinition 节点定义
     * @param wfState 工作流状态
     * @param nodeState 节点状态
     * @return 节点实例
     */
    public static AbstractWfNode create(AiWorkflowComponentEntity wfComponent,
                                       AiWorkflowNodeVo nodeDefinition,
                                       WfState wfState,
                                       WfNodeState nodeState) {
        AbstractWfNode wfNode = null;
        String componentName = wfComponent.getName();

        if ("Start".equals(componentName)) {
            wfNode = new StartNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("Answer".equals(componentName)) {
            wfNode = new LLMAnswerNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("Classifier".equals(componentName)) {
            wfNode = new ClassifierNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("Switcher".equals(componentName)) {
            wfNode = new SwitcherNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("Template".equals(componentName)) {
            wfNode = new TemplateNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("KnowledgeRetrieval".equals(componentName)) {
            wfNode = new KnowledgeRetrievalNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("HumanFeedback".equals(componentName)) {
            wfNode = new HumanFeedbackNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("HttpRequest".equals(componentName)) {
            wfNode = new HttpRequestNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("MailSend".equals(componentName)) {
            wfNode = new MailSendNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("FaqExtractor".equals(componentName)) {
            wfNode = new FaqExtractorNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("KeywordExtractor".equals(componentName)) {
            wfNode = new KeywordExtractorNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("DocumentExtractor".equals(componentName)) {
            wfNode = new DocumentExtractorNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("SubWorkflow".equals(componentName)) {
            wfNode = new SubWorkflowNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("McpTool".equals(componentName)) {
            wfNode = new McpToolNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("End".equals(componentName)) {
            wfNode = new EndNode(wfComponent, nodeDefinition, wfState, nodeState);
        }

        return wfNode;
    }
}
