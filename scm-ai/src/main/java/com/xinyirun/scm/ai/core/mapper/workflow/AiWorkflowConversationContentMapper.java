package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowConversationContentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowConversationContentVo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI工作流对话内容表 Mapper接口
 *
 * @author SCM-AI开发团队
 * @since 2025-01-08
 */
@Repository
@Mapper
public interface AiWorkflowConversationContentMapper extends BaseMapper<AiWorkflowConversationContentEntity> {

    /**
     * 根据对话ID物理删除对话历史记录
     *
     * @param conversationId 对话ID（格式：tenantCode::workflowUuid::userId）
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_workflow_conversation_content
        WHERE conversation_id = #{conversationId}
        """)
    int deleteByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据对话ID查询所有消息ID列表
     *
     * @param conversationId 对话ID
     * @return 消息ID列表
     */
    @Select("""
        SELECT message_id AS messageId
        FROM ai_workflow_conversation_content
        WHERE conversation_id = #{conversationId}
        ORDER BY c_time ASC
        """)
    List<String> selectMessageIdsByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据对话ID查询最近N条对话记录
     *
     * @param conversationId 对话ID（格式：tenantCode::workflowUuid::userId）
     * @param limit 查询数量（跳过最新的1条，查询后续limit条）
     * @return 对话记录列表（按时间倒序）
     */
    @Select("""
        SELECT
            id AS id,
            message_id AS messageId,
            conversation_id AS conversationId,
            type AS type,
            content AS content,
            model_source_id AS modelSourceId,
            provider_name AS providerName,
            base_name AS baseName,
            c_time AS cTime,
            u_time AS uTime,
            c_id AS cId,
            u_id AS uId,
            dbversion AS dbversion
        FROM ai_workflow_conversation_content
        WHERE conversation_id = #{conversationId}
        ORDER BY c_time DESC
        LIMIT 1, #{limit}
        """)
    List<AiWorkflowConversationContentVo> selectLastByConversationIdByLimit(
        @Param("conversationId") String conversationId,
        @Param("limit") int limit);

}
