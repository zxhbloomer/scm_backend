package com.xinyirun.scm.ai.core.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentRefEmbeddingEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI对话消息-向量引用Mapper
 *
 * @author SCM-AI团队
 * @since 2025-11-08
 */
@Mapper
public interface AiConversationContentRefEmbeddingMapper extends BaseMapper<AiConversationContentRefEmbeddingEntity> {

    /**
     * 根据消息ID列表批量删除向量引用记录
     *
     * @param messageIds 消息ID列表
     * @return 删除数量
     */
    @Delete("""
        <script>
        DELETE FROM ai_conversation_content_ref_embedding
        WHERE message_id IN
        <foreach collection='messageIds' item='id' open='(' separator=',' close=')'>
            #{id}
        </foreach>
        </script>
        """)
    int deleteByMessageIds(@Param("messageIds") List<String> messageIds);
}
