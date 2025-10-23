package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowRuntimeEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI工作流运行时 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiWorkflowRuntimeMapper extends BaseMapper<AiWorkflowRuntimeEntity> {

    /**
     * 按UUID查询工作流运行时
     *
     * @param runtime_uuid 运行时UUID
     * @return 运行时实体
     */
    @Select("""
        SELECT
            id,
            runtime_uuid AS runtimeUuid,
            workflow_uuid AS workflowUuid,
            workflow_id AS workflowId,
            user_id AS userId,
            status,
            start_time AS startTime,
            end_time AS endTime,
            error_message AS errorMessage,
            input_data AS inputData,
            output_data AS outputData,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_runtime
        WHERE runtime_uuid = #{runtime_uuid}
          AND is_deleted = 0
    """)
    AiWorkflowRuntimeEntity selectByRuntimeUuid(@Param("runtime_uuid") String runtime_uuid);

    /**
     * 查询用户的工作流运行记录
     * 状态值：0-待执行,1-执行中,2-成功,3-失败,4-已取消
     *
     * @param user_id 用户ID
     * @return 运行记录列表
     */
    @Select("""
        SELECT
            id,
            runtime_uuid AS runtimeUuid,
            workflow_uuid AS workflowUuid,
            workflow_id AS workflowId,
            user_id AS userId,
            status,
            start_time AS startTime,
            end_time AS endTime,
            error_message AS errorMessage,
            input_data AS inputData,
            output_data AS outputData,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_runtime
        WHERE user_id = #{user_id}
          AND is_deleted = 0
        ORDER BY c_time DESC
    """)
    List<AiWorkflowRuntimeEntity> selectByUserId(@Param("user_id") Long user_id);

    /**
     * 按工作流UUID查询运行记录
     *
     * @param workflow_uuid 工作流UUID
     * @return 运行记录列表
     */
    @Select("""
        SELECT
            id,
            runtime_uuid AS runtimeUuid,
            workflow_uuid AS workflowUuid,
            workflow_id AS workflowId,
            user_id AS userId,
            status,
            start_time AS startTime,
            end_time AS endTime,
            error_message AS errorMessage,
            input_data AS inputData,
            output_data AS outputData,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_runtime
        WHERE workflow_uuid = #{workflow_uuid}
          AND is_deleted = 0
        ORDER BY c_time DESC
    """)
    List<AiWorkflowRuntimeEntity> selectByWorkflowUuid(@Param("workflow_uuid") String workflow_uuid);

    /**
     * 更新运行时状态
     * 状态值：0-待执行,1-执行中,2-成功,3-失败,4-已取消
     *
     * @param runtime_uuid 运行时UUID
     * @param status 状态
     * @return 更新的行数
     */
    @Update("""
        UPDATE ai_workflow_runtime
        SET status = #{status}
        WHERE runtime_uuid = #{runtime_uuid}
    """)
    int updateStatus(@Param("runtime_uuid") String runtime_uuid,
                     @Param("status") Integer status);
}
