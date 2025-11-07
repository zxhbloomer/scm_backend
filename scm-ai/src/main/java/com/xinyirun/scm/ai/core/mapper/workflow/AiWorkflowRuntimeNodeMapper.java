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

    /**
     * 根据运行实例ID批量物理删除节点记录
     *
     * @param runtimeId 运行实例ID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_workflow_runtime_node
        WHERE workflow_runtime_id = #{runtimeId}
    """)
    int deleteByRuntimeId(@Param("runtimeId") Long runtimeId);
}
