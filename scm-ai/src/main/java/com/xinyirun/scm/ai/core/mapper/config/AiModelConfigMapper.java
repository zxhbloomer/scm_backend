package com.xinyirun.scm.ai.core.mapper.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.config.AiModelConfigEntity;
import com.xinyirun.scm.ai.bean.vo.config.AiModelConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI模型配置Mapper接口
 */
@Mapper
public interface AiModelConfigMapper extends BaseMapper<AiModelConfigEntity> {

    /**
     * 查询模型配置列表(带创建者和更新者姓名)
     *
     * @param keyword 关键词
     * @param providerName 供应商名称
     * @return 模型配置VO列表
     */
    @Select("""
        <script>
        SELECT
            t1.id,
            t1.model_name AS modelName,
            t1.model_type AS modelType,
            t1.provider,
            t1.api_key AS apiKey,
            t1.base_url AS baseUrl,
            t1.deployment_name AS deploymentName,
            t1.temperature,
            t1.max_tokens AS maxTokens,
            t1.top_p AS topP,
            t1.timeout,
            t1.enabled,
            t1.support_chat AS supportChat,
            t1.support_vision AS supportVision,
            t1.support_embedding AS supportEmbedding,
            t1.c_time AS cTime,
            t1.u_time AS uTime,
            t1.c_id AS cId,
            t1.u_id AS uId,
            t1.dbversion,
            t2.name AS createUserName,
            t3.name AS updateUserName
        FROM ai_model_config t1
        LEFT JOIN m_staff t2 ON t1.c_id = t2.id
        LEFT JOIN m_staff t3 ON t1.u_id = t3.id
        <where>
            <if test="providerName != null and providerName != ''">
                AND t1.provider = #{providerName}
            </if>
            <if test="keyword != null and keyword != ''">
                AND (t1.model_name LIKE CONCAT('%', #{keyword}, '%')
                     OR t1.deployment_name LIKE CONCAT('%', #{keyword}, '%'))
            </if>
        </where>
        ORDER BY t1.c_time DESC
        </script>
    """)
    List<AiModelConfigVo> selectListWithUserName(@Param("keyword") String keyword,
                                                   @Param("providerName") String providerName);

    /**
     * 根据ID查询模型配置(带创建者和更新者姓名)
     *
     * @param id 模型ID
     * @return 模型配置VO
     */
    @Select("""
        SELECT
            t1.id,
            t1.model_name AS modelName,
            t1.model_type AS modelType,
            t1.provider,
            t1.api_key AS apiKey,
            t1.base_url AS baseUrl,
            t1.deployment_name AS deploymentName,
            t1.temperature,
            t1.max_tokens AS maxTokens,
            t1.top_p AS topP,
            t1.timeout,
            t1.enabled,
            t1.support_chat AS supportChat,
            t1.support_vision AS supportVision,
            t1.support_embedding AS supportEmbedding,
            t1.c_time AS cTime,
            t1.u_time AS uTime,
            t1.c_id AS cId,
            t1.u_id AS uId,
            t1.dbversion,
            t2.name AS createUserName,
            t3.name AS updateUserName
        FROM ai_model_config t1
        LEFT JOIN m_staff t2 ON t1.c_id = t2.id
        LEFT JOIN m_staff t3 ON t1.u_id = t3.id
        WHERE t1.id = #{id}
    """)
    AiModelConfigVo selectByIdWithUserName(@Param("id") Long id);

    /**
     * 查询可用的语言模型列表（support_chat=true且enabled=true）
     *
     * @return 模型配置列表
     */
    @Select("""
        SELECT id, model_name AS modelName, provider, deployment_name AS deploymentName
        FROM ai_model_config
        WHERE enabled = 1 AND support_chat = 1
        ORDER BY c_time DESC
    """)
    List<AiModelConfigVo> selectAvailableLlmModels();

    /**
     * 查询可用的视觉模型列表（support_vision=true且enabled=true）
     *
     * @return 模型配置列表
     */
    @Select("""
        SELECT id, model_name AS modelName, provider, deployment_name AS deploymentName
        FROM ai_model_config
        WHERE enabled = 1 AND support_vision = 1
        ORDER BY c_time DESC
    """)
    List<AiModelConfigVo> selectAvailableVisionModels();

    /**
     * 查询可用的嵌入模型列表（support_embedding=true且enabled=true）
     *
     * @return 模型配置列表
     */
    @Select("""
        SELECT id, model_name AS modelName, provider, deployment_name AS deploymentName
        FROM ai_model_config
        WHERE enabled = 1 AND support_embedding = 1
        ORDER BY c_time DESC
    """)
    List<AiModelConfigVo> selectAvailableEmbeddingModels();
}
