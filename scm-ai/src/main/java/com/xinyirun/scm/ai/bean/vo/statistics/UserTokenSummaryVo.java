package com.xinyirun.scm.ai.bean.vo.statistics;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 用户Token使用汇总VO
 *
 * @author SCM-AI重构团队
 * @since 2025-09-29
 */
@Data
public class UserTokenSummaryVo {

    /**
     * 用户ID
     */
    private String user_id;

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
     * 首次使用时间
     */
    private Long first_usage_time;

    /**
     * 最后使用时间
     */
    private Long last_usage_time;
}