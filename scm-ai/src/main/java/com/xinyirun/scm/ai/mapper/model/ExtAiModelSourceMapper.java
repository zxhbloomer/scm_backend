package com.xinyirun.scm.ai.mapper.model;

import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceCreateNameVo;
import com.xinyirun.scm.ai.bean.vo.response.ModelSourceOptionVo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AI模型源扩展Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface ExtAiModelSourceMapper {

    /**
     * 根据请求条件查询模型源列表
     */
    @Select("""
    <script>
        SELECT
            ai.id,
            ai.name,
            ai.type,
            ai.owner,
            ai.status,
            ai.owner_type as ownerType,
            ai.base_name as baseName,
            ai.app_key as appKey,
            ai.api_url as apiUrl,
            ai.provider_name as providerName,
            ai.c_time as createTime,
            ai.permission_type as permissionType,
            ai.c_id as createUser,
            ai.u_time,
            ai.u_id,
            ai.dbversion,
            ai.is_default,
            ai.ai_config_id
        FROM ai_model_source ai
        WHERE 1=1
        <if test='request.keyword != null and request.keyword != &quot;&quot;'>
            AND ai.name LIKE CONCAT('%', #{request.keyword}, '%')
        </if>
        <if test='request.owner != null and request.owner != &quot;&quot;'>
            AND ai.owner = #{request.owner}
        </if>
        <if test='request.providerName != null and request.providerName != &quot;&quot;'>
            AND ai.provider_name = #{request.providerName}
        </if>
        ORDER BY ai.c_time DESC
    </script>
    """)
    List<AiModelSourceCreateNameVo> list(@Param("request") AiModelSourceRequestVo aiModelSourceRequest);

    /**
     * 查询启用的模型源名称列表
     */
    @Select("SELECT id as value, name as text " +
            "FROM ai_model_source " +
            "WHERE status = 1 " +
            "AND (owner = 'system' OR c_id = #{userId}) " +
            "ORDER BY permission_type ASC")
    List<ModelSourceOptionVo> enableSourceNameList(@Param("userId") Long userId);

    /**
     * 查询启用的个人模型源名称列表
     */
    @Select("SELECT id as value, name as text " +
            "FROM ai_model_source " +
            "WHERE status = 1 " +
            "AND c_id = #{userId}")
    List<ModelSourceOptionVo> enablePersonalSourceNameList(@Param("userId") Long userId);

    /**
     * 查询公共模型源列表
     */
    @Select("SELECT id as value, name as text " +
            "FROM ai_model_source " +
            "WHERE status = 1 " +
            "AND permission_type = 'public' " +
            "ORDER BY c_time DESC")
    List<ModelSourceOptionVo> getPublicSourceList();

    /**
     * 查询用户私有模型源列表
     */
    @Select("SELECT id as value, name as text " +
            "FROM ai_model_source " +
            "WHERE status = 1 " +
            "AND c_id = #{userId} " +
            "AND permission_type = 'private' " +
            "ORDER BY c_time DESC")
    List<ModelSourceOptionVo> getPrivateSourceList(@Param("userId") Long userId);

    /**
     * 根据模型类型查询选项列表
     */
    @Select("SELECT id as value, CONCAT(name, ' (', model_type, ')') as text " +
            "FROM ai_model_source " +
            "WHERE status = 1 " +
            "AND model_type = #{modelType} " +
            "ORDER BY c_time DESC")
    List<ModelSourceOptionVo> getSourceListByType(@Param("modelType") String modelType);

    /**
     * 查询模型源统计信息
     */
    @Select("SELECT " +
            "model_type, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as active_count " +
            "FROM ai_model_source " +
            "GROUP BY model_type " +
            "ORDER BY total_count DESC")
    List<AiModelSourceVo> getModelSourceStats();

    /**
     * 查询最受欢迎的模型源
     */
    @Select("SELECT ams.id, ams.name, ams.model_type, " +
            "COUNT(acc.model_source_id) as usage_count " +
            "FROM ai_model_source ams " +
            "LEFT JOIN ai_conversation_content acc ON ams.id = acc.model_source_id " +
            "WHERE ams.status = 1 " +
            "GROUP BY ams.id, ams.name, ams.model_type " +
            "ORDER BY usage_count DESC " +
            "LIMIT #{limit}")
    List<AiModelSourceVo> getMostPopularSources(@Param("limit") Integer limit);

    /**
     * 根据提供商查询模型源选项
     */
    @Select("SELECT id as value, CONCAT(name, ' - ', provider_name) as text " +
            "FROM ai_model_source " +
            "WHERE status = 1 " +
            "AND provider_name = #{providerName} " +
            "ORDER BY c_time DESC")
    List<ModelSourceOptionVo> getSourceListByProvider(@Param("providerName") String providerName);

    /**
     * 查询默认模型源选项
     */
    @Select("SELECT id as value, name as text " +
            "FROM ai_model_source " +
            "WHERE status = 1 " +
            "AND is_default = 1 " +
            "ORDER BY c_time ASC " +
            "LIMIT 1")
    List<ModelSourceOptionVo> getDefaultSourceOption();

    /**
     * 统计租户模型源数量
     */
    @Select("""
        SELECT COUNT(*)
        FROM ai_model_source
        """)
    long countByTenant();

    /**
     * 统计启用的模型源数量
     */
    @Select("""
        SELECT COUNT(*)
        FROM ai_model_source
        WHERE status = 1
        """)
    long countActiveByTenant();
}