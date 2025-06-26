package com.xinyirun.scm.bean.entity.log.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@TableName("s_log_sys")
public class SLogSysEntity extends BaseEntity<SLogSysEntity> implements Serializable {

    private static final long serialVersionUID = -4758264062460745328L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 异常"NG"，正常"OK"
     */
    @TableField("type")
    private String type;

    /**
     * 操作用户
     */
    @TableField("user_name")
    private String user_name;

    /**
     * 操作说明
     */
    @TableField("operation")
    private String operation;

    /**
     * 耗时（毫秒）
     */
    @TableField("time")
    private Long time;

    @TableField("class_name")
    private String class_name;

    @TableField("class_method")
    private String class_method;

    /**
     * HTTP方法
     */
    @TableField("http_method")
    private String http_method;

    /**
     * 参数
     */
    @TableField("params")
    private String params;

    /**
     * session json
     */
    @TableField("session")
    private String session;

    /**
     * url
     */
    @TableField("url")
    private String url;

    /**
     * IP地址
     */
    @TableField("ip")
    private String ip;

    /**
     * 异常信息
     */
    @TableField("exception")
    private String exception;

    @TableField(value="c_time", fill = FieldFill.INSERT)
    private LocalDateTime c_time;


}
