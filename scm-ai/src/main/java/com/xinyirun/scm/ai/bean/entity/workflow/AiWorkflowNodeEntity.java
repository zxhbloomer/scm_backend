package com.xinyirun.scm.ai.bean.entity.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeInputConfigVo;
import com.xinyirun.scm.ai.config.handler.FastjsonInputConfigTypeHandler;
import com.xinyirun.scm.ai.config.handler.FastjsonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI工作流节点实体类
 * 对应数据表：ai_workflow_node
 *
 * 功能说明：存储工作流中的节点实例，包括输入配置、节点配置、位置坐标等
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ai_workflow_node", autoResultMap = true)
public class AiWorkflowNodeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 节点UUID(业务主键)
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 所属工作流ID
     */
    @TableField("workflow_id")
    private Long workflowId;

    /**
     * 组件ID
     */
    @TableField("workflow_component_id")
    private Long workflowComponentId;

    /**
     * 节点标题
     */
    @TableField("title")
    private String title;

    /**
     * 节点描述
     */
    @TableField("remark")
    private String remark;

    /**
     * 输入配置(JSON格式)
     * 参考 aideepin: 使用强类型 AiWfNodeInputConfigVo 替代 Map
     * 使用 FastjsonInputConfigTypeHandler 替代 JacksonTypeHandler
     */
    @TableField(value = "input_config", typeHandler = FastjsonInputConfigTypeHandler.class)
    private AiWfNodeInputConfigVo inputConfig;

    /**
     * 节点配置(JSON格式)
     * 使用 Fastjson2 的 JSONObject 替代 Jackson 的 ObjectNode
     */
    @TableField(value = "node_config", typeHandler = FastjsonTypeHandler.class)
    private JSONObject nodeConfig;

    /**
     * 节点X坐标
     */
    @TableField("position_x")
    private BigDecimal positionX;

    /**
     * 节点Y坐标
     */
    @TableField("position_y")
    private BigDecimal positionY;

    /**
     * 是否删除(0-未删除,1-已删除)
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime uTime;

    /**
     * 创建人ID
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    /**
     * 修改人ID
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    /**
     * 数据版本(乐观锁)
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;
}
