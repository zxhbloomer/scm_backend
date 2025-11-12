package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiConversationWorkflowRuntimeNodeEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * AI Chat调用Workflow节点执行Mapper
 *
 * <p>负责ai_conversation_workflow_runtime_node表的数据访问</p>
 *
 * @author SCM-AI团队
 * @since 2025-11-11
 */
@Mapper
public interface AiConversationWorkflowRuntimeNodeMapper extends BaseMapper<AiConversationWorkflowRuntimeNodeEntity> {

    /**
     * 根据运行实例ID批量物理删除节点记录
     *
     * @param runtimeId 运行实例ID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_conversation_workflow_runtime_node
        WHERE conversation_workflow_runtime_id = #{runtimeId}
    """)
    int deleteByRuntimeId(@Param("runtimeId") Long runtimeId);
}
