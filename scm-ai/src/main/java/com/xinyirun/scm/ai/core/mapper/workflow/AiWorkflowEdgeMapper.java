package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEdgeEntity;
import org.apache.ibatis.annotations.*;

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
     * 按UUID查询边
     *
     * @param edge_uuid 边UUID
     * @return 边实体
     */
    @Select("""
        SELECT
            id,
            edge_uuid AS edgeUuid,
            workflow_id AS workflowId,
            workflow_uuid AS workflowUuid,
            source_node_uuid AS sourceNodeUuid,
            target_node_uuid AS targetNodeUuid,
            source_handle AS sourceHandle,
            target_handle AS targetHandle,
            edge_type AS edgeType,
            edge_label AS edgeLabel,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_edge
        WHERE edge_uuid = #{edge_uuid}
    """)
    AiWorkflowEdgeEntity selectByEdgeUuid(@Param("edge_uuid") String edge_uuid);

    /**
     * 按workflow_uuid查询所有边
     *
     * @param workflow_uuid 工作流UUID
     * @return 边列表
     */
    @Select("""
        SELECT
            id,
            edge_uuid AS edgeUuid,
            workflow_id AS workflowId,
            workflow_uuid AS workflowUuid,
            source_node_uuid AS sourceNodeUuid,
            target_node_uuid AS targetNodeUuid,
            source_handle AS sourceHandle,
            target_handle AS targetHandle,
            edge_type AS edgeType,
            edge_label AS edgeLabel,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_edge
        WHERE workflow_uuid = #{workflow_uuid}
        ORDER BY c_time ASC
    """)
    List<AiWorkflowEdgeEntity> selectByWorkflowUuid(@Param("workflow_uuid") String workflow_uuid);

    /**
     * 查询从指定源节点出发的所有边
     *
     * @param workflow_uuid 工作流UUID
     * @param source_node_uuid 源节点UUID
     * @return 边列表
     */
    @Select("""
        SELECT
            id,
            edge_uuid AS edgeUuid,
            workflow_id AS workflowId,
            workflow_uuid AS workflowUuid,
            source_node_uuid AS sourceNodeUuid,
            target_node_uuid AS targetNodeUuid,
            source_handle AS sourceHandle,
            target_handle AS targetHandle,
            edge_type AS edgeType,
            edge_label AS edgeLabel,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_edge
        WHERE workflow_uuid = #{workflow_uuid}
            AND source_node_uuid = #{source_node_uuid}
    """)
    List<AiWorkflowEdgeEntity> selectBySourceNodeUuid(@Param("workflow_uuid") String workflow_uuid,
                                                       @Param("source_node_uuid") String source_node_uuid);

    /**
     * 按workflow_uuid物理删除所有边
     *
     * @param workflow_uuid 工作流UUID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_workflow_edge
        WHERE workflow_uuid = #{workflow_uuid}
    """)
    int deleteByWorkflowUuid(@Param("workflow_uuid") String workflow_uuid);

    /**
     * 按edge_uuid物理删除边
     *
     * @param edge_uuid 边UUID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_workflow_edge
        WHERE edge_uuid = #{edge_uuid}
    """)
    int deleteByEdgeUuid(@Param("edge_uuid") String edge_uuid);
}
