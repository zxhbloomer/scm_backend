package com.xinyirun.scm.ai.bean.vo.mcp;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * AI用户MCP配置VO类
 * 对应实体类:AiUserMcpEntity
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
public class AiUserMcpVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户MCP UUID(业务主键)
     */
    private String userMcpUuid;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * MCP模板ID
     */
    private Long mcpId;

    /**
     * 用户自定义参数值
     */
    private Map<String, Object> mcpCustomizedParams;

    /**
     * 是否启用(false-禁用,true-启用)
     */
    private Boolean isEnable;

    /**
     * MCP模板信息(关联查询)
     */
    private AiMcpVo mcpInfo;
}
