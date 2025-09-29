package com.xinyirun.scm.ai.mapper.statistics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.statistics.AiUserQuotaEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * AI用户配额表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface AiUserQuotaMapper extends BaseMapper<AiUserQuotaEntity> {

    /**
     * 批量插入用户配额记录
     */
    @Insert("""
    <script>
        INSERT INTO ai_user_quota (
            id,
            user_id,
            daily_limit,
            monthly_limit,
            daily_used,
            monthly_used,
            daily_reset_date,
            monthly_reset_date,
            total_cost,
            status,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        )
        VALUES
        <foreach collection='list' item='item' separator=','>
            (
                #{item.id},
                #{item.user_id},
                #{item.daily_limit},
                #{item.monthly_limit},
                #{item.daily_used},
                #{item.monthly_used},
                #{item.daily_reset_date},
                #{item.monthly_reset_date},
                #{item.total_cost},
                #{item.status},
                #{item.c_time},
                #{item.u_time},
                #{item.c_id},
                #{item.u_id},
                #{item.dbversion}
            )
        </foreach>
    </script>
    """)
    int batchInsert(@Param("list") List<AiUserQuotaEntity> list);

    /**
     * 根据用户ID和租户查询配额
     */
    @Select("""
        SELECT
            id,
            user_id,
            quota_type,
            total_quota,
            used_quota,
            remaining_quota,
            reset_period,
            last_reset_time,
            is_active,
            create_time,
            update_time
        FROM ai_user_quota
        WHERE user_id = #{userId}
            AND is_active = 1
        """)
    List<AiUserQuotaEntity> selectByUserAndTenant(@Param("userId") String userId);

    /**
     * 根据用户ID、租户和配额类型查询配额
     */
    @Select("""
        SELECT
            id,
            user_id,
            quota_type,
            total_quota,
            used_quota,
            remaining_quota,
            reset_period,
            last_reset_time,
            is_active,
            create_time,
            update_time
        FROM ai_user_quota
        WHERE user_id = #{userId}
            AND quota_type = #{quotaType}
            AND is_active = 1
        LIMIT 1
        """)
    AiUserQuotaEntity selectByUserTenantAndType(@Param("userId") String userId,
                                               @Param("quotaType") String quotaType);

    /**
     * 更新已使用配额
     */
    @Update("""
        UPDATE ai_user_quota SET
            used_quota = #{usedQuota},
            remaining_quota = total_quota - #{usedQuota},
            update_time = #{updateTime}
        WHERE id = #{id}
        """)
    int updateUsedQuota(@Param("id") String id,
                       @Param("usedQuota") Long usedQuota,
                       @Param("updateTime") Long updateTime);

    /**
     * 增加已使用配额
     */
    @Update("""
        UPDATE ai_user_quota SET
            used_quota = used_quota + #{increment},
            remaining_quota = total_quota - (used_quota + #{increment}),
            update_time = #{updateTime}
        WHERE id = #{id}
        """)
    int increaseUsedQuota(@Param("id") String id,
                         @Param("increment") Long increment,
                         @Param("updateTime") Long updateTime);

    /**
     * 重置用户配额
     */
    @Update("""
        UPDATE ai_user_quota SET
            used_quota = 0,
            remaining_quota = total_quota,
            last_reset_time = #{resetTime},
            update_time = #{updateTime}
        WHERE user_id = #{userId}
            AND quota_type = #{quotaType}
        """)
    int resetUserQuota(@Param("userId") String userId,
                      @Param("quotaType") String quotaType,
                      @Param("resetTime") Long resetTime,
                      @Param("updateTime") Long updateTime);

    /**
     * 查询需要重置的配额记录
     */
    @Select("""
        SELECT
            id,
            user_id,
            quota_type,
            total_quota,
            used_quota,
            remaining_quota,
            reset_period,
            last_reset_time,
            is_active,
            create_time,
            update_time
        FROM ai_user_quota
        WHERE is_active = 1
            AND reset_period = #{resetPeriod}
            AND last_reset_time < #{beforeTime}
        """)
    List<AiUserQuotaEntity> selectQuotasNeedReset(@Param("resetPeriod") String resetPeriod,
                                                 @Param("beforeTime") Long beforeTime);

    /**
     * 查询配额即将耗尽的用户
     */
    @Select("""
        SELECT
            id,
            user_id,
            quota_type,
            total_quota,
            used_quota,
            remaining_quota,
            reset_period,
            last_reset_time,
            is_active,
            create_time,
            update_time
        FROM ai_user_quota
        WHERE is_active = 1
            AND remaining_quota <= #{threshold}
            AND remaining_quota > 0
        """)
    List<AiUserQuotaEntity> selectQuotasNearLimit(@Param("threshold") Long threshold);

    /**
     * 查询已耗尽配额的用户
     */
    @Select("""
        SELECT
            id,
            user_id,
            quota_type,
            total_quota,
            used_quota,
            remaining_quota,
            reset_period,
            last_reset_time,
            is_active,
            create_time,
            update_time
        FROM ai_user_quota
        WHERE is_active = 1
            AND remaining_quota <= 0
        """)
    List<AiUserQuotaEntity> selectExhaustedQuotas();

    /**
     * 根据租户查询配额统计
     */
    @Select("""
        SELECT
            quota_type,
            COUNT(*) as user_count,
            SUM(total_quota) as total_quota_sum,
            SUM(used_quota) as used_quota_sum,
            SUM(remaining_quota) as remaining_quota_sum
        FROM ai_user_quota
        WHERE is_active = 1
        GROUP BY quota_type
        """)
    List<AiUserQuotaEntity> selectQuotaStatsByTenant();

    /**
     * 更新配额状态
     */
    @Update("""
        UPDATE ai_user_quota SET
            is_active = #{isActive},
            update_time = #{updateTime}
        WHERE id = #{id}
        """)
    int updateActiveStatus(@Param("id") String id,
                          @Param("isActive") Boolean isActive,
                          @Param("updateTime") Long updateTime);

    /**
     * 批量重置配额
     */
    @Update("""
    <script>
        UPDATE ai_user_quota SET
            used_quota = 0,
            remaining_quota = total_quota,
            last_reset_time = #{resetTime},
            update_time = #{updateTime}
        WHERE id IN
        <foreach collection='ids' item='id' open='(' separator=',' close=')'>
            #{id}
        </foreach>
    </script>
    """)
    int batchResetQuotas(@Param("ids") List<String> ids,
                        @Param("resetTime") Long resetTime,
                        @Param("updateTime") Long updateTime);

    // ============== 备份系统必需的存储过程方法 ==============

    /**
     * 根据用户ID和租户查询配额
     */
    @Select("""
        SELECT * FROM ai_user_quota
        WHERE user_id = #{userId}
            AND is_active = 1
        LIMIT 1
        """)
    AiUserQuotaEntity selectByUserIdAndTenant(@Param("userId") String userId);

    /**
     * 调用存储过程更新用户Token使用量
     */
    @Select("""
        {CALL UpdateUserTokenUsage(#{userId}, #{tokenCount}, #{cost})}
        """)
    void callUpdateUserTokenUsage(@Param("userId") String userId,
                                 @Param("tokenCount") Long tokenCount,
                                 @Param("cost") BigDecimal cost);

    /**
     * 调用存储过程重置用户日配额
     */
    @Select("""
        {CALL ResetUserDailyQuota()}
        """)
    void callResetUserDailyQuota();

    /**
     * 调用存储过程重置用户月配额
     */
    @Select("""
        {CALL ResetUserMonthlyQuota()}
        """)
    void callResetUserMonthlyQuota();

    /**
     * 批量查询需要重置日配额的用户
     */
    @Select("""
        SELECT * FROM ai_user_quota
        WHERE is_active = 1
            AND (daily_reset_date IS NULL OR daily_reset_date < CURDATE())
        """)
    List<AiUserQuotaEntity> selectUsersNeedDailyReset();

    /**
     * 批量查询需要重置月配额的用户
     */
    @Select("""
        SELECT * FROM ai_user_quota
        WHERE is_active = 1
            AND (monthly_reset_date IS NULL OR monthly_reset_date < DATE_FORMAT(NOW(), '%Y-%m-01'))
        """)
    List<AiUserQuotaEntity> selectUsersNeedMonthlyReset();
}