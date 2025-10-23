package com.xinyirun.scm.ai.core.mapper.draw;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.draw.AiDrawCommentEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI绘图评论 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiDrawCommentMapper extends BaseMapper<AiDrawCommentEntity> {

    /**
     * 查询绘图的评论列表
     *
     * @param draw_id 绘图ID
     * @param current_page 当前页
     * @param page_size 每页数量
     * @return 评论列表
     */
    @Select("""
        SELECT
            id,
            comment_uuid AS commentUuid,
            draw_id AS drawId,
            user_id AS userId,
            remark,
            parent_comment_id AS parentCommentId,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_draw_comment
        WHERE draw_id = #{draw_id}
          AND is_deleted = 0
        ORDER BY c_time DESC
        LIMIT #{offset}, #{page_size}
    """)
    List<AiDrawCommentEntity> selectByDrawIdWithPaging(@Param("draw_id") Long draw_id,
                                                        @Param("offset") Integer offset,
                                                        @Param("page_size") Integer page_size);

    /**
     * 查询评论的回复列表
     *
     * @param parent_comment_id 父评论ID
     * @return 回复列表
     */
    @Select("""
        SELECT
            id,
            comment_uuid AS commentUuid,
            draw_id AS drawId,
            user_id AS userId,
            remark,
            parent_comment_id AS parentCommentId,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_draw_comment
        WHERE parent_comment_id = #{parent_comment_id}
          AND is_deleted = 0
        ORDER BY c_time ASC
    """)
    List<AiDrawCommentEntity> selectByParentCommentId(@Param("parent_comment_id") Long parent_comment_id);

    /**
     * 统计绘图的评论数
     *
     * @param draw_id 绘图ID
     * @return 评论数
     */
    @Select("""
        SELECT COUNT(*)
        FROM ai_draw_comment
        WHERE draw_id = #{draw_id}
          AND is_deleted = 0
    """)
    int countByDrawId(@Param("draw_id") Long draw_id);

    /**
     * 按UUID查询评论
     *
     * @param comment_uuid 评论UUID
     * @return 评论实体
     */
    @Select("""
        SELECT
            id,
            comment_uuid AS commentUuid,
            draw_id AS drawId,
            user_id AS userId,
            remark,
            parent_comment_id AS parentCommentId,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_draw_comment
        WHERE comment_uuid = #{comment_uuid}
          AND is_deleted = 0
    """)
    AiDrawCommentEntity selectByCommentUuid(@Param("comment_uuid") String comment_uuid);
}
