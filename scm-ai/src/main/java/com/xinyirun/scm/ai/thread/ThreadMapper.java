package com.xinyirun.scm.ai.thread;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.thread.entity.ThreadEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Thread数据访问接口
 *
 * @author SCM-AI Module
 * @version 1.0.0
 * @since 2025-01-01
 */
@Mapper
public interface ThreadMapper extends BaseMapper<ThreadEntity> {

    /**
     * 根据topic查询线程
     */
    @Select("SELECT * FROM scm_ai_thread WHERE thread_topic = #{topic} AND deleted = 0")
    ThreadEntity findByTopic(@Param("topic") String topic);

    /**
     * 根据用户UID查询线程列表
     */
    @Select("SELECT * FROM scm_ai_thread WHERE owner_uid = #{ownerUid} AND deleted = 0 ORDER BY u_time DESC")
    List<ThreadEntity> findByOwnerUid(@Param("ownerUid") String ownerUid);

    /**
     * 根据线程类型查询
     */
    @Select("SELECT * FROM scm_ai_thread WHERE thread_type = #{type} AND deleted = 0 ORDER BY u_time DESC")
    List<ThreadEntity> findByType(@Param("type") String type);

    /**
     * 根据线程状态查询
     */
    @Select("SELECT * FROM scm_ai_thread WHERE thread_status = #{status} AND deleted = 0 ORDER BY u_time DESC")
    List<ThreadEntity> findByStatus(@Param("status") String status);

    /**
     * 查询活跃线程（状态为NEW, ROBOTING, QUEUING, CHATTING）
     */
    @Select("SELECT * FROM scm_ai_thread WHERE thread_status IN ('NEW', 'ROBOTING', 'QUEUING', 'CHATTING') AND deleted = 0 ORDER BY u_time DESC")
    List<ThreadEntity> findActiveThreads();

    /**
     * 根据渠道查询线程
     */
    @Select("SELECT * FROM scm_ai_thread WHERE channel = #{channel} AND deleted = 0 ORDER BY u_time DESC")
    List<ThreadEntity> findByChannel(@Param("channel") String channel);

    /**
     * 查询置顶线程
     */
    @Select("SELECT * FROM scm_ai_thread WHERE is_top = 1 AND deleted = 0 ORDER BY u_time DESC")
    List<ThreadEntity> findTopThreads();

    /**
     * 查询未读线程
     */
    @Select("SELECT * FROM scm_ai_thread WHERE is_unread = 1 AND deleted = 0 ORDER BY u_time DESC")
    List<ThreadEntity> findUnreadThreads();

    /**
     * 分页查询线程
     */
    @Select("SELECT * FROM scm_ai_thread WHERE deleted = 0 ORDER BY u_time DESC")
    IPage<ThreadEntity> findThreadsPage(Page<ThreadEntity> page);

    /**
     * 根据条件分页查询线程
     */
    @Select("<script>" +
            "SELECT * FROM scm_ai_thread WHERE deleted = 0 " +
            "<if test='type != null and type != \"\"'> AND thread_type = #{type} </if>" +
            "<if test='status != null and status != \"\"'> AND thread_status = #{status} </if>" +
            "<if test='ownerUid != null and ownerUid != \"\"'> AND owner_uid = #{ownerUid} </if>" +
            "<if test='channel != null and channel != \"\"'> AND channel = #{channel} </if>" +
            "ORDER BY u_time DESC" +
            "</script>")
    IPage<ThreadEntity> findThreadsPageByCondition(
            Page<ThreadEntity> page,
            @Param("type") String type,
            @Param("status") String status,
            @Param("ownerUid") String ownerUid,
            @Param("channel") String channel
    );

    /**
     * 更新线程状态
     */
    @Update("UPDATE scm_ai_thread SET thread_status = #{status}, u_time = #{updateTime} WHERE uid = #{uid}")
    int updateStatusByUid(@Param("uid") String uid, @Param("status") String status, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 更新最后活跃时间
     */
    @Update("UPDATE scm_ai_thread SET u_time = #{activeTime} WHERE uid = #{uid}")
    int updateLastActiveTime(@Param("uid") String uid, @Param("activeTime") LocalDateTime activeTime);

    /**
     * 设置线程置顶状态
     */
    @Update("UPDATE scm_ai_thread SET is_top = #{isTop}, u_time = #{updateTime} WHERE uid = #{uid}")
    int updateTopStatus(@Param("uid") String uid, @Param("isTop") Boolean isTop, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 设置线程未读状态
     */
    @Update("UPDATE scm_ai_thread SET is_unread = #{isUnread}, u_time = #{updateTime} WHERE uid = #{uid}")
    int updateUnreadStatus(@Param("uid") String uid, @Param("isUnread") Boolean isUnread, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 设置线程免打扰状态
     */
    @Update("UPDATE scm_ai_thread SET is_mute = #{isMute}, u_time = #{updateTime} WHERE uid = #{uid}")
    int updateMuteStatus(@Param("uid") String uid, @Param("isMute") Boolean isMute, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 统计线程总数
     */
    @Select("SELECT COUNT(*) FROM scm_ai_thread WHERE deleted = 0")
    Long countAllThreads();

    /**
     * 根据状态统计线程数量
     */
    @Select("SELECT COUNT(*) FROM scm_ai_thread WHERE thread_status = #{status} AND deleted = 0")
    Long countByStatus(@Param("status") String status);

    /**
     * 根据类型统计线程数量
     */
    @Select("SELECT COUNT(*) FROM scm_ai_thread WHERE thread_type = #{type} AND deleted = 0")
    Long countByType(@Param("type") String type);

    /**
     * 统计用户的线程数量
     */
    @Select("SELECT COUNT(*) FROM scm_ai_thread WHERE owner_uid = #{ownerUid} AND deleted = 0")
    Long countByOwnerUid(@Param("ownerUid") String ownerUid);

}