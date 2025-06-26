package com.xinyirun.scm.bean.system.vo.sys.log;

import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 系统日志表
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SLogSysVo extends BaseEntity<SLogSysVo> implements Serializable {

    private static final long serialVersionUID = -3041300599432483529L;

    private Long id;

    /**
     * 异常"NG"，正常"OK"
     */
    private String type;

    /**
     * 操作用户
     */
    private String user_name;

    /**
     * 操作说明
     */
    private String operation;

    /**
     * 耗时（毫秒）
     */
    private Long time;

    private String class_name;

    private String class_method;

    /**
     * HTTP方法
     */
    private String http_method;

    /**
     * 参数
     */
    private String params;

    /**
     * session json
     */
    private String session;

    /**
     * url
     */
    private String url;

    /**
     * IP地址
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
     * 开始时间
     */
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    private LocalDateTime over_time;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}
