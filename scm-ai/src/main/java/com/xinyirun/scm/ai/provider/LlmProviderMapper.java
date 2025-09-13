/*
 * SCM AI Module - LLM Provider Mapper
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: AI提供商数据访问层接口
 */
package com.xinyirun.scm.ai.provider;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// 注释：SCM系统中mapper不使用@DataSourceAnnotion，数据源切换通过其他机制实现  
// import com.xinyirun.scm.common.annotations.DataSourceAnnotion;
// import com.xinyirun.scm.common.enums.datasource.DataSourceTypeEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * AI提供商数据访问接口
 * 提供AI提供商的CRUD操作和业务查询方法
 */
@Mapper
public interface LlmProviderMapper extends BaseMapper<LlmProviderEntity> {

    /**
     * 根据提供商名称查询（唯一）
     * 
     * @param name 提供商名称
     * @return 提供商实体
     */
    @Select("SELECT * FROM scm_ai_provider WHERE name = #{name} LIMIT 1")
    LlmProviderEntity findByName(@Param("name") String name);

    /**
     * 根据提供商类型查询启用的提供商列表
     * 
     * @param providerType 提供商类型
     * @return 提供商列表
     */
    @Select("SELECT * FROM scm_ai_provider WHERE provider_type = #{providerType} AND is_enabled = 1 ORDER BY sort_order ASC")
    List<LlmProviderEntity> findByProviderType(@Param("providerType") String providerType);

    /**
     * 根据状态查询提供商列表
     * 
     * @param status 状态
     * @return 提供商列表
     */
    @Select("SELECT * FROM scm_ai_provider WHERE status = #{status} AND is_enabled = 1 ORDER BY sort_order ASC")
    List<LlmProviderEntity> findByStatus(@Param("status") String status);

    /**
     * 查询所有启用的提供商
     * 
     * @return 启用的提供商列表
     */
    @Select("SELECT * FROM scm_ai_provider WHERE is_enabled = 1 ORDER BY sort_order ASC, c_time DESC")
    List<LlmProviderEntity> findAllEnabled();

    /**
     * 查询系统级别启用的提供商
     * 
     * @return 系统启用的提供商列表
     */
    @Select("SELECT * FROM scm_ai_provider WHERE is_enabled = 1 AND is_system_enabled = 1 ORDER BY sort_order ASC")
    List<LlmProviderEntity> findSystemEnabled();

    /**
     * 查询生产环境可用的提供商
     * 
     * @return 生产环境提供商列表
     */
    @Select("SELECT * FROM scm_ai_provider WHERE status = 'PRODUCTION' AND is_enabled = 1 ORDER BY sort_order ASC")
    List<LlmProviderEntity> findProductionProviders();

    /**
     * 查询开发环境可用的提供商
     * 
     * @return 开发环境提供商列表
     */
    @Select("SELECT * FROM scm_ai_provider WHERE status = 'DEVELOPMENT' AND is_enabled = 1 ORDER BY sort_order ASC")
    List<LlmProviderEntity> findDevelopmentProviders();

    /**
     * 检查提供商名称是否存在
     * 
     * @param name 提供商名称
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 存在数量
     */
    @Select("SELECT COUNT(1) FROM scm_ai_provider WHERE name = #{name} " +
            "AND (#{excludeId} IS NULL OR id != #{excludeId})")
    int countByName(@Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * 启用/禁用提供商
     * 
     * @param id 提供商ID
     * @param enabled 是否启用
     * @return 影响行数
     */
    @Update("UPDATE scm_ai_provider SET is_enabled = #{enabled}, u_time = NOW() WHERE id = #{id}")
    int updateEnabled(@Param("id") Long id, @Param("enabled") Boolean enabled);

    /**
     * 设置系统级别启用状态
     * 
     * @param id 提供商ID
     * @param systemEnabled 是否系统级别启用
     * @return 影响行数
     */
    @Update("UPDATE scm_ai_provider SET is_system_enabled = #{systemEnabled}, u_time = NOW() WHERE id = #{id}")
    int updateSystemEnabled(@Param("id") Long id, @Param("systemEnabled") Boolean systemEnabled);

    /**
     * 更新提供商状态
     * 
     * @param id 提供商ID
     * @param status 新状态
     * @return 影响行数
     */
    @Update("UPDATE scm_ai_provider SET status = #{status}, u_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新排序字段
     * 
     * @param id 提供商ID
     * @param sortOrder 排序值
     * @return 影响行数
     */
    @Update("UPDATE scm_ai_provider SET sort_order = #{sortOrder}, u_time = NOW() WHERE id = #{id}")
    int updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    /**
     * 更新API配置
     * 
     * @param id 提供商ID
     * @param baseUrl 基础URL
     * @param apiKey API密钥
     * @return 影响行数
     */
    @Update("UPDATE scm_ai_provider SET base_url = #{baseUrl}, api_key = #{apiKey}, u_time = NOW() WHERE id = #{id}")
    int updateApiConfig(@Param("id") Long id, @Param("baseUrl") String baseUrl, @Param("apiKey") String apiKey);

    /**
     * 查询特定类型且配置完整的提供商数量
     * 
     * @param providerType 提供商类型
     * @return 配置完整的提供商数量
     */
    @Select("SELECT COUNT(1) FROM scm_ai_provider WHERE provider_type = #{providerType} " +
            "AND is_enabled = 1 AND base_url IS NOT NULL AND base_url != '' " +
            "AND api_key IS NOT NULL AND api_key != ''")
    int countConfiguredByType(@Param("providerType") String providerType);

    /**
     * 根据Coze Bot ID查询提供商
     * 
     * @param cozeBotId Coze Bot ID
     * @return 提供商实体
     */
    @Select("SELECT * FROM scm_ai_provider WHERE coze_bot_id = #{cozeBotId} AND is_enabled = 1 LIMIT 1")
    LlmProviderEntity findByCozeBotId(@Param("cozeBotId") String cozeBotId);
}