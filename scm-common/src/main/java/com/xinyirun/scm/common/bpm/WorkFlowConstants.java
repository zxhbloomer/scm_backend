package com.xinyirun.scm.common.bpm;

/**
 * @author LoveMyOrange
 * @create 2022-10-10 17:40
 */
public interface WorkFlowConstants {

    // PROCESS_PREFIX	流程前缀，可能用于标识流程相关的名称或ID。
    String PROCESS_PREFIX = "Flowable";

    // START_EVENT_ID	流程启动事件的ID，指代流程的开始节点。
    String START_EVENT_ID = "startEventNode";

    // END_EVENT_ID	流程结束事件的ID，指代流程的结束节点。
    String END_EVENT_ID = "endEventNode";

    // EXPRESSION_CLASS	表达式类的前缀，可能用于引用某个工具类或方法（如 exUtils）。
    String EXPRESSION_CLASS = "exUtils.";

    // DEFAULT_NULL_ASSIGNEE	默认的无分配人标识，可能用于处理未指定任务分配人的情况。
    String DEFAULT_NULL_ASSIGNEE = "100000000000";

    // DEFAULT_ADMIN_ASSIGNEE	默认的管理员分配人ID，用于特定情况下的任务分配。
    String DEFAULT_ADMIN_ASSIGNEE = "381496";

    // AUTO_REFUSE_STR	自动驳回的标识字符串，可能用于标识某个操作的类型。
    String AUTO_REFUSE_STR = "autoRefuse";

    // FLOWABLE_NAME_SPACE_NAME	Flowable 的命名空间名称，可能用于 XML 配置中。
    String FLOWABLE_NAME_SPACE_NAME = "BPM";

    // FLOWABLE_NAME_SPACE	Flowable 的命名空间 URL，通常用于 XML 定义中，指定流程引擎所用的命名空间。
    String FLOWABLE_NAME_SPACE = "http://flowable.org/bpmn";

    // VIEW_PROCESS_JSON_NAME	视图中流程的 JSON 名称，可能用于展示流程信息。
    String VIEW_PROCESS_JSON_NAME = "processJson";

    // VIEW_ASSIGNEE_USER_NAME	视图中分配用户的名称，可能用于界面显示任务分配的用户信息。
    String VIEW_ASSIGNEE_USER_NAME = "assignedUser";

    // VIEW_ID_NAME	视图中 ID 的名称，可能用于标识流程实例或任务的唯一性。
    String VIEW_ID_NAME = "id";
    String VIEW_CODE_NAME = "code";

    // ASSIGNEE_LIST_SUFFIX	分配人列表的后缀，用于构建或处理分配人列表的标识。
    String ASSIGNEE_LIST_SUFFIX = "assigneeList";

    // ASSIGNEE_NULL_ACTION_NAME	无分配人操作的名称，可能用于处理未分配人的任务。
    String ASSIGNEE_NULL_ACTION_NAME = "handler";

    // TO_PASS_ACTION	通过操作的标识，可能用于在流程中标记某个任务的通过状态。
    String TO_PASS_ACTION = "TO_PASS";

    // TO_REFUSE_ACTION	驳回操作的标识，可能用于在流程中标记某个任务的驳回状态。
    String TO_REFUSE_ACTION = "TO_REFUSE";

    // TO_ADMIN_ACTION	分配给管理员操作的标识，可能用于特定的管理员任务处理。
    String TO_ADMIN_ACTION = "TO_ADMIN";

    // TO_USER_ACTION	分配给普通用户操作的标识，可能用于用户任务的处理。
    String TO_USER_ACTION = "TO_USER";

    // OPINION_COMMENT	意见评论的标识，可能用于在任务或流程中记录用户的评论意见。
    String OPINION_COMMENT = "opinion";

    // OPTION_COMMENT	选项评论的标识，可能用于记录用户选择的选项。
    String OPTION_COMMENT = "option";

    // SIGN_COMMENT	签名评论的标识，可能用于记录任务或流程中的签名信息。
    String SIGN_COMMENT = "sign";

    // COMMENTS_COMMENT	评论的标识，可能用于收集用户在流程中的各种评论信息。
    String COMMENTS_COMMENT = "comments";

    /**
     * 转交用户code
     */
    String TRANSFER_ASSIGNEE_CODE = "transfer_assignee_code_%s";
}
