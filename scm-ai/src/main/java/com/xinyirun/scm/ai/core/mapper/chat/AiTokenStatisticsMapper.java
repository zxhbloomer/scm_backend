package com.xinyirun.scm.ai.core.mapper.chat;

import com.xinyirun.scm.ai.bean.domain.AiTokenStatistics;
import com.xinyirun.scm.ai.bean.domain.AiTokenStatisticsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AiTokenStatisticsMapper {
    long countByExample(AiTokenStatisticsExample example);

    int deleteByExample(AiTokenStatisticsExample example);

    int deleteByPrimaryKey(String id);

    int insert(AiTokenStatistics record);

    int insertSelective(AiTokenStatistics record);

    List<AiTokenStatistics> selectByExample(AiTokenStatisticsExample example);

    AiTokenStatistics selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") AiTokenStatistics record, @Param("example") AiTokenStatisticsExample example);

    int updateByExample(@Param("record") AiTokenStatistics record, @Param("example") AiTokenStatisticsExample example);

    int updateByPrimaryKeySelective(AiTokenStatistics record);

    int updateByPrimaryKey(AiTokenStatistics record);

    /**
     * 查询用户统计数据
     */
    List<AiTokenStatistics> selectUserStatistics(@Param("userId") String userId,
                                                @Param("tenant") String tenant,
                                                @Param("statType") String statType);

    /**
     * 查询租户统计数据
     */
    List<AiTokenStatistics> selectTenantStatistics(@Param("tenant") String tenant,
                                                  @Param("statType") String statType);

    /**
     * 更新或插入统计数据
     */
    int upsertStatistics(AiTokenStatistics record);

    /**
     * 批量更新统计数据
     */
    int batchUpsertStatistics(@Param("records") List<AiTokenStatistics> records);
}