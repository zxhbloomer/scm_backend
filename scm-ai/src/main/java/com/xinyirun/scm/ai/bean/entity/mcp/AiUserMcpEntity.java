package com.xinyirun.scm.ai.bean.entity.mcp;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户MCP配置实体类
 * 对应数据表：ai_user_mcp
 *
 * 功能说明：存储用户个性化的MCP实例配置，基于MCP模板的用户自定义参数
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ai_user_mcp", autoResultMap = true)
public class AiUserMcpEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户MCP UUID(业务主键)
     */
    @TableField("user_mcp_uuid")
    private String userMcpUuid;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * MCP模板ID
     */
    @TableField("mcp_id")
    private Long mcpId;

    /**
     * 用户自定义参数值
     */
    @TableField(value = "mcp_customized_params", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> mcpCustomizedParams;

    /**
     * 是否启用(0-禁用,1-启用)
     */
    @TableField("is_enable")
    private Boolean isEnable;

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
