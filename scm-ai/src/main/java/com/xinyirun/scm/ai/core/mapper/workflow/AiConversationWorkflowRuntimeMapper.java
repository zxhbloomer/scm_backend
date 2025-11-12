package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiConversationWorkflowRuntimeEntity;
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
     * 根据runtime_uuid查询运行时实例
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
}
