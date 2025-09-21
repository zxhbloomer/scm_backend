package com.xinyirun.scm.ai.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.AiConversationContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI会话内容Mapper接口
 *
 * 基于MyBatis Plus的数据访问层接口
 * 提供AI会话内容的CRUD操作
 *
 * 从MeterSphere迁移而来，适配scm-ai的MyBatis Plus架构
 *
 * @Author: 迁移适配
 * @Migration: 2025-09-21 (迁移到scm-ai)
 */
@Mapper
public interface AiConversationContentMapper extends BaseMapper<AiConversationContent> {

    /**
     * 根据会话ID查询内容列表
     *
     * @param conversationId 会话ID
     * @return 内容列表，按创建时间排序
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} ORDER BY create_time ASC")
    List<AiConversationContent> selectByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID查询内容数量
     *
     * @param conversationId 会话ID
     * @return 内容数量
     */
    @Select("SELECT COUNT(*) FROM ai_conversation_content WHERE conversation_id = #{conversationId}")
    Long countByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID和消息类型查询内容列表
     *
     * @param conversationId 会话ID
     * @param type 消息类型（USER/AI/SYSTEM）
     * @return 指定类型的内容列表
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} AND type = #{type} ORDER BY create_time ASC")
    List<AiConversationContent> selectByConversationIdAndType(@Param("conversationId") String conversationId,
                                                             @Param("type") String type);

    /**
     * 根据会话ID获取最新的内容记录
     *
     * @param conversationId 会话ID
     * @param limit 限制数量
     * @return 最新的内容列表
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} ORDER BY create_time DESC LIMIT #{limit}")
    List<AiConversationContent> selectLatestByConversationId(@Param("conversationId") String conversationId,
                                                            @Param("limit") Integer limit);

    /**
     * 根据会话ID获取最后一条消息
     *
     * @param conversationId 会话ID
     * @return 最后一条消息
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} ORDER BY create_time DESC LIMIT 1")
    AiConversationContent selectLastByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID删除所有内容
     *
     * @param conversationId 会话ID
     * @return 删除的记录数
     */
    @Select("DELETE FROM ai_conversation_content WHERE conversation_id = #{conversationId}")
    Integer deleteByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID和时间范围查询内容
     *
     * @param conversationId 会话ID
     * @param startTime 开始时间（毫秒时间戳）
     * @param endTime 结束时间（毫秒时间戳）
     * @return 时间范围内的内容列表
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} AND create_time BETWEEN #{startTime} AND #{endTime} ORDER BY create_time ASC")
    List<AiConversationContent> selectByConversationIdAndTimeRange(@Param("conversationId") String conversationId,
                                                                  @Param("startTime") Long startTime,
                                                                  @Param("endTime") Long endTime);

    /**
     * 批量删除会话内容
     *
     * @param conversationIds 会话ID列表
     * @return 删除的记录数
     */
    @Select("DELETE FROM ai_conversation_content WHERE conversation_id IN (${conversationIds})")
    Integer deleteByConversationIds(@Param("conversationIds") String conversationIds);

    /**
     * 统计用户的总消息数
     *
     * @param userId 用户ID（通过会话关联）
     * @return 总消息数
     */
    @Select("SELECT COUNT(*) FROM ai_conversation_content acc " +
            "INNER JOIN ai_conversation ac ON acc.conversation_id = ac.id " +
            "WHERE ac.create_user = #{userId}")
    Long countByUserId(@Param("userId") String userId);

    /**
     * 根据内容关键词搜索
     *
     * @param conversationId 会话ID
     * @param keyword 关键词
     * @return 匹配的内容列表
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} AND content LIKE CONCAT('%', #{keyword}, '%') ORDER BY create_time ASC")
    List<AiConversationContent> searchByKeyword(@Param("conversationId") String conversationId,
                                               @Param("keyword") String keyword);
}