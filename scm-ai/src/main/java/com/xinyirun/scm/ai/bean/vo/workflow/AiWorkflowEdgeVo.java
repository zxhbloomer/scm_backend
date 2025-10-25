package com.xinyirun.scm.ai.bean.vo.workflow;

import lombok.Data;

/**
 * AI工作流连接边VO类
 * 对应实体类:AiWorkflowEdgeEntity
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
public class AiWorkflowEdgeVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 连接边UUID(业务主键)
     */
    private String uuid;

    /**
     * 工作流ID
     */
    private Long workflowId;

    /**
     * 源节点UUID
     */
    private String sourceNodeUuid;

    /**
     * 源节点句柄
     */
    private String sourceHandle;

    /**
     * 目标节点UUID
     */
    private String targetNodeUuid;

    /**
     * 目标节点句柄
     */
    private String targetHandle;

    /**
     * 是否新增(仅用于前端标识)
     */
    private Boolean isNew;
}
