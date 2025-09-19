package com.xinyirunscm.scm.clickhouse.vo;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClickHouse 系统日志查询条件VO
 * 与MongoDB的SLogSysMongoVo保持一致的查询功能
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ClickHouseSysLogQueryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

    /**
     * 日志类型：异常"NG"，正常"OK"
     */
    private String type;

    /**
     * 操作用户账号
     */
    private String user_name;

    /**
     * 员工姓名
     */
    private String staff_name;

    /**
     * 操作说明描述
     */
    private String operation;

    /**
     * 操作耗时（毫秒）
     */
    private Long time;

    /**
     * 调用的类名
     */
    private String class_name;

    /**
     * 调用的方法名
     */
    private String class_method;

    /**
     * HTTP请求方法类型
     */
    private String http_method;

    /**
     * 请求参数，JSON字符串格式
     */
    private String params;

    /**
     * Session信息，JSON字符串格式
     */
    private String session;

    /**
     * 请求URL地址
     */
    private String url;

    /**
     * 客户端IP地址
     */
    private String ip;

    /**
     * 异常信息
     */
    private String exception;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 请求唯一标识ID
     */
    private String request_id;

    /**
     * 接口返回信息
     */
    private String result;

    /**
     * 租户代码
     */
    private String tenant_code;

    // ============ 查询专用字段 ============

    /**
     * 查询开始时间
     */
    private LocalDateTime start_time;

    /**
     * 查询结束时间
     */
    private LocalDateTime over_time;

    /**
     * 分页条件
     */
    private PageCondition pageCondition;

    /**
     * 耗时范围查询 - 最小值（毫秒）
     */
    private Long min_time;

    /**
     * 耗时范围查询 - 最大值（毫秒）
     */
    private Long max_time;

    /**
     * 是否排除大字段（提升查询性能）
     * 默认true，排除params、session、exception字段
     */
    private Boolean excludeLargeFields = true;

    /**
     * 排序字段，默认按创建时间倒序
     */
    private String orderBy = "c_time DESC";

    /**
     * 查询限制，默认1000条
     */
    private Integer limit = 1000;

    /**
     * 判断是否为异常日志查询
     */
    public boolean isErrorLogQuery() {
        return "NG".equals(this.type);
    }

    /**
     * 判断是否为正常日志查询
     */
    public boolean isNormalLogQuery() {
        return "OK".equals(this.type);
    }

    /**
     * 判断是否有时间范围查询
     */
    public boolean hasTimeRange() {
        return start_time != null || over_time != null;
    }

    /**
     * 判断是否有耗时范围查询
     */
    public boolean hasTimeSpanRange() {
        return min_time != null || max_time != null;
    }

    /**
     * 获取安全的查询限制
     */
    public int getSafeLimit() {
        if (limit == null || limit <= 0) {
            return 1000;
        }
        return Math.min(limit, 10000); // 最大限制10000，防止性能问题
    }
}