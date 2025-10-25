package com.xinyirun.scm.ai.core.mapper.search;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.search.AiSearchRecordEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI搜索记录 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiSearchRecordMapper extends BaseMapper<AiSearchRecordEntity> {

    /**
     * 按UUID查询搜索记录
     *
     * @param search_uuid 搜索UUID
     * @return 搜索记录实体
     */
    @Select("""
        SELECT
            id,
            search_uuid AS searchUuid,
            user_id AS userId,
            user_uuid AS userUuid,
            ai_model_id AS aiModelId,
            question,
            search_engine_resp AS searchEngineResp,
            prompt,
            prompt_tokens AS promptTokens,
            answer,
            answer_tokens AS answerTokens,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_search_record
        WHERE search_uuid = #{search_uuid}
          AND is_deleted = 0
    """)
    AiSearchRecordEntity selectBySearchUuid(@Param("search_uuid") String search_uuid);

    /**
     * 查询用户的搜索历史
     *
     * @param user_id 用户ID
     * @param max_id 最大ID
     * @param page_size 每页数量
     * @return 搜索记录列表
     */
    @Select("""
        SELECT
            id,
            search_uuid AS searchUuid,
            user_id AS userId,
            user_uuid AS userUuid,
            ai_model_id AS aiModelId,
            question,
            search_engine_resp AS searchEngineResp,
            prompt,
            prompt_tokens AS promptTokens,
            answer,
            answer_tokens AS answerTokens,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_search_record
        WHERE user_id = #{user_id}
          AND is_deleted = 0
          AND id < #{max_id}
        ORDER BY c_time DESC
        LIMIT #{page_size}
    """)
    List<AiSearchRecordEntity> selectByUserIdWithPaging(@Param("user_id") Long user_id,
                                                         @Param("max_id") Long max_id,
                                                         @Param("page_size") Integer page_size);

    /**
     * 按关键词搜索历史记录
     *
     * @param user_id 用户ID
     * @param keyword 关键词
     * @return 搜索记录列表
     */
    @Select("""
        SELECT
            id,
            search_uuid AS searchUuid,
            user_id AS userId,
            user_uuid AS userUuid,
            ai_model_id AS aiModelId,
            question,
            search_engine_resp AS searchEngineResp,
            prompt,
            prompt_tokens AS promptTokens,
            answer,
            answer_tokens AS answerTokens,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_search_record
        WHERE user_id = #{user_id}
          AND is_deleted = 0
          AND question LIKE CONCAT('%', #{keyword}, '%')
        ORDER BY c_time DESC
    """)
    List<AiSearchRecordEntity> searchByKeyword(@Param("user_id") Long user_id,
                                                @Param("keyword") String keyword);
}
