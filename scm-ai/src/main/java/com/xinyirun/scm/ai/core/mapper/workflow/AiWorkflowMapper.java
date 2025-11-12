package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowVo;
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

    /**
     * 查询可用的工作流列表（用于子工作流选择器）
     * 返回所有公开的工作流 + 当前用户自己的工作流
     * 状态值：is_enable 0-禁用,1-启用; is_public 0-私有,1-公开
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
        WHERE (is_public = 1 OR user_id = #{user_id})
            AND is_enable = 1
            AND is_deleted = 0
        ORDER BY is_public DESC, title ASC
    """)
    List<AiWorkflowEntity> selectAvailableWorkflows(@Param("user_id") Long user_id);

    // ==================== 智能路由新增方法 (2025-11-10) ====================

    /**
     * 查询用户可用的工作流 (用于智能路由)
     *
     * 查询规则:
     * 1. 已发布: is_enable = 1
     * 2. 可见性: is_public = 1 OR c_id = userId
     * 3. 排序: 个人优先 > 优先级高 > 最新更新
     * 4. 关联字典表获取分类名称
     *
     * @param userId 当前用户ID
     * @return 可用工作流Vo列表(含分类名称)
     */
    @Select("""
        SELECT
            t1.id,
            t1.workflow_uuid AS workflowUuid,
            t1.title,
            t1.remark,
            t1.`desc`,
            t1.keywords,
            t1.category,
            t2.label AS categoryName,
            t1.priority,
            t1.user_id AS userId,
            t1.is_public AS isPublic,
            t1.is_enable AS isEnable,
            t1.is_deleted AS isDeleted,
            t1.last_test_time AS lastTestTime,
            t1.c_time AS cTime,
            t1.c_id AS cId,
            t1.u_time AS uTime,
            t1.u_id AS uId,
            t1.dbversion
        FROM ai_workflow t1
        LEFT JOIN s_dict_data t2
          ON t2.code = 'ai_workflow_category'
          AND t2.dict_value = t1.category
        WHERE t1.is_enable = 1
          AND (t1.is_public = 1 OR t1.c_id = #{userId})
        ORDER BY
          CASE WHEN t1.c_id = #{userId} THEN 0 ELSE 1 END,
          t1.priority DESC,
          t1.u_time DESC
        """)
    List<AiWorkflowVo> selectAvailableWorkflowsForRouting(
        @Param("userId") Long userId
    );

    /**
     * 查询用户所有工作流 (包括未发布的,用于管理页面)
     *
     * @param userId 当前用户ID
     * @return 用户所有工作流列表
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
            `desc`,
            keywords,
            category,
            priority,
            last_test_time AS lastTestTime,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow
        WHERE (is_public = 1 OR c_id = #{userId})
        ORDER BY u_time DESC
        """)
    List<AiWorkflowEntity> selectAllUserWorkflows(
        @Param("userId") Long userId
    );

    /**
     * 查询默认工作流 (兜底策略)
     * 返回优先级最高的已发布公开工作流
     *
     * @return 默认工作流
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
            `desc`,
            keywords,
            category,
            priority,
            last_test_time AS lastTestTime,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow
        WHERE is_enable = 1
          AND is_public = 1
        ORDER BY priority DESC
        LIMIT 1
        """)
    AiWorkflowEntity selectDefaultWorkflow();

    /**
     * 前端Tab筛选查询(可选按category筛选)
     *
     * @param userId 当前用户ID
     * @param category 分类值('0'/'1'/'2'/null),null表示不筛选
     * @return 已发布工作流列表
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
            `desc`,
            keywords,
            category,
            priority,
            last_test_time AS lastTestTime,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_workflow
        WHERE is_enable = 1
          AND (is_public = 1 OR c_id = #{userId})
          AND (#{category} IS NULL OR category = #{category})
        ORDER BY priority DESC, u_time DESC
        """)
    List<AiWorkflowEntity> selectPublishedWorkflows(
        @Param("userId") Long userId,
        @Param("category") String category
    );
}
