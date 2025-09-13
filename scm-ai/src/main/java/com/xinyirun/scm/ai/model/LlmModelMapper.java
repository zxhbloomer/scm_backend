/*
 * SCM AI Module - LLM Model Mapper
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: LLM模型数据访问层接口
 */
package com.xinyirun.scm.ai.model;

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
 * LLM模型数据访问接口
 * 提供LLM模型的CRUD操作和业务查询方法
 */
@Mapper  
public interface LlmModelMapper extends BaseMapper<LlmModelEntity> {

    /**
     * 根据提供商UID查询模型列表
     * 
     * @param providerUid 提供商UID
     * @return 模型列表
     */
    @Select("SELECT * FROM scm_ai_model WHERE provider_uid = #{providerUid} AND is_enabled = 1 ORDER BY sort_order ASC")
    List<LlmModelEntity> findByProviderUid(@Param("providerUid") String providerUid);

    /**
     * 根据模型类型查询启用的模型列表
     * 
     * @param type 模型类型
     * @return 模型列表
     */
    @Select("SELECT * FROM scm_ai_model WHERE model_type = #{type} AND is_enabled = 1 ORDER BY sort_order ASC")
    List<LlmModelEntity> findByType(@Param("type") String type);

    /**
     * 根据提供商名称查询模型列表
     * 
     * @param providerName 提供商名称
     * @return 模型列表
     */
    @Select("SELECT * FROM scm_ai_model WHERE provider_name = #{providerName} AND is_enabled = 1 ORDER BY sort_order ASC")
    List<LlmModelEntity> findByProviderName(@Param("providerName") String providerName);

    /**
     * 查询所有启用的模型
     * 
     * @return 启用的模型列表
     */
    @Select("SELECT * FROM scm_ai_model WHERE is_enabled = 1 ORDER BY sort_order ASC, c_time DESC")
    List<LlmModelEntity> findAllEnabled();

    /**
     * 查询系统级别启用的模型
     * 
     * @return 系统启用的模型列表
     */
    @Select("SELECT * FROM scm_ai_model WHERE is_enabled = 1 AND is_system_enabled = 1 ORDER BY sort_order ASC")
    List<LlmModelEntity> findSystemEnabled();

    /**
     * 根据模型名称查询模型
     * 
     * @param name 模型名称
     * @return 模型实体
     */
    @Select("SELECT * FROM scm_ai_model WHERE name = #{name} LIMIT 1")
    LlmModelEntity findByName(@Param("name") String name);

    /**
     * 检查模型名称是否存在
     * 
     * @param name 模型名称
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 存在数量
     */
    @Select("SELECT COUNT(1) FROM scm_ai_model WHERE name = #{name} " +
            "AND (#{excludeId} IS NULL OR id != #{excludeId})")
    int countByName(@Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * 启用/禁用模型
     * 
     * @param id 模型ID
     * @param enabled 是否启用
     * @return 影响行数
     */
    @Update("UPDATE scm_ai_model SET is_enabled = #{enabled}, u_time = NOW() WHERE id = #{id}")
    int updateEnabled(@Param("id") Long id, @Param("enabled") Boolean enabled);

    /**
     * 设置系统级别启用状态
     * 
     * @param id 模型ID
     * @param systemEnabled 是否系统级别启用
     * @return 影响行数
     */
    @Update("UPDATE scm_ai_model SET is_system_enabled = #{systemEnabled}, u_time = NOW() WHERE id = #{id}")
    int updateSystemEnabled(@Param("id") Long id, @Param("systemEnabled") Boolean systemEnabled);

    /**
     * 更新排序字段
     * 
     * @param id 模型ID
     * @param sortOrder 排序值
     * @return 影响行数
     */
    @Update("UPDATE scm_ai_model SET sort_order = #{sortOrder}, u_time = NOW() WHERE id = #{id}")
    int updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    /**
     * 查询特定提供商下特定类型的模型数量
     * 
     * @param providerUid 提供商UID
     * @param type 模型类型
     * @return 模型数量
     */
    @Select("SELECT COUNT(1) FROM scm_ai_model WHERE provider_uid = #{providerUid} AND model_type = #{type}")
    int countByProviderAndType(@Param("providerUid") String providerUid, @Param("type") String type);
}