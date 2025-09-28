package com.xinyirun.scm.ai.mapper.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.config.AiConfigEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
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
     * 根据配置键和租户查询配置
     */
    @Select("SELECT id, config_key, config_value, description, tenant, create_time, update_time " +
            "FROM ai_config " +
            "WHERE config_key = #{configKey} AND tenant = #{tenant} " +
            "LIMIT 1")
    AiConfigEntity selectByConfigKeyAndTenant(@Param("configKey") String configKey, @Param("tenant") String tenant);

    /**
     * 查询所有Token价格配置
     */
    @Select("SELECT id, config_key, config_value, description, tenant, create_time, update_time " +
            "FROM ai_config " +
            "WHERE config_key LIKE 'token.price.%' " +
            "ORDER BY config_key")
    List<AiConfigEntity> selectPriceConfigs();

    /**
     * 批量插入配置
     */
    @Insert("<script>" +
            "INSERT INTO ai_config (id, config_key, config_value, description, tenant, create_time, update_time) " +
            "VALUES " +
            "<foreach collection='records' item='record' separator=','>" +
            "(#{record.id}, #{record.configKey}, #{record.configValue}, #{record.description}, #{record.tenant}, #{record.createTime}, #{record.updateTime})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("records") List<AiConfigEntity> records);

    /**
     * 根据配置键前缀查询
     */
    @Select("SELECT id, config_key, config_value, description, tenant, create_time, update_time " +
            "FROM ai_config " +
            "WHERE config_key LIKE CONCAT(#{keyPrefix}, '%') AND tenant = #{tenant} " +
            "ORDER BY config_key")
    List<AiConfigEntity> selectByConfigKeyPrefix(@Param("keyPrefix") String keyPrefix, @Param("tenant") String tenant);

    /**
     * 根据配置键查询配置
     */
    @Select("SELECT id, config_key, config_value, description, tenant, create_time, update_time " +
            "FROM ai_config " +
            "WHERE config_key = #{configKey} " +
            "LIMIT 1")
    AiConfigEntity selectByConfigKey(@Param("configKey") String configKey);

    /**
     * 根据配置类型查询配置列表
     */
    @Select("SELECT id, config_key, config_value, description, tenant, create_time, update_time " +
            "FROM ai_config " +
            "WHERE tenant = #{tenant} " +
            "ORDER BY config_key")
    List<AiConfigEntity> selectByConfigType(@Param("tenant") String tenant);

    /**
     * 获取所有激活的配置
     */
    @Select("SELECT id, config_key, config_value, description, tenant, create_time, update_time " +
            "FROM ai_config " +
            "ORDER BY tenant, config_key")
    List<AiConfigEntity> selectActiveConfigs();

    /**
     * 根据配置键列表批量查询
     */
    @Select("<script>" +
            "SELECT id, config_key, config_value, description, tenant, create_time, update_time " +
            "FROM ai_config " +
            "WHERE config_key IN " +
            "<foreach collection='configKeys' item='key' open='(' separator=',' close=')'>" +
            "#{key}" +
            "</foreach> " +
            "ORDER BY config_key" +
            "</script>")
    List<AiConfigEntity> selectByConfigKeys(@Param("configKeys") List<String> configKeys);

    /**
     * 更新配置值
     */
    @Update("UPDATE ai_config " +
            "SET config_value = #{configValue}, update_time = #{updateTime} " +
            "WHERE config_key = #{configKey}")
    int updateConfigValue(@Param("configKey") String configKey,
                         @Param("configValue") String configValue,
                         @Param("updateTime") Long updateTime);
}