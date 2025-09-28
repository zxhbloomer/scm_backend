package com.xinyirun.scm.ai.mapper.model;

import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceCreateNameVo;
import com.xinyirun.scm.ai.mapper.model.OptionVo;
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
    @Select("<script>" +
            "SELECT ai.id, ai.model_name as name, ai.type, ai.owner, ai.is_enabled as status, " +
            "ai.owner_type as ownerType, ai.base_name as baseName, ai.api_key as appKey, " +
            "ai.api_url as apiUrl, ai.provider as providerName, ai.c_time as createTime, " +
            "ai.permission_type as permissionType, ai.create_user as createUser, " +
            "m.name as createUserName " +
            "FROM ai_model_source ai LEFT JOIN m_staff m ON m.code = ai.create_user " +
            "WHERE 1=1 " +
            "<if test='request.keyword != null and request.keyword != \"\"'>" +
            "AND ai.model_name LIKE CONCAT('%', #{request.keyword}, '%') " +
            "</if>" +
            "<if test='request.owner != null and request.owner != \"\"'>" +
            "AND ai.owner = #{request.owner} " +
            "</if>" +
            "<if test='request.providerName != null and request.providerName != \"\"'>" +
            "AND ai.provider = #{request.providerName} " +
            "</if>" +
            "ORDER BY ai.c_time DESC" +
            "</script>")
    List<AiModelSourceCreateNameVo> list(@Param("request") AiModelSourceRequestVo aiModelSourceRequest);

    /**
     * 查询启用的模型源名称列表
     */
    @Select("SELECT id as value, model_name as text " +
            "FROM ai_model_source " +
            "WHERE is_enabled = 1 " +
            "AND (owner = 'system' OR owner = #{userId}) " +
            "ORDER BY permission_type ASC")
    List<OptionVo> enableSourceNameList(@Param("userId") String userId);

    /**
     * 查询启用的个人模型源名称列表
     */
    @Select("SELECT id as value, model_name as text " +
            "FROM ai_model_source " +
            "WHERE is_enabled = 1 " +
            "AND owner = #{userId}")
    List<OptionVo> enablePersonalSourceNameList(@Param("userId") String userId);

    /**
     * 查询公共模型源列表
     */
    @Select("SELECT id as value, name as text " +
            "FROM ai_model_source " +
            "WHERE is_active = 1 " +
            "AND create_user IS NULL " +
            "ORDER BY create_time DESC")
    List<OptionVo> getPublicSourceList();

    /**
     * 查询用户私有模型源列表
     */
    @Select("SELECT id as value, name as text " +
            "FROM ai_model_source " +
            "WHERE is_active = 1 " +
            "AND create_user = #{userId} " +
            "ORDER BY create_time DESC")
    List<OptionVo> getPrivateSourceList(@Param("userId") String userId);

    /**
     * 根据模型类型查询选项列表
     */
    @Select("SELECT id as value, CONCAT(name, ' (', model_type, ')') as text " +
            "FROM ai_model_source " +
            "WHERE is_active = 1 " +
            "AND model_type = #{modelType} " +
            "ORDER BY create_time DESC")
    List<OptionVo> getSourceListByType(@Param("modelType") String modelType);

    /**
     * 查询模型源统计信息
     */
    @Select("SELECT " +
            "model_type, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN is_active = 1 THEN 1 ELSE 0 END) as active_count " +
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
            "WHERE ams.is_active = 1 " +
            "GROUP BY ams.id, ams.name, ams.model_type " +
            "ORDER BY usage_count DESC " +
            "LIMIT #{limit}")
    List<AiModelSourceVo> getMostPopularSources(@Param("limit") Integer limit);
}