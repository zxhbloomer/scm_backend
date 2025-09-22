package com.xinyirun.scm.bean.system.vo.clickhouse.datachange;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 数据变更操作日志表 VO
 * </p>
 *
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-09-19 - 数据变更日志架构实现
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SLogDataChangeOperateClickHouseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -637597325091147930L;

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
     * 员工ID
     */
    private String staff_id;

    /**
     * 操作说明描述
     */
    private String operation;

    /**
     * 操作耗时毫秒
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
     * HTTP请求方法
     */
    private String http_method;

    /**
     * 请求URL地址
     */
    private String url;

    /**
     * 客户端IP地址
     */
    private String ip;

    /**
     * 异常信息详情
     */
    private String exception;

    /**
     * 操作时间
     */
    private LocalDateTime operate_time;

    /**
     * 页面名称
     */
    private String page_name;

    /**
     * 请求ID
     */
    private String request_id;

    /**
     * 终端类型：pc、app、api
     */
    private String terminal;

    /**
     * 接口返回信息
     */
    private String result;

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

    List<SLogDataChangeDetailClickHouseVo> dataChangeList;

    /**
     * 具体的变更前、变更后的数据
     */
    private List<SLogDataChangeDetailOldNewVo> oldNewdetails;

    /**
     * 分页条件
     */
    private PageCondition pageCondition;
}