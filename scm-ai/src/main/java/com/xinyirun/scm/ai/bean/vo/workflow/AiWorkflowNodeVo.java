package com.xinyirun.scm.ai.bean.vo.workflow;

import com.alibaba.fastjson2.JSONObject;
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
    private String uuid;

    /**
     * 工作流ID
     */
    private Long workflowId;

    /**
     * 组件ID
     */
    private Long workflowComponentId;

    /**
     * 节点标题（对应 aideepin 的 title 字段）
     */
    private String title;

    /**
     * 节点描述
     */
    private String remark;

    /**
     * 输入配置(JSON格式)
     * 与 Entity 保持一致，使用强类型 AiWfNodeInputConfigVo
     * 可以直接通过 BeanUtils.copyProperties 复制
     */
    private AiWfNodeInputConfigVo inputConfig;

    /**
     * 节点配置(JSON格式)
     * 使用 Fastjson2 的 JSONObject 替代 Jackson 的 ObjectNode
     */
    private JSONObject nodeConfig;

    /**
     * X轴位置
     */
    private BigDecimal positionX;

    /**
     * Y轴位置
     */
    private BigDecimal positionY;
}
