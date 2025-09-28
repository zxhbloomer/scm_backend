package com.xinyirun.scm.ai.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.chat.AiPromptEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AI提示词表 Mapper接口
 *
 * 提供AI提示词的数据访问操作，使用注解SQL实现
 * 支持提示词的增删改查和业务查询
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Repository
@Mapper
public interface AiPromptMapper extends BaseMapper<AiPromptEntity> {

    /**
     * 根据提示词类型查询启用的提示词
     */
    @Select("SELECT id, prompt_name, prompt_content, prompt_type, is_system, tenant, " +
            "c_time, u_time, c_id, u_id, dbversion " +
            "FROM ai_prompt " +
            "WHERE prompt_type = #{promptType} AND tenant = #{tenant} " +
            "ORDER BY prompt_name")
    List<AiPromptEntity> selectByTypeAndTenant(@Param("promptType") String promptType, @Param("tenant") String tenant);

    /**
     * 根据提示词名称和租户查询
     */
    @Select("SELECT id, prompt_name, prompt_content, prompt_type, is_system, tenant, " +
            "c_time, u_time, c_id, u_id, dbversion " +
            "FROM ai_prompt " +
            "WHERE prompt_name = #{promptName} AND tenant = #{tenant} " +
            "LIMIT 1")
    AiPromptEntity selectByNameAndTenant(@Param("promptName") String promptName, @Param("tenant") String tenant);

    /**
     * 查询系统内置提示词
     */
    @Select("SELECT id, prompt_name, prompt_content, prompt_type, is_system, tenant, " +
            "c_time, u_time, c_id, u_id, dbversion " +
            "FROM ai_prompt " +
            "WHERE is_system = 1 " +
            "ORDER BY prompt_type, prompt_name")
    List<AiPromptEntity> selectSystemPrompts();

    /**
     * 查询用户自定义提示词
     */
    @Select("SELECT id, prompt_name, prompt_content, prompt_type, is_system, tenant, " +
            "c_time, u_time, c_id, u_id, dbversion " +
            "FROM ai_prompt " +
            "WHERE is_system = 0 AND tenant = #{tenant} " +
            "ORDER BY prompt_type, prompt_name")
    List<AiPromptEntity> selectUserPrompts(@Param("tenant") String tenant);

    /**
     * 批量插入提示词记录
     */
    @Insert("<script>" +
            "INSERT INTO ai_prompt (prompt_name, prompt_content, prompt_type, is_system, tenant, " +
            "c_time, u_time, c_id, u_id, dbversion) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.prompt_name}, #{item.prompt_content}, #{item.prompt_type}, #{item.is_system}, #{item.tenant}, " +
            "#{item.c_time}, #{item.u_time}, #{item.c_id}, #{item.u_id}, #{item.dbversion})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<AiPromptEntity> list);

    /**
     * 更新提示词内容
     */
    @Update("UPDATE ai_prompt SET " +
            "prompt_content = #{promptContent}, " +
            "u_time = #{uTime}, " +
            "u_id = #{uId}, " +
            "dbversion = dbversion + 1 " +
            "WHERE id = #{id} AND tenant = #{tenant}")
    int updatePromptContent(@Param("id") Integer id,
                           @Param("promptContent") String promptContent,
                           @Param("uTime") java.time.LocalDateTime uTime,
                           @Param("uId") Long uId,
                           @Param("tenant") String tenant);
}