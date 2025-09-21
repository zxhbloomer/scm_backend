package com.xinyirun.scm.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.ai.entity.AiConversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI对话会话数据访问接口
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Mapper
public interface AiConversationMapper extends BaseMapper<AiConversation> {

    /**
     * 根据用户ID查询会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY u_time DESC")
    List<AiConversation> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID分页查询会话
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @return 分页结果
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY u_time DESC")
    IPage<AiConversation> selectPageByUserId(IPage<AiConversation> page, @Param("userId") Long userId);

    /**
     * 根据状态查询会话
     *
     * @param status 会话状态
     * @return 会话列表
     */
    @Select("SELECT * FROM ai_conversation WHERE status = #{status} AND is_deleted = 0 ORDER BY u_time DESC")
    List<AiConversation> selectByStatus(@Param("status") Integer status);

    /**
     * 根据用户ID和状态查询会话
     *
     * @param userId 用户ID
     * @param status 会话状态
     * @return 会话列表
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} AND status = #{status} AND is_deleted = 0 ORDER BY u_time DESC")
    List<AiConversation> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 统计用户会话总数
     *
     * @param userId 用户ID
     * @return 会话总数
     */
    @Select("SELECT COUNT(*) FROM ai_conversation WHERE user_id = #{userId} AND is_deleted = 0")
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 查询用户最近的会话
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近会话列表
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY u_time DESC LIMIT #{limit}")
    List<AiConversation> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 查询指定时间范围内的会话
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 会话列表
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} AND c_time BETWEEN #{startTime} AND #{endTime} AND is_deleted = 0 ORDER BY c_time DESC")
    List<AiConversation> selectByTimeRange(@Param("userId") Long userId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 根据关键字搜索会话标题
     *
     * @param userId 用户ID
     * @param keyword 关键字
     * @return 会话列表
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} AND title LIKE CONCAT('%', #{keyword}, '%') AND is_deleted = 0 ORDER BY u_time DESC")
    List<AiConversation> searchByTitle(@Param("userId") Long userId, @Param("keyword") String keyword);
}