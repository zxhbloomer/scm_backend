package com.xinyirun.scm.core.bpm.config;

import java.util.regex.Pattern;

/**
 * 审批流程全局变量定义
 *
 * @author : willian fu
 * @date : 2022/9/4
 */
public class WflowGlobalVarDef {

    // 审批自动驳回
    public static final String WFLOW_TASK_REFUSE = "WFLOW_TASK_REFUSE";
    // 审批自动通过
    public static final String WFLOW_TASK_AGRRE = "WFLOW_TASK_AGRRE";

    // 流程Node节点变量KEY
    public static final String WFLOW_NODE_PROPS = "WFLOW_NODE_PROPS";
    // 表单变量KEY
    public static final String WFLOW_FORMS = "WFLOW_FORMS";

    // 系统审批管理员角色
    public static final String WFLOW_APPROVAL_ADMIN = "WFLOW_APPROVAL_ADMIN";

    // 流程发起人变量
    public static final String INITIATOR = "initiator";

    // 模板变量替换正则编译
    public static final Pattern TEMPLATE_REPLACE_REG = Pattern.compile("\\$\\{(.+?)\\}");

    // 流程引擎id前缀
    public static final String FLOWABLE = "Flowable";

    // 流程引擎id后缀
    public static final String BPMN = ".bpmn";

    // 审批流程任务变量前缀
    public static final String APPROVE = "approve_";

    /**
     * 转交用户code
     */
    public static final String TRANSFER_ASSIGNEE_CODE = "transfer_assignee_code_";

    // 	意见评论的标识，可能用于在任务或流程中记录用户的评论意见。
    public static final String OPINION_COMMENT = "opinion";

    // OPTION_COMMENT	选项评论的标识，可能用于记录用户选择的选项。
    public static final String OPTION_COMMENT = "option";

    // COMMENTS_COMMENT	评论的标识，可能用于收集用户在流程中的各种评论信息。
    public static final String COMMENTS_COMMENT = "comments";
}
