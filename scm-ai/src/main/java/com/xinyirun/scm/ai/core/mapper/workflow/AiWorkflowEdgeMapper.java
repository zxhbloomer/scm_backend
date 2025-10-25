package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEdgeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI工作流边 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiWorkflowEdgeMapper extends BaseMapper<AiWorkflowEdgeEntity> {

    /**
     * 按 uuid 查询边（排除已删除）
     *
     * @param uuid 边UUID
     * @return 边实体
     */
    @Select("""
        SELECT
            id,
            uuid,
            workflow_id AS workflowId,
            source_node_uuid AS sourceNodeUuid,
            source_handle AS sourceHandle,
            target_node_uuid AS targetNodeUuid,
            target_handle AS targetHandle,
            is_deleted AS isDeleted,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_edge
        WHERE uuid = #{uuid}
            AND is_deleted = 0
        LIMIT 1
    """)
    AiWorkflowEdgeEntity selectByUuid(@Param("uuid") String uuid);

    /**
     * 按 workflow_id 查询所有边（排除已删除）
     *
     * @param workflowId 工作流ID
     * @return 边列表
     */
    @Select("""
        SELECT
            id,
            uuid,
            workflow_id AS workflowId,
            source_node_uuid AS sourceNodeUuid,
            source_handle AS sourceHandle,
            target_node_uuid AS targetNodeUuid,
            target_handle AS targetHandle,
            is_deleted AS isDeleted,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_edge
        WHERE workflow_id = #{workflowId}
            AND is_deleted = 0
        ORDER BY c_time ASC
    """)
    List<AiWorkflowEdgeEntity> selectByWorkflowId(@Param("workflowId") Long workflowId);
}
