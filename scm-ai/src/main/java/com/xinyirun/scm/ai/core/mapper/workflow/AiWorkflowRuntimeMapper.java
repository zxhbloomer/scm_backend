package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 按runtimeUuid查询运行时实例
     *
     * @param runtimeUuid 运行时UUID
     * @return 运行时实体
     */
    @Select("""
        SELECT
            id,
            runtime_uuid AS runtimeUuid,
            workflow_id AS workflowId,
            user_id AS userId,
            conversation_id AS conversationId,
            input_data AS inputData,
            output_data AS outputData,
            status,
            status_remark AS statusRemark,
            c_time AS cTime,
            u_time AS uTime,
            c_id AS cId,
            u_id AS uId,
            dbversion
        FROM ai_workflow_runtime
        WHERE runtime_uuid = #{runtimeUuid}
    """)
    AiWorkflowRuntimeEntity selectByRuntimeUuid(@Param("runtimeUuid") String runtimeUuid);

    /**
     * 按workflowId分页查询运行时列表，按更新时间降序
     *
     * @param page 分页参数
     * @param workflowId 工作流ID
     * @return 分页结果
     */
    @Select("""
        SELECT
            id,
            runtime_uuid AS runtimeUuid,
            workflow_id AS workflowId,
            user_id AS userId,
            conversation_id AS conversationId,
            input_data AS inputData,
            output_data AS outputData,
            status,
            status_remark AS statusRemark,
            c_time AS cTime,
            u_time AS uTime,
            c_id AS cId,
            u_id AS uId,
            dbversion
        FROM ai_workflow_runtime
        WHERE workflow_id = #{workflowId}
        ORDER BY u_time DESC
    """)
    IPage<AiWorkflowRuntimeEntity> selectPageByWorkflowId(Page<AiWorkflowRuntimeEntity> page,
                                                           @Param("workflowId") Long workflowId);

    /**
     * 按workflowId查询所有运行时列表
     *
     * @param workflowId 工作流ID
     * @return 运行时列表
     */
    @Select("""
        SELECT
            id,
            runtime_uuid AS runtimeUuid,
            workflow_id AS workflowId,
            user_id AS userId,
            conversation_id AS conversationId,
            input_data AS inputData,
            output_data AS outputData,
            status,
            status_remark AS statusRemark,
            c_time AS cTime,
            u_time AS uTime,
            c_id AS cId,
            u_id AS uId,
            dbversion
        FROM ai_workflow_runtime
        WHERE workflow_id = #{workflowId}
    """)
    List<AiWorkflowRuntimeEntity> selectListByWorkflowId(@Param("workflowId") Long workflowId);
}
