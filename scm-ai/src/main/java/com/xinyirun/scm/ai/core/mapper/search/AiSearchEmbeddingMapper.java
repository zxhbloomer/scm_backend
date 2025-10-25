package com.xinyirun.scm.ai.core.mapper.search;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.search.AiSearchEmbeddingEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI搜索向量 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiSearchEmbeddingMapper extends BaseMapper<AiSearchEmbeddingEntity> {

    /**
     * 按搜索UUID查询向量列表
     *
     * @param search_uuid 搜索UUID
     * @return 向量列表
     */
    @Select("""
        SELECT
            id,
            search_uuid AS searchUuid,
            engine_name AS engineName,
            content,
            embedding_vector AS embeddingVector,
            metadata,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_search_embedding
        WHERE search_uuid = #{search_uuid}
          AND is_deleted = 0
        ORDER BY c_time ASC
    """)
    List<AiSearchEmbeddingEntity> selectBySearchUuid(@Param("search_uuid") String search_uuid);

    /**
     * 按引擎名称查询向量
     *
     * @param engine_name 搜索引擎名称
     * @param search_uuid 搜索UUID
     * @return 向量列表
     */
    @Select("""
        SELECT
            id,
            search_uuid AS searchUuid,
            engine_name AS engineName,
            content,
            embedding_vector AS embeddingVector,
            metadata,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_search_embedding
        WHERE engine_name = #{engine_name}
          AND search_uuid = #{search_uuid}
          AND is_deleted = 0
        ORDER BY c_time ASC
    """)
    List<AiSearchEmbeddingEntity> selectByEngineName(@Param("engine_name") String engine_name,
                                                      @Param("search_uuid") String search_uuid);

    /**
     * 批量删除搜索UUID相关的向量
     *
     * @param search_uuid 搜索UUID
     * @return 删除的行数
     */
    @Update("""
        UPDATE ai_search_embedding
        SET is_deleted = 1
        WHERE search_uuid = #{search_uuid}
    """)
    int deleteBySearchUuid(@Param("search_uuid") String search_uuid);
}
