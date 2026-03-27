package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowInteractionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * AI工作流人机交互Mapper
 *
 * <p>负责ai_workflow_interaction表的数据访问</p>
 *
 * @author SCM-AI团队
 * @since 2026-03-06
 */
@Mapper
public interface AiWorkflowInteractionMapper extends BaseMapper<AiWorkflowInteractionEntity> {

    /**
     * 根据interaction_uuid查询交互记录
     */
    @Select("""
        SELECT
            id,
            interaction_uuid AS interactionUuid,
            conversation_id AS conversationId,
            runtime_uuid AS runtimeUuid,
            node_uuid AS nodeUuid,
            interaction_type AS interactionType,
            interaction_params AS interactionParams,
            description,
            status,
            timeout_minutes AS timeoutMinutes,
            timeout_at AS timeoutAt,
            feedback_data AS feedbackData,
            feedback_action AS feedbackAction,
            submitted_at AS submittedAt,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_workflow_interaction
        WHERE interaction_uuid = #{interactionUuid}
        LIMIT 1
        """)
    AiWorkflowInteractionEntity selectByInteractionUuid(
            @Param("interactionUuid") String interactionUuid);

    /**
     * 查找对话中WAITING状态的交互记录
     */
    @Select("""
        SELECT
            id,
            interaction_uuid AS interactionUuid,
            conversation_id AS conversationId,
            runtime_uuid AS runtimeUuid,
            node_uuid AS nodeUuid,
            interaction_type AS interactionType,
            interaction_params AS interactionParams,
            description,
            status,
            timeout_minutes AS timeoutMinutes,
            timeout_at AS timeoutAt,
            feedback_data AS feedbackData,
            feedback_action AS feedbackAction,
            submitted_at AS submittedAt,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_workflow_interaction
        WHERE conversation_id = #{conversationId}
            AND status = 'WAITING'
        ORDER BY c_time DESC
        LIMIT 1
        """)
    AiWorkflowInteractionEntity selectWaitingByConversationId(
            @Param("conversationId") String conversationId);

    /**
     * 查找已超时的交互记录(status=WAITING且timeout_at已过期)
     */
    @Select("""
        SELECT
            id,
            interaction_uuid AS interactionUuid,
            conversation_id AS conversationId,
            runtime_uuid AS runtimeUuid,
            node_uuid AS nodeUuid,
            interaction_type AS interactionType,
            interaction_params AS interactionParams,
            description,
            status,
            timeout_minutes AS timeoutMinutes,
            timeout_at AS timeoutAt,
            feedback_data AS feedbackData,
            feedback_action AS feedbackAction,
            submitted_at AS submittedAt,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_workflow_interaction
        WHERE status = 'WAITING'
            AND timeout_at IS NOT NULL
            AND timeout_at < NOW()
        LIMIT 200
        """)
    List<AiWorkflowInteractionEntity> selectExpiredInteractions();
}
