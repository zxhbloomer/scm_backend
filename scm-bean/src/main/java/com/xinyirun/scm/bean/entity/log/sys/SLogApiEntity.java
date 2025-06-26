package com.xinyirun.scm.bean.entity.log.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@TableName("s_log_api")
public class SLogApiEntity extends BaseEntity<SLogApiEntity> implements Serializable {


    private static final long serialVersionUID = 5465020529206971573L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 异常"NG"，正常"OK"
     */
    @TableField("type")
    private String type;

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
     * 来源
     */
    @TableField("app_code")
    private String app_code;

    /**
     * 返回结果
     */
    @TableField("result")
    private String result;

    /**
     * 异常信息
     */
    @TableField("exception")
    private String exception;

    @TableField(value="c_time", fill = FieldFill.INSERT)
    private LocalDateTime c_time;


}
