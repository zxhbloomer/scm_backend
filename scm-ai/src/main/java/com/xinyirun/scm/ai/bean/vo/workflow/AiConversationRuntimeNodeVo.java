package com.xinyirun.scm.ai.bean.vo.workflow;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI Chat调用Workflow节点执行VO
 *
 * <p>用于AI Chat调用Workflow时的节点执行数据传输</p>
 *
 * @author SCM-AI团队
 * @since 2025-11-11
 */
@Data
public class AiConversationRuntimeNodeVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 运行时节点UUID(业务主键)
     */
    private String runtimeNodeUuid;

    /**
     * AI Chat工作流运行时ID
     */
    private Long conversationWorkflowRuntimeId;

    /**
     * 节点ID
     */
    private Long nodeId;

    /**
     * 节点标题（直接从ai_workflow_node表查询）
     * 前端执行详情页面直接使用此字段，避免通过nodeId匹配workflow.nodes
     */
    private String nodeTitle;

    /**
     * 节点输入数据(JSON对象)
     */
    private JSONObject inputData;

    /**
     * 节点输出数据(JSON对象)
     */
    private JSONObject outputData;

    /**
     * 执行状态(1-等待中,2-运行中,3-成功,4-失败)
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

    /**
     * 输入Token数(从ai_token_usage查询)
     */
    private Long promptTokens;

    /**
     * 输出Token数(从ai_token_usage查询)
     */
    private Long completionTokens;

    /**
     * 总Token数(从ai_token_usage查询)
     */
    private Long totalTokens;
}
