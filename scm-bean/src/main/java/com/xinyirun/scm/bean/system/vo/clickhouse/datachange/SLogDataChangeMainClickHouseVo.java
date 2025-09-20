package com.xinyirun.scm.bean.system.vo.clickhouse.datachange;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 数据变更主日志表 VO
 * </p>
 *
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-09-19 - 数据变更日志架构实现
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SLogDataChangeMainClickHouseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -537597325091147930L;

    private String id;

    /**
     * 单号类型
     */
    private String order_type;

    /**
     * 单号
     */
    private String order_code;

    /**
     * 名称
     */
    private String name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 最后更新时间
     */
    private LocalDateTime u_time;

    /**
     * 更新人名称
     */
    private String u_name;

    /**
     * 更新人ID
     */
    private String u_id;

    /**
     * 请求ID
     */
    private String request_id;

    /**
     * 租户代码
     */
    private String tenant_code;

    /**
     * 开始时间（查询用）
     */
    private LocalDateTime start_time;

    /**
     * 结束时间（查询用）
     */
    private LocalDateTime over_time;

    /**
     * 分页条件
     */
    private PageCondition pageCondition;
}