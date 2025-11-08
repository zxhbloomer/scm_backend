package com.xinyirun.scm.ai.core.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationPresetEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * AI对话预设Mapper
 *
 * @author SCM-AI团队
 * @since 2025-11-08
 */
@Mapper
public interface AiConversationPresetMapper extends BaseMapper<AiConversationPresetEntity> {

    /**
     * 查询公开的预设列表
     *
     * @return 公开预设列表
     */
    @Select("""
        SELECT
            id,
            uuid,
            title,
            remark,
            ai_system_message AS aiSystemMessage,
            is_public AS isPublic,
            category,
            sort_order AS sortOrder,
            use_count AS useCount,
            creator_type AS creatorType,
            c_id AS cId,
            c_time AS cTime,
            u_id AS uId,
            u_time AS uTime,
            dbversion,
            create_user AS createUser
        FROM ai_conversation_preset
        WHERE is_public = 1
        ORDER BY sort_order ASC, c_time DESC
        """)
    List<AiConversationPresetEntity> selectPublicPresets();

    /**
     * 查询用户创建的预设列表
     *
     * @param userId 用户ID
     * @return 用户预设列表
     */
    @Select("""
        SELECT
            id,
            uuid,
            title,
            remark,
            ai_system_message AS aiSystemMessage,
            is_public AS isPublic,
            category,
            sort_order AS sortOrder,
            use_count AS useCount,
            creator_type AS creatorType,
            c_id AS cId,
            c_time AS cTime,
            u_id AS uId,
            u_time AS uTime,
            dbversion,
            create_user AS createUser
        FROM ai_conversation_preset
        WHERE c_id = #{userId} AND creator_type = 'USER'
        ORDER BY sort_order ASC, c_time DESC
        """)
    List<AiConversationPresetEntity> selectUserPresets(@Param("userId") Long userId);

    /**
     * 增加预设使用次数
     *
     * @param id 预设ID
     * @return 更新行数
     */
    @Update("UPDATE ai_conversation_preset SET use_count = use_count + 1 WHERE id = #{id}")
    int incrementUseCount(@Param("id") String id);

    /**
     * 根据UUID查询预设
     *
     * @param uuid 预设UUID
     * @return 预设实体
     */
    @Select("""
        SELECT
            id,
            uuid,
            title,
            remark,
            ai_system_message AS aiSystemMessage,
            is_public AS isPublic,
            category,
            sort_order AS sortOrder,
            use_count AS useCount,
            creator_type AS creatorType,
            c_id AS cId,
            c_time AS cTime,
            u_id AS uId,
            u_time AS uTime,
            dbversion,
            create_user AS createUser
        FROM ai_conversation_preset
        WHERE uuid = #{uuid}
        """)
    AiConversationPresetEntity selectByUuid(@Param("uuid") String uuid);
}
