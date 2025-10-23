package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI工作流 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiWorkflowMapper extends BaseMapper<AiWorkflowEntity> {

    /**
     * 按UUID查询工作流
     *
     * @param workflow_uuid 工作流UUID
     * @return 工作流实体
     */
    @Select("""
        SELECT
            id,
            workflow_uuid AS workflowUuid,
            title,
            icon,
            tags,
            description,
            is_public AS isPublic,
            is_enable AS isEnable,
            owner_uuid AS ownerUuid,
            owner_id AS ownerId,
            owner_name AS ownerName,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow
        WHERE workflow_uuid = #{workflow_uuid}
    """)
    AiWorkflowEntity selectByWorkflowUuid(@Param("workflow_uuid") String workflow_uuid);

    /**
     * 按UUID物理删除工作流
     *
     * @param workflow_uuid 工作流UUID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_workflow
        WHERE workflow_uuid = #{workflow_uuid}
    """)
    int deleteByWorkflowUuid(@Param("workflow_uuid") String workflow_uuid);

    /**
     * 查询用户的工作流列表（所有者或公开的）
     * 状态值：is_enable 0-禁用,1-启用
     *
     * @param owner_id 拥有者ID
     * @return 工作流列表
     */
    @Select("""
        SELECT
            id,
            workflow_uuid AS workflowUuid,
            title,
            icon,
            tags,
            description,
            is_public AS isPublic,
            is_enable AS isEnable,
            owner_uuid AS ownerUuid,
            owner_id AS ownerId,
            owner_name AS ownerName,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow
        WHERE (owner_id = #{owner_id} OR is_public = 1)
        ORDER BY u_time DESC
    """)
    List<AiWorkflowEntity> selectByOwnerIdOrPublic(@Param("owner_id") Long owner_id);

    /**
     * 按标题模糊搜索工作流
     *
     * @param owner_id 拥有者ID
     * @param keyword 搜索关键词
     * @return 工作流列表
     */
    @Select("""
        SELECT
            id,
            workflow_uuid AS workflowUuid,
            title,
            icon,
            tags,
            description,
            is_public AS isPublic,
            is_enable AS isEnable,
            owner_uuid AS ownerUuid,
            owner_id AS ownerId,
            owner_name AS ownerName,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow
        WHERE (owner_id = #{owner_id} OR is_public = 1)
            AND title LIKE CONCAT('%', #{keyword}, '%')
        ORDER BY u_time DESC
    """)
    List<AiWorkflowEntity> searchByKeyword(@Param("owner_id") Long owner_id,
                                           @Param("keyword") String keyword);

    /**
     * 更新工作流启用状态
     * 状态值：0-禁用,1-启用
     *
     * @param workflow_uuid 工作流UUID
     * @param is_enable 启用状态（0或1）
     * @return 更新的行数
     */
    @Update("""
        UPDATE ai_workflow
        SET is_enable = #{is_enable}
        WHERE workflow_uuid = #{workflow_uuid}
    """)
    int updateEnableStatus(@Param("workflow_uuid") String workflow_uuid,
                          @Param("is_enable") Integer is_enable);
}
