package com.xinyirun.scm.ai.core.mapper.chat;

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

}