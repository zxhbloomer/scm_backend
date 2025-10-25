package com.xinyirun.scm.ai.core.mapper.draw;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.draw.AiDrawStarEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI绘图收藏 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiDrawStarMapper extends BaseMapper<AiDrawStarEntity> {

    /**
     * 查询用户是否收藏了某个绘图
     *
     * @param draw_id 绘图ID
     * @param user_id 用户ID
     * @return 收藏记录
     */
    @Select("""
        SELECT
            id,
            draw_id AS drawId,
            user_id AS userId,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_draw_star
        WHERE draw_id = #{draw_id}
          AND user_id = #{user_id}
          AND is_deleted = 0
    """)
    AiDrawStarEntity selectByDrawIdAndUserId(@Param("draw_id") Long draw_id,
                                              @Param("user_id") Long user_id);

    /**
     * 查询用户的收藏列表
     *
     * @param user_id 用户ID
     * @param max_id 最大ID
     * @param page_size 每页数量
     * @return 收藏列表
     */
    @Select("""
        SELECT
            id,
            draw_id AS drawId,
            user_id AS userId,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_draw_star
        WHERE user_id = #{user_id}
          AND is_deleted = 0
          AND id < #{max_id}
        ORDER BY u_time DESC
        LIMIT #{page_size}
    """)
    List<AiDrawStarEntity> selectByUserIdWithPaging(@Param("user_id") Long user_id,
                                                     @Param("max_id") Long max_id,
                                                     @Param("page_size") Integer page_size);

    /**
     * 统计绘图的收藏数
     *
     * @param draw_id 绘图ID
     * @return 收藏数
     */
    @Select("""
        SELECT COUNT(*)
        FROM ai_draw_star
        WHERE draw_id = #{draw_id}
          AND is_deleted = 0
    """)
    int countByDrawId(@Param("draw_id") Long draw_id);
}
