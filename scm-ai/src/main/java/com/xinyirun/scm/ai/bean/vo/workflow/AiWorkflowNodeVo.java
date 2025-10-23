package com.xinyirun.scm.ai.bean.vo.workflow;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.math.BigDecimal;

/**
 * AI工作流节点VO类
 * 对应实体类:AiWorkflowNodeEntity
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
public class AiWorkflowNodeVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 节点UUID(业务主键)
     */
    private String nodeUuid;

    /**
     * 工作流ID
     */
    private Long workflowId;

    /**
     * 组件ID
     */
    private Long workflowComponentId;

    /**
     * 节点标题
     */
    private String title;

    /**
     * 节点描述
     */
    private String remark;

    /**
     * 输入配置(JSON格式)
     */
    private ObjectNode inputConfig;

    /**
     * 节点配置(JSON格式)
     */
    private ObjectNode nodeConfig;

    /**
     * X轴位置
     */
    private BigDecimal positionX;

    /**
     * Y轴位置
     */
    private BigDecimal positionY;
}
