package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaEntity;
import com.xinyirun.scm.ai.bean.vo.response.KnowledgeBaseQaResponseVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 知识库问答记录 Mapper接口
 * 对应 aideepin：KnowledgeBaseQaService
 *
 * @author zxh
 * @since 2025-10-12
 */
@Mapper
public interface AiKnowledgeBaseQaMapper extends BaseMapper<AiKnowledgeBaseQaEntity> {

    /**
     * 根据UUID查询问答记录详情
     * 注意：使用AS别名转驼峰（map-underscore-to-camel-case: false）
     */
    @Select("""
        SELECT
            qa.id AS id,
            qa.uuid AS uuid,
            qa.kb_id AS kbId,
            qa.kb_uuid AS kbUuid,
            qa.question AS question,
            qa.prompt AS prompt,
            qa.prompt_tokens AS promptTokens,
            qa.answer AS answer,
            qa.answer_tokens AS answerTokens,
            qa.source_file_ids AS sourceFileIds,
            qa.user_id AS userId,
            qa.ai_model_id AS aiModelId,
            qa.ai_model_name AS aiModelName,
            qa.enable_status AS enableStatus,
            qa.create_time AS createTime,
            qa.update_time AS updateTime,
            qa.is_deleted AS isDeleted,
            qa.create_user AS createUser
        FROM ai_knowledge_base_qa qa
        WHERE qa.uuid = #{uuid}
          AND qa.is_deleted = 0
    """)
    KnowledgeBaseQaResponseVo selectDetailByUuid(@Param("uuid") String uuid);

    /**
     * 分页搜索问答记录
     * 动态SQL：根据条件过滤
     */
    @Select("""
        <script>
        SELECT
            qa.id AS id,
            qa.uuid AS uuid,
            qa.kb_id AS kbId,
            qa.kb_uuid AS kbUuid,
            qa.question AS question,
            qa.prompt AS prompt,
            qa.prompt_tokens AS promptTokens,
            qa.answer AS answer,
            qa.answer_tokens AS answerTokens,
            qa.source_file_ids AS sourceFileIds,
            qa.user_id AS userId,
            qa.ai_model_id AS aiModelId,
            qa.ai_model_name AS aiModelName,
            qa.enable_status AS enableStatus,
            qa.create_time AS createTime,
            qa.update_time AS updateTime,
            qa.create_user AS createUser
        FROM ai_knowledge_base_qa qa
        WHERE qa.is_deleted = 0
          <if test="kbUuid != null and kbUuid != ''">
          AND qa.kb_uuid = #{kbUuid}
          </if>
          <if test="questionKeyword != null and questionKeyword != ''">
          AND qa.question LIKE CONCAT('%', #{questionKeyword}, '%')
          </if>
          <if test="answerKeyword != null and answerKeyword != ''">
          AND qa.answer LIKE CONCAT('%', #{answerKeyword}, '%')
          </if>
          <if test="aiModelId != null and aiModelId != ''">
          AND qa.ai_model_id = #{aiModelId}
          </if>
          <if test="userId != null">
          AND qa.user_id = #{userId}
          </if>
          <if test="startTime != null">
          AND qa.create_time &gt;= #{startTime}
          </if>
          <if test="endTime != null">
          AND qa.create_time &lt;= #{endTime}
          </if>
          <if test="enableStatus != null">
          AND qa.enable_status = #{enableStatus}
          </if>
        ORDER BY
          <choose>
            <when test="sortField == 'prompt_tokens'">
              qa.prompt_tokens
            </when>
            <when test="sortField == 'answer_tokens'">
              qa.answer_tokens
            </when>
            <otherwise>
              qa.create_time
            </otherwise>
          </choose>
          <choose>
            <when test="sortOrder == 'ASC'">
              ASC
            </when>
            <otherwise>
              DESC
            </otherwise>
          </choose>
        </script>
    """)
    List<KnowledgeBaseQaResponseVo> searchPage(
        @Param("kbUuid") String kbUuid,
        @Param("questionKeyword") String questionKeyword,
        @Param("answerKeyword") String answerKeyword,
        @Param("aiModelId") String aiModelId,
        @Param("userId") Long userId,
        @Param("startTime") Long startTime,
        @Param("endTime") Long endTime,
        @Param("enableStatus") Integer enableStatus,
        @Param("sortField") String sortField,
        @Param("sortOrder") String sortOrder
    );
}
