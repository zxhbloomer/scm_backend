package com.xinyirun.scm.bean.app.bo.log.sys;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统日志bo
 * @author zhangxh
 */
@Data
@Builder
@AllArgsConstructor
public class AppLogBo implements Serializable {

    private static final long serialVersionUID = 5574193723551123682L;

    /**
     * 异常"NG"，正常"OK"
     */
    private String type;

    private String className;

    private String httpMethod;

    private String classMethod;

    private String params;

    private Long execTime;

    private String remark;

    private LocalDateTime createDate;

    private String url;

    private String ip;

    /**
     * 异常信息
     */
    private String exception;

}
