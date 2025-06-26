package com.xinyirun.scm.bean.system.vo.mail;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/12/12 11:31
 */

@Data
public class SMailConfigVo implements Serializable {

    private static final long serialVersionUID = -6567990114822048741L;

    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 邮箱服务器host
     */
    private String host;

    /**
     * 邮箱服务器 port
     */
    private Integer port;

    /**
     * 邮箱服务器 username
     */
    private String username;

    /**
     * 邮箱服务器 password
     */
    private String password;

    /**
     * 邮箱服务器 sender
     */
    private String sender;

    /**
     * 邮箱服务器 from
     */
    private String from_;

    /**
     * 是否默认配置，0:否。1:是
     */
    private Boolean activity;

    /**
     * 安全传输方式 1:plain 2:tls 3:ssl
     */
    private Integer security_type;

    /**
     * 创建人
     */
    private Long c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 最后修改人
     */
    private Long u_id;

    /**
     * 最后修改时间
     */
    private LocalDateTime u_time;

    /**
     * 0:未删除。1:已删除
     */
    private Boolean deleted;

    /**
     * 版本
     */
    private Integer dbversion;
}
