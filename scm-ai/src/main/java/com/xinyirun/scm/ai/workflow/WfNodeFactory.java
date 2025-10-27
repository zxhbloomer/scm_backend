package com.xinyirun.scm.ai.workflow;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
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
import com.xinyirun.scm.ai.workflow.node.switcher.SwitcherNode;
import com.xinyirun.scm.ai.workflow.node.template.TemplateNode;

/**
 * 工作流节点工厂类
 * 参考 aideepin: com.moyz.adi.common.workflow.WfNodeFactory
 *
 * 功能：根据组件类型创建对应的节点实例
 *
 * @author SCM-AI团队
 * @since 2025-10-25
 */
public class WfNodeFactory {

    /**
     * 根据组件类型创建对应的节点实例
     * 参考 aideepin: WfNodeFactory.create() 第24-78行
     *
     * @param wfComponent 组件定义
     * @param nodeDefinition 节点定义
     * @param wfState 工作流状态
     * @param nodeState 节点状态
     * @return 节点实例
     */
    public static AbstractWfNode create(AiWorkflowComponentEntity wfComponent,
                                       AiWorkflowNodeEntity nodeDefinition,
                                       WfState wfState,
                                       WfNodeState nodeState) {
        AbstractWfNode wfNode = null;
        String componentName = wfComponent.getName();

        // 参考 aideepin 第26-74行的 switch 逻辑
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
        } else if ("End".equals(componentName)) {
            wfNode = new EndNode(wfComponent, nodeDefinition, wfState, nodeState);
        }

        return wfNode;
    }
}
