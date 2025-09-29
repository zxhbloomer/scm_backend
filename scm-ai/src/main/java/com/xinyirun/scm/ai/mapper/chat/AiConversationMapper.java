package com.xinyirun.scm.ai.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI会话表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface AiConversationMapper extends BaseMapper<AiConversationEntity> {

    /**
     * 批量插入会话记录
     */
    @Insert("""
    <script>
        INSERT INTO ai_conversation (
            id,
            title,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            ai_config_id
        )
        VALUES
        <foreach collection='list' item='item' separator=','>
            (
                #{item.id},
                #{item.title},
                #{item.c_time},
                #{item.u_time},
                #{item.c_id},
                #{item.u_id},
                #{item.dbversion},
                #{item.ai_config_id}
            )
        </foreach>
    </script>
    """)
    int batchInsert(@Param("list") List<AiConversationEntity> list);

    /**
     * 根据用户查询会话列表
     */
    @Select("""
        SELECT
            id,
            title,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            ai_config_id
        FROM ai_conversation
        WHERE c_id = #{cId}
        ORDER BY c_time DESC
        """)
    List<AiConversationEntity> selectByCreateUser(@Param("cId") Long cId);

    /**
     * 根据租户查询会话列表
     */
    @Select("""
        SELECT
            id,
            title,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            ai_config_id
        FROM ai_conversation
        ORDER BY c_time DESC
        """)
    List<AiConversationEntity> selectByTenant();

    /**
     * 根据标题模糊查询会话
     */
    @Select("""
        SELECT
            id,
            title,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            ai_config_id
        FROM ai_conversation
        WHERE title LIKE CONCAT('%', #{title}, '%')
        ORDER BY c_time DESC
        """)
    List<AiConversationEntity> selectByTitleLike(@Param("title") String title);

    /**
     * 根据用户和时间范围查询会话
     */
    @Select("""
        SELECT
            id,
            title,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            ai_config_id
        FROM ai_conversation
        WHERE c_id = #{cId}
            AND c_time >= #{startTime}
            AND c_time <= #{endTime}
        ORDER BY c_time DESC
        """)
    List<AiConversationEntity> selectByUserAndTimeRange(@Param("cId") Long cId,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户会话数量
     */
    @Select("""
        SELECT COUNT(*)
        FROM ai_conversation
        WHERE c_id = #{cId}
        """)
    long countByUser(@Param("cId") Long cId);

    /**
     * 删除指定时间之前的会话
     */
    @Delete("""
        DELETE FROM ai_conversation
        WHERE c_time < #{beforeTime}
        """)
    int deleteByCreateTimeBefore(@Param("beforeTime") LocalDateTime beforeTime);
}