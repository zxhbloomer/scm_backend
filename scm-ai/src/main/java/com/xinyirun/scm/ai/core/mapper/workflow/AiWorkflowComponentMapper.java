package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowComponentVo;
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
            title,
            icon,
            remark,
            display_order AS displayOrder,
            is_enable AS isEnable,
            is_deleted AS isDeleted,
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
            title,
            icon,
            remark,
            display_order AS displayOrder,
            is_enable AS isEnable,
            is_deleted AS isDeleted,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow_component
        WHERE is_enable = 1
        ORDER BY display_order ASC, name
    """)
    List<AiWorkflowComponentEntity> selectAllEnabled();


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

    /**
     * 分页搜索工作流组件
     * 状态值：is_deleted 0-未删除,1-已删除；is_enable 0-禁用,1-启用
     *
     * @param page 分页参数
     * @param title 标题关键词（模糊匹配）
     * @param isEnable 启用状态（null表示不过滤）
     * @return 分页结果
     */
    @Select("""
        <script>
        SELECT
            id,
            component_uuid AS componentUuid,
            name,
            title,
            remark,
            display_order AS displayOrder,
            is_enable AS isEnable
        FROM ai_workflow_component
        WHERE is_deleted = 0
        <if test="isEnable != null">
            AND is_enable = #{isEnable}
        </if>
        <if test="title != null and title != ''">
            AND title LIKE CONCAT('%', #{title}, '%')
        </if>
        ORDER BY display_order ASC, id ASC
        </script>
    """)
    IPage<AiWorkflowComponentVo> searchPage(Page<AiWorkflowComponentVo> page,
                                             @Param("title") String title,
                                             @Param("isEnable") Integer isEnable);
}
