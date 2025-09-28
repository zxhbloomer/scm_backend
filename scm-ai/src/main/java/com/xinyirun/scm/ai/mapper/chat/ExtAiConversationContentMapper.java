package com.xinyirun.scm.ai.mapper.chat;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AI会话内容扩展Mapper接口
 * 租户，多数据源，在线程内，需要手工指定
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface ExtAiConversationContentMapper {

    /**
     * 根据会话ID查询最后N条记录
     */
    @Select("SELECT id, conversation_id, type, create_time, content, model_source_id " +
            "FROM ai_conversation_content " +
            "WHERE conversation_id = #{conversationId} " +
            "ORDER BY create_time DESC " +
            "LIMIT #{limit}")
    List<AiConversationContentVo> selectLastByConversationIdByLimit(@Param("conversationId") String conversationId,
                                                                    @Param("limit") int limit);

    /**
     * 根据用户查询最近的会话内容
     */
    @Select("SELECT acc.id, acc.conversation_id, acc.type, acc.create_time, acc.content, acc.model_source_id " +
            "FROM ai_conversation_content acc " +
            "INNER JOIN ai_conversation ac ON acc.conversation_id = ac.id " +
            "WHERE ac.create_user = #{userId} " +
            "ORDER BY acc.create_time DESC " +
            "LIMIT #{limit}")
    List<AiConversationContentEntity> selectRecentByUser(@Param("userId") String userId, @Param("limit") int limit);

    /**
     * 统计用户的消息数量
     */
    @Select("SELECT COUNT(*) " +
            "FROM ai_conversation_content acc " +
            "INNER JOIN ai_conversation ac ON acc.conversation_id = ac.id " +
            "WHERE ac.create_user = #{userId}")
    long countByUser(@Param("userId") String userId);

    /**
     * 查询用户指定类型的消息内容
     */
    @Select("SELECT acc.id, acc.conversation_id, acc.type, acc.create_time, acc.content, acc.model_source_id " +
            "FROM ai_conversation_content acc " +
            "INNER JOIN ai_conversation ac ON acc.conversation_id = ac.id " +
            "WHERE ac.create_user = #{userId} AND acc.type = #{type} " +
            "ORDER BY acc.create_time DESC " +
            "LIMIT #{limit}")
    List<AiConversationContentEntity> selectByUserAndType(@Param("userId") String userId,
                                                         @Param("type") String type,
                                                         @Param("limit") int limit);

    /**
     * 查询会话内容统计信息
     */
    @Select("SELECT " +
            "conversation_id, " +
            "COUNT(*) as message_count, " +
            "MAX(create_time) as last_message_time, " +
            "MIN(create_time) as first_message_time " +
            "FROM ai_conversation_content " +
            "WHERE conversation_id = #{conversationId} " +
            "GROUP BY conversation_id")
    AiConversationContentEntity selectConversationStats(@Param("conversationId") String conversationId);
}