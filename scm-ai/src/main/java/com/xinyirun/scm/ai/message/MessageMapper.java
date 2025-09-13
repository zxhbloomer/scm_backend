package com.xinyirun.scm.ai.message;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Message数据访问接口
 *
 * @author SCM-AI Module
 * @version 1.0.0
 * @since 2025-01-01
 */
@Mapper
public interface MessageMapper extends BaseMapper<MessageEntity> {

    /**
     * 根据线程UID查询消息列表
     */
    @Select("SELECT * FROM scm_ai_message WHERE thread_uid = #{threadUid} AND deleted = 0 ORDER BY created_at ASC")
    List<MessageEntity> findByThreadUid(@Param("threadUid") String threadUid);

    /**
     * 根据消息类型查询
     */
    @Select("SELECT * FROM scm_ai_message WHERE message_type = #{type} AND deleted = 0 ORDER BY created_at DESC")
    List<MessageEntity> findByType(@Param("type") String type);

    /**
     * 根据消息状态查询
     */
    @Select("SELECT * FROM scm_ai_message WHERE status = #{status} AND deleted = 0 ORDER BY created_at DESC")
    List<MessageEntity> findByStatus(@Param("status") String status);

    /**
     * 根据发送者UID查询消息
     */
    @Select("SELECT * FROM scm_ai_message WHERE user_uid = #{userUid} AND deleted = 0 ORDER BY created_at DESC")
    List<MessageEntity> findByUserUid(@Param("userUid") String userUid);

    /**
     * 查询未读消息
     */
    @Select("SELECT * FROM scm_ai_message WHERE status IN ('SENDING', 'SUCCESS', 'DELIVERED') AND deleted = 0 ORDER BY created_at DESC")
    List<MessageEntity> findUnreadMessages();

    /**
     * 根据渠道查询消息
     */
    @Select("SELECT * FROM scm_ai_message WHERE channel = #{channel} AND deleted = 0 ORDER BY created_at DESC")
    List<MessageEntity> findByChannel(@Param("channel") String channel);

    /**
     * 分页查询消息
     */
    @Select("SELECT * FROM scm_ai_message WHERE deleted = 0 ORDER BY created_at DESC")
    IPage<MessageEntity> findMessagesPage(Page<MessageEntity> page);

    /**
     * 根据条件分页查询消息
     */
    @Select("<script>" +
            "SELECT * FROM scm_ai_message WHERE deleted = 0 " +
            "<if test='threadUid != null and threadUid != \"\"'> AND thread_uid = #{threadUid} </if>" +
            "<if test='type != null and type != \"\"'> AND message_type = #{type} </if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "<if test='userUid != null and userUid != \"\"'> AND user_uid = #{userUid} </if>" +
            "<if test='channel != null and channel != \"\"'> AND channel = #{channel} </if>" +
            "ORDER BY created_at DESC" +
            "</script>")
    IPage<MessageEntity> findMessagesPageByCondition(
            Page<MessageEntity> page,
            @Param("threadUid") String threadUid,
            @Param("type") String type,
            @Param("status") String status,
            @Param("userUid") String userUid,
            @Param("channel") String channel
    );

    /**
     * 根据线程UID分页查询消息
     */
    @Select("SELECT * FROM scm_ai_message WHERE thread_uid = #{threadUid} AND deleted = 0 ORDER BY created_at ASC")
    IPage<MessageEntity> findByThreadUidPage(Page<MessageEntity> page, @Param("threadUid") String threadUid);

    /**
     * 更新消息状态
     */
    @Update("UPDATE scm_ai_message SET status = #{status}, updated_at = #{updateTime} WHERE uid = #{uid}")
    int updateStatusByUid(@Param("uid") String uid, @Param("status") String status, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 根据线程UID统计消息数量
     */
    @Select("SELECT COUNT(*) FROM scm_ai_message WHERE thread_uid = #{threadUid} AND deleted = 0")
    Long countByThreadUid(@Param("threadUid") String threadUid);

    /**
     * 根据消息类型统计数量
     */
    @Select("SELECT COUNT(*) FROM scm_ai_message WHERE message_type = #{type} AND deleted = 0")
    Long countByType(@Param("type") String type);

    /**
     * 根据状态统计消息数量
     */
    @Select("SELECT COUNT(*) FROM scm_ai_message WHERE status = #{status} AND deleted = 0")
    Long countByStatus(@Param("status") String status);

    /**
     * 统计用户的消息数量
     */
    @Select("SELECT COUNT(*) FROM scm_ai_message WHERE user_uid = #{userUid} AND deleted = 0")
    Long countByUserUid(@Param("userUid") String userUid);

    /**
     * 统计线程中各类型消息数量
     */
    @Select("SELECT COUNT(*) FROM scm_ai_message WHERE thread_uid = #{threadUid} AND message_type = #{type} AND deleted = 0")
    Long countByThreadUidAndType(@Param("threadUid") String threadUid, @Param("type") String type);

    /**
     * 查询线程最后一条消息
     */
    @Select("SELECT * FROM scm_ai_message WHERE thread_uid = #{threadUid} AND deleted = 0 ORDER BY created_at DESC LIMIT 1")
    MessageEntity findLastMessageByThreadUid(@Param("threadUid") String threadUid);

    /**
     * 查询线程中指定时间之后的消息
     */
    @Select("SELECT * FROM scm_ai_message WHERE thread_uid = #{threadUid} AND created_at > #{afterTime} AND deleted = 0 ORDER BY created_at ASC")
    List<MessageEntity> findByThreadUidAfterTime(@Param("threadUid") String threadUid, @Param("afterTime") LocalDateTime afterTime);

    /**
     * 根据内容关键字搜索消息
     */
    @Select("SELECT * FROM scm_ai_message WHERE content LIKE CONCAT('%', #{keyword}, '%') AND deleted = 0 ORDER BY created_at DESC")
    List<MessageEntity> searchByContent(@Param("keyword") String keyword);

    /**
     * 根据线程UID和关键字搜索消息
     */
    @Select("SELECT * FROM scm_ai_message WHERE thread_uid = #{threadUid} AND content LIKE CONCAT('%', #{keyword}, '%') AND deleted = 0 ORDER BY created_at DESC")
    List<MessageEntity> searchByThreadUidAndContent(@Param("threadUid") String threadUid, @Param("keyword") String keyword);

    /**
     * 统计消息总数
     */
    @Select("SELECT COUNT(*) FROM scm_ai_message WHERE deleted = 0")
    Long countAllMessages();

}