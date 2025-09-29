package com.xinyirun.scm.ai.mapper.model;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.model.AiModelSourceEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AI模型源表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface AiModelSourceMapper extends BaseMapper<AiModelSourceEntity> {

    /**
     * 批量插入模型源记录
     */
    @Insert("""
    <script>
        INSERT INTO ai_model_source (id, name, type, provider_name, permission_type, status, owner, owner_type,
        base_name, model_type, app_key, api_url, adv_settings, c_time, u_time, c_id, u_id, dbversion, is_default, ai_config_id)
        VALUES
        <foreach collection='list' item='item' separator=','>
            (#{item.id}, #{item.name}, #{item.type}, #{item.provider_name}, #{item.permission_type}, #{item.status},
            #{item.owner}, #{item.owner_type}, #{item.base_name}, #{item.model_type}, #{item.app_key}, #{item.api_url},
            #{item.adv_settings}, #{item.c_time}, #{item.u_time}, #{item.c_id}, #{item.u_id}, #{item.dbversion},
            #{item.is_default}, #{item.ai_config_id})
        </foreach>
    </script>
    """)
    int batchInsert(@Param("list") List<AiModelSourceEntity> list);

    /**
     * 查询所有激活的模型源
     */
    @Select("""
        SELECT
            id,
            name,
            type,
            provider_name,
            permission_type,
            status,
            owner,
            owner_type,
            base_name,
            model_type,
            app_key,
            api_url,
            adv_settings,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            is_default,
            ai_config_id
        FROM ai_model_source
        WHERE status = 1
        ORDER BY c_time DESC
        """)
    List<AiModelSourceEntity> selectActiveModels();

    /**
     * 根据模型类型查询模型源
     */
    @Select("""
        SELECT
            id,
            name,
            type,
            provider_name,
            permission_type,
            status,
            owner,
            owner_type,
            base_name,
            model_type,
            app_key,
            api_url,
            adv_settings,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            is_default,
            ai_config_id
        FROM ai_model_source
        WHERE model_type = #{modelType} AND status = 1
        ORDER BY c_time DESC
        """)
    List<AiModelSourceEntity> selectByModelType(@Param("modelType") String modelType);

    /**
     * 根据名称查询模型源
     */
    @Select("""
        SELECT
            id,
            name,
            type,
            provider_name,
            permission_type,
            status,
            owner,
            owner_type,
            base_name,
            model_type,
            app_key,
            api_url,
            adv_settings,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            is_default,
            ai_config_id
        FROM ai_model_source
        WHERE name = #{name}
        LIMIT 1
        """)
    AiModelSourceEntity selectByName(@Param("name") String name);

    /**
     * 根据名称模糊查询模型源
     */
    @Select("""
        SELECT
            id,
            name,
            type,
            provider_name,
            permission_type,
            status,
            owner,
            owner_type,
            base_name,
            model_type,
            app_key,
            api_url,
            adv_settings,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            is_default,
            ai_config_id
        FROM ai_model_source
        WHERE name LIKE CONCAT('%', #{name}, '%')
        ORDER BY c_time DESC
        """)
    List<AiModelSourceEntity> selectByNameLike(@Param("name") String name);

    /**
     * 更新模型源状态
     */
    @Update("""
        UPDATE ai_model_source
        SET status = #{status}, u_time = #{updateTime}, u_id = #{updateUserId}, dbversion = dbversion + 1
        WHERE id = #{id}
        """)
    int updateActiveStatus(@Param("id") String id,
                          @Param("status") Boolean status,
                          @Param("updateTime") java.time.LocalDateTime updateTime,
                          @Param("updateUserId") Long updateUserId);

    /**
     * 统计模型源数量
     */
    @Select("""
        SELECT COUNT(*) FROM ai_model_source
        WHERE status = 1
        """)
    long countActiveModels();

    /**
     * 根据API URL查询模型源
     */
    @Select("""
        SELECT
            id,
            name,
            type,
            provider_name,
            permission_type,
            status,
            owner,
            owner_type,
            base_name,
            model_type,
            app_key,
            api_url,
            adv_settings,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            is_default,
            ai_config_id
        FROM ai_model_source
        WHERE api_url = #{apiUrl}
        LIMIT 1
        """)
    AiModelSourceEntity selectByApiUrl(@Param("apiUrl") String apiUrl);

    /**
     * 查询默认模型源
     */
    @Select("""
        SELECT
            id,
            name,
            type,
            provider_name,
            permission_type,
            status,
            owner,
            owner_type,
            base_name,
            model_type,
            app_key,
            api_url,
            adv_settings,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            is_default,
            ai_config_id
        FROM ai_model_source
        WHERE is_default = 1 AND status = 1
        ORDER BY c_time ASC
        LIMIT 1
        """)
    AiModelSourceEntity selectDefaultModel();

    /**
     * 根据提供商查询模型源
     */
    @Select("""
        SELECT
            id,
            name,
            type,
            provider_name,
            permission_type,
            status,
            owner,
            owner_type,
            base_name,
            model_type,
            app_key,
            api_url,
            adv_settings,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            is_default,
            ai_config_id
        FROM ai_model_source
        WHERE provider_name = #{providerName} AND status = 1
        ORDER BY c_time DESC
        """)
    List<AiModelSourceEntity> selectByProvider(@Param("providerName") String providerName);

    /**
     * 根据权限类型查询模型源
     */
    @Select("""
        SELECT
            id,
            name,
            type,
            provider_name,
            permission_type,
            status,
            owner,
            owner_type,
            base_name,
            model_type,
            app_key,
            api_url,
            adv_settings,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion,
            is_default,
            ai_config_id
        FROM ai_model_source
        WHERE permission_type = #{permissionType} AND status = 1
        ORDER BY c_time DESC
        """)
    List<AiModelSourceEntity> selectByPermissionType(@Param("permissionType") String permissionType);
}