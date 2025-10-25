package com.xinyirun.scm.ai.core.mapper.mcp;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.mcp.AiMcpEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI MCP服务器配置 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiMcpMapper extends BaseMapper<AiMcpEntity> {

    /**
     * 按UUID查询MCP配置
     *
     * @param mcp_uuid MCP UUID
     * @return MCP配置实体
     */
    @Select("""
        SELECT
            id,
            mcp_uuid AS mcpUuid,
            title,
            description,
            icon,
            install_type AS installType,
            transport_type AS transportType,
            command,
            args,
            env_vars AS envVars,
            url,
            preset_params AS presetParams,
            is_enable AS isEnable,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_mcp
        WHERE mcp_uuid = #{mcp_uuid}
          AND is_deleted = 0
    """)
    AiMcpEntity selectByMcpUuid(@Param("mcp_uuid") String mcp_uuid);

    /**
     * 查询所有启用的MCP配置
     * 启用状态：0-禁用,1-启用
     *
     * @return MCP配置列表
     */
    @Select("""
        SELECT
            id,
            mcp_uuid AS mcpUuid,
            title,
            description,
            icon,
            install_type AS installType,
            transport_type AS transportType,
            command,
            args,
            env_vars AS envVars,
            url,
            preset_params AS presetParams,
            is_enable AS isEnable,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_mcp
        WHERE is_deleted = 0
          AND is_enable = 1
        ORDER BY u_time DESC
    """)
    List<AiMcpEntity> selectAllEnabled();

    /**
     * 按关键词搜索MCP配置
     *
     * @param keyword 关键词
     * @return MCP配置列表
     */
    @Select("""
        SELECT
            id,
            mcp_uuid AS mcpUuid,
            title,
            description,
            icon,
            install_type AS installType,
            transport_type AS transportType,
            command,
            args,
            env_vars AS envVars,
            url,
            preset_params AS presetParams,
            is_enable AS isEnable,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_mcp
        WHERE is_deleted = 0
          AND (title LIKE CONCAT('%', #{keyword}, '%')
               OR description LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY u_time DESC
    """)
    List<AiMcpEntity> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 更新MCP启用状态
     * 启用状态：0-禁用,1-启用
     *
     * @param mcp_uuid MCP UUID
     * @param is_enable 启用状态
     * @return 更新的行数
     */
    @Update("""
        UPDATE ai_mcp
        SET is_enable = #{is_enable}
        WHERE mcp_uuid = #{mcp_uuid}
    """)
    int updateEnableStatus(@Param("mcp_uuid") String mcp_uuid,
                          @Param("is_enable") Integer is_enable);
}
