package com.xinyirun.scm.ai.core.mapper.statistics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.statistics.AiTokenUsageEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AI Token使用记录表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface AiTokenUsageMapper extends BaseMapper<AiTokenUsageEntity> {

    /**
     * 按serialIds聚合查询Token总数
     * serial_type固定为ai_conversation_runtime_node
     *
     * @param serialIds 业务记录ID列表（varchar类型）
     * @return Token总数（可能为null）
     */
    @Select("""
        <script>
        SELECT SUM(total_tokens) AS totalTokens
        FROM ai_token_usage
        WHERE serial_type = 'ai_conversation_runtime_node'
          AND serial_id IN
          <foreach item="id" collection="serialIds" open="(" separator="," close=")">
              #{id}
          </foreach>
        </script>
    """)
    Long sumTotalTokensBySerialIds(@Param("serialIds") List<String> serialIds);

    /**
     * 按serialType和serialId查询Token使用记录
     *
     * @param serialType 业务类型（表名）
     * @param serialId 业务记录ID
     * @return Token使用记录
     */
    @Select("""
        SELECT
            id,
            conversation_id AS conversationId,
            user_id AS userId,
            model_source_id AS modelSourceId,
            serial_type AS serialType,
            serial_id AS serialId,
            provider_name AS providerName,
            model_type AS modelType,
            prompt_tokens AS promptTokens,
            completion_tokens AS completionTokens,
            total_tokens AS totalTokens,
            usage_time AS usageTime,
            token_unit_price AS tokenUnitPrice,
            cost,
            success,
            response_time AS responseTime,
            ai_config_id AS aiConfigId
        FROM ai_token_usage
        WHERE serial_type = #{serialType}
          AND serial_id = #{serialId}
        LIMIT 1
    """)
    AiTokenUsageEntity selectBySerialTypeAndSerialId(@Param("serialType") String serialType,
                                                      @Param("serialId") String serialId);
}