package com.xinyirun.scm.ai.core.mapper.chat;

import com.xinyirun.scm.ai.bean.domain.AiUserQuota;
import com.xinyirun.scm.ai.bean.domain.AiUserQuotaExample;
import java.util.List;
import java.math.BigDecimal;
import org.apache.ibatis.annotations.Param;

public interface AiUserQuotaMapper {
    long countByExample(AiUserQuotaExample example);

    int deleteByExample(AiUserQuotaExample example);

    int deleteByPrimaryKey(String id);

    int insert(AiUserQuota record);

    int insertSelective(AiUserQuota record);

    List<AiUserQuota> selectByExample(AiUserQuotaExample example);

    AiUserQuota selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") AiUserQuota record, @Param("example") AiUserQuotaExample example);

    int updateByExample(@Param("record") AiUserQuota record, @Param("example") AiUserQuotaExample example);

    int updateByPrimaryKeySelective(AiUserQuota record);

    int updateByPrimaryKey(AiUserQuota record);

    /**
     * 根据用户ID和租户查询配额
     */
    AiUserQuota selectByUserIdAndTenant(@Param("userId") String userId, @Param("tenant") String tenant);

    /**
     * 调用存储过程更新用户Token使用量
     */
    void callUpdateUserTokenUsage(@Param("userId") String userId,
                                 @Param("tenant") String tenant,
                                 @Param("tokenCount") Long tokenCount,
                                 @Param("cost") BigDecimal cost);

    /**
     * 调用存储过程重置用户日配额
     */
    void callResetUserDailyQuota();

    /**
     * 调用存储过程重置用户月配额
     */
    void callResetUserMonthlyQuota();

    /**
     * 批量查询需要重置日配额的用户
     */
    List<AiUserQuota> selectUsersNeedDailyReset();

    /**
     * 批量查询需要重置月配额的用户
     */
    List<AiUserQuota> selectUsersNeedMonthlyReset();
}