package com.xinyirun.scm.ai.thread.consts;

import com.xinyirun.scm.ai.thread.enums.ThreadProcessStatusEnum;

public class ThreadConsts {

    public static final String THREAD_PROCESS_NAME = "thread.process.name";
    
    public static final String THREAD_PROCESS_KEY = "threadProcess";

    public static final String THREAD_PROCESS_PATH = "processes/thread-process.bpmn20.xml";

    public static final String THREAD_VARIABLE_THREAD_UID = "threadUid";

    public static final String THREAD_VARIABLE_ORGUID = "orgUid";

    public static final String THREAD_VARIABLE_STATUS = "status";
    
    public static final String THREAD_VARIABLE_THREAD_STATUS = "threadStatus";

    public static final String THREAD_VARIABLE_USER_UID = "userUid";

    public static final String THREAD_VARIABLE_AGENT_UID = "agentUid";

    public static final String THREAD_VARIABLE_WORKGROUP_UID = "workgroupUid";

    public static final String THREAD_VARIABLE_START_TIME = "startTime";

    public static final String THREAD_VARIABLE_SLA_TIME = "slaTime";

    public static final String THREAD_VARIABLE_ASSIGNEE = "assignee";
    
    public static final String THREAD_VARIABLE_ROBOT_ENABLED = "robotEnabled";
    
    public static final String THREAD_VARIABLE_NEED_HUMAN_SERVICE = "needHumanService";
    
    public static final String THREAD_VARIABLE_AGENTS_OFFLINE = "agentsOffline";
    
    public static final String THREAD_VARIABLE_AGENTS_BUSY = "agentsBusy";
    
    public static final String THREAD_VARIABLE_ROBOT_IDLE_TIMEOUT = "robotIdleTimeout";
    
    public static final String THREAD_VARIABLE_HUMAN_IDLE_TIMEOUT = "humanIdleTimeout";
    
    public static final String THREAD_VARIABLE_LAST_VISITOR_MESSAGE_TIME = "lastVisitorMessageTime";
    
    public static final String THREAD_VARIABLE_LAST_VISITOR_ACTIVITY_TIME = "lastVisitorActivityTime";
    
    public static final String THREAD_VARIABLE_THREAD_TYPE = "threadType";
    public static final String THREAD_TYPE_AGENT = "agent";
    public static final String THREAD_TYPE_WORKGROUP = "workgroup";
    public static final String THREAD_TYPE_ROBOT = "robot";

    public static final String THREAD_GATEWAY_IS_ROBOT_ENABLED = "isRobotEnabled";
    
    public static final String THREAD_GATEWAY_TRANSFER_TO_HUMAN = "transferToHuman";
    
    public static final String THREAD_GATEWAY_IS_AGENTS_OFFLINE = "isAgentsOffline";
    
    public static final String THREAD_GATEWAY_IS_AGENTS_BUSY = "isAgentsBusy";

    public static final String THREAD_VARIABLE_ROBOT_SERVICE_EXECUTION_COUNT = "robotServiceExecutionCount";
    public static final String THREAD_VARIABLE_ROBOT_SERVICE_START_TIME = "robotServiceStartTime";
    public static final String THREAD_VARIABLE_ROBOT_SERVICE_END_TIME = "robotServiceEndTime";
    public static final String THREAD_VARIABLE_ROBOT_SERVICE_DURATION = "robotServiceDuration";
    public static final String THREAD_VARIABLE_ROBOT_SERVICE_SUMMARY = "robotServiceSummary";
    public static final String THREAD_VARIABLE_ROBOT_SERVICE_ERROR = "robotServiceError";
    public static final String THREAD_VARIABLE_TRANSFER_REASON = "transferReason";
    public static final String THREAD_VARIABLE_TRANSFER_PRIORITY = "transferPriority";
    
    public static final int THREAD_MAX_ROBOT_EXECUTION_COUNT = 3;

    public static final String ACTIVITY_TYPE_SEQUENCE_FLOW = "sequenceFlow";
    public static final String ACTIVITY_TYPE_EXCLUSIVE_GATEWAY = "exclusiveGateway";
    public static final String ACTIVITY_TYPE_COMMENT = "comment";
    
    public static final String ACTIVITY_ID_TRANSFER_TO_HUMAN_TASK = "transferToHumanTask";
    public static final String ACTIVITY_ID_AGENTS_OFFLINE_SERVICE = "agentsOfflineService";
    public static final String ACTIVITY_ID_END = "end";
    public static final String ACTIVITY_ID_ROBOT_SERVICE = "robotService";
    public static final String ACTIVITY_ID_HUMAN_SERVICE = "humanService";
    public static final String ACTIVITY_ID_QUEUE_SERVICE = "queueService";
    
    public static final int DEFAULT_SLA_TIME = 30 * 60 * 1000;
    public static final int DEFAULT_HUMAN_IDLE_TIMEOUT = 15 * 60 * 1000;
    public static final int DEFAULT_ROBOT_IDLE_TIMEOUT = 5 * 60 * 1000;
    
    public static final String THREAD_VARIABLE_HUMAN_IDLE_TIMEOUT_ISO = "humanIdleTimeoutISO";
    public static final String THREAD_VARIABLE_ROBOT_IDLE_TIMEOUT_ISO = "robotIdleTimeoutISO";
    public static final String THREAD_VARIABLE_SLA_TIME_ISO = "slaTimeISO";
    
    public static final String THREAD_STATUS_NEW = ThreadProcessStatusEnum.NEW.name();
    public static final String THREAD_STATUS_WAITING = ThreadProcessStatusEnum.QUEUING.name();
    public static final String THREAD_STATUS_ONGOING = ThreadProcessStatusEnum.CHATTING.name();
    public static final String THREAD_STATUS_CLOSED = ThreadProcessStatusEnum.CLOSED.name();
    
    public static final String THREAD_STATUS_TRANSFERRED = "TRANSFERRED";
    
    public static final String THREAD_STATUS_QUEUING = ThreadProcessStatusEnum.QUEUING.name();
    public static final String THREAD_STATUS_OFFLINE = "OFFLINE";
    
    public static final String THREAD_STATUS_INVITE = "INVITE_PENDING";
    
    public static final String THREAD_STATUS_TRANSFER = "TRANSFER_PENDING";
    
    public static final String THREAD_VARIABLE_ROBOT_UNANSWERED_COUNT = "robotUnansweredCount";
    public static final String THREAD_VARIABLE_VISITOR_REQUESTED_TRANSFER = "visitorRequestedTransfer";
    
    public static final String THREAD_VARIABLE_QUEUE_START_TIME = "queueStartTime";
    
    public static final String THREAD_VARIABLE_TRANSFER_TYPE = "transferType";
    public static final String TRANSFER_TYPE_UI = "UI";
    public static final String TRANSFER_TYPE_KEYWORD = "KEYWORD";
    public static final String TRANSFER_TYPE_TIMEOUT = "TIMEOUT";
}