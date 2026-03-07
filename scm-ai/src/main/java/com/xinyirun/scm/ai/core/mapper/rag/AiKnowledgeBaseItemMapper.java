package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseItemVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI知识库文档项 Mapper接口
 *
 * @author SCM AI Team
 * @since 2025-10-03
 */
@Mapper
public interface AiKnowledgeBaseItemMapper extends BaseMapper<AiKnowledgeBaseItemEntity> {

    /**
     * 按UUID查询文档（用于Bean操作的selectById）
     *
     * @param item_uuid 文档UUID
     * @return 文档实体
     */
    @Select("""
        SELECT
            id,
            item_uuid AS itemUuid,
            kb_id AS kbId,
            kb_uuid AS kbUuid,
            title,
            brief,
            remark,
            source_file_id AS sourceFileId,
            source_file_name AS sourceFileName,
            source_file_upload_time AS sourceFileUploadTime,
            embedding_model AS embeddingModel,
            embedding_status AS embeddingStatus,
            embedding_status_change_time AS embeddingStatusChangeTime,
            graphical_status AS graphicalStatus,
            graphical_status_change_time AS graphicalStatusChangeTime,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_knowledge_base_item
        WHERE item_uuid = #{item_uuid}
    """)
    AiKnowledgeBaseItemEntity selectByItemUuid(@Param("item_uuid") String item_uuid);

    /**
     * 按向量化状态统计文档数量（查询操作用SQL）
     * 状态值：0-未索引，1-待处理，2-处理中，3-已完成，4-失败
     *
     * @param kb_uuid 知识库UUID
     * @param embedding_status 向量化状态（0/1/2/3/4）
     * @return 文档数量
     */
    @Select("""
        SELECT COUNT(*)
        FROM ai_knowledge_base_item
        WHERE kb_uuid = #{kb_uuid} AND embedding_status = #{embedding_status}
    """)
    Long countByKbUuidAndEmbeddingStatus(@Param("kb_uuid") String kb_uuid,
                                          @Param("embedding_status") Integer embedding_status);

    /**
     * 根据知识库UUID物理删除所有知识项
     *
     * @param kb_uuid 知识库UUID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_knowledge_base_item
        WHERE kb_uuid = #{kb_uuid}
    """)
    int deleteByKbUuid(@Param("kb_uuid") String kb_uuid);

    /**
     * 按知识库UUID查询所有文档项
     *
     * @param kb_uuid 知识库UUID
     * @return 文档项列表
     */
    @Select("""
        SELECT
            id,
            item_uuid AS itemUuid,
            kb_id AS kbId,
            kb_uuid AS kbUuid,
            title,
            brief,
            remark,
            source_file_id AS sourceFileId,
            source_file_name AS sourceFileName,
            source_file_upload_time AS sourceFileUploadTime,
            embedding_model AS embeddingModel,
            embedding_status AS embeddingStatus,
            embedding_status_change_time AS embeddingStatusChangeTime,
            graphical_status AS graphicalStatus,
            graphical_status_change_time AS graphicalStatusChangeTime,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_knowledge_base_item
        WHERE kb_uuid = #{kb_uuid}
    """)
    List<AiKnowledgeBaseItemEntity> selectListByKbUuid(@Param("kb_uuid") String kb_uuid);

    /**
     * 分页搜索知识库文档项
     * 按title或sourceFileName模糊匹配keyword
     *
     * @param page 分页参数
     * @param kbUuid 知识库UUID
     * @param keyword 关键词（模糊匹配title或source_file_name）
     * @return 分页结果
     */
    @Select("""
        <script>
        SELECT
            id,
            item_uuid AS itemUuid,
            kb_id AS kbId,
            kb_uuid AS kbUuid,
            title,
            brief,
            remark,
            source_file_id AS sourceFileId,
            source_file_name AS sourceFileName,
            source_file_upload_time AS sourceFileUploadTime,
            embedding_model AS embeddingModel,
            embedding_status AS embeddingStatus,
            embedding_status_change_time AS embeddingStatusChangeTime,
            graphical_status AS graphicalStatus,
            graphical_status_change_time AS graphicalStatusChangeTime,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_knowledge_base_item
        WHERE kb_uuid = #{kbUuid}
        <if test="keyword != null and keyword != ''">
            AND (title LIKE CONCAT('%', #{keyword}, '%')
                 OR source_file_name LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        ORDER BY c_time DESC
        </script>
    """)
    IPage<AiKnowledgeBaseItemVo> searchByKbUuid(Page<AiKnowledgeBaseItemVo> page,
                                                  @Param("kbUuid") String kbUuid,
                                                  @Param("keyword") String keyword);
}
