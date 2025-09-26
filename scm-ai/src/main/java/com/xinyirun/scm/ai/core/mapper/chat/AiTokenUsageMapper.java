package com.xinyirun.scm.ai.core.mapper.chat;

import com.xinyirun.scm.ai.bean.domain.AiTokenUsage;
import com.xinyirun.scm.ai.bean.domain.AiTokenUsageExample;
import java.util.List;
import java.math.BigDecimal;
import org.apache.ibatis.annotations.Param;

public interface AiTokenUsageMapper {
    long countByExample(AiTokenUsageExample example);

    int deleteByExample(AiTokenUsageExample example);

    int deleteByPrimaryKey(String id);

    int insert(AiTokenUsage record);

    int insertSelective(AiTokenUsage record);

    List<AiTokenUsage> selectByExample(AiTokenUsageExample example);

    AiTokenUsage selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") AiTokenUsage record, @Param("example") AiTokenUsageExample example);

    int updateByExample(@Param("record") AiTokenUsage record, @Param("example") AiTokenUsageExample example);

    int updateByPrimaryKeySelective(AiTokenUsage record);

    int updateByPrimaryKey(AiTokenUsage record);

    /**
     * 查询用户今日Token使用量
     */
    Long getTodayTokenUsageByUser(@Param("userId") String userId, @Param("tenant") String tenant);

    /**
     * 查询用户本月Token使用量
     */
    Long getMonthlyTokenUsageByUser(@Param("userId") String userId, @Param("tenant") String tenant);

    /**
     * 查询用户累计费用
     */
    BigDecimal getTotalCostByUser(@Param("userId") String userId, @Param("tenant") String tenant);

    /**
     * 查询对话Token使用统计
     */
    List<AiTokenUsage> selectByConversationId(@Param("conversationId") String conversationId);

    /**
     * 批量插入Token使用记录
     */
    int batchInsert(@Param("records") List<AiTokenUsage> records);
}