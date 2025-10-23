package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI工作流节点 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiWorkflowNodeMapper extends BaseMapper<AiWorkflowNodeEntity> {

    /**
     * 按UUID查询节点
     *
     * @param node_uuid 节点UUID
     * @return 节点实体
     */
    @Select("""
        SELECT
            id,
            node_uuid AS nodeUuid,
            workflow_id AS workflowId,
            workflow_uuid AS workflowUuid,
            node_name AS nodeName,
            node_type AS nodeType,
            input_config AS inputConfig,
            node_config AS nodeConfig,
            position_x AS positionX,
            position_y AS positionY,
            width,
            height,
            selected,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_node
        WHERE node_uuid = #{node_uuid}
    """)
    AiWorkflowNodeEntity selectByNodeUuid(@Param("node_uuid") String node_uuid);

    /**
     * 按workflow_uuid查询所有节点
     *
     * @param workflow_uuid 工作流UUID
     * @return 节点列表
     */
    @Select("""
        SELECT
            id,
            node_uuid AS nodeUuid,
            workflow_id AS workflowId,
            workflow_uuid AS workflowUuid,
            node_name AS nodeName,
            node_type AS nodeType,
            input_config AS inputConfig,
            node_config AS nodeConfig,
            position_x AS positionX,
            position_y AS positionY,
            width,
            height,
            selected,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_node
        WHERE workflow_uuid = #{workflow_uuid}
        ORDER BY c_time ASC
    """)
    List<AiWorkflowNodeEntity> selectByWorkflowUuid(@Param("workflow_uuid") String workflow_uuid);

    /**
     * 获取工作流的起始节点（node_type='start'）
     *
     * @param workflow_uuid 工作流UUID
     * @return 起始节点
     */
    @Select("""
        SELECT
            id,
            node_uuid AS nodeUuid,
            workflow_id AS workflowId,
            workflow_uuid AS workflowUuid,
            node_name AS nodeName,
            node_type AS nodeType,
            input_config AS inputConfig,
            node_config AS nodeConfig,
            position_x AS positionX,
            position_y AS positionY,
            width,
            height,
            selected,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_node
        WHERE workflow_uuid = #{workflow_uuid}
            AND node_type = 'start'
        LIMIT 1
    """)
    AiWorkflowNodeEntity getStartNode(@Param("workflow_uuid") String workflow_uuid);

    /**
     * 按workflow_uuid物理删除所有节点
     *
     * @param workflow_uuid 工作流UUID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_workflow_node
        WHERE workflow_uuid = #{workflow_uuid}
    """)
    int deleteByWorkflowUuid(@Param("workflow_uuid") String workflow_uuid);

    /**
     * 按node_uuid物理删除节点
     *
     * @param node_uuid 节点UUID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_workflow_node
        WHERE node_uuid = #{node_uuid}
    """)
    int deleteByNodeUuid(@Param("node_uuid") String node_uuid);
}
