package com.xinyirun.scm.ai.bean.vo.workflow;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI Chat调用Workflow运行时VO
 *
 * <p>用于AI Chat调用Workflow时的运行时实例数据传输</p>
 *
 * @author SCM-AI团队
 * @since 2025-11-11
 */
@Data
public class AiConversationWorkflowRuntimeVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 运行时UUID(业务主键)
     */
    private String runtimeUuid;

    /**
     * 对话ID,格式:tenantId::uuid
     */
    private String conversationId;

    /**
     * 工作流ID
     */
    private Long workflowId;

    /**
     * 执行用户ID
     */
    private Long userId;

    /**
     * 输入数据(JSON对象)
     */
    private JSONObject inputData;

    /**
     * 输出数据(JSON对象)
     */
    private JSONObject outputData;

    /**
     * 执行状态(1-运行中,2-成功,3-失败)
     */
    private Integer status;

    /**
     * 状态说明
     */
    private String statusRemark;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人ID
     */
    private Long c_id;

    /**
     * 修改人ID
     */
    private Long u_id;

    /**
     * 数据版本(乐观锁)
     */
    private Integer dbversion;

    // ========== 扩展字段(用于详情展示) ==========

    /**
     * 工作流名称(从ai_workflow表关联查询)
     */
    private String workflow_name;

    /**
     * 工作流UUID(从ai_workflow表关联查询)
     */
    private String workflowUuid;

    /**
     * 执行时长(毫秒)(计算得出: u_time - c_time)
     */
    private Long elapsed_time;

    /**
     * 开始时间(即创建时间c_time的别名)
     */
    private LocalDateTime start_time;

    /**
     * 结束时间(即更新时间u_time的别名)
     */
    private LocalDateTime end_time;

    /**
     * 创建人姓名(从用户表关联查询)
     */
    private String c_name;
}
