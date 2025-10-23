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
 * MCP服务器模板实体类
 * 对应数据表：ai_mcp
 *
 * 功能说明：存储MCP(Model Context Protocol)服务器的模板配置信息
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ai_mcp", autoResultMap = true)
public class AiMcpEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * MCP UUID(业务主键)
     */
    @TableField("mcp_uuid")
    private String mcpUuid;

    /**
     * MCP名称
     */
    @TableField("name")
    private String name;

    /**
     * MCP图标
     */
    @TableField("icon")
    private String icon;

    /**
     * MCP描述
     */
    @TableField("remark")
    private String remark;

    /**
     * 传输类型(sse/stdio)
     */
    @TableField("transport_type")
    private String transportType;

    /**
     * SSE连接URL
     */
    @TableField("sse_url")
    private String sseUrl;

    /**
     * STDIO命令
     */
    @TableField("stdio_command")
    private String stdioCommand;

    /**
     * 预设参数(管理员配置)
     */
    @TableField(value = "preset_params", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> presetParams;

    /**
     * 可自定义参数定义(用户可配置项)
     */
    @TableField(value = "customized_param_definitions", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> customizedParamDefinitions;

    /**
     * 安装类型(docker/local/remote/wasm)
     */
    @TableField("install_type")
    private String installType;

    /**
     * 是否启用(0-禁用,1-启用)
     */
    @TableField("is_enable")
    private Integer isEnable;

    /**
     * 是否删除(0-未删除,1-已删除)
     */
    @TableField("is_deleted")
    private Integer isDeleted;

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
