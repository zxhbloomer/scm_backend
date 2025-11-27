package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiConversationRuntimeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationRuntimeVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI Chat调用Workflow运行时Mapper
 *
 * <p>负责ai_conversation_runtime表的数据访问</p>
 *
 * @author SCM-AI团队
 * @since 2025-11-11
 */
@Mapper
public interface AiConversationRuntimeMapper extends BaseMapper<AiConversationRuntimeEntity> {

    /**
     * 根据runtime_uuid查询运行时实例（Entity，不含关联字段）
     *
     * @param runtimeUuid 运行时UUID
     * @return 运行时实例Entity
     */
    @Select("""
        SELECT
            id,
            runtime_uuid,
            conversation_id,
            user_id,
            input_data,
            output_data,
            status,
            status_remark,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_conversation_runtime
        WHERE runtime_uuid = #{runtimeUuid}
        LIMIT 1
        """)
    AiConversationRuntimeEntity selectByRuntimeUuid(@Param("runtimeUuid") String runtimeUuid);

    /**
     * 根据runtime_uuid查询运行时实例详情（VO，包含创建人姓名）
     *
     * @param runtimeUuid 运行时UUID
     * @return 运行时实例VO（包含c_name等扩展字段）
     */
    @Select("""
        SELECT
            t.id,
            t.runtime_uuid AS runtime_uuid,
            t.conversation_id AS conversation_id,
            t.user_id AS user_id,
            t.input_data AS input_data,
            t.output_data AS output_data,
            t.status,
            t.status_remark AS status_remark,
            t.c_time AS c_time,
            t.u_time AS u_time,
            t.c_id AS c_id,
            t.u_id AS u_id,
            t.dbversion,
            t1.name AS c_name
        FROM ai_conversation_runtime t
        LEFT JOIN m_staff t1 ON t.c_id = t1.id
        WHERE t.runtime_uuid = #{runtimeUuid}
        LIMIT 1
        """)
    AiConversationRuntimeVo selectVoByRuntimeUuid(@Param("runtimeUuid") String runtimeUuid);

    /**
     * 根据conversation_id查询所有运行时实例ID
     *
     * @param conversationId 对话ID
     * @return 运行时实例ID列表
     */
    @Select("""
        SELECT id
        FROM ai_conversation_runtime
        WHERE conversation_id = #{conversationId}
        """)
    List<Long> selectIdsByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据conversation_id删除所有运行时实例
     *
     * @param conversationId 对话ID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_conversation_runtime
        WHERE conversation_id = #{conversationId}
        """)
    int deleteByConversationId(@Param("conversationId") String conversationId);
}
