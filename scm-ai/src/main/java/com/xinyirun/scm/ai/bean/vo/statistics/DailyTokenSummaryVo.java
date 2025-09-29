package com.xinyirun.scm.ai.bean.vo.statistics;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 每日Token统计汇总VO
 *
 * @author SCM-AI重构团队
 * @since 2025-09-29
 */
@Data
public class DailyTokenSummaryVo {

    /**
     * 使用日期
     */
    private LocalDate usage_date;

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
     * 唯一用户数
     */
    private Long unique_users;

    /**
     * 唯一模型数
     */
    private Long unique_models;

    /**
     * 平均响应时间
     */
    private BigDecimal avg_response_time;
}