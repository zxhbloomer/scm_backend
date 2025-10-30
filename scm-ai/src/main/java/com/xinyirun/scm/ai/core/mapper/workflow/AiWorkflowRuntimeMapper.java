package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowRuntimeEntity;
import org.apache.ibatis.annotations.*;

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
}
