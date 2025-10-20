package com.xinyirun.scm.ai.core.mapper.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.config.AiConfigEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI系统配置表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface AiConfigMapper extends BaseMapper<AiConfigEntity> {

    /**
     * 查询默认模型配置
     * 一次性查询LLM、VISION、EMBEDDING三种类型的默认模型ID
     *
     * @return 配置列表
     */
    @Select("""
        SELECT config_key, config_value
        FROM ai_config
        WHERE config_key IN ('DEFAULT_LLM_MODEL_ID', 'DEFAULT_VISION_MODEL_ID', 'DEFAULT_EMBEDDING_MODEL_ID')
    """)
    @Results({
        @Result(column = "config_key", property = "configKey"),
        @Result(column = "config_value", property = "configValue")
    })
    List<AiConfigEntity> selectDefaultModels();

    /**
     * 根据配置键查询配置
     *
     * @param configKey 配置键
     * @return 配置实体
     */
    @Select("""
        SELECT id, config_key, config_value, description, c_time, u_time, c_id, u_id, dbversion
        FROM ai_config
        WHERE config_key = #{configKey}
    """)
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "config_key", property = "configKey"),
        @Result(column = "config_value", property = "configValue"),
        @Result(column = "description", property = "description"),
        @Result(column = "c_time", property = "cTime"),
        @Result(column = "u_time", property = "uTime"),
        @Result(column = "c_id", property = "cId"),
        @Result(column = "u_id", property = "uId"),
        @Result(column = "dbversion", property = "dbversion")
    })
    AiConfigEntity selectByConfigKey(@Param("configKey") String configKey);

}