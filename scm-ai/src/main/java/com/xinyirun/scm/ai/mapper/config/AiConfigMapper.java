package com.xinyirun.scm.ai.mapper.config;

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
     * 根据配置键和租户查询配置
     */
    @Select("""
        SELECT id, config_key, config_value, description, c_time, u_time, c_id, u_id, dbversion
        FROM ai_config
        WHERE config_key = #{configKey}
        LIMIT 1
        """)
    AiConfigEntity selectByConfigKeyAndTenant(@Param("configKey") String configKey);

    /**
     * 查询所有Token价格配置
     */
    @Select("""
        SELECT id, config_key, config_value, description, c_time, u_time, c_id, u_id, dbversion
        FROM ai_config
        WHERE config_key LIKE 'token.price.%'
        ORDER BY config_key
        """)
    List<AiConfigEntity> selectPriceConfigs();

    /**
     * 批量插入配置
     */
    @Insert("""
    <script>
        INSERT INTO ai_config (id, config_key, config_value, description, c_time, u_time, c_id, u_id, dbversion)
        VALUES
        <foreach collection='records' item='record' separator=','>
            (#{record.id}, #{record.config_key}, #{record.config_value}, #{record.description}, #{record.c_time}, #{record.u_time}, #{record.c_id}, #{record.u_id}, #{record.dbversion})
        </foreach>
    </script>
    """)
    int batchInsert(@Param("records") List<AiConfigEntity> records);

    /**
     * 根据配置键前缀查询
     */
    @Select("""
        SELECT id, config_key, config_value, description, c_time, u_time, c_id, u_id, dbversion
        FROM ai_config
        WHERE config_key LIKE CONCAT(#{keyPrefix}, '%')
        ORDER BY config_key
        """)
    List<AiConfigEntity> selectByConfigKeyPrefix(@Param("keyPrefix") String keyPrefix);

    /**
     * 根据配置键查询配置
     */
    @Select("""
        SELECT id, config_key, config_value, description, c_time, u_time, c_id, u_id, dbversion
        FROM ai_config
        WHERE config_key = #{configKey}
        LIMIT 1
        """)
    AiConfigEntity selectByConfigKey(@Param("configKey") String configKey);

    /**
     * 根据配置类型查询配置列表
     */
    @Select("""
        SELECT id, config_key, config_value, description, c_time, u_time, c_id, u_id, dbversion
        FROM ai_config
        ORDER BY config_key
        """)
    List<AiConfigEntity> selectByConfigType();

    /**
     * 获取所有激活的配置
     */
    @Select("""
        SELECT id, config_key, config_value, description, c_time, u_time, c_id, u_id, dbversion
        FROM ai_config
        ORDER BY config_key
        """)
    List<AiConfigEntity> selectActiveConfigs();

    /**
     * 根据配置键列表批量查询
     */
    @Select("""
    <script>
        SELECT id, config_key, config_value, description, c_time, u_time, c_id, u_id, dbversion
        FROM ai_config
        WHERE config_key IN
        <foreach collection='configKeys' item='key' open='(' separator=',' close=')'>
            #{key}
        </foreach>
        ORDER BY config_key
    </script>
    """)
    List<AiConfigEntity> selectByConfigKeys(@Param("configKeys") List<String> configKeys);

    /**
     * 更新配置值
     */
    @Update("""
        UPDATE ai_config
        SET config_value = #{configValue}, u_time = #{uTime}, u_id = #{uId}
        WHERE config_key = #{configKey}
        """)
    int updateConfigValue(@Param("configKey") String configKey,
                         @Param("configValue") String configValue,
                         @Param("uTime") LocalDateTime uTime,
                         @Param("uId") Long uId);
}