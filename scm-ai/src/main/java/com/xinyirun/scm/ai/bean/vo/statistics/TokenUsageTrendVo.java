package com.xinyirun.scm.ai.bean.vo.statistics;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Token使用趋势VO
 *
 * @author SCM-AI重构团队
 * @since 2025-09-29
 */
@Data
public class TokenUsageTrendVo {

    /**
     * 使用日期
     */
    private LocalDate usage_date;

    /**
     * 请求次数
     */
    private Long request_count;

    /**
     * 总Token数
     */
    private Long total_tokens;

    /**
     * 总费用
     */
    private BigDecimal total_cost;

    /**
     * 活跃用户数
     */
    private Long active_users;
}