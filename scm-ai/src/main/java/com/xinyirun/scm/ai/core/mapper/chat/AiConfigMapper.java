package com.xinyirun.scm.ai.core.mapper.chat;

import com.xinyirun.scm.ai.bean.domain.AiConfig;
import com.xinyirun.scm.ai.bean.domain.AiConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AiConfigMapper {
    long countByExample(AiConfigExample example);

    int deleteByExample(AiConfigExample example);

    int deleteByPrimaryKey(String id);

    int insert(AiConfig record);

    int insertSelective(AiConfig record);

    List<AiConfig> selectByExample(AiConfigExample example);

    AiConfig selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") AiConfig record, @Param("example") AiConfigExample example);

    int updateByExample(@Param("record") AiConfig record, @Param("example") AiConfigExample example);

    int updateByPrimaryKeySelective(AiConfig record);

    int updateByPrimaryKey(AiConfig record);

    /**
     * 根据配置键和租户查询配置
     */
    AiConfig selectByConfigKeyAndTenant(@Param("configKey") String configKey, @Param("tenant") String tenant);

    /**
     * 查询所有Token价格配置
     */
    List<AiConfig> selectPriceConfigs();

    /**
     * 批量插入配置
     */
    int batchInsert(@Param("records") List<AiConfig> records);

    /**
     * 根据配置键前缀查询
     */
    List<AiConfig> selectByConfigKeyPrefix(@Param("keyPrefix") String keyPrefix, @Param("tenant") String tenant);
}