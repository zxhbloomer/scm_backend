package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaEntity;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseQaVo;
import com.xinyirun.scm.ai.bean.vo.response.KnowledgeBaseQaResponseVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 知识库问答记录 Mapper接口
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
     * 根据知识库UUID删除问答记录（物理删除）
     *
     * @param kbUuid 知识库UUID
     * @return 删除的记录数
     */
    @Select("DELETE FROM ai_knowledge_base_qa WHERE kb_uuid = #{kbUuid}")
    Integer deleteByKbUuid(@Param("kbUuid") String kbUuid);

    /**
     * 分页搜索问答记录
     *
     * @param page 分页参数
     * @param kbUuid 知识库UUID
     * @param userId 用户ID
     * @param keyword 关键词（模糊匹配question）
     * @return 分页结果
     */
    @Select("""
        SELECT
            id AS id,
            uuid AS uuid,
            kb_id AS kbId,
            kb_uuid AS kbUuid,
            question AS question,
            prompt AS prompt,
            prompt_tokens AS promptTokens,
            answer AS answer,
            answer_tokens AS answerTokens,
            source_file_ids AS sourceFileIds,
            user_id AS userId,
            ai_model_id AS aiModelId,
            create_time AS createTime
        FROM ai_knowledge_base_qa
        WHERE kb_uuid = #{kbUuid}
          AND user_id = #{userId}
          AND is_deleted = 0
          AND (#{keyword} IS NULL OR #{keyword} = '' OR question LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY update_time DESC
    """)
    IPage<AiKnowledgeBaseQaVo> searchByKbUuidAndUserId(Page<AiKnowledgeBaseQaVo> page,
                                                        @Param("kbUuid") String kbUuid,
                                                        @Param("userId") Long userId,
                                                        @Param("keyword") String keyword);

    /**
     * 按UUID查询问答记录（用于删除前校验）
     *
     * @param uuid 问答记录UUID
     * @return 问答记录实体
     */
    @Select("""
        SELECT
            id AS id,
            uuid AS uuid,
            kb_id AS kbId,
            kb_uuid AS kbUuid,
            question AS question,
            user_id AS userId,
            is_deleted AS isDeleted
        FROM ai_knowledge_base_qa
        WHERE uuid = #{uuid}
          AND is_deleted = 0
    """)
    AiKnowledgeBaseQaEntity selectByUuid(@Param("uuid") String uuid);

    /**
     * 统计用户问答记录数量
     *
     * @param userId 用户ID
     * @return 记录数量
     */
    @Select("""
        SELECT COUNT(*)
        FROM ai_knowledge_base_qa
        WHERE user_id = #{userId}
          AND is_deleted = 0
    """)
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 按用户ID物理删除所有问答记录
     *
     * @param userId 用户ID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_knowledge_base_qa
        WHERE user_id = #{userId}
    """)
    int deleteByUserId(@Param("userId") Long userId);

}
