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
            remark,
            user_id AS userId,
            is_public AS isPublic,
            is_enable AS isEnable,
            is_deleted AS isDeleted,
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
     * @param user_id 用户ID
     * @return 工作流列表
     */
    @Select("""
        SELECT
            id,
            workflow_uuid AS workflowUuid,
            title,
            remark,
            user_id AS userId,
            is_public AS isPublic,
            is_enable AS isEnable,
            is_deleted AS isDeleted,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow
        WHERE (user_id = #{user_id} OR is_public = 1)
            AND is_deleted = 0
        ORDER BY u_time DESC
    """)
    List<AiWorkflowEntity> selectByOwnerIdOrPublic(@Param("user_id") Long user_id);

    /**
     * 按标题模糊搜索工作流
     *
     * @param user_id 用户ID
     * @param keyword 搜索关键词
     * @return 工作流列表
     */
    @Select("""
        SELECT
            id,
            workflow_uuid AS workflowUuid,
            title,
            remark,
            user_id AS userId,
            is_public AS isPublic,
            is_enable AS isEnable,
            is_deleted AS isDeleted,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow
        WHERE (user_id = #{user_id} OR is_public = 1)
            AND is_deleted = 0
            AND title LIKE CONCAT('%', #{keyword}, '%')
        ORDER BY u_time DESC
    """)
    List<AiWorkflowEntity> searchByKeyword(@Param("user_id") Long user_id,
                                           @Param("keyword") String keyword);

    /**
     * 更新工作流启用状态
     * 状态值：false-禁用,true-启用
     *
     * @param workflow_uuid 工作流UUID
     * @param is_enable 启用状态（true或false）
     * @return 更新的行数
     */
    @Update("""
        UPDATE ai_workflow
        SET is_enable = #{is_enable}
        WHERE workflow_uuid = #{workflow_uuid}
    """)
    int updateEnableStatus(@Param("workflow_uuid") String workflow_uuid,
                          @Param("is_enable") Boolean is_enable);
}
