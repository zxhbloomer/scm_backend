package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowRuntimeNodeEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI工作流运行时节点 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiWorkflowRuntimeNodeMapper extends BaseMapper<AiWorkflowRuntimeNodeEntity> {

    /**
     * 按运行时UUID查询所有节点
     *
     * @param runtime_uuid 运行时UUID
     * @return 节点列表
     */
    @Select("""
        SELECT
            id,
            runtime_uuid AS runtimeUuid,
            node_uuid AS nodeUuid,
            node_name AS nodeName,
            status,
            start_time AS startTime,
            end_time AS endTime,
            input_data AS inputData,
            output_data AS outputData,
            error_message AS errorMessage,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_runtime_node
        WHERE runtime_uuid = #{runtime_uuid}
          AND is_deleted = 0
        ORDER BY start_time ASC
    """)
    List<AiWorkflowRuntimeNodeEntity> selectByRuntimeUuid(@Param("runtime_uuid") String runtime_uuid);

    /**
     * 按节点UUID查询运行记录
     * 状态值：0-待执行,1-执行中,2-成功,3-失败,4-已跳过
     *
     * @param runtime_uuid 运行时UUID
     * @param node_uuid 节点UUID
     * @return 节点运行记录
     */
    @Select("""
        SELECT
            id,
            runtime_uuid AS runtimeUuid,
            node_uuid AS nodeUuid,
            node_name AS nodeName,
            status,
            start_time AS startTime,
            end_time AS endTime,
            input_data AS inputData,
            output_data AS outputData,
            error_message AS errorMessage,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_runtime_node
        WHERE runtime_uuid = #{runtime_uuid}
          AND node_uuid = #{node_uuid}
          AND is_deleted = 0
    """)
    AiWorkflowRuntimeNodeEntity selectByNodeUuid(@Param("runtime_uuid") String runtime_uuid,
                                                   @Param("node_uuid") String node_uuid);

    /**
     * 更新节点状态
     * 状态值：0-待执行,1-执行中,2-成功,3-失败,4-已跳过
     *
     * @param id 节点ID
     * @param status 状态
     * @return 更新的行数
     */
    @Update("""
        UPDATE ai_workflow_runtime_node
        SET status = #{status}
        WHERE id = #{id}
    """)
    int updateStatus(@Param("id") Long id,
                     @Param("status") Integer status);
}
