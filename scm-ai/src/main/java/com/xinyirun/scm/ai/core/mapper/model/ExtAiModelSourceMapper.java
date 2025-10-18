package com.xinyirun.scm.ai.core.mapper.model;

import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceRequestVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
    @Select("""
    <script>
        SELECT
            ai.id,
            ai.name AS model_name,
            ai.type,
            ai.owner,
            ai.status,
            ai.owner_type AS owner_type,
            ai.base_name AS base_name,
            ai.app_key AS api_key,
            ai.api_url AS api_url,
            ai.provider_name AS provider,
            ai.c_time,
            ai.permission_type AS permission_type,
            ai.c_id,
            ai.create_user AS createUserName,
            ai.u_time,
            ai.u_id,
            ai.dbversion,
            ai.context_window AS context_window,
            ai.max_input_tokens AS max_input_tokens,
            ai.max_output_tokens AS max_output_tokens
        FROM ai_model_source ai
        WHERE 1=1
        <if test='request.keyword != null and request.keyword != &quot;&quot;'>
            AND ai.name LIKE CONCAT('%', #{request.keyword}, '%')
        </if>
        <if test='request.owner != null and request.owner != &quot;&quot;'>
            AND ai.owner = #{request.owner}
        </if>
        <if test='request.providerName != null and request.providerName != &quot;&quot;'>
            AND ai.provider_name = #{request.providerName}
        </if>
        ORDER BY ai.c_time DESC
    </script>
    """)
    List<AiModelSourceVo> list(@Param("request") AiModelSourceRequestVo aiModelSourceRequest);

}