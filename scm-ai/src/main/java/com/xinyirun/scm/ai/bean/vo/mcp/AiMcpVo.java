package com.xinyirun.scm.ai.bean.vo.mcp;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * AI MCP服务器模板VO类
 * 对应实体类:AiMcpEntity
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
public class AiMcpVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * MCP UUID(业务主键)
     */
    private String mcpUuid;

    /**
     * MCP名称
     */
    private String name;

    /**
     * MCP图标
     */
    private String icon;

    /**
     * MCP描述
     */
    private String remark;

    /**
     * 传输类型(sse/stdio)
     */
    private String transportType;

    /**
     * SSE连接URL
     */
    private String sseUrl;

    /**
     * STDIO命令
     */
    private String stdioCommand;

    /**
     * 预设参数(管理员配置)
     */
    private Map<String, Object> presetParams;

    /**
     * 可自定义参数定义(用户可配置项)
     */
    private Map<String, Object> customizedParamDefinitions;

    /**
     * 安装类型(docker/local/remote/wasm)
     */
    private String installType;

    /**
     * 是否启用(0-禁用,1-启用)
     */
    private Integer isEnable;
}
