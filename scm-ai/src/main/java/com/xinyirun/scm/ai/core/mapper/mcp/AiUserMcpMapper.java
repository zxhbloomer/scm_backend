package com.xinyirun.scm.ai.core.mapper.mcp;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.mcp.AiUserMcpEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI用户MCP配置 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiUserMcpMapper extends BaseMapper<AiUserMcpEntity> {

    /**
     * 查询用户的MCP配置列表
     *
     * @param user_id 用户ID
     * @return 用户MCP配置列表
     */
    @Select("""
        SELECT
            id,
            user_id AS userId,
            mcp_id AS mcpId,
            user_params AS userParams,
            is_enable AS isEnable,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_user_mcp
        WHERE user_id = #{user_id}
          AND is_deleted = 0
        ORDER BY u_time DESC
    """)
    List<AiUserMcpEntity> selectByUserId(@Param("user_id") Long user_id);

    /**
     * 查询用户的启用MCP配置
     * 启用状态：0-禁用,1-启用
     *
     * @param user_id 用户ID
     * @return 启用的MCP配置列表
     */
    @Select("""
        SELECT
            id,
            user_id AS userId,
            mcp_id AS mcpId,
            user_params AS userParams,
            is_enable AS isEnable,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_user_mcp
        WHERE user_id = #{user_id}
          AND is_deleted = 0
          AND is_enable = 1
        ORDER BY u_time DESC
    """)
    List<AiUserMcpEntity> selectEnabledByUserId(@Param("user_id") Long user_id);

    /**
     * 查询用户的特定MCP配置
     *
     * @param user_id 用户ID
     * @param mcp_id MCP ID
     * @return 用户MCP配置
     */
    @Select("""
        SELECT
            id,
            user_id AS userId,
            mcp_id AS mcpId,
            user_params AS userParams,
            is_enable AS isEnable,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_user_mcp
        WHERE user_id = #{user_id}
          AND mcp_id = #{mcp_id}
          AND is_deleted = 0
    """)
    AiUserMcpEntity selectByUserIdAndMcpId(@Param("user_id") Long user_id,
                                           @Param("mcp_id") Long mcp_id);

    /**
     * 更新用户MCP启用状态
     * 启用状态：0-禁用,1-启用
     *
     * @param id 用户MCP配置ID
     * @param is_enable 启用状态
     * @return 更新的行数
     */
    @Update("""
        UPDATE ai_user_mcp
        SET is_enable = #{is_enable}
        WHERE id = #{id}
    """)
    int updateEnableStatus(@Param("id") Long id,
                          @Param("is_enable") Integer is_enable);
}
