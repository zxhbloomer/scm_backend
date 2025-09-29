package com.xinyirun.scm.ai.bean.vo.statistics;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 用户排行榜VO
 *
 * @author SCM-AI重构团队
 * @since 2025-09-29
 */
@Data
public class UserRankingVo {

    /**
     * 用户ID
     */
    private String user_id;

    /**
     * 租户
     */
    private String tenant;

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
     * 平均响应时间
     */
    private BigDecimal avg_response_time;
}