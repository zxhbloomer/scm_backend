package com.xinyirun.scm.ai.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.AiConversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI会话Mapper接口
 *
 * 基于MyBatis Plus的数据访问层接口
 * 提供AI会话的CRUD操作
 *
 * 从MeterSphere迁移而来，适配scm-ai的MyBatis Plus架构
 *
 * @Author: 迁移适配
 * @Migration: 2025-09-21 (迁移到scm-ai)
 */
@Mapper
public interface AiConversationMapper extends BaseMapper<AiConversation> {

    /**
     * 根据用户ID查询会话列表
     *
     * @param userId 用户ID
     * @return 会话列表，按创建时间倒序
     */
    @Select("SELECT * FROM ai_conversation WHERE create_user = #{userId} ORDER BY create_time DESC")
    List<AiConversation> selectByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID查询会话数量
     *
     * @param userId 用户ID
     * @return 会话数量
     */
    @Select("SELECT COUNT(*) FROM ai_conversation WHERE create_user = #{userId}")
    Long countByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID和标题模糊查询会话
     *
     * @param userId 用户ID
     * @param title 标题关键词
     * @return 匹配的会话列表
     */
    @Select("SELECT * FROM ai_conversation WHERE create_user = #{userId} AND title LIKE CONCAT('%', #{title}, '%') ORDER BY create_time DESC")
    List<AiConversation> selectByUserIdAndTitle(@Param("userId") String userId, @Param("title") String title);

    /**
     * 根据用户ID获取最近的会话列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近的会话列表
     */
    @Select("SELECT * FROM ai_conversation WHERE create_user = #{userId} ORDER BY create_time DESC LIMIT #{limit}")
    List<AiConversation> selectRecentByUserId(@Param("userId") String userId, @Param("limit") Integer limit);

    /**
     * 根据时间范围查询用户会话
     *
     * @param userId 用户ID
     * @param startTime 开始时间（毫秒时间戳）
     * @param endTime 结束时间（毫秒时间戳）
     * @return 时间范围内的会话列表
     */
    @Select("SELECT * FROM ai_conversation WHERE create_user = #{userId} AND create_time BETWEEN #{startTime} AND #{endTime} ORDER BY create_time DESC")
    List<AiConversation> selectByUserIdAndTimeRange(@Param("userId") String userId,
                                                   @Param("startTime") Long startTime,
                                                   @Param("endTime") Long endTime);

    /**
     * 批量删除用户的会话
     *
     * @param userId 用户ID
     * @param conversationIds 会话ID列表
     * @return 删除的记录数
     */
    @Select("DELETE FROM ai_conversation WHERE create_user = #{userId} AND id IN (${conversationIds})")
    Integer deleteByUserIdAndIds(@Param("userId") String userId, @Param("conversationIds") String conversationIds);

    /**
     * 更新会话标题
     *
     * @param id 会话ID
     * @param title 新标题
     * @return 更新的记录数
     */
    @Select("UPDATE ai_conversation SET title = #{title} WHERE id = #{id}")
    Integer updateTitleById(@Param("id") String id, @Param("title") String title);

    /**
     * 检查会话是否属于指定用户
     *
     * @param id 会话ID
     * @param userId 用户ID
     * @return 匹配的记录数（0或1）
     */
    @Select("SELECT COUNT(*) FROM ai_conversation WHERE id = #{id} AND create_user = #{userId}")
    Integer checkOwnership(@Param("id") String id, @Param("userId") String userId);
}