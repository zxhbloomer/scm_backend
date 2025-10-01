package com.xinyirun.scm.ai.core.mapper.model;

import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceCreateNameVo;
import com.xinyirun.scm.ai.bean.vo.response.ModelSourceOptionVo;
import org.apache.ibatis.annotations.*;
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
            ai.name,
            ai.type,
            ai.owner,
            ai.status,
            ai.owner_type as ownerType,
            ai.base_name as baseName,
            ai.app_key as appKey,
            ai.api_url as apiUrl,
            ai.provider_name as providerName,
            ai.c_time as createTime,
            ai.permission_type as permissionType,
            ai.c_id as createUser,
            ai.u_time,
            ai.u_id,
            ai.dbversion,
            ai.is_default,
            ai.ai_config_id
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
    List<AiModelSourceCreateNameVo> list(@Param("request") AiModelSourceRequestVo aiModelSourceRequest);

}