package com.xinyirun.scm.ai.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.model.AiPromptEntity;
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
     * 根据提示词类型分页查询
     */
    @Select("""
    <script>
        SELECT
            id,
            code,
            nickname,
            `desc`,
            type,
            prompt,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_prompt
        WHERE type = #{type}
        ORDER BY c_time DESC
    </script>
    """)
    IPage<AiPromptEntity> selectPageByType(Page<AiPromptEntity> page, @Param("type") Integer type);

    /**
     * 查询所有有效提示词，按类型和名称排序
     */
    @Select("""
        SELECT
            id,
            code,
            nickname,
            `desc`,
            type,
            prompt,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_prompt
        ORDER BY type ASC, nickname ASC
        """)
    List<AiPromptEntity> selectAllActivePrompts();

    /**
     * 根据昵称查询提示词
     */
    @Select("""
        SELECT
            id,
            code,
            nickname,
            `desc`,
            type,
            prompt,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_prompt
        WHERE nickname = #{nickname}
        LIMIT 1
        """)
    AiPromptEntity selectByNickname(@Param("nickname") String nickname);

    /**
     * 根据编码查询提示词
     */
    @Select("""
        SELECT
            id,
            code,
            nickname,
            `desc`,
            type,
            prompt,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_prompt
        WHERE code = #{code}
        LIMIT 1
        """)
    AiPromptEntity selectByCode(@Param("code") String code);

    /**
     * 根据提示词类型查询列表
     */
    @Select("""
        SELECT
            id,
            code,
            nickname,
            `desc`,
            type,
            prompt,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_prompt
        WHERE type = #{type}
        ORDER BY nickname ASC
        """)
    List<AiPromptEntity> selectByType(@Param("type") Integer type);

    /**
     * 批量插入提示词记录
     */
    @Insert("""
    <script>
        INSERT INTO ai_prompt (
            id,
            code,
            nickname,
            `desc`,
            type,
            prompt,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        )
        VALUES
        <foreach collection='list' item='item' separator=','>
            (
                #{item.id},
                #{item.code},
                #{item.nickname},
                #{item.desc},
                #{item.type},
                #{item.prompt},
                #{item.c_time},
                #{item.u_time},
                #{item.c_id},
                #{item.u_id},
                #{item.dbversion}
            )
        </foreach>
    </script>
    """)
    int batchInsert(@Param("list") List<AiPromptEntity> list);

    /**
     * 更新提示词内容
     */
    @Update("""
        UPDATE ai_prompt SET
            prompt = #{prompt},
            u_time = #{uTime},
            u_id = #{uId},
            dbversion = dbversion + 1
        WHERE id = #{id}
        """)
    int updatePromptContent(@Param("id") String id,
                           @Param("prompt") String prompt,
                           @Param("uTime") java.time.LocalDateTime uTime,
                           @Param("uId") Long uId);

    /**
     * 根据编码模糊查询
     */
    @Select("""
        SELECT
            id,
            code,
            nickname,
            `desc`,
            type,
            prompt,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_prompt
        WHERE code LIKE CONCAT('%', #{code}, '%')
        ORDER BY nickname ASC
        """)
    List<AiPromptEntity> selectByCodeLike(@Param("code") String code);

    /**
     * 根据简称模糊查询
     */
    @Select("""
        SELECT
            id,
            code,
            nickname,
            `desc`,
            type,
            prompt,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_prompt
        WHERE nickname LIKE CONCAT('%', #{nickname}, '%')
        ORDER BY nickname ASC
        """)
    List<AiPromptEntity> selectByNicknameLike(@Param("nickname") String nickname);

    /**
     * 统计提示词总数量
     */
    @Select("""
        SELECT COUNT(*)
        FROM ai_prompt
        """)
    long countTotal();

    /**
     * 统计指定类型提示词数量
     */
    @Select("""
        SELECT COUNT(*)
        FROM ai_prompt
        WHERE type = #{type}
        """)
    long countByType(@Param("type") Integer type);
}