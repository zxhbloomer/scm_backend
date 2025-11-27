package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiConversationRuntimeNodeEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI Chat调用Workflow节点执行Mapper
 *
 * <p>负责ai_conversation_runtime_node表的数据访问</p>
 *
 * @author SCM-AI团队
 * @since 2025-11-11
 */
@Mapper
public interface AiConversationRuntimeNodeMapper extends BaseMapper<AiConversationRuntimeNodeEntity> {

    /**
     * 根据运行实例ID批量物理删除节点记录
     *
     * @param runtimeId 运行实例ID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_conversation_runtime_node
        WHERE conversation_workflow_runtime_id = #{runtimeId}
    """)
    int deleteByRuntimeId(@Param("runtimeId") Long runtimeId);

    /**
     * 根据运行实例ID查询所有节点ID列表
     *
     * @param runtimeId 运行实例ID
     * @return 节点ID列表
     */
    @Select("""
        SELECT id
        FROM ai_conversation_runtime_node
        WHERE conversation_workflow_runtime_id = #{runtimeId}
        ORDER BY id
    """)
    List<Long> selectIdsByRuntimeId(@Param("runtimeId") Long runtimeId);
}
