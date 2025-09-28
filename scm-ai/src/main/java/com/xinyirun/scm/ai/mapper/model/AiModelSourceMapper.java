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
    @Insert("<script>" +
            "INSERT INTO ai_model_source (id, name, base_url, api_key, model_type, description, is_active, create_time, update_time) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.id}, #{item.name}, #{item.baseUrl}, #{item.apiKey}, #{item.modelType}, #{item.description}, #{item.isActive}, #{item.createTime}, #{item.updateTime})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<AiModelSourceEntity> list);

    /**
     * 查询所有激活的模型源
     */
    @Select("SELECT id, name, base_url, api_key, model_type, description, is_active, create_time, update_time " +
            "FROM ai_model_source " +
            "WHERE is_active = 1 " +
            "ORDER BY create_time DESC")
    List<AiModelSourceEntity> selectActiveModels();

    /**
     * 根据模型类型查询模型源
     */
    @Select("SELECT id, name, base_url, api_key, model_type, description, is_active, create_time, update_time " +
            "FROM ai_model_source " +
            "WHERE model_type = #{modelType} AND is_active = 1 " +
            "ORDER BY create_time DESC")
    List<AiModelSourceEntity> selectByModelType(@Param("modelType") String modelType);

    /**
     * 根据名称查询模型源
     */
    @Select("SELECT id, name, base_url, api_key, model_type, description, is_active, create_time, update_time " +
            "FROM ai_model_source " +
            "WHERE name = #{name} " +
            "LIMIT 1")
    AiModelSourceEntity selectByName(@Param("name") String name);

    /**
     * 根据名称模糊查询模型源
     */
    @Select("SELECT id, name, base_url, api_key, model_type, description, is_active, create_time, update_time " +
            "FROM ai_model_source " +
            "WHERE name LIKE CONCAT('%', #{name}, '%') " +
            "ORDER BY create_time DESC")
    List<AiModelSourceEntity> selectByNameLike(@Param("name") String name);

    /**
     * 更新模型源状态
     */
    @Update("UPDATE ai_model_source " +
            "SET is_active = #{isActive}, update_time = #{updateTime} " +
            "WHERE id = #{id}")
    int updateActiveStatus(@Param("id") String id,
                          @Param("isActive") Boolean isActive,
                          @Param("updateTime") Long updateTime);

    /**
     * 统计模型源数量
     */
    @Select("SELECT COUNT(*) FROM ai_model_source " +
            "WHERE is_active = 1")
    long countActiveModels();

    /**
     * 根据基础URL查询模型源
     */
    @Select("SELECT id, name, base_url, api_key, model_type, description, is_active, create_time, update_time " +
            "FROM ai_model_source " +
            "WHERE base_url = #{baseUrl} " +
            "LIMIT 1")
    AiModelSourceEntity selectByBaseUrl(@Param("baseUrl") String baseUrl);

    /**
     * 查询默认模型源
     */
    @Select("SELECT id, name, base_url, api_key, model_type, description, is_active, create_time, update_time " +
            "FROM ai_model_source " +
            "WHERE is_active = 1 " +
            "ORDER BY create_time ASC " +
            "LIMIT 1")
    AiModelSourceEntity selectDefaultModel();
}