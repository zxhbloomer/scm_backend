package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI工作流组件 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiWorkflowComponentMapper extends BaseMapper<AiWorkflowComponentEntity> {

    /**
     * 按UUID查询组件
     *
     * @param component_uuid 组件UUID
     * @return 组件实体
     */
    @Select("""
        SELECT
            id,
            component_uuid AS componentUuid,
            name,
            icon,
            description,
            category,
            input_schema AS inputSchema,
            output_schema AS outputSchema,
            default_config AS defaultConfig,
            version,
            is_enable AS isEnable,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_component
        WHERE component_uuid = #{component_uuid}
    """)
    AiWorkflowComponentEntity selectByComponentUuid(@Param("component_uuid") String component_uuid);

    /**
     * 查询所有启用的组件
     * 状态值：is_enable 0-禁用,1-启用
     *
     * @return 组件列表
     */
    @Select("""
        SELECT
            id,
            component_uuid AS componentUuid,
            name,
            icon,
            description,
            category,
            input_schema AS inputSchema,
            output_schema AS outputSchema,
            default_config AS defaultConfig,
            version,
            is_enable AS isEnable,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_component
        WHERE is_enable = 1
        ORDER BY category, name
    """)
    List<AiWorkflowComponentEntity> selectAllEnabled();

    /**
     * 按分类查询启用的组件
     * 状态值：is_enable 0-禁用,1-启用
     *
     * @param category 组件分类
     * @return 组件列表
     */
    @Select("""
        SELECT
            id,
            component_uuid AS componentUuid,
            name,
            icon,
            description,
            category,
            input_schema AS inputSchema,
            output_schema AS outputSchema,
            default_config AS defaultConfig,
            version,
            is_enable AS isEnable,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_component
        WHERE category = #{category} AND is_enable = 1
        ORDER BY name
    """)
    List<AiWorkflowComponentEntity> selectByCategory(@Param("category") String category);

    /**
     * 按component_uuid物理删除组件
     *
     * @param component_uuid 组件UUID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_workflow_component
        WHERE component_uuid = #{component_uuid}
    """)
    int deleteByComponentUuid(@Param("component_uuid") String component_uuid);
}
