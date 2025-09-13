/**
 * Thread历史活动响应对象，用于BPM工作流历史活动信息的返回
 */
package com.xinyirun.scm.ai.thread.dto;

import java.util.Date;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadHistoryActivityResponse {
    private String id;                    // 活动实例ID
    private String activityId;            // 活动定义ID
    private String activityName;          // 活动名称
    private String activityType;          // 活动类型
    private String processDefinitionId;   // 流程定义ID
    private String processInstanceId;     // 流程实例ID
    private String executionId;           // 执行实例ID
    private String taskId;                // 任务ID
    private String calledProcessInstanceId; // 被调用的子流程实例ID
    private String assignee;              // 处理人
    private Date startTime;               // 开始时间
    private Date endTime;                 // 结束时间
    private Long durationInMillis;        // 持续时间
    private String tenantId;              // 租户ID
    private String description;           
    
    private Map<String, Object> taskLocalVariables;    // 任务局部变量
    private Map<String, Object> processVariables;      // 流程变量
}