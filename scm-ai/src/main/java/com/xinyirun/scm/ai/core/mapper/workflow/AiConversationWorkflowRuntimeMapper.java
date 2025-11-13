package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiConversationWorkflowRuntimeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationWorkflowRuntimeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * AI Chat调用Workflow运行时Mapper
 *
 * <p>负责ai_conversation_workflow_runtime表的数据访问</p>
 *
 * @author SCM-AI团队
 * @since 2025-11-11
 */
@Mapper
public interface AiConversationWorkflowRuntimeMapper extends BaseMapper<AiConversationWorkflowRuntimeEntity> {

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
            workflow_id,
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
        FROM ai_conversation_workflow_runtime
        WHERE runtime_uuid = #{runtimeUuid}
        LIMIT 1
        """)
    AiConversationWorkflowRuntimeEntity selectByRuntimeUuid(@Param("runtimeUuid") String runtimeUuid);

    /**
     * 根据runtime_uuid查询运行时实例详情（VO，包含创建人姓名）
     *
     * @param runtimeUuid 运行时UUID
     * @return 运行时实例VO（包含c_name等扩展字段）
     */
    @Select("""
        SELECT
            t.id,
            t.runtime_uuid,
            t.conversation_id,
            t.workflow_id,
            t.user_id,
            t.input_data,
            t.output_data,
            t.status,
            t.status_remark,
            t.c_time,
            t.u_time,
            t.c_id,
            t.u_id,
            t.dbversion,
            t1.name AS c_name
        FROM ai_conversation_workflow_runtime t
        LEFT JOIN m_staff t1 ON t.c_id = t1.id
        WHERE t.runtime_uuid = #{runtimeUuid}
        LIMIT 1
        """)
    AiConversationWorkflowRuntimeVo selectVoByRuntimeUuid(@Param("runtimeUuid") String runtimeUuid);
}
