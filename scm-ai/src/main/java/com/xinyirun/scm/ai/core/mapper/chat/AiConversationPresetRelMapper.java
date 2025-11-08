package com.xinyirun.scm.ai.core.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationPresetRelEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * AI对话预设关系Mapper
 *
 * @author SCM-AI团队
 * @since 2025-11-08
 */
@Mapper
public interface AiConversationPresetRelMapper extends BaseMapper<AiConversationPresetRelEntity> {

    /**
     * 根据对话ID删除预设关系
     *
     * @param conversationId 对话ID
     * @return 删除数量
     */
    @Delete("""
        DELETE FROM ai_conversation_preset_rel
        WHERE user_conv_id = #{conversationId}
        """)
    int deleteByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据预设ID删除预设关系
     *
     * @param presetId 预设ID
     * @return 删除数量
     */
    @Delete("""
        DELETE FROM ai_conversation_preset_rel
        WHERE preset_conv_id = #{presetId}
        """)
    int deleteByPresetId(@Param("presetId") String presetId);
}
