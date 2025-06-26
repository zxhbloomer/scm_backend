package com.xinyirun.scm.bean.entity.mongo.log.app;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * api系统日志
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Document("s_log_app")
public class SLogAppMongoEntity implements Serializable {

    private static final long serialVersionUID = 3203707511748771834L;

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
     * 返回结果
     */
    private String result;

    /**
     * 异常信息
     */
    private String exception;

    private LocalDateTime c_time;

    private String request_id;

    /**
     * 租户code
     */
    private String tenant_code;
}
