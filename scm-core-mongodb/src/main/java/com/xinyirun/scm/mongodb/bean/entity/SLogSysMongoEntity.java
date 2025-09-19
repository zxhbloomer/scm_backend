package com.xinyirun.scm.mongodb.bean.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(collection = "s_log_sys")
public class SLogSysMongoEntity  implements Serializable {

    private static final long serialVersionUID = -5126665294285188559L;

    @Id
    private String id;

    /**
     * 异常"NG"，正常"OK"
     */
    private String type;

    /**
     * 操作用户
     */
    private String user_name;

    /**
     * 员工姓名
     */
    private String staff_name;

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

    private LocalDateTime c_time;

    /**
     * 请求id
     */
    private String request_id;


    /**
     * 返回信息
     */
    private String result;

    /**
     * 租户code
     */
    private String tenant_code;
}
