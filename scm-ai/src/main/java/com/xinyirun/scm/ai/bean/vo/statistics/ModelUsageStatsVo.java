package com.xinyirun.scm.ai.bean.vo.statistics;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 模型使用统计VO
 *
 * @author SCM-AI重构团队
 * @since 2025-09-29
 */
@Data
public class ModelUsageStatsVo {

    /**
     * AI提供商
     */
    private String ai_provider;

    /**
     * AI模型类型
     */
    private String ai_model_type;

    /**
     * 租户
     */
    private String tenant;

    /**
     * 总请求次数
     */
    private Long total_requests;

    /**
     * 成功请求次数
     */
    private Long success_requests;

    /**
     * 总Token数
     */
    private Long total_tokens;

    /**
     * 总费用
     */
    private BigDecimal total_cost;

    /**
     * 平均响应时间
     */
    private BigDecimal avg_response_time;

    /**
     * 唯一用户数
     */
    private Long unique_users;
}